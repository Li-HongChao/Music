package com.example;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.os.Environment;
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
import com.example.unitl.FileUnit;
import com.example.unitl.MediaUtils;
import com.example.unitl.StatusBarUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import android.os.Handler;

@RequiresApi(api = Build.VERSION_CODES.R)
public class Music_Main extends AppCompatActivity implements View.OnClickListener {
    private TextView musicName;
    private TextView musicSinger;
    private ImageView playMusic;

    // 要申请的权限
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    //播放按钮当前状态
    public boolean playStatus = true;
    //判断当前时多选状态还是一般状态
    public boolean selectStatus = true;
    //音乐信息
    private List<Music> musicMsg;
    //被删除的音乐位置
    private List<Integer> deleteMusics;
    //当前播放位置（在列表中）
    public int playLocation = -1;
    //进度条
    public SeekBar seekBar;
    private Music_Adapter adapter;
    private ImageView search;
    private ImageView topList;

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
        search = findViewById(R.id.main_search);
        topList = findViewById(R.id.main_list_top);
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
        seekBar.setEnabled(true);
        deleteMusics = new ArrayList<>();
        setStatusBar();
        new GetTime().start();
        setList();
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            Log.e(TAG, "getUser: 权限已获取");
        } else {
            AlertDialog dialog2 = new AlertDialog.Builder(this)
                    .setTitle("获取文件管理权限")
                    .setMessage("请在文件管理权限中选择本应用开启权限")
                    .setPositiveButton("立即开启", (dialog1, which) -> {
                        // 跳转到应用设置界面
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", (dialog12, which) -> {
                    }).setCancelable(false).show();
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
                Log.e(TAG, "onClick: " + playStatus);
                if (playStatus) {
                    stop();
                } else {
                    goOn();
                }
                break;
            case R.id.main_search:
                if (selectStatus) {
                    startActivity(new Intent(Music_Main.this, Music_search.class));
                } else {
                    AlertDialog alertDialog2 = new AlertDialog.Builder(Music_Main.this)
                            .setTitle("是否确认删除?")
                            .setMessage("注意：\n一旦删除无法恢复!是否确认删除？")
                            .setPositiveButton("取消", (dialogInterface, i) -> {

                            })
                            .setNeutralButton("确定", (dialogInterface, i) -> {
                                Log.e(TAG, "onClick: "+deleteMusics );
                                deleteMusics.stream().distinct().collect(Collectors.toList());
                                for (Integer musicPosition : deleteMusics) {
                                    deleteMusic(musicPosition);
                                }
                                deleteMusics.clear();
                                refresh();
                                checksAfter();
                            })
                            .create();
                    alertDialog2.show();
                }
                break;
            case R.id.main_list_top:
                if (selectStatus) {
                    checksBefore();
                } else {
                    checksAfter();
                }

        }
    }

    /**
     * 点击删除后
     */
    private void checksAfter() {
        deleteMusics.clear();
        selectStatus = true;
        topList.setImageResource(R.mipmap.list_cb);
        search.setImageResource(R.mipmap.search);
        refresh();
    }

    /**
     * 点击删除前
     */
    private void checksBefore() {
        selectStatus = false;
        topList.setImageResource(R.mipmap.remove);
        search.setImageResource(R.mipmap.ack);
    }

    /**
     * 列表添加数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private void setList() {
        RecyclerView musicList = findViewById(R.id.main_music_list);
        musicMsg = AudioUtils.getAllSongs(Music_Main.this);
        adapter = new Music_Adapter(musicMsg);
        musicList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        musicList.setAdapter(adapter);


        //检测点击的位置
        adapter.setOnClickItem((v, i) -> {
            if (selectStatus) {
                play(i);
            } else {
                Music music = musicMsg.get(i);
                music.setType("delete");
                musicMsg.set(i, music);
                adapter.notifyDataSetChanged();
                deleteMusics.add(i);
            }

        });
        adapter.setOnItemLongClickItem((view, position) -> {
            if (selectStatus){
                AlertDialog alertDialog = new AlertDialog.Builder(Music_Main.this)
                        .setTitle("是否确认删除?")
                        .setMessage("歌曲：\"" + musicMsg.get(position).getFileName() + "\"\n" + "注意：\n一旦删除无法恢复!是否确认删除？")
                        .setPositiveButton("取消", (dialogInterface, i) -> {

                        })
                        .setNeutralButton("确定", (dialogInterface, i) -> {
                                deleteMusic(position);
                        })
                        .create();
                alertDialog.show();
            }else {
                return false;
            }
            return false;
        });
    }

    /**
     * 删除方法
     *
     * @param position 删除文件的位置
     */
    private void deleteMusic(int position) {
        if (selectStatus) {
            FileUnit.deleteDate(this, musicMsg.get(position).getFileUrl());
            refresh();
        } else {
            FileUnit.deleteDate(this, musicMsg.get(position).getFileUrl());
        }
    }

    /**
     * 播放
     *
     * @param location 当前播放位置--列表位置
     */
    private void play(int location) {
        //记录当前播放位置
        playLocation = location;
        //更新状态
        musicName.setText(musicMsg.get(location).getFileName());
        musicSinger.setText(musicMsg.get(location).getSinger());
        playMusic.setImageResource(R.mipmap.stop);
        playStatus = true;
        //开始播放
        Log.e(TAG, "play: " + musicMsg.get(location).getFileUrl());
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
        if (playStatus) playStatus = false;
    }

    /**
     * 继续播放
     */
    private void goOn() {
        MediaUtils.resume();
        playMusic.setImageResource(R.mipmap.stop);
        if (!playStatus) playStatus = true;
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
        seekBar.setEnabled(true);
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

    /**
     * 刷新方法
     */
    @SuppressLint("NotifyDataSetChanged")
    public void refresh() {
        musicMsg = AudioUtils.getAllSongs(Music_Main.this);
        adapter.setList(musicMsg);
        adapter.notifyDataSetChanged();
    }

    /**
     * 来回刷新
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }
}