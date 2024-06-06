package br.com.devzone.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.devzone.R;
import br.com.devzone.activities.CourseActivity;
import br.com.devzone.classes.CourseVideo;
import br.com.devzone.classes.UserCourseVideo;

public class CourseVideoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CourseVideo> items;
    private OnItemClickListener onItemClickListener;
    private Map<String, Boolean> watchedStatusMap = new HashMap<>();

    public CourseVideoAdapter(Context context, ArrayList<CourseVideo> items) {
        this.context = context;
        this.items = items;
        fetchWatchedStatus();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CourseVideo getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_course_video, parent, false);
        }

        CourseVideo item = items.get(position);
        TextView order = convertView.findViewById(R.id.course_video_position);

        // Acrescenta mais um para a listagem dos vídeos não iniciar em 0
        String positionInList = String.valueOf(position + 1);
        order.setText(positionInList + ".");

        TextView title = convertView.findViewById(R.id.course_video_name);
        title.setText(item.getName());

        // Resgata instância do Firebasee Auth e atribui usuário logado
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Verifique o status de visualização armazenado
        Boolean watched = watchedStatusMap.get(item.getId());
        if (Boolean.TRUE.equals(watched)) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.info));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        // Set onClickListener for item
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);

                    CourseVideo courseVideo = items.get(position);

                    // Inicialização do Firestore e referência à coleção
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference userCoursesRef = db.collection("user_courses");

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();

                    Map<String, Object> userCourseData = new HashMap<>();
                    userCourseData.put("currentCourseVideoId", courseVideo.getId());

                    userCoursesRef
                        .whereEqualTo("courseId", item.getCourse().getId())
                        .whereEqualTo("userId", user.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Obtendo o ID do documento para atualizar
                                String documentIdToUpdate = queryDocumentSnapshots.getDocuments().get(0).getId();

                                // Atualizando o documento com os dados fornecidos
                                userCoursesRef.document(documentIdToUpdate)
                                        .set(userCourseData, SetOptions.merge())
                                        .addOnSuccessListener(aVoid -> {
                                            // Sucesso na atualização, inicia nova activity e finaliza a atual
                                            Log.d("Firestore Update", "Documento atualizado com sucesso.");
                                            Intent intent = new Intent(context, CourseActivity.class);
                                            intent.putExtra("course_id", item.getCourse().getId());
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Falha na atualização
                                            Log.w("Firestore Update", "Erro ao atualizar documento", e);
                                        });
                            }
                    })
                    .addOnFailureListener(e -> {
                        // Falha na busca
                        Log.w("Firestore Query", "Erro ao buscar documento", e);
                    });
                }
            }
        });

        return convertView;
    }

    private void fetchWatchedStatus() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            for (CourseVideo item : items) {
                UserCourseVideo.checkIfUserWatchedVideo(user.getUid(), item.getCourse().getId(), item.getId(), new userWatchedVideoCallback() {
                    @Override
                    public void onCallback(boolean watched) {
                        watchedStatusMap.put(item.getId(), watched);
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public interface userWatchedVideoCallback {
        void onCallback(boolean watched);
    }
}