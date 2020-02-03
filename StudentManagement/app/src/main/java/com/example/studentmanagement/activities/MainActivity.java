package com.example.studentmanagement.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentmanagement.R;
import com.example.studentmanagement.database.StudentDatabaseHandle;

public class MainActivity extends AppCompatActivity {

    private StudentDatabaseHandle studentDatabaseHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentDatabaseHandle = new StudentDatabaseHandle(this);
    }

    public void onButtonLoginClickListener(View view) {
        String username = ((EditText) findViewById(R.id.edt_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.edt_password)).getText().toString();

        if (studentDatabaseHandle.checkLogin(username, password)) {
            Intent intent = new Intent(MainActivity.this, FacultyActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    public void onButtonExitClickListener(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn thoát ứng dụng không?");
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
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
