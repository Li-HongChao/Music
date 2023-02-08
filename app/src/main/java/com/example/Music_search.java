package com.example;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adapter.Music_Adapter;
import com.example.entity.Music;
import com.example.unitl.AudioUtils;
import com.example.unitl.MediaUtils;
import com.example.unitl.StatusBarUtils;
import com.example.unitl.WebUnits;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class Music_search extends AppCompatActivity {

    private EditText editText;
    private ImageView imageView;
    private List<Music> allSongs;
    private Music_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_search);
        setInit();
    }

    private void setInit() {
        editText = findViewById(R.id.search_et);
        imageView = findViewById(R.id.search_img);

        setStatusBar();

        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                try {
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
            if (msg.what == 1) {
                try {
                    setList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    class GetMsg extends Thread {
        @Override
        public void run() {
            try {
                allSongs = WebUnits.getUrl(editText.getText().toString().trim());
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception e) {
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
        adapter.setOnClickItem((v, i) -> MediaUtils.playSound(allSongs.get(i).getFileUrl(), mediaPlayer -> {
        }));
        adapter.setOnItemLongClickItem(new Music_Adapter.OnItemLongClickItem() {
            @Override
            public boolean onItemLongClickItem(View view, int position) {
                Toast.makeText(Music_search.this, "当前长按对象为："+allSongs.get(position).getFileName(), Toast.LENGTH_SHORT).show();
                return true;
            }
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