package com.example.studentmanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Class;
import com.example.studentmanagement.models.Faculty;

public class EditClassActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private String facultyId;
    private Faculty faculty;
    private String classId;
    private Class aClass;
    private boolean isUpdate;

    private EditText edtClassId;
    private EditText edtClassName;
    private EditText edtClassFaculty;

    /*
     * Intent: facultyId, classId, isUpdate
     * isUpdate = true: cập nhật thông tin lớp
     * isUpdate = false: thêm lớp mới
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        studentDatabaseHandle = new StudentDatabaseHandle(this);

        Intent intent = getIntent();
        facultyId = intent.getStringExtra("facultyId");
        faculty = studentDatabaseHandle.getFacultyById(facultyId);
        isUpdate = intent.getBooleanExtra("isUpdate", false);

        edtClassId = findViewById(R.id.edt_class_id);

        if (isUpdate) {
            edtClassId.setEnabled(false);
            classId = intent.getStringExtra("classId");
            aClass = studentDatabaseHandle.getClassById(classId);

            //Button xoá lớp
            findViewById(R.id.btn_delete_class).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditClassActivity.this);
                    builder.setMessage("Bạn có muốn xoá lớp này không?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                studentDatabaseHandle.deleteClassById(classId);
                                finish();
                            } catch (SQLiteConstraintException e) {
                                Toast.makeText(EditClassActivity.this, "Không thể xoá lớp này", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

        } else {
            aClass = new Class("", "", facultyId);

            findViewById(R.id.btn_delete_class).setVisibility(View.GONE);
            findViewById(R.id.btn_manage_student).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_save_class)).setText("Tạo lớp mới");
        }

        //Cập nhật View
        edtClassName = findViewById(R.id.edt_class_name);
        edtClassFaculty = findViewById(R.id.edt_class_faculty);

        edtClassId.setText(aClass.getId());
        edtClassName.setText(aClass.getName());
        edtClassFaculty.setText(faculty.getName());

        /*
         * Button lưu
         * isUpdate = true: cập nhật thông tin lớp
         * isUpdate = false: thêm lớp mới
         */
        findViewById(R.id.btn_save_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aClass.setName(edtClassName.getText().toString());
                aClass.setFacultyId(facultyId);

                if (isUpdate) {
                    studentDatabaseHandle.updateClass(aClass);
                    finish();
                } else {
                    if (!edtClassId.getText().toString().equals("")) {
                        aClass.setId(edtClassId.getText().toString());
                        try {
                            studentDatabaseHandle.insertClass(aClass);
                            finish();
                        } catch (SQLiteConstraintException e) {
                            Toast.makeText(EditClassActivity.this, "ID lớp đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditClassActivity.this, "ID lớp không được để trống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Button quản lý sinh viên, mở StudentActivity để quản lý sinh viên của lớp
        findViewById(R.id.btn_manage_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditClassActivity.this, StudentActivity.class);
                intent.putExtra("classId", classId);
                startActivity(intent);
            }
        });
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
