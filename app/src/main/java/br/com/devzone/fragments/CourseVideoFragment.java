package br.com.devzone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import br.com.devzone.R;
import br.com.devzone.adapters.CourseVideoAdapter;
import br.com.devzone.classes.CourseVideo;

/**
 * A fragment representing a list of Items.
 */
public class CourseVideoFragment extends Fragment {

    private ArrayList<CourseVideo> courseVideos;

    public CourseVideoFragment() {
    }

    @SuppressWarnings("unused")
    public static CourseVideoFragment newInstance(ArrayList<CourseVideo> courseVideos) {
        CourseVideoFragment fragment = new CourseVideoFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("courseVideos", courseVideos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseVideos = getArguments().getParcelableArrayList("courseVideos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_video, container, false);
        ListView listView = root.findViewById(R.id.listView);

        // Adicione dados ao ListView
        CourseVideoAdapter adapter = new CourseVideoAdapter(getContext(), courseVideos);
        listView.setAdapter(adapter);

        // Set the item click listener
        adapter.setOnItemClickListener(new CourseVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CourseVideo video = courseVideos.get(position);
            }
        });

        return root;
    }
}