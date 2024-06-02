package br.com.devzone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.devzone.R;
import br.com.devzone.classes.CourseVideo;

public class CourseVideoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CourseVideo> items;

    public CourseVideoAdapter(Context context, ArrayList<CourseVideo> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_course_video, parent, false);
        }

        CourseVideo item = items.get(position);

        TextView order = convertView.findViewById(R.id.course_video_position);
        order.setText(position + ".");

        TextView title = convertView.findViewById(R.id.course_video_name);
        title.setText(item.getName());

        return convertView;
    }
}