package br.com.devzone.classes;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserCourse {

    private String course_id;
    private String user_id;
    private String currentCourseVideoId;

    public UserCourse() {}

    public UserCourse(String course_id, String user_id, String currentCourseVideoId) {
        this.course_id = course_id;
        this.user_id = user_id;
        this.currentCourseVideoId = currentCourseVideoId;
    }

    public String getCourseId() {
        return course_id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getCurrentCourseVideoId() {
        return currentCourseVideoId;
    }

    public static void createUserCourse(Course course, String userId, OnUserCourseCreatedListener listener) {

        CourseVideo.getCourseVideoByOrder(course, 1, new CourseVideo.OnCourseVideoLoadedListener() {
            @Override
            public void onCourseVideoLoaded(CourseVideo courseVideo) {
                if (courseVideo == null) {
                    listener.onUserCourseCreated(null, new Exception("CourseVideo not found"));
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference userCoursesRef = db.collection("user_courses");

                // Crie um novo objeto UserCourse com os dados fornecidos
                final UserCourse userCourse = new UserCourse(course.getId(), userId, courseVideo.getId());

                userCoursesRef.add(userCourse).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            listener.onUserCourseCreated(userCourse, null);
                        } else {
                            listener.onUserCourseCreated(null, task.getException());
                        }
                    }
                });
            }
        });
    }

    public interface OnUserCourseCreatedListener {
        void onUserCourseCreated(UserCourse userCourse, Exception e);
    }

    public static void getUserCourse(String courseId, String userId, OnUserCourseLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCoursesRef = db.collection("user_courses");

        Query query = userCoursesRef.whereEqualTo("courseId", courseId).whereEqualTo("userId", userId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        UserCourse userCourse = new UserCourse(
                                                        document.getString("courseId"),
                                                        document.getString("userId"),
                                                        document.getString("currentCourseVideoId")
                                                );
                        listener.onUserCourseLoaded(userCourse);
                    } else {
                        listener.onUserCourseLoaded(null);
                    }
                } else {
                    task.getException().printStackTrace();
                    listener.onUserCourseLoaded(null);
                }
            }
        });
    }

    public interface OnUserCourseLoadedListener {
        void onUserCourseLoaded(UserCourse userCourse);
    }
}
