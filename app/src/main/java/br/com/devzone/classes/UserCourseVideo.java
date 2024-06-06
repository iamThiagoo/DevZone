package br.com.devzone.classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import br.com.devzone.adapters.CourseVideoAdapter;

public class UserCourseVideo {

    public static void checkIfUserWatchedVideo(String userId, String courseId, String videoId, final CourseVideoAdapter.userWatchedVideoCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user_course_videos")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("course_id", courseId)
            .whereEqualTo("course_video_id", videoId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            callback.onCallback(true);
                        } else {
                            callback.onCallback(false);
                        }
                    } else {
                        Log.d("getVideoError", "Error getting documents: ", task.getException());
                        callback.onCallback(false);
                    }
                }
            });
    }

}
