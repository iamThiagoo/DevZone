package br.com.devzone.adapters;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import br.com.devzone.classes.Course;
import br.com.devzone.classes.CourseVideo;
import br.com.devzone.fragments.CourseVideoFragment;
import br.com.devzone.fragments.QuestionnaireFragment;

public class CourseViewAdapter extends FragmentStateAdapter {

    private ArrayList<CourseVideo> videos;
    private Course course;

    public CourseViewAdapter(FragmentActivity fm, ArrayList<CourseVideo> videos, Course course) {
        super(fm);
        this.videos = videos;
        this.course = course;
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // Aulas
            return CourseVideoFragment.newInstance(videos, course);
        } else {
            // Atividades
            return new QuestionnaireFragment(course.getId());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void setOnItemClickListener(CourseViewAdapter.OnItemClickListener teste) {}

    public static abstract class OnItemClickListener {

        public abstract void onItemClick(int position);
    }
}