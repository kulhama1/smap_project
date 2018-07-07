package com.example.martin.smapy.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {


    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public Cursor queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
        return null;
    }
    public void insertData2(int id_z_webu, String name, String description, double gps_x, double gps_y, String adresa_obrazku){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO TABULKA2 VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1, id_z_webu);
        statement.bindString(2, name);
        statement.bindString(3, description);
        statement.bindDouble(4, gps_x);
        statement.bindDouble(5, gps_y);
        statement.bindString(6, adresa_obrazku);

        statement.executeInsert();
    }
    public void insertData3(int id_z_webu, String name, String description, double gps_x, double gps_y, String adresa_obrazku){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO TABULKA3 VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1, id_z_webu);
        statement.bindString(2, name);
        statement.bindString(3, description);
        statement.bindDouble(4, gps_x);
        statement.bindDouble(5, gps_y);
        statement.bindString(6, adresa_obrazku);

        statement.executeInsert();
    }

    public void insertData(int id_z_webu, String name, String description, double gps_x, double gps_y, String adresa_obrazku){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO TABULKA1 VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1, id_z_webu);
        statement.bindString(2, name);
        statement.bindString(3, description);
        statement.bindDouble(4, gps_x);
        statement.bindDouble(5, gps_y);
        statement.bindString(6, adresa_obrazku);

        statement.executeInsert();
    }
    public void insertData1(String name, String description, double gps_x, double gps_y, int evaluation, String link, String city, String weather, double temperature, double kilometers){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO TABULKANAVSTEVA VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, description);
        statement.bindDouble(3, gps_x);
        statement.bindDouble(4, gps_y);
        statement.bindDouble(5, evaluation);
        statement.bindString(6, link);
        statement.bindString(7, city);
        statement.bindString(8, weather);
        statement.bindDouble(9, temperature);
        statement.bindDouble(10, kilometers);

        statement.executeInsert();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
