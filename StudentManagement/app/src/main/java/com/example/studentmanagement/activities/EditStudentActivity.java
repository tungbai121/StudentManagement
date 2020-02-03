package com.example.studentmanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;
import com.example.studentmanagement.models.Class;
import com.example.studentmanagement.models.Faculty;
import com.example.studentmanagement.models.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditStudentActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;
    private String classId;
    private Class aClass;
    private Faculty faculty;
    private int studentId;
    private Student student;
    private boolean isUpdate;

    private EditText edtStudentId;
    private EditText edtStudentName;
    private EditText edtStudentBirthDate;
    private RadioGroup rdoGrStudentGender;
    private RadioButton rdoBtnStudentGenderMale;
    private RadioButton rdoBtnStudentGenderFemale;
    private EditText edtStudentHometown;
    private EditText edtStudentClassName;
    private EditText edtStudentFacultyName;


    /*
     * Intent: classId, studentId, isUpdate
     * isUpdate = true: cập nhật thông tin sinh viên
     * isUpdate = false: thêm sinh viên mới
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        studentDatabaseHandle = new StudentDatabaseHandle(this);

        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        aClass = studentDatabaseHandle.getClassById(classId);
        faculty = studentDatabaseHandle.getFacultyById(aClass.getFacultyId());
        isUpdate = intent.getBooleanExtra("isUpdate", false);

        edtStudentId = findViewById(R.id.edt_student_id);

        if (isUpdate) {
            edtStudentId.setEnabled(false);
            studentId = intent.getIntExtra("studentId", 0);
            student = studentDatabaseHandle.getStudentById(studentId);

            //Button xoá sinh viên
            findViewById(R.id.btn_delete_student).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditStudentActivity.this);
                    builder.setMessage("Bạn có muốn xoá sinh viên này không?");
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
                                studentDatabaseHandle.deleteStudentById(studentId);
                                finish();
                            } catch (SQLiteConstraintException e) {
                                Toast.makeText(EditStudentActivity.this, "Không thể xoá sinh viên này", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            student = new Student(0, "", new Date(), "Nam", "", classId);

            findViewById(R.id.btn_delete_student).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_save_student)).setText("Tạo sinh viên mới");
        }

        //Cập nhật View
        edtStudentName = findViewById(R.id.edt_student_name);
        edtStudentBirthDate = findViewById(R.id.edt_student_birth_date);
        rdoBtnStudentGenderMale = findViewById(R.id.rdoBtn_student_gender_male);
        rdoBtnStudentGenderFemale = findViewById(R.id.rdoBtn_student_gender_female);
        edtStudentHometown = findViewById(R.id.edt_student_hometown);
        edtStudentClassName = findViewById(R.id.edt_student_class_name);
        edtStudentFacultyName = findViewById(R.id.edt_student_faculty_name);

        edtStudentId.setText(String.valueOf(student.getId()));
        edtStudentName.setText(student.getName());
        edtStudentBirthDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(student.getBirthDate()));
        switch (student.getGender()) {
            case "Nam": rdoBtnStudentGenderMale.setChecked(true);
                break;
            case "Nữ": rdoBtnStudentGenderFemale.setChecked(true);
                break;
            default:
                break;
        }
        edtStudentHometown.setText(student.getHometown());
        edtStudentClassName.setText(aClass.getName());
        edtStudentFacultyName.setText(faculty.getName());

        rdoGrStudentGender = findViewById(R.id.rdoGr_student_gender);
        rdoGrStudentGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdoBtn_student_gender_male:
                        student.setGender(rdoBtnStudentGenderMale.getText().toString());
                        break;
                    case R.id.rdoBtn_student_gender_female:
                        student.setGender(rdoBtnStudentGenderFemale.getText().toString());
                        break;
                    default:
                        break;
                }
            }
        });

        /*
         * Button lưu
         * isUpdate = true: cập nhật thông tin sinh viên
         * isUpdate = false: thêm sinh viên mới
         */
        findViewById(R.id.btn_save_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.setName(edtStudentName.getText().toString());
                try {
                    student.setBirthDate(new SimpleDateFormat("MM/dd/yyyy").parse(edtStudentBirthDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rdoGrStudentGender = findViewById(R.id.rdoGr_student_gender);
                student.setHometown(edtStudentHometown.getText().toString());
                student.setClassId(classId);

                if (isUpdate) {
                    studentDatabaseHandle.updateStudent(student);
                    finish();
                } else {
                    if (!edtStudentId.getText().toString().equals("")) {
                        student.setId(Integer.parseInt(edtStudentId.getText().toString()));
                        try {
                            studentDatabaseHandle.insertStudent(student);
                            finish();
                        } catch (SQLiteConstraintException e) {
                            Toast.makeText(EditStudentActivity.this, "ID sinh viên đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditStudentActivity.this, "ID sinh viên không được để trống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onEditTextStudentBirthDateClick(View view) {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(edtStudentBirthDate.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatePickerDialog studentBirthDatePicker = new DatePickerDialog(this);
        studentBirthDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        studentBirthDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar birthDate = Calendar.getInstance();
                birthDate.set(Calendar.YEAR, year);
                birthDate.set(Calendar.MONTH, month);
                birthDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                edtStudentBirthDate.setText(sdf.format(birthDate.getTime()));
            }
        });

        studentBirthDatePicker.show();
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

