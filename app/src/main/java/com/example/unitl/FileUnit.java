package com.example.unitl;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.Music_Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileUnit {
    //下载
    public static void getDate(String path, String fileName, String url, Context context) throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();

        InputStream input;
        FileOutputStream output;

        byte[] bytes = new byte[1024 * 10];
        int length;
        String paths = path + "/MyMusic/" + fileName + ".mp3";

        //查看是否有本应用的文件夹
        File file = new File(path + "/" + "/MyMusic/");
        if (!file.exists()) {// 判断目录是否存在
            file.mkdir();
        }

        //获取到二进制流
        input = response.body().byteStream();
        //给文件一个路径和名字
        file = new File(paths);
        //开启流
        output = new FileOutputStream(file);

        //正式开始存储
        while ((length = input.read(bytes)) != -1) {
            output.write(bytes, 0, length);
            Log.e(TAG, "getDate: 存储中");
        }
        input.close();
        output.close();

        //媒体库更新，坑死我了
        scanFile(context, paths);

        Log.e(TAG, "没有意外的话是存完了--getDate: " + paths);
    }

    //删除
    public static void deleteDate(Context context ,String path) {
        File file = new File(path);
        boolean flag = false;
        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }
        if (flag) {
            Log.e(TAG, "True  deleteDate: " + path);
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "False  deleteDate: " + path);
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "deleteDate: 当前删除文件"+path);
    }

    //刷新媒体库
    public static void scanFile(Context context, String filePath) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);
            Log.e(TAG, "deleteDate: \"/storage/emulated/0/Download/MyMusic/探窗(纯音乐).mp3\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


/*
              if (Objects.equals(url.substring(url.indexOf('.') + 1), "flac")) {
                  AlertDialog alertDialog2 = new AlertDialog.Builder(context)
                          .setMessage("\n" + fileName + "-----" + "删除失败(flac文件为无损音质的特殊文件，需手动删除！)")
                          .setPositiveButton("确定", (dialogInterface, i) -> {
                          }).create();
                  alertDialog2.show();
              }
 */