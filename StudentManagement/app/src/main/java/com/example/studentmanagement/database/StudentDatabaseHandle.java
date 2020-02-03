package com.example.studentmanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.studentmanagement.models.Class;
import com.example.studentmanagement.models.Faculty;
import com.example.studentmanagement.models.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentDatabaseHandle extends SQLiteOpenHelper {

    /*
     * Các thành phần của cơ sở dữ liệu quản lý sinh viên
     */
    private static final String DATABASE_NAME = "studentmanagement.db";
    private static final int DATABASE_VERSION = 1;

    //Bảng
    private static final String USER = "User";
    private static final String FACULTY = "Faculty";
    private static final String CLASS = "Class";
    private static final String STUDENT = "Student";

    //Cột
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";

    private static final String FACULTY_ID = "FacultyId";
    private static final String FACULTY_NAME = "FacultyName";
    private static final String FACULTY_ADDRESS = "FacultyAddress";
    private static final String FACULTY_PHONE = "FacultyPhone";

    private static final String CLASS_ID = "ClassId";
    private static final String CLASS_NAME = "ClassName";
    private static final String CLASS_FACULTY_ID = "FacultyId";

    private static final String STUDENT_ID = "StudentId";
    private static final String STUDENT_NAME = "StudentName";
    private static final String STUDENT_BIRTH_DATE = "StudentBirthDate";
    private static final String STUDENT_GENDER = "StudentGender";
    private static final String STUDENT_HOMETOWN = "StudentHometown";
    private static final String STUDENT_CLASS_ID = "ClassId";

    //Truy vấn tạo bảng
    private static final String QUERY_CREATE_TABLE_USER = "create table " + USER + "("
            + USERNAME + " text primary key, "
            + PASSWORD + " text not null);";

    private static final String QUERY_CREATE_TABLE_FACULTY = "create table " + FACULTY + "("
            + FACULTY_ID + " text primary key, "
            + FACULTY_NAME + " text, "
            + FACULTY_ADDRESS + " text, "
            + FACULTY_PHONE + " text);";

    private static final String QUERY_CREATE_TABLE_CLASS = "create table " + CLASS + "("
            + CLASS_ID + " text primary key, "
            + CLASS_NAME + " text, "
            + CLASS_FACULTY_ID + " text not null, "
            + "foreign key (" + CLASS_FACULTY_ID + ") references " + FACULTY + "(" + FACULTY_ID + "));";

    private static final String QUERY_CREATE_TABLE_STUDENT = "create table " + STUDENT + "("
            + STUDENT_ID + " integer primary key, "
            + STUDENT_NAME + " text, "
            + STUDENT_BIRTH_DATE + " date, "
            + STUDENT_GENDER + " text, "
            + STUDENT_HOMETOWN + " text, "
            + STUDENT_CLASS_ID + " text not null, "
            + "foreign key (" + STUDENT_CLASS_ID + ") references " + CLASS + "(" + CLASS_ID + "));";

    public StudentDatabaseHandle(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE_USER);
        db.execSQL(QUERY_CREATE_TABLE_FACULTY);
        db.execSQL(QUERY_CREATE_TABLE_CLASS);
        db.execSQL(QUERY_CREATE_TABLE_STUDENT);

        //Tạo tài khoản Admin
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", "admin");
        contentValues.put("password", "admin");

        db.insert(USER, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + USER);
        db.execSQL("drop table if exists " + FACULTY);
        db.execSQL("drop table if exists " + CLASS);
        db.execSQL("drop table if exists " + STUDENT);

        onCreate(db);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(USER, new String[] {USERNAME, PASSWORD},
                USERNAME + "=? and " + PASSWORD + "=?",
                new String[] {username, password}, null, null, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        cursor.close();

        return false;
    }

    /*
     * Quản lý khoa
     */
    public List<Faculty> getAllFaculties() {
        List<Faculty> faculties = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FACULTY, new String[] {FACULTY_ID, FACULTY_NAME, FACULTY_ADDRESS, FACULTY_PHONE},
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            String phone = cursor.getString(3);

            faculties.add(new Faculty(id, name, address, phone));

            cursor.moveToNext();
        }

        cursor.close();

        return faculties;
    }

    public Faculty getFacultyById(String facultyId) {
        Faculty faculty = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FACULTY, new String[] {FACULTY_NAME, FACULTY_ADDRESS, FACULTY_PHONE},
                FACULTY_ID + "=?", new String[] {facultyId}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(0);
            String address = cursor.getString(1);
            String phone = cursor.getString(2);

            faculty = new Faculty(facultyId, name, address, phone);
        }

        cursor.close();

        return faculty;
    }

    public void insertFaculty(Faculty faculty) {
        SQLiteDatabase db = getWritableDatabase();
        String queryInsertFaculty = "insert into " + FACULTY + "("
                + FACULTY_ID + ", " + FACULTY_NAME + ", " + FACULTY_ADDRESS + ", " + FACULTY_PHONE + ") values(?, ?, ?, ?)";
        db.execSQL(queryInsertFaculty, new String[] {faculty.getId(), faculty.getName(), faculty.getAddress(), faculty.getPhone()});
    }

    public void updateFaculty(Faculty faculty) {
        SQLiteDatabase db = getWritableDatabase();
        String queryUpdateFaculty = "update " + FACULTY
                + " set " + FACULTY_NAME + "=?, " + FACULTY_ADDRESS + "=?, " + FACULTY_PHONE + "=?"
                + " where " + FACULTY_ID + "=?";
        db.execSQL(queryUpdateFaculty, new String[] {faculty.getName(), faculty.getAddress(), faculty.getPhone(), faculty.getId()});
    }

    public void deleteFacultyById(String facultyId) {
        SQLiteDatabase db = getWritableDatabase();
        String queryDeleteFaculty = "delete from " + FACULTY
                + " where " + FACULTY_ID + "=?";
        db.execSQL(queryDeleteFaculty, new String[] {facultyId});
    }

    /*
     * Quản lý lớp
     */
    public List<Class> getAllClasses() {
        List<Class> classes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CLASS, new String[] {CLASS_ID, CLASS_NAME, CLASS_FACULTY_ID},
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String facultyId = cursor.getString(2);

            classes.add(new Class(id, name, facultyId));

            cursor.moveToNext();
        }

        cursor.close();

        return classes;
    }

    public List<Class> getClassesByFacultyId(String facultyId) {
        List<Class> classes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CLASS, new String[] {CLASS_ID, CLASS_NAME},
                CLASS_FACULTY_ID + "=?", new String[] {facultyId}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);

            classes.add(new Class(id, name, facultyId));

            cursor.moveToNext();
        }

        cursor.close();

        return classes;
    }

    public Class getClassById(String classId) {
        Class aClass = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CLASS, new String[] {CLASS_NAME, CLASS_FACULTY_ID},
                CLASS_ID + "=?", new String[] {classId}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(0);
            String facultyId = cursor.getString(1);

            aClass = new Class(classId, name, facultyId);
        }

        cursor.close();

        return aClass;
    }

    public List<Class> getClassesByName(String className) {
        List<Class> classes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CLASS, new String[] {CLASS_ID, CLASS_NAME, CLASS_FACULTY_ID},
                CLASS_NAME + " like '%" + className + "%'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String facultyId = cursor.getString(2);

            classes.add(new Class(id, name, facultyId));

            cursor.moveToNext();
        }

        cursor.close();

        return classes;
    }

    public void insertClass(Class aClass) {
        SQLiteDatabase db = getWritableDatabase();
        String queryInsertClass = "insert into " + CLASS + "("
                + CLASS_ID + ", " + CLASS_NAME + ", " + CLASS_FACULTY_ID + ") values(?, ?, ?)";
        db.execSQL(queryInsertClass, new String[] {aClass.getId(), aClass.getName(), aClass.getFacultyId()});
    }

    public void updateClass(Class aClass) {
        SQLiteDatabase db = getWritableDatabase();
        String queryUpdateClass = "update " + CLASS
                + " set " + CLASS_NAME + "=?, " + CLASS_FACULTY_ID + "=?"
                + " where " + CLASS_ID + "=?";
        db.execSQL(queryUpdateClass, new String[] {aClass.getName(), aClass.getFacultyId(), aClass.getId()});
    }

    public void deleteClassById(String classId) {
        SQLiteDatabase db = getWritableDatabase();
        String queryDeleteClass = "delete from " + CLASS
                + " where " + CLASS_ID + "=?";
        db.execSQL(queryDeleteClass, new String[] {classId});
    }

    /*
     * Quản lý sinh viên
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(STUDENT, new String[] {STUDENT_ID, STUDENT_NAME, STUDENT_BIRTH_DATE, STUDENT_GENDER, STUDENT_HOMETOWN, STUDENT_CLASS_ID},
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("MM/dd/yyyy").parse(cursor.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String gender = cursor.getString(3);
            String hometown = cursor.getString(4);
            String classId = cursor.getString(5);

            students.add(new Student(id, name, birthDate, gender, hometown, classId));

            cursor.moveToNext();
        }

        cursor.close();

        return students;
    }

    public List<Student> getStudentsByClassId(String classId) {
        List<Student> students = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(STUDENT, new String[] {STUDENT_ID, STUDENT_NAME, STUDENT_BIRTH_DATE, STUDENT_GENDER, STUDENT_HOMETOWN, STUDENT_CLASS_ID},
                STUDENT_CLASS_ID + "=?", new String[] {classId}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("MM/dd/yyyy").parse(cursor.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String gender = cursor.getString(3);
            String hometown = cursor.getString(4);

            students.add(new Student(id, name, birthDate, gender, hometown, classId));

            cursor.moveToNext();
        }

        cursor.close();

        return students;
    }

    public Student getStudentById(int studentId) {
        Student student = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(STUDENT, new String[] {STUDENT_NAME, STUDENT_BIRTH_DATE, STUDENT_GENDER, STUDENT_HOMETOWN, STUDENT_CLASS_ID},
                STUDENT_ID + "=?", new String[] {String.valueOf(studentId)}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(0);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("MM/dd/yyyy").parse(cursor.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String gender = cursor.getString(2);
            String hometown = cursor.getString(3);
            String classId = cursor.getString(4);

            student = new Student(studentId, name, birthDate, gender, hometown, classId);
        }

        cursor.close();

        return student;
    }

    public List<Student> getStudentsById(int studentId) {
        List<Student> students = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(STUDENT, new String[] {STUDENT_ID, STUDENT_NAME, STUDENT_BIRTH_DATE, STUDENT_GENDER, STUDENT_HOMETOWN, STUDENT_CLASS_ID},
                 STUDENT_ID + " like '%" + studentId + "%'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("MM/dd/yyyy").parse(cursor.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String gender = cursor.getString(3);
            String hometown = cursor.getString(4);
            String classId = cursor.getString(5);

            students.add(new Student(id, name, birthDate, gender, hometown, classId));

            cursor.moveToNext();
        }

        return students;
    }

    public void insertStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        String queryInsertStudent = "insert into " + STUDENT + "("
                + STUDENT_ID + ", " + STUDENT_NAME + ", " + STUDENT_BIRTH_DATE + ", " + STUDENT_GENDER + ", " + STUDENT_HOMETOWN + ", " + STUDENT_CLASS_ID + ") values(?, ?, ?, ?, ?, ?)";
        db.execSQL(queryInsertStudent, new String[] {String.valueOf(student.getId()), student.getName(), new SimpleDateFormat("MM/dd/yyyy").format(student.getBirthDate()), student.getGender(), student.getHometown(), student.getClassId()});
    }

    public void updateStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        String queryUpdateStudent = "update " + STUDENT
                + " set " + STUDENT_NAME + "=?, " + STUDENT_BIRTH_DATE + "=?, " + STUDENT_GENDER + "=?, " + STUDENT_HOMETOWN + "=?, " + STUDENT_CLASS_ID + "=?"
                + " where " + STUDENT_ID + "=?";
        db.execSQL(queryUpdateStudent, new String[] {student.getName(), new SimpleDateFormat("MM/dd/yyyy").format(student.getBirthDate()), student.getGender(), student.getHometown(), student.getClassId(), String.valueOf(student.getId())});
    }

    public void deleteStudentById(int studentId) {
        SQLiteDatabase db = getWritableDatabase();
        String queryDeleteStudent = "delete from " + STUDENT
                + " where " + STUDENT_ID + "=?";
        db.execSQL(queryDeleteStudent, new String[] {String.valueOf(studentId)});
    }
}
