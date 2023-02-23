package com.example.dao;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBManger {

    private Context context;

    private static  DBManger instance;
    // 操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;

    private DBManger(Context context ) {
//        this.historyName = historyName;
        this.context = context;
        SQL dbHelper = new SQL(context, 1);
        writableDatabase = dbHelper.getWritableDatabase();
    }

    //真正的调用对象
    public static DBManger getInstance(Context context) {
        if (instance == null) {
            synchronized (DBManger.class) {
                if (instance == null) {
                    instance = new DBManger(context);
                }
            }
        }
        return instance;
    }

    //添加
    public void add(String historyName) {
        ContentValues values = new ContentValues();
        values.put("name",historyName);
        writableDatabase.insert("history", null, values);
    }

    //删除单个
    public void del(String historyName) {
        writableDatabase.delete("history", "name = ?", new String[]{historyName});
    }
    //全部删除
    public void delAll(){
        writableDatabase.execSQL("delete from history");
    }

    //修改
    public void update() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("列名", "修改后的字符串");
        writableDatabase.update("表面", contentValues, "条件 = ?", new String[]{"字符串,?代表的"});
    }

    //查询
    public List<String> select() {
        //获取游标
        Cursor cursor = writableDatabase.query("history", null, null, null, "name", null, null, null);
        int position = cursor.getPosition();
        Log.e(TAG, "select: 游标默认位置：" + position);
        //返回值容器
        List<String> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(String.valueOf(cursor.getString(0)));
        }

        return result;

    }
}
