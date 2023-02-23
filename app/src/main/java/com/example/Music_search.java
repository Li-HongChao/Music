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

import com.example.adapter.History_Adapter;
import com.example.adapter.Music_Adapter;
import com.example.adapter.MyLayoutManager;
import com.example.dao.DBManger;
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
    private List<Music> allSongs;
    private Music_Adapter adapter;
    public int positions;
    private ImageView searchLoad;
    private List<String> setText;
    private RecyclerView history;
    private ImageView searchClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_search);
        setInit();
    }

    private void setInit() {
        searchEt = findViewById(R.id.search_et);
        ImageView searchImg = findViewById(R.id.search_img);
        searchLoad = findViewById(R.id.search_load);
        searchEt.requestFocus();
        searchClear = findViewById(R.id.search_clear_history);

        //设置历史记录
        setHistory();
        setStatusBar();

        //搜索框点击事件
        searchImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                try {
                    //将历史记录保存到数据库
                    if (searchEt.getText().toString().trim().equals("")) {
                        Toast.makeText(Music_search.this, "您输入的内容为空", Toast.LENGTH_SHORT).show();
                    } else {
                        //将历史记录存入数据库
                        DBManger.getInstance(Music_search.this).add(searchEt.getText().toString());
                        //隐藏历史记录
                        history.setVisibility(View.INVISIBLE);
                        //隐藏删除图标
                        history.setVisibility(View.INVISIBLE);
                        //开启加载动画
                        searchLoad.setVisibility(View.VISIBLE);
                        //开启线程
                        new GetMsg().start();
                        //将列表重置
                        allSongs.clear();
                        //刷新视图
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //删除键点击事件
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBManger.getInstance(Music_search.this).delAll();
                setHistory();
            }
        });
    }

    //显示历史记录,这里用的布局管理器是从网上找的
    private void setHistory() {
        //从数据库中获取历史记录
        setText = DBManger.getInstance(this).select();
        Log.e(TAG, "setHistory: " + DBManger.getInstance(this).select());

        //控制删除按键显示与否
        if (setText.isEmpty()) {
            searchClear.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "历史记录已清除~", Toast.LENGTH_SHORT).show();
        }

        //初始化视图
        history = findViewById(R.id.history);
        history.setVisibility(View.VISIBLE);
        History_Adapter history_adapter = new History_Adapter(setText);
        MyLayoutManager layout = new MyLayoutManager();
        layout.setAutoMeasureEnabled(true);//防止recyclerview高度为wrap时测量item高度0(一定要加这个属性，否则显示不出来）
        history.setLayoutManager(layout);
        history.setAdapter(history_adapter);

        //单击事件
        history_adapter.setOnClick(new History_Adapter.onClick() {
            @Override
            public void OnClick(View v, int i) {
                searchEt.setText(setText.get(i));
            }
        });
    }

    /**
     * handler是用来接收异步信息的
     */
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        //隐藏加载动画
                        searchLoad.setVisibility(View.INVISIBLE);
                        //传数据
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

    //线程1,获取链接
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

    //线程2,下载数据
    class Downloads extends Thread {
        @Override
        public void run() {
            try {
                FileUnit.getDate(Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE).toString() + File.separatorChar,
                        allSongs.get(positions).getFileName(),
                        allSongs.get(positions).getFileUrl(),
                        Music_search.this);
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
        adapter.setOnClickItem((v, i) -> {
            Log.e(TAG, "setList: " + allSongs.get(i).getFileUrl());
            if (!allSongs.get(i).getFileUrl().trim().equals("")) {
                Toast.makeText(Music_search.this, "正在播放" + allSongs.get(i).getFileName(), Toast.LENGTH_SHORT).show();
                MediaUtils.playSound(allSongs.get(i).getFileUrl(), mediaPlayer -> {
                });
            } else {
                Toast.makeText(Music_search.this, "亲，灰色歌曲不可用的哦~", Toast.LENGTH_SHORT).show();
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