package com.example.studentmanagement.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.Faculty;

import java.util.ArrayList;

public class FacultyListViewAdapter extends BaseAdapter {

    private ArrayList<Faculty> faculties;

    public FacultyListViewAdapter(ArrayList<Faculty> faculties) {
        this.faculties = faculties;
    }

    public ArrayList<Faculty> getFaculties() {
        return faculties;
    }

    public void setFaculties(ArrayList<Faculty> faculties) {
        this.faculties = faculties;
    }

    @Override
    public int getCount() {
        return faculties.size();
    }

    @Override
    public Object getItem(int position) {
        return faculties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View facultyView;

        if (convertView == null) {
            facultyView = View.inflate(parent.getContext(), R.layout.faculty_view, null);
        } else {
            facultyView = convertView;
        }

        TextView tvFacultyId = facultyView.findViewById(R.id.tv_faculty_id);
        TextView tvFacultyName = facultyView.findViewById(R.id.tv_faculty_name);
        TextView tvFacultyAddress = facultyView.findViewById(R.id.tv_faculty_address);
        TextView tvFacultyPhone = facultyView.findViewById(R.id.tv_faculty_phone);

        Faculty faculty = (Faculty) getItem(position);

        tvFacultyId.setText("ID khoa: " + faculty.getId());
        tvFacultyName.setText("Tên khoa: " + faculty.getName());
        tvFacultyAddress.setText("Địa chỉ: " + faculty.getAddress());
        tvFacultyPhone.setText("Điện thoại: " + faculty.getPhone());

        return facultyView;
    }
}
