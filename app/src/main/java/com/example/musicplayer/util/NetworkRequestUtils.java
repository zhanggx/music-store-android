package com.example.musicplayer.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.Singer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NetworkRequestUtils {
    private static final Gson gson=new Gson();
    //private static final String HOST_BASE_URL="http://192.168.1.67:18086";
    private static final String HOST_BASE_URL="http://dawan.youke.ykhdedu.com/musicstore";
    private static final String RECOMMEND_MUSIC_URL=HOST_BASE_URL + "/recommend/getList";
    private static final String ALBUM_MUSIC_URL=HOST_BASE_URL + "/music/getList?albumId=";
    private static final String ALBUM_URL=HOST_BASE_URL + "/album/getList";
    private static final String SINGER_ALBUM_URL=HOST_BASE_URL + "/album/getList?singerId=";
    private static final String SINGER_URL=HOST_BASE_URL + "/singer/getList";

    public static ResultBean<List<Music>> getRecommendMusicList(){
        try {
            String result = get(RECOMMEND_MUSIC_URL);
            if (!TextUtils.isEmpty(result)){
                ResultBean<List<Music>> resultBean = gson.fromJson(result, new TypeToken<ResultBean<List<Music>>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBean<List<Music>> getMusicListByAlbumId(int albumId){
        try {
            String url=ALBUM_MUSIC_URL + albumId;
            String result = get(url);
            if (!TextUtils.isEmpty(result)){
                ResultBean<List<Music>> resultBean = gson.fromJson(result, new TypeToken<ResultBean<List<Music>>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }

    public static ResultBean<List<Album>> getAlbumList(){
        try {
            String result = get(ALBUM_URL);
            if (!TextUtils.isEmpty(result)){
                ResultBean<List<Album>> resultBean = gson.fromJson(result, new TypeToken<ResultBean<List<Album>>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBean<List<Album>> getAlbumListBySingerId(int signerId){
        try {
            String url=SINGER_ALBUM_URL + signerId;
            String result = get(url);
            if (!TextUtils.isEmpty(result)){
                ResultBean<List<Album>> resultBean = gson.fromJson(result, new TypeToken<ResultBean<List<Album>>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }

    public static ResultBean<List<Singer>> getSingerList(){
        try {
            String result = get(SINGER_URL);
            if (!TextUtils.isEmpty(result)){
                ResultBean<List<Singer>> resultBean = gson.fromJson(result, new TypeToken<ResultBean<List<Singer>>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }

    public static String get(String httpUrl) throws IOException {

        URL url = new URL(httpUrl);
        //得到connection对象。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("GET");
        //连接
        connection.connect();
        //得到响应码
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            //得到响应流
            InputStream inputStream = connection.getInputStream();
            //将响应流转换成字符串
            String result = getStringFromStream(inputStream);//将流转换为字符串。
            return result;
        }
        connection.disconnect();
        return null;
	}
    private static String getStringFromStream(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"utf-8"));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        String response = stringBuilder.toString().trim();
        return response;
    }
}
