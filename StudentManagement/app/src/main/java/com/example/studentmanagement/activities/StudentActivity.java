package com.example.studentmanagement.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.adapters.StudentListViewAdapter;
import com.example.studentmanagement.models.Student;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private String classId;
    private ArrayList<Student> students;
    private ListView lvStudent;
    StudentListViewAdapter studentListViewAdapter;

    private final int RESULT_STUDENT_ACTIVITY = 1;

    //Intent: classId
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load sinh viên lên ListView
        studentDatabaseHandle = new StudentDatabaseHandle(this);

        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");

        Log.i("INTENT", "classId = " + classId);

        students = new ArrayList<>();
        loadStudents(classId);

        lvStudent = findViewById(R.id.lv_student);
        studentListViewAdapter = new StudentListViewAdapter(students);
        lvStudent.setAdapter(studentListViewAdapter);

        //Button thêm sinh viên, mở EditStudentActivity để tạo sinh viên mới
        findViewById(R.id.btn_add_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, EditStudentActivity.class);
                intent.putExtra("isUpdate", false);
                intent.putExtra("classId", classId);
                startActivityForResult(intent, RESULT_STUDENT_ACTIVITY);
            }
        });

        //Lắng nghe sự kiện một sinh viên được chọn, mở EditStudentActivity để edit sinh viên
        lvStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) studentListViewAdapter.getItem(position);

                Intent intent = new Intent(StudentActivity.this, EditStudentActivity.class);
                intent.putExtra("isUpdate", true);
                intent.putExtra("studentId", student.getId());
                intent.putExtra("classId", classId);

                startActivityForResult(intent, RESULT_STUDENT_ACTIVITY);
            }
        });
    }

    public void loadStudents(String classId) {
        students.clear();
        students.addAll(studentDatabaseHandle.getStudentsByClassId(classId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //Khi đóng EditStudentActivity thì nạp lại dữ liệu
            case RESULT_STUDENT_ACTIVITY:
                loadStudents(classId);
                studentListViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
