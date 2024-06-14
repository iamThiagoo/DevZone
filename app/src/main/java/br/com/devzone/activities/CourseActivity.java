package br.com.devzone.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.devzone.R;
import br.com.devzone.adapters.CourseViewAdapter;
import br.com.devzone.classes.Course;
import br.com.devzone.classes.CourseVideo;
import br.com.devzone.classes.UserCourse;

public class CourseActivity extends AppCompatActivity {

    protected FirebaseUser user;
    protected String courseId;
    protected Course course;
    protected ArrayList<CourseVideo> videos;
    protected CourseViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Resgata instância do Firebasee Auth e atribui usuário logado
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Resgata a Intent e atribui courseId a classe
        Intent intent = getIntent();
        courseId = intent.getStringExtra("course_id");

        // Resgata o curso e seus vídeos
        getCourseAndVideos();
    }


    /**
     * Método que resgata o curso e seus vídeos do banco de dados e disponibiliza
     * para o uso da classe
     */
    protected void getCourseAndVideos() {
        Course.getCourseById(courseId, new Course.OnCourseLoadedListener() {
            @Override
            public void onCourseLoaded(Course loadedCourse) {
                // Disponibiliza o curso para a classe
                course = loadedCourse;

                // Define o nome do Toolbar
                getSupportActionBar().setTitle(course.getNome());

                UserCourse.getUserCourse(courseId, user.getUid(), new UserCourse.OnUserCourseLoadedListener() {
                    @Override
                    public void onUserCourseLoaded(UserCourse userCourse) {
                        if (userCourse != null) {
                            // Carrega vídeos do curso
                            loadCourseVideos().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadVideoInWebView(userCourse);
                                    } else {
                                        Log.d("loadCourseVideos", "Erro ao carregar vídeos: ", task.getException());
                                    }
                                }
                            });
                        } else {
                            // Matricula usuário no curso
                            Log.d("Firestore", "UserCourse não encontrado");

                            UserCourse.createUserCourse(course, user.getUid(), new UserCourse.OnUserCourseCreatedListener() {
                                @Override
                                public void onUserCourseCreated(UserCourse createdUserCourse, Exception e) {
                                    if (createdUserCourse != null) {
                                        Log.d("Firestore", "UserCourse criado");
                                        loadCourseVideos();
                                    } else {
                                        // Lida com o erro ao criar o UserCourse
                                        handleUserCourseCreationError(e);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Registra log
                Log.d("Firestore", "Error loading course: " + errorMessage);

                // Mostra toast para o usuário
                Toast.makeText(getApplicationContext(), "Erro ao carregar curso", Toast.LENGTH_SHORT).show();

                // Volta para a tela inicial do app
                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     *
     */
    private Task<Void> loadCourseVideos() {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        course.loadVideos().addOnCompleteListener(new OnCompleteListener<ArrayList<CourseVideo>>() {
            @Override
            public void onComplete(@NonNull Task<ArrayList<CourseVideo>> task) {
                if (task.isSuccessful()) {
                    videos = task.getResult();
                    loadVideosInListView();
                    taskCompletionSource.setResult(null); // Task<Void> expected result
                } else {
                    String errorMessage = task.getException().getMessage();
                    Log.d("Firestore", "Error loading videos: " + errorMessage);
                    Toast.makeText(getApplicationContext(), "Erro ao carregar vídeos do curso", Toast.LENGTH_SHORT).show();

                    // Volta para a tela inicial do app
                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                    startActivity(intent);
                    finish();
                    taskCompletionSource.setException(task.getException());
                }
            }
        });

        return taskCompletionSource.getTask();
    }


    /**
     * Método que carrega o vídeo atual do usuário
     */
    protected void loadVideosInListView() {
        adapter = new CourseViewAdapter(this, videos, course);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        String[] tabTitles = new String[]{"Aulas", "Atividades"};

        TabLayout tabs = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }


    /**
     * Método que carrega o vídeo no WebView para usuário assistir
     */
    private void loadVideoInWebView(UserCourse userCourse)
    {
        CourseVideo courseVideo = null;

        for(CourseVideo video : videos) {
            if (video.getId().equals(userCourse.getCurrentCourseVideoId())) {
                courseVideo = video;
            }
        }

        String iframe = courseVideo.getIframeEmbed();
        WebView webView = findViewById(R.id.courseWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadData(iframe, "text/html", "UTF-8");

        // Verifica se usuário já assistiu esse vídeo, se não ativa ação de ouvir o player do video
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("user_course_videos")
                .whereEqualTo("course_video_id", courseVideo.getId())
                .whereEqualTo("user_id", user.getUid());

        CourseVideo finalCourseVideo = courseVideo;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    boolean videoWatched = !querySnapshot.isEmpty(); // Se o querySnapshot não estiver vazio, significa que o usuário já assistiu ao vídeo

                    if (!videoWatched) {
                        // Se o vídeo ainda não foi assistido, atribua o OnTouchListener ao WebView
                        webView.setOnTouchListener(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                // Desabilita o OnTouchListener do WebView
                                webView.setOnTouchListener(null);

                                // Adiciona no banco que usuário assistiu o vídeo
                                Map<String, Object> userCourseVideo = new HashMap<>();
                                userCourseVideo.put("user_id", user.getUid());
                                userCourseVideo.put("course_video_id", finalCourseVideo.getId());
                                userCourseVideo.put("course_id", courseId);
                                userCourseVideo.put("created_at", FieldValue.serverTimestamp());

                                db.collection("user_course_videos")
                                    .add(userCourseVideo)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Integer totalVideos = videos.size();
                                            // 2. Obtenha o número de vídeos que o usuário assistiu
                                            db.collection("user_course_videos")
                                                    .whereEqualTo("user_id", user.getUid())
                                                    .whereEqualTo("course_id", courseId)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                int watchedVideos = task.getResult().size();
                                                                Log.d("Firestore", "Total de vídeo assistidos " + String.valueOf(watchedVideos));

                                                                Query query = db.collection("user_courses")
                                                                        .whereEqualTo("courseId", userCourse.getCourseId())
                                                                        .whereEqualTo("userId", userCourse.getUserId());

                                                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Iterar sobre os documentos correspondentes
                                                                            for (DocumentSnapshot document : task.getResult()) {
                                                                                // Obter a referência do documento
                                                                                DocumentReference docRef = document.getReference();

                                                                                double completionPercentage = ((double) watchedVideos / totalVideos) * 100;
                                                                                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                                                                String formattedPercentage = decimalFormat.format(completionPercentage);

                                                                                Map<String, Object> update = new HashMap<>();
                                                                                update.put("completion_percentage", completionPercentage);

                                                                                docRef.set(update, SetOptions.merge())
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Log.d("Firestore", "Porcentagem de conclusão atualizada com sucesso");
                                                                                            } else {
                                                                                                Log.e("Firestore", "Erro ao atualizar a porcentagem de conclusão", task.getException());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            }
                                                                        } else {
                                                                            Log.e("Firestore", "Erro ao executar a consulta", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Log.e("Firestore", "Erro ao obter vídeos assistidos", task.getException());
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firestore", "Erro ao inserir o registro", e);
                                        }
                                    });

                                return false;
                            }
                        });
                    }
                } else {
                    // Ocorreu um erro ao executar a consulta
                    Log.d("Firestore", "Erro ao executar a consulta: " + task.getException().getMessage());
                }
            }
        });
    }

    private void handleUserCourseCreationError(Exception exception) {
        // Lida com o erro ao criar o UserCourse
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    private void goToHomeScreen() {
        // Volta para a tela inicial do app
        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}