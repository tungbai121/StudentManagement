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
import com.example.studentmanagement.models.Faculty;

public class EditFacultyActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private String facultyId;
    private Faculty faculty;
    private boolean isUpdate;

    private EditText edtFacultyId;
    private EditText edtFacultyName;
    private EditText edtFacultyAddress;
    private EditText edtFacultyPhone;

    /*
     * Intent: facultyId, isUpdate
     * isUpdate = true: cập nhật thông tin khoa
     * isUpdate = false: thêm khoa mới
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_faculty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        studentDatabaseHandle = new StudentDatabaseHandle(this);

        Intent intent = getIntent();
        isUpdate = intent.getBooleanExtra("isUpdate", false);

        edtFacultyId = findViewById(R.id.edt_faculty_id);

        if (isUpdate) {
            edtFacultyId.setEnabled(false);
            facultyId = intent.getStringExtra("facultyId");
            faculty = studentDatabaseHandle.getFacultyById(facultyId);

            //Button xoá khoa
            findViewById(R.id.btn_delete_faculty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFacultyActivity.this);
                    builder.setMessage("Bạn có muốn xoá khoa này không?");
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
                                studentDatabaseHandle.deleteFacultyById(facultyId);
                                finish();
                            } catch (SQLiteConstraintException e) {
                                Toast.makeText(EditFacultyActivity.this, "Không thể xoá khoa này", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            faculty = new Faculty("", "", "", "");

            findViewById(R.id.btn_delete_faculty).setVisibility(View.GONE);
            findViewById(R.id.btn_manage_class).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_save_faculty)).setText("Tạo khoa mới");
        }

        //Cập nhật View
        edtFacultyName = findViewById(R.id.edt_faculty_name);
        edtFacultyAddress = findViewById(R.id.edt_faculty_address);
        edtFacultyPhone = findViewById(R.id.edt_faculty_phone);

        edtFacultyId.setText(faculty.getId());
        edtFacultyName.setText(faculty.getName());
        edtFacultyAddress.setText(faculty.getAddress());
        edtFacultyPhone.setText(faculty.getPhone());

        /*
         * Button lưu
         * isUpdate = true: cập nhật thông tin khoa
         * isUpdate = false: thêm khoa mới
         */
        findViewById(R.id.btn_save_faculty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faculty.setName(edtFacultyName.getText().toString());
                faculty.setAddress(edtFacultyAddress.getText().toString());
                faculty.setPhone(edtFacultyPhone.getText().toString());

                if (isUpdate) {
                    studentDatabaseHandle.updateFaculty(faculty);
                    finish();
                } else {
                    if (!edtFacultyId.getText().toString().equals("")) {
                        faculty.setId(edtFacultyId.getText().toString());
                        try {
                            studentDatabaseHandle.insertFaculty(faculty);
                            finish();
                        } catch (SQLiteConstraintException e) {
                            Toast.makeText(EditFacultyActivity.this, "ID khoa đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditFacultyActivity.this, "ID khoa không được để trống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Button quản lý lớp, mở ClassActivity để quản lý lớp của khoa
        findViewById(R.id.btn_manage_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditFacultyActivity.this, ClassActivity.class);
                intent.putExtra("facultyId", facultyId);
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
