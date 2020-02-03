package com.example.studentmanagement.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Class;
import com.example.studentmanagement.models.Faculty;
import com.example.studentmanagement.models.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StudentListViewAdapter extends BaseAdapter {

    private ArrayList<Student> students;

    public StudentListViewAdapter() {
        super();
    }

    public StudentListViewAdapter(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return students.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View studentView;

        if (convertView == null) {
            studentView = View.inflate(parent.getContext(), R.layout.student_view, null);
        } else {
            studentView = convertView;
        }

        TextView tvStudentId = studentView.findViewById(R.id.tv_student_id);
        TextView tvStudentName = studentView.findViewById(R.id.tv_student_name);
        TextView tvStudentBirthDate = studentView.findViewById(R.id.tv_student_birth_date);
        TextView tvStudentClassName = studentView.findViewById(R.id.tv_student_class_name);
        TextView tvStudentFacultyName = studentView.findViewById(R.id.tv_student_faculty_name);

        Student student = (Student) getItem(position);
        StudentDatabaseHandle studentDatabaseHandle = new StudentDatabaseHandle(studentView.getContext());
        Class aClass = studentDatabaseHandle.getClassById(student.getClassId());
        Faculty faculty = studentDatabaseHandle.getFacultyById(aClass.getFacultyId());

        tvStudentId.setText("ID sinh viên: " + student.getId());
        tvStudentName.setText("Tên sinh viên: " + student.getName());
        tvStudentBirthDate.setText("Ngày sinh: " + new SimpleDateFormat("MM/dd/yyyy").format(student.getBirthDate()));
        tvStudentClassName.setText("Lớp: " + aClass.getName());
        tvStudentFacultyName.setText("Khoa: " + faculty.getName());

        return studentView;
    }
}
