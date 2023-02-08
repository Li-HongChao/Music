package com.example.unitl;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.entity.Music;

import java.util.ArrayList;
import java.util.List;

public class AudioUtils {

    @NonNull
    @SuppressLint("Range")
    public static List<Music> getAllSongs(@NonNull Context context) {

        //容器
        List<Music> musics = new ArrayList<>();
        //获取音乐文件路径
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //开始查询
        @SuppressLint("Recycle")
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        //开始遍历游标
        while (cursor.moveToNext()) {
            Music music = new Music();
            // 文件名
            music.setFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            // 歌曲名
            music.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            // 时长
            music.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            // 歌手名
            music.setSinger(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            // 专辑名
            music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            // 年代
            if (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)) != null) {
                music.setYear(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));
            } else {
                music.setYear("未知");
            }
//            // 文件大小
//            if (cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)) != 0) {
//                float size = cursor.getInt(8) / 1024f / 1024f;
//                song.setSize((size + "").substring(0, 4) + "M");
//            } else {
//                song.setSize("未知");
//            }
            //文件路径
            if (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)) != null) {
                music.setFileUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            }
            //排除过小的音频文件
            if (cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) >= 30000) {
                musics.add(music);
            }
        }
        //关闭游标
        cursor.close();

        return musics;
    }

}