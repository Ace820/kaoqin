package com.zhangche.kaoqin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

/* 表结构：：
=================================================================================
table main:
所有书籍的基础信息列表，使用文件路径的hash作为主键
-----------------------------------------------------------------------------
date           |workOnTime     |workOffTime
日期            |上班时间        |下班时间
-----------------------------------------------------------------------------
=================================================================================
 */
public class DataBaseFunc extends SQLiteOpenHelper {
    private final static String DATABASENAME = "kaoqin";
    private final static int DATABASEVERSION = 1;
    SQLiteDatabase dataBase;
    Context context;

    public DataBaseFunc(Context contexts) {
        super(contexts,DATABASENAME,null,DATABASEVERSION);
        context = contexts;
        dataBase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS main "
                + "(date VARCHAR(8) PRIMARY KEY,"
                + " workOnTime VARCHAR(4),"
                + " workOffTime VARCHAR(4))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String[] getData(String date) {
        String[] result = {"",""};
        String sql = "select * from main where date is '" + date + "'";
        Cursor cursor = dataBase.rawQuery(sql,null);
        cursor.moveToFirst();
        result[0] = cursor.getString(1);
        result[1] = cursor.getString(2);
        cursor.close();
        return result;
    }
    public void insert(String date,String workOnTime, String workOffTime) {
        String sql;
        if (isDateSeted(date))
            sql = "update main set workOnTime='" +workOnTime+ "',workOffTime='" + workOffTime + "' where date is '" + date +"'";
        else
            sql = "insert into main values ('" + date + "','" + workOnTime + "','" + workOffTime + "')";
        Log.d(TAG,sql);
        dataBase.execSQL(sql);
    }

    public boolean isDateSeted(String date) {
        String sql = "select * from main where date is '" + date + "'";
        Cursor cursor = dataBase.rawQuery(sql,null);
        boolean exist = (cursor.getCount() != 0);
        cursor.close();
        return exist;
    }

    public void close() {
        dataBase.close();
    }
}
