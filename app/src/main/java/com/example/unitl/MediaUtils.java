package com.example.unitl;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MediaUtils {
    private static MediaPlayer mPlayer;
    private static boolean isPause = false;

    //播放
    public static void playSound(String filePath, OnCompletionListener listener) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(listener);
        mPlayer.setOnErrorListener((mp, what, extra) -> {
            mPlayer.reset();
            return false;
        });
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
        } catch (Exception e) {
            System.out.println(e);
        }
        mPlayer.start();
        isPause = false;
    }

    //暂停
    public static void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    // 继续
    public static void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }

    //停止
    public static void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    //获取当前播放位置
    public static int position(){
       return mPlayer.getCurrentPosition();
    }

    //改变当前播放位置
    public static void seek(int position){
        mPlayer.seekTo(position);
    }

    //获取当前音乐总长度
    public static int size(){
        return mPlayer.getDuration();
    }
}