package com.example.studentmanagement.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Class;
import com.example.studentmanagement.models.Faculty;

import java.util.ArrayList;

public class ClassListViewAdapter extends BaseAdapter {

    private ArrayList<Class> classes;

    public ClassListViewAdapter(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Override
    public Object getItem(int position) {
        return classes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View classView;

        if (convertView == null) {
            classView = View.inflate(parent.getContext(), R.layout.class_view, null);
        } else {
            classView = convertView;
        }

        TextView tvClassId = classView.findViewById(R.id.tv_class_id);
        TextView tvClassName = classView.findViewById(R.id.tv_class_name);
        TextView tvClassFacultyName = classView.findViewById(R.id.tv_class_faculty_name);

        Class aClass = (Class) getItem(position);
        StudentDatabaseHandle studentDatabaseHandle = new StudentDatabaseHandle(classView.getContext());
        Faculty faculty = studentDatabaseHandle.getFacultyById(aClass.getFacultyId());

        tvClassId.setText("ID lớp: " + aClass.getId());
        tvClassName.setText("Tên lớp: " + aClass.getName());
        tvClassFacultyName.setText("Khoa: " + faculty.getName());

        return classView;
    }
}
