package com.example.entity;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Music {

    private String fileName;
    private String title;
    private String duration;
    private String singer;
    private String album;
    private String year;
    private String type;
    private String size;
    private String fileUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    @SuppressLint("SimpleDateFormat")
    public void setDuration(long duration) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        this.duration = format.format(new Date(duration));
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Music() {
        super();
    }

    public Music(String fileName, String title, String duration, String singer,
                 String album, String year, String type, String size, String fileUrl) {
        super();
        this.fileName = fileName;
        this.title = title;
        this.duration = duration;
        this.singer = singer;
        this.album = album;
        this.year = year;
        this.type = type;
        this.size = size;
        this.fileUrl = fileUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "Song [fileName=" + fileName + ", title=" + title
                + ", duration=" + duration + ", singer=" + singer + ", album="
                + album + ", year=" + year + ", type=" + type + ", size="
                + size + ", fileUrl=" + fileUrl + "]";
    }

}