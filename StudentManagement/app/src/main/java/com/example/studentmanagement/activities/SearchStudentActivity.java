package com.example.studentmanagement.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.adapters.StudentListViewAdapter;
import com.example.studentmanagement.models.Student;

import java.util.ArrayList;

public class SearchStudentActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private ArrayList<Student> students;
    private ListView lvStudentSearch;
    private StudentListViewAdapter studentListViewAdapter;

    private EditText edtSearchStudent;

    private final int RESULT_STUDENT_SEARCH_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load sinh viên lên ListView
        studentDatabaseHandle = new StudentDatabaseHandle(this);

        students = new ArrayList<>();
        loadStudents();

        lvStudentSearch = findViewById(R.id.lv_student_search);
        studentListViewAdapter = new StudentListViewAdapter(students);
        lvStudentSearch.setAdapter(studentListViewAdapter);
        edtSearchStudent = findViewById(R.id.edt_search_student);

        //Lắng nghe sự kiện text tìm kiếm lớp thay đổi
        edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearchStudent.getText().toString().equals("")) {
                    loadStudents();
                    studentListViewAdapter.notifyDataSetChanged();
                } else {
                    loadStudents(Integer.parseInt(edtSearchStudent.getText().toString()));
                    studentListViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Lắng nghe sự kiện một sinh viên được chọn, mở EditStudentActivity để edit sinh viên
        lvStudentSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) studentListViewAdapter.getItem(position);

                Intent intent = new Intent(SearchStudentActivity.this, EditStudentActivity.class);
                intent.putExtra("isUpdate", true);
                intent.putExtra("studentId", student.getId());
                intent.putExtra("classId", student.getClassId());

                startActivityForResult(intent, RESULT_STUDENT_SEARCH_ACTIVITY);
            }
        });
    }

    public void loadStudents() {
        students.clear();
        students.addAll(studentDatabaseHandle.getAllStudents());
    }

    private void loadStudents(int studentId) {
        students.clear();
        students.addAll(studentDatabaseHandle.getStudentsById(studentId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //Khi đóng EditStudentActivity thì nạp lại dữ liệu
            case RESULT_STUDENT_SEARCH_ACTIVITY:
                if (edtSearchStudent.getText().toString().equals("")) {
                    loadStudents();
                    studentListViewAdapter.notifyDataSetChanged();
                } else {
                    loadStudents(Integer.parseInt(edtSearchStudent.getText().toString()));
                    studentListViewAdapter.notifyDataSetChanged();
                }
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
