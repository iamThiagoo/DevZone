package br.com.devzone.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import br.com.devzone.R;
import br.com.devzone.activities.MainActivity;
import br.com.devzone.adapters.CourseVideoAdapter;
import br.com.devzone.classes.Course;
import br.com.devzone.classes.CourseVideo;

/**
 * A fragment representing a list of Items.
 */
public class CourseVideoFragment extends Fragment {

    private ArrayList<CourseVideo> courseVideos;
    private String courseId;
    private Course course;
    private View root;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private static final String CHANNEL_ID = "pdf_download_channel";
    private static final int NOTIFICATION_ID = 1;


    public CourseVideoFragment() {
    }

    @SuppressWarnings("unused")
    public static CourseVideoFragment newInstance(ArrayList<CourseVideo> courseVideos, Course course) {
        CourseVideoFragment fragment = new CourseVideoFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("courseVideos", courseVideos);
        args.putString("courseId", course.getId());
        args.putParcelable("course", course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseVideos = getArguments().getParcelableArrayList("courseVideos");
            courseId = getArguments().getString("courseId");
            course = getArguments().getParcelable("course");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_course_video, container, false);
        ListView listView = root.findViewById(R.id.listView);

        // Adicione dados ao ListView
        CourseVideoAdapter adapter = new CourseVideoAdapter(getContext(), courseVideos);
        listView.setAdapter(adapter);

        // Resgata instância do Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Resgata possível usuário logado
        user = mAuth.getCurrentUser();

        // Habilita ou não botão de baixar certificado
        enableCertificateDownload();

        // Set the item click listener
        adapter.setOnItemClickListener(new CourseVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CourseVideo video = courseVideos.get(position);
            }
        });

        return root;
    }


    /**
     * Método que habilita ou não botão de baixar certificado
     */
    private void enableCertificateDownload() {

        // Resgata instância do Firebasee Auth e atribui usuário logado
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Button certificateDownloadBtn = root.findViewById(R.id.btnCertificateDownload);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("user_courses")
                .whereEqualTo("courseId", courseId)
                .whereEqualTo("userId", user.getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String documentId = document.getId();
                            Map<String, Object> data = document.getData();
                            Double completion_percentage = document.getDouble("completion_percentage");

                            if (completion_percentage != null && completion_percentage.equals(100.00)) {
                                certificateDownloadBtn.setEnabled(true);

                                certificateDownloadBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        createAndDownloadPdf();
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d("Firestore", "Nenhum documento encontrado");
                    }
                } else {
                    Log.e("Firestore", "Erro ao executar a consulta", task.getException());
                }
            }
        });
    }


    /**
     * Método que cria e baixa certificado do curso
     */
    private void createAndDownloadPdf() {

        ContentResolver resolver = requireContext().getContentResolver();
        ContentValues contentValues = new ContentValues();

        String nomeCertificado = course.getNome().trim().replaceAll(" ", "_").toLowerCase();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "certificado_" + nomeCertificado + ".pdf");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri pdfUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            pdfUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        }

        if (pdfUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(pdfUri)) {
                if (outputStream != null) {
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdfDocument = new PdfDocument(writer);
                    Document document = new Document(pdfDocument);

                    Table table = new Table(1);
                    table.setWidth(500); // largura da tabela
                    table.setTextAlignment(TextAlignment.CENTER);

                    // Adicionar o título do certificado
                    Cell titleCell = new Cell();
                    titleCell.add(new Paragraph("Certificado de Conclusão de Curso")
                            .setFontSize(24)
                            .setBold());
                    table.addCell(titleCell);

                    // Adicionar as informações do certificado
                    Cell studentInfoCell = new Cell();
                    studentInfoCell.add(new Paragraph("\n"));
                    studentInfoCell.add(new Paragraph("Curso: " + course.getNome()));
                    studentInfoCell.add(new Paragraph("Este certificado é concedido a: " + user.getDisplayName()));
                    studentInfoCell.add(new Paragraph("\n"));
                    studentInfoCell.add(new Paragraph("\n"));

                    studentInfoCell.add(new Paragraph("Parabéns pelo esforço e dedicação!"));
                    studentInfoCell.add(new Paragraph("DevZone"));

                    table.addCell(studentInfoCell);

                    // Adicionar a tabela ao documento
                    document.add(table);

                    // Fecha o documento
                    document.close();

                    // Cria notificação indicando que o PDF foi salvo com sucesso
                    showDownloadNotification();

                    Log.d("PDF", "Baixou e gerou arquivo pdf com sucesso!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void showDownloadNotification() {
        String channelName = "PDF Download";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(requireContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_devzone_foreground)
                .setContentTitle("PDF Download")
                .setContentText("O PDF foi baixado com sucesso!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}