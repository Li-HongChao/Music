package com.example;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adapter.Music_Adapter;
import com.example.entity.Music;
import com.example.unitl.FileUnit;
import com.example.unitl.MediaUtils;
import com.example.unitl.StatusBarUtils;
import com.example.unitl.WebUnits;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Music_search extends AppCompatActivity {

    private EditText searchEt;
    private ImageView searchImg;
    private List<Music> allSongs;
    private Music_Adapter adapter;
    public int positions;
    private ImageView searchLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_search);
        setInit();
    }

    private void setInit() {
        searchEt = findViewById(R.id.search_et);
        searchImg = findViewById(R.id.search_img);
        searchLoad = findViewById(R.id.search_load);
        searchEt.requestFocus();

        setStatusBar();

        searchImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                try {
                    searchLoad.setVisibility(View.VISIBLE);

                    new GetMsg().start();
                    allSongs.clear();
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * handler和GetMsg都是为了异步获取网络数据来的
     */
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        searchLoad.setVisibility(View.INVISIBLE);
                        setList();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case 2:
                    Toast.makeText(Music_search.this, "存储完成~", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    class GetMsg extends Thread {
        @Override
        public void run() {
            try {
                allSongs = WebUnits.getUrl(searchEt.getText().toString().trim());
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Downloads extends Thread {
        @Override
        public void run() {
            try {
                new FileUnit(Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE).toString() + File.separatorChar,
                        allSongs.get(positions).getFileName(),
                        allSongs.get(positions).getFileUrl(),
                        Music_search.this).getDate();
                Log.e(TAG, "run: 发过去了，就不知道存没存上");
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //填充数据
    @SuppressLint("NotifyDataSetChanged")
    private void setList() {
        RecyclerView musicList = findViewById(R.id.search_list);
        adapter = new Music_Adapter(allSongs);
        musicList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        musicList.setAdapter(adapter);
        //检测点击的位置
        adapter.setOnClickItem(new Music_Adapter.OnClickItem() {
            @Override
            public void onClickItem(View v, int i) {
                Log.e(TAG, "setList: " + allSongs.get(i).getFileUrl());
                MediaUtils.playSound(allSongs.get(i).getFileUrl(), mediaPlayer -> {
                });
            }
        });
        adapter.setOnItemLongClickItem((view, position) -> {
            positions = position;

            AlertDialog alertDialog = new AlertDialog.Builder(Music_search.this)
                    .setTitle("\t是否下载?")
                    .setMessage("\n" + allSongs.get(position).getFileName())
                    .setPositiveButton("取消", (dialogInterface, i) -> {

                    })
                    .setNeutralButton("确定", (dialogInterface, i) -> {
                        new Downloads().start();
                    })
                    .create();
            alertDialog.show();
            return true;
        });
    }

    /**
     * 工具类，改变状态栏
     */
    private void setStatusBar() {
        //外面找的工具类
        StatusBarUtils statusBarUtils = new StatusBarUtils(Music_search.this);
        //设置颜色为半透明
        // statusBar.setStatusBarColor(R.color.translucent);
        //设置颜色为透明
        //statusBar.setStatusBarColor(R.color.transparent);
        //隐藏状态栏
        statusBarUtils.hideStatusBar();
    }

}