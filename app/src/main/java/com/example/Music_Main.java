package com.example;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.Music_Adapter;
import com.example.entity.Music;
import com.example.unitl.AudioUtils;
import com.example.unitl.MediaUtils;
import com.example.unitl.StatusBarUtils;

import java.util.List;

import android.os.Handler;

public class Music_Main extends AppCompatActivity implements View.OnClickListener {
    private TextView musicName;
    private TextView musicSinger;
    private ImageView playMusic;

    // 要申请的权限
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET};
    //播放按钮当前状态
    public boolean playStatus = true;
    //音乐信息
    private List<Music> musicMsg;
    //当前播放位置（在列表中）
    public int playLocation = -1;
    //进度条
    public SeekBar seekBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setInit();
    }

    /**
     * 初始化
     */
    private void setInit() {

        //权限申请
        getUser();

        //初始化UI
        musicName = findViewById(R.id.main_music_name);
        musicSinger = findViewById(R.id.main_music_singer);
        ImageView search = findViewById(R.id.main_search);
        ImageView topList = findViewById(R.id.main_list_top);
        ImageView upMusic = findViewById(R.id.main_up);
        ImageView nextMusic = findViewById(R.id.main_next);
        playMusic = findViewById(R.id.main_play);
        seekBar = findViewById(R.id.seekBar);

        //基本设置开启
        upMusic.setOnClickListener(this);
        nextMusic.setOnClickListener(this);
        playMusic.setOnClickListener(this);
        search.setOnClickListener(this);
        topList.setOnClickListener(this);
        setStatusBar();
        setList();
        new GetTime().start();
    }

    /**
     * 工具类，改变状态栏
     */
    private void setStatusBar() {
        //外面找的工具类
        StatusBarUtils statusBarUtils = new StatusBarUtils(Music_Main.this);
        //设置颜色为半透明
        // statusBar.setStatusBarColor(R.color.translucent);
        //设置颜色为透明
        //statusBar.setStatusBarColor(R.color.transparent);
        //隐藏状态栏
        statusBarUtils.hideStatusBar();
    }

    /**
     * 判断权限
     */
    private void getUser() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    /**
     * 用户权限 申请 的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("存储权限不可用")
                            .setMessage("请在-应用设置-权限-中，允许应用使用存储权限来保存用户数据")
                            .setPositiveButton("立即开启", (dialog1, which) -> {
                                // 跳转到应用设置界面
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 123);
                            })
                            .setNegativeButton("取消", (dialog12, which) -> {
                            }).setCancelable(false).show();
                } else {
                    setList();
                }
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_next:
                if (playLocation >= musicMsg.size() - 1) {
                    play(0);
                } else {
                    play(playLocation + 1);
                }
                break;
            case R.id.main_up:
                if (playLocation == 0) {
                    play(musicMsg.size() - 1);
                } else {
                    play(playLocation - 1);
                }
                break;
            case R.id.main_play:
                if (playStatus) {
                    stop();
                } else {
                    goOn();
                }
        }
    }

    /**
     * 列表添加数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private void setList() {
        RecyclerView musicList = findViewById(R.id.main_music_list);
        musicMsg = AudioUtils.getAllSongs(Music_Main.this);
        Music_Adapter adapter = new Music_Adapter(musicMsg);
        musicList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        musicList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //检测点击的位置
        adapter.setOnClickItem((v, i) -> {
            play(i);
        });
    }

    /**
     * 播放
     *
     * @param location 当前播放位置--列表位置
     */
    private void play(int location) {
        //记录当前播放位置
        playLocation = location;
        //更新歌曲状态
        musicName.setText(musicMsg.get(location).getFileName());
        musicSinger.setText(musicMsg.get(location).getSinger());
        playMusic.setImageResource(R.mipmap.stop);
        //开始播放
        MediaUtils.playSound(musicMsg.get(location).getFileUrl(), mediaPlayer -> {
        });
        seekBar.setMax(MediaUtils.size());
        seekBar.setProgress(0);

    }

    /**
     * 暂停
     */
    private void stop() {
        MediaUtils.pause();
        playMusic.setImageResource(R.mipmap.play);
        playStatus = false;
    }

    /**
     * 继续播放
     */
    private void goOn() {
        MediaUtils.resume();
        playMusic.setImageResource(R.mipmap.stop);
        playStatus = true;
    }

    /**
     * 持续更新进度条
     */
    class GetTime extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            while (true) {
                try {
                    sleep(1);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && playLocation != -1) {

                playInProgress();
            }
        }
    };

    /**
     * 进度条更新方法
     */
    private void playInProgress() {
        seekBar.setProgress(MediaUtils.position());
        if (seekBar.getProgress() >= MediaUtils.size() - 200) {
            if (playLocation >= musicMsg.size() - 1) {
                play(0);
            } else {
                Log.e(TAG, "handleMessage: " + playLocation);
                play(playLocation + 1);
            }
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaUtils.seek(seekBar.getProgress());
            }
        });
    }
}