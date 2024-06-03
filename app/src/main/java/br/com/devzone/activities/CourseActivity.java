package br.com.devzone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

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
                                        loadVideoInWebView(userCourse.getCurrentCourseVideoId());
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
        CourseViewAdapter adapter = new CourseViewAdapter(this, videos);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        String[] tabTitles = new String[]{"Aulas", "Atividades"};

        TabLayout tabs = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }


    /**
     * Método que carrega o vídeo no WebView para usuário assistir
     */
    private void loadVideoInWebView(String videoId)
    {
        CourseVideo courseVideo = null;

        for(CourseVideo video : videos) {
            if (video.getId().equals(videoId)) {
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
    }


    private void handleUserCourseCreationError(Exception exception) {
        // Lida com o erro ao criar o UserCourse
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    private void handleVideoLoadingError(String errorMessage) {
        // Lida com o erro ao carregar os vídeos
        Log.d("Firestore", "Error loading videos: " + errorMessage);
        Toast.makeText(getApplicationContext(), "Erro ao carregar curso", Toast.LENGTH_SHORT).show();
        goToHomeScreen();
    }

    private void handleCourseLoadingError(String errorMessage) {
        // Lida com o erro ao carregar o curso
        Log.d("Firestore", "Error loading course: " + errorMessage);
        Toast.makeText(getApplicationContext(), "Erro ao carregar curso", Toast.LENGTH_SHORT).show();
        goToHomeScreen();
    }

    private void goToHomeScreen() {
        // Volta para a tela inicial do app
        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}