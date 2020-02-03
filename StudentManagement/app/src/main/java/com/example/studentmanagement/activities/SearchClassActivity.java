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

import com.example.studentmanagement.adapters.ClassListViewAdapter;
import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Class;

import java.util.ArrayList;

public class SearchClassActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private ArrayList<Class> classes;
    private ListView lvClassSearch;
    private ClassListViewAdapter classListViewAdapter;

    private EditText edtSearchClass;

    private final int RESULT_CLASS_SEARCH_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Load lớp lên ListView
        studentDatabaseHandle = new StudentDatabaseHandle(this);

        classes = new ArrayList<>();
        loadClasses();

        lvClassSearch = findViewById(R.id.lv_class_search);
        classListViewAdapter = new ClassListViewAdapter(classes);
        lvClassSearch.setAdapter(classListViewAdapter);
        edtSearchClass = findViewById(R.id.edt_search_class);

        //Lắng nghe sự kiện text tìm kiếm lớp thay đổi
        edtSearchClass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadClasses(edtSearchClass.getText().toString());
                classListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Lắng nghe sự kiện một lớp được chọn, mở EditClassActivity để edit lớp
        lvClassSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class aClass = (Class) classListViewAdapter.getItem(position);

                Intent intent = new Intent(SearchClassActivity.this, EditClassActivity.class);
                intent.putExtra("facultyId", aClass.getFacultyId());
                intent.putExtra("classId", aClass.getId());
                intent.putExtra("isUpdate", true);

                startActivityForResult(intent, RESULT_CLASS_SEARCH_ACTIVITY);
            }
        });
    }

    public void loadClasses() {
        classes.clear();
        classes.addAll(studentDatabaseHandle.getAllClasses());
    }

    public void loadClasses(String className) {
        classes.clear();
        classes.addAll(studentDatabaseHandle.getClassesByName(className));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //Khi đóng EditClassActivity thì nạp lại dữ liệu
            case RESULT_CLASS_SEARCH_ACTIVITY:
                loadClasses(edtSearchClass.getText().toString());
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
