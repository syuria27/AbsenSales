package com.syuria.android.absensales.helper;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
/**
 * Created by HP on 15/01/2017.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "absen_api";

    // Login table name
    private static final String TABLE_USER = "sales";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_KODE_SALES = "kode_sales";
    private static final String KEY_NAMA_SALES = "nama_sales";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_KODE_TOKO = "kode_toko";
    private static final String KEY_NAMA_TOKO = "nama_toko";
    private static final String KEY_DEPOT = "depot";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_KODE_SALES + " TEXT,"
                + KEY_NAMA_SALES + " TEXT," + KEY_EMAIL + " TEXT,"
                + KEY_KODE_TOKO + " TEXT," + KEY_NAMA_TOKO + " TEXT,"
                + KEY_DEPOT + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String kode_sales, String nama_sales, String email,
                        String kode_toko, String nama_toko, String depot,
                        String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KODE_SALES, kode_sales);
        values.put(KEY_NAMA_SALES, nama_sales); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_KODE_TOKO, kode_toko);
        values.put(KEY_NAMA_TOKO, nama_toko); // Name
        values.put(KEY_DEPOT, depot); // depot
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getSalesDetails() {
        HashMap<String, String> sales = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            sales.put("kode_sales", cursor.getString(1));
            sales.put("nama_sales", cursor.getString(2));
            sales.put("email", cursor.getString(3));
            sales.put("kode_toko", cursor.getString(4));
            sales.put("nama_toko", cursor.getString(5));
            sales.put("depot", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + sales.toString());

        return sales;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteSales() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
