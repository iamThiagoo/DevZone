package br.com.devzone.adapters;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import br.com.devzone.classes.CourseVideo;
import br.com.devzone.fragments.CourseVideoFragment;
import br.com.devzone.fragments.EmptyFragment;

public class CourseViewAdapter extends FragmentStateAdapter {

    private ArrayList<CourseVideo> videos;

    public CourseViewAdapter(FragmentActivity fm, ArrayList<CourseVideo> videos) {
        super(fm);
        this.videos = videos;
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // Aulas
            return CourseVideoFragment.newInstance(videos);
        } else {
            // Atividades
            return new EmptyFragment();
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