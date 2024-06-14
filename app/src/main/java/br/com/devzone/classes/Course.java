package br.com.devzone.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Course implements Parcelable {
    private String id;
    private String nome;
    private int quantidade;
    private String caminhoImagem;
    private ArrayList<CourseVideo> videos = new ArrayList<CourseVideo>();

    public Course(String id, String nome, int quantidade, String caminhoImagem) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.caminhoImagem = caminhoImagem;
    }

    public String getId() { return id; }

    public String getNome() {
        return nome;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public static void getCourseById(String id, final OnCourseLoadedListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("courses").document(id).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Course course = new Course(
                                    document.getId(),
                                    document.getString("name"),
                                    0,
                                    document.getString("image_url")
                            );
                            listener.onCourseLoaded(course);
                        } else {
                            listener.onError("No such document");
                        }
                    } else {
                        Log.d("getCourseError", "Error getting document: ", task.getException());
                        listener.onError(task.getException().getMessage());
                    }
                }
            });
    }

    public Task<ArrayList<CourseVideo>> loadVideos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TaskCompletionSource<ArrayList<CourseVideo>> taskCompletionSource = new TaskCompletionSource<>();

        db.collection("course_videos")
                .whereEqualTo("course_id", id)
                .orderBy("order")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<CourseVideo> loadedVideos = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CourseVideo video = new CourseVideo(
                                        Course.this, // Passa a referência do curso atual
                                        document.getId(),
                                        document.getString("name"),
                                        document.getLong("order").intValue(),
                                        document.getString("uri")
                                );
                                loadedVideos.add(video);
                            }
                            taskCompletionSource.setResult(loadedVideos);
                        } else {
                            taskCompletionSource.setException(task.getException());
                        }
                    }
                });

        return taskCompletionSource.getTask();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public interface OnVideosLoadedListener {
        void onVideosLoaded(ArrayList<CourseVideo> videos);
        void onError(String errorMessage);
    }


    public interface OnCourseLoadedListener {
        void onCourseLoaded(Course course);
        void onError(String errorMessage);
    }
}
