package com.example.studentmanagement.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.studentmanagement.adapters.FacultyListViewAdapter;
import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Faculty;

import java.util.ArrayList;

public class FacultyActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private ArrayList<Faculty> faculties;
    private ListView lvFaculty;
    private FacultyListViewAdapter facultyListViewAdapter;

    private final int RESULT_FACULTY_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        //Load khoa lên ListView
        studentDatabaseHandle = new StudentDatabaseHandle(this);

        faculties = new ArrayList<>();
        loadFaculties();

        lvFaculty = findViewById(R.id.lv_faculty);
        facultyListViewAdapter = new FacultyListViewAdapter(faculties);
        lvFaculty.setAdapter(facultyListViewAdapter);

        //Button thêm khoa, mở EditFacultyActivity để tạo khoa mới
        findViewById(R.id.btn_add_faculty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyActivity.this, EditFacultyActivity.class);
                intent.putExtra("isUpdate", false);
                startActivityForResult(intent, RESULT_FACULTY_ACTIVITY);
            }
        });

        //Lắng nghe sự kiện một khoa được chọn, mở EditFacultyActivity để edit khoa
        lvFaculty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Faculty faculty = (Faculty) facultyListViewAdapter.getItem(position);

                Intent intent = new Intent(FacultyActivity.this, EditFacultyActivity.class);
                intent.putExtra("facultyId", faculty.getId());
                intent.putExtra("isUpdate", true);

                startActivityForResult(intent, RESULT_FACULTY_ACTIVITY);
            }
        });

    }

    public void loadFaculties() {
        faculties.clear();
        faculties.addAll(studentDatabaseHandle.getAllFaculties());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //Khi đóng EditFacultyActivity thì nạp lại dữ liệu
            case RESULT_FACULTY_ACTIVITY:
                loadFaculties();
                facultyListViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    public void onSearchClassButtonClick(View view) {
        Intent intent = new Intent(FacultyActivity.this, SearchClassActivity.class);
        startActivity(intent);
    }

    public void onSearchStudentButtonClick(View view) {
        Intent intent = new Intent(FacultyActivity.this, SearchStudentActivity.class);
        startActivity(intent);
    }
}
