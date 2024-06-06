package br.com.devzone.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class CourseVideo implements Parcelable {

    private String id;
    private Course course;
    private String name;
    private int order;
    private String uri;

    public CourseVideo(Course course, String id, String name, Integer order, String uri) {
        this.course = course;
        this.id = id;
        this.name = name;
        this.order = order;
        this.uri = uri;
    }

    protected CourseVideo(Parcel in) {
        id = in.readString();
        name = in.readString();
        order = in.readInt();
        uri = in.readString();
    }

    public Course getCourse() { return course; }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public Integer getOrder() {
        return order;
    }

    public String getUri() {
        return uri;
    }

    public String getIframeEmbed() {

        return "<iframe " +
                "width=\"100%\" " +
                "height=\"100%\" " +
                "src=\"https://www.youtube.com/embed/" + this.uri + "\" " +
                "title=\"YouTube video player\" " +
                "frameborder=\"0\" " +
                "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" " +
                "referrerpolicy=\"strict-origin-when-cross-origin\" " +
                "allowfullscreen></iframe>";

    }


    public static void getCourseVideoByOrder(Course course, int order, OnCourseVideoLoadedListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference videosRef = db.collection("course_videos");

        Query query = videosRef.whereEqualTo("order", order).whereEqualTo("course_id", course.getId());

        db.collection("course_videos").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                if (querySnapshot != null && !querySnapshot.isEmpty()) {

                    String videoId = document.getId();
                    String videoName = document.getString("title");
                    Integer videoOrder = document.getLong("order").intValue();
                    String videoUri = document.getString("uri");

                    CourseVideo courseVideo = new CourseVideo(course, videoId, videoName, videoOrder, videoUri);
                    listener.onCourseVideoLoaded(courseVideo);
                } else {
                    Log.d("getCourseVideoByOrder", "No video found for order: " + order);
                    listener.onCourseVideoLoaded(null);
                }
            }
        });
    }

    public interface OnCourseVideoLoadedListener {
        void onCourseVideoLoaded(CourseVideo courseVideo);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeString(uri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CourseVideo> CREATOR = new Creator<CourseVideo>() {
        @Override
        public CourseVideo createFromParcel(Parcel in) {
            return new CourseVideo(in);
        }

        @Override
        public CourseVideo[] newArray(int size) {
            return new CourseVideo[size];
        }
    };
}
