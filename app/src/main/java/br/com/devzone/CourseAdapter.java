package br.com.devzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

public class CourseAdapter extends BaseAdapter {

    private Context mContext;
    private List<Course> mCourseList;

    public CourseAdapter(Context context, List<Course> courseList){
        mContext = context;
        mCourseList = courseList;
    }

    public int getCount() {
        return mCourseList.size();
    }

    public Object getItem(int position) {
        return mCourseList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_courses, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textCourse = convertView.findViewById(R.id.textCourse);
            viewHolder.imageCourse = convertView.findViewById(R.id.imageCourse);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Course course = mCourseList.get(position);
        viewHolder.textCourse.setText(course.getNome());

        // Carrega a imagem usando Glide
        Glide.with(mContext).load(course.getCaminhoImagem()).into(viewHolder.imageCourse);

        return convertView;
    }

    /**
     * Classe ViewHolder para melhorar o desempenho
      */
    private static class ViewHolder {
        TextView textCourse;
        ImageView imageCourse;
    }


}
