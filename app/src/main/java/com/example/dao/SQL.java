package com.example.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQL extends SQLiteOpenHelper {
    //数据库名称
    private static final String NAME_DB = "music.db";
    //表名称
    private static final String NAME_TABLE = "history";
    //sql语句
    String sql;

    //构建数据库
    public SQL(Context context, int version) {
        super(context, NAME_DB, null, version);
    }

    //初次创建时调用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sql = "CREATE TABLE IF NOT EXISTS " +
                NAME_TABLE+"(name VARCHAR(255))";
        sqLiteDatabase.execSQL(sql);
    }

    //版本号更新时调用
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
