package com.example.unitl;


import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.entity.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebUnits {
    public static List<Music> getUrl(String name) throws IOException, JSONException {
        List<String> hash = new ArrayList<>();
        List<String> songNames = new ArrayList<>();
        List<String> authors = new ArrayList<>();
        List<String> albums = new ArrayList<>();
        List<Music> urls = new ArrayList<>();

        String url;
        String songName;
        String author;
        String album;
        String infoUrl = "http://mobilecdn.kugou.com/api/v3/search/song?keyword=" + name + "&page=1&pagesize=20";

        Request request = new Request.Builder().url(infoUrl).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        String infoJson = Objects.requireNonNull(response.body()).string();
        String data = new JSONObject(infoJson).getString("data");
        JSONArray info = new JSONObject(data).getJSONArray("info");
        System.out.println(info);
        for (int i = 0; i < info.length(); i++) {
            hash.add(((JSONObject) info.get(i)).getString("hash"));
            songNames.add(((JSONObject) info.get(i)).getString("songname"));
            authors.add(((JSONObject) info.get(i)).getString("singername"));
            albums.add(((JSONObject) info.get(i)).getString("album_name"));
        }
        for (int i = 0; i < hash.size(); i++) {
            url = "https://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=" + hash.get(i);
            songName = songNames.get(i);
            author = authors.get(i);
            album = albums.get(i);
            System.out.println(
                    (i + 1) + "歌曲名：" + songName + "\t歌手：" + author + "\t专辑:" + album + "\turl:" + url);

            Request requests = new Request.Builder().url(url).build();
            Response responses = okHttpClient.newCall(requests).execute();
            String json = Objects.requireNonNull(responses.body()).string();
            Music music = new Music();
            music.setAlbum(album);
            music.setFileName(songName);
            music.setSinger(author);
            music.setFileUrl(new JSONObject(json).getString("url"));
            urls.add(music);
        }
        Log.e(TAG, "getUrl: "+urls );
        return urls;
    }
}