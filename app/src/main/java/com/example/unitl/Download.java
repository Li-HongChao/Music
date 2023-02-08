package com.example.unitl;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Download {
    //文件路径
    public String path;
    //文件名
    public String fileName;
    //网络地址
    public String url;

    public Download(String path, String fileName, String url) {
        this.path = path;
        this.fileName = fileName;
        this.url = url;
    }

    public void getDate() throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();

        InputStream input;
        FileOutputStream output;

        File file;
        byte[] bytes = new byte[1024 * 10];
        int length = 0;

        //获取到二进制流
        input = response.body().byteStream();
        //给文件一个路径和名字
        file = new File(path + fileName );
        //放进去
        output = new FileOutputStream(file);

        //正式开始存储
        while ((length = input.read(bytes)) != -1) {
            output.write(bytes, 0, length);
            Log.e(TAG, "getDate: 存储中");
        }
        input.close();
        output.close();
//        output.flush();

        Log.e(TAG, "没有意外的话是存完了--getDate: "+ path + fileName + ".mp3");
    }

}
