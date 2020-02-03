package com.example.studentmanagement.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.studentmanagement.adapters.ClassListViewAdapter;
import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Class;

import java.util.ArrayList;

public class ClassActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private String facultyId;
    private ArrayList<Class> classes;
    private ListView lvClass;
    private ClassListViewAdapter classListViewAdapter;

    private final int RESULT_CLASS_ACTIVITY = 1;

    //Intent: facultyId
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load lớp lên ListView
        studentDatabaseHandle = new StudentDatabaseHandle(this);

        Intent intent = getIntent();
        facultyId = intent.getStringExtra("facultyId");

        classes = new ArrayList<>();
        loadClasses(facultyId);

        lvClass = findViewById(R.id.lv_class);
        classListViewAdapter = new ClassListViewAdapter(classes);
        lvClass.setAdapter(classListViewAdapter);

        //Button thêm lớp, mở EditClassActivity để tạo lớp mới
        findViewById(R.id.btn_add_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassActivity.this, EditClassActivity.class);
                intent.putExtra("facultyId", facultyId);
                intent.putExtra("isUpdate", false);
                startActivityForResult(intent, RESULT_CLASS_ACTIVITY);
            }
        });

        //Lắng nghe sự kiện một lớp được chọn, mở EditClassActivity để edit lớp
        lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class aClass = (Class) classListViewAdapter.getItem(position);

                Intent intent = new Intent(ClassActivity.this, EditClassActivity.class);
                intent.putExtra("facultyId", facultyId);
                intent.putExtra("classId", aClass.getId());
                intent.putExtra("isUpdate", true);

                startActivityForResult(intent, RESULT_CLASS_ACTIVITY);
            }
        });

    }

    private void loadClasses(String facultyId) {
        classes.clear();
        classes.addAll(studentDatabaseHandle.getClassesByFacultyId(facultyId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //Khi đóng EditClassActivity thì nạp lại dữ liệu
            case RESULT_CLASS_ACTIVITY:
                loadClasses(facultyId);
                classListViewAdapter.notifyDataSetChanged();
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
