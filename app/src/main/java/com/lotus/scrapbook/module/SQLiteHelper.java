package com.lotus.scrapbook.module;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DataBaseName = "mark";
    private static final int DataBaseVersion = 1;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        reset_table(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion!=newVersion){
            reset_table(sqLiteDatabase);
        }
    }

    public static void reset_table(SQLiteDatabase sql){
        String cmd = "DROP TABLE IF EXISTS markinfo";
        sql.execSQL(cmd);
        String create_markinfo="CREATE TABLE IF NOT EXISTS markinfo " +
                "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title VARCHAR(50) not null default '' , " + // 標題
                "inupt VARCHAR(50) not null default '' , " + // 內容
                "created_at VARCHAR(50) not null default '' , " + // 是否有密碼
                "check_pwd VARCHAR(50) not null default '' , " + // 密碼
                "password VARCHAR(50) not null default ''"+//創建時間
                ")";
        sql.execSQL(create_markinfo);
    }

}
