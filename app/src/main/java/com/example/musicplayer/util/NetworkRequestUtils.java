package com.example.musicplayer.util;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.MusicTheme;
import com.example.musicplayer.entity.RequestBean;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.ResultBeanBase;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.entity.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class NetworkRequestUtils {
    private static final Gson gson=new Gson();
    //private static final String HOST_BASE_URL="http://192.168.1.67:18086";
    private static final String HOST_BASE_URL="http://dawan.youke.ykhdedu.com/musicstore";
    private static final String LOGIN_URL=HOST_BASE_URL + "/user/login";
    private static final String RECOMMEND_MUSIC_URL=HOST_BASE_URL + "/recommend/getList";
    private static final String ALBUM_MUSIC_URL=HOST_BASE_URL + "/music/getList?albumId=";
    private static final String MUSIC_LIST_URL=HOST_BASE_URL + "/music/getList";
    private static final String REMOVE_MUSIC_URL=HOST_BASE_URL + "/music/delete";
    private static final String SAVE_MUSIC_URL=HOST_BASE_URL + "/music/saveMusic";
    private static final String ALBUM_URL=HOST_BASE_URL + "/album/getList";
    private static final String SAVE_ALBUM_URL=HOST_BASE_URL + "/album/saveAlbum";
    private static final String GET_ALBUM_BY_ID_URL=HOST_BASE_URL + "/album/getAlbum?albumId=";
    private static final String REMOVE_ALBUM_URL=HOST_BASE_URL + "/album/delete";
    private static final String SINGER_ALBUM_URL=HOST_BASE_URL + "/album/getList?singerId=";
    private static final String SINGER_URL=HOST_BASE_URL + "/singer/getList";
    private static final String SAVE_SINGER_URL=HOST_BASE_URL + "/singer/saveSinger";
    private static final String REMOVE_SINGER_URL=HOST_BASE_URL + "/singer/delete";
    private static final String THEME_URL=HOST_BASE_URL + "/theme/getList";
    private static final String GET_MUSIC_BY_ID_URL=HOST_BASE_URL + "/music/getMusic?musicId=";
    private static final String GET_SINGER_BY_ID_URL=HOST_BASE_URL + "/singer/getSinger?singerId=";
    private static final String POST_IMAGE_FILE=HOST_BASE_URL + "/file/uploadImg";
    private static final String POST_MUSIC_FILE=HOST_BASE_URL + "/file/uploadMusic";
    private static final String CANCEL_RECOMMEND_URL=HOST_BASE_URL + "/recommend/cancel";
    private static final String ADD_RECOMMEND_URL=HOST_BASE_URL + "/recommend/add";
    private static final String MOVE_RECOMMEND_URL=HOST_BASE_URL + "/recommend/move";

    public static ResultBean<User> login(String account, String password){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setAccount(account);
            requestBean.setPassword(password);
            String json=gson.toJson(requestBean);
            String result = postJsonData(LOGIN_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBean<User> resultBean = gson.fromJson(result, new TypeToken<ResultBean<User>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }

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
    public static ResultBean<List<Music>> getMusicList(){
        try {
            String result = get(MUSIC_LIST_URL);
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
    public static ResultBean<Music> getMusicById(int musicId){
        try {
            String url=GET_MUSIC_BY_ID_URL + musicId;
            String result = get(url);
            if (!TextUtils.isEmpty(result)){
                ResultBean<Music> resultBean = gson.fromJson(result, new TypeToken<ResultBean<Music>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase login(Music music){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setId(music.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(REMOVE_MUSIC_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase removeMusic(Music music){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setId(music.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(REMOVE_MUSIC_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }


    public static ResultBean<List<MusicTheme>> getThemeList(){
        try {
            String result = get(THEME_URL);
            if (!TextUtils.isEmpty(result)){
                ResultBean<List<MusicTheme>> resultBean = gson.fromJson(result, new TypeToken<ResultBean<List<MusicTheme>>>() {
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
    public static ResultBean<Album> getAlbumById(int albumId){
        try {
            String url=GET_ALBUM_BY_ID_URL + albumId;
            String result = get(url);
            if (!TextUtils.isEmpty(result)){
                ResultBean<Album> resultBean = gson.fromJson(result, new TypeToken<ResultBean<Album>>() {
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
    public static ResultBeanBase saveAlbum(Album album){
        try {
            String json=gson.toJson(album);
            String result = postJsonData(SAVE_ALBUM_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase removeAlbum(Album album){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setId(album.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(REMOVE_ALBUM_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
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
    public static ResultBean<Singer> getSingerById(int singerId){
        try {
            String url=GET_SINGER_BY_ID_URL + singerId;
            String result = get(url);
            if (!TextUtils.isEmpty(result)){
                ResultBean<Singer> resultBean = gson.fromJson(result, new TypeToken<ResultBean<Singer>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase saveMusic(Music music){
        try {
            String json=gson.toJson(music);
            String result = postJsonData(SAVE_MUSIC_URL,json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }

    public static ResultBeanBase saveSinger(Singer singer){
        try {
            String json=gson.toJson(singer);
            String result = postJsonData(SAVE_SINGER_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase removeSinger(Singer singer){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setId(singer.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(REMOVE_SINGER_URL, json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase addMusicRecommend(Music music){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setId(music.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(ADD_RECOMMEND_URL,json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBeanBase cancelMusicRecommend(Music music){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setId(music.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(CANCEL_RECOMMEND_URL,json);
            if (!TextUtils.isEmpty(result)){
                ResultBeanBase resultBean = gson.fromJson(result, new TypeToken<ResultBeanBase>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBean<List<Music>> moveMusicRecommend(Music src,Music dst){
        try {
            RequestBean requestBean=new RequestBean();
            requestBean.setSourceId(src.getId());
            requestBean.setDestId(dst.getId());
            String json=gson.toJson(requestBean);
            String result = postJsonData(MOVE_RECOMMEND_URL,json);
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
    public static ResultBean<String> uploadImageFile(File file){
        try {
            String result = postFile(POST_IMAGE_FILE,file);
            if (!TextUtils.isEmpty(result)){
                ResultBean<String> resultBean = gson.fromJson(result, new TypeToken<ResultBean<String>>() {
                }.getType());
                return resultBean;
            }
        }catch(Throwable tr){
            tr.printStackTrace();
        }
        return null;
    }
    public static ResultBean<String> uploadMusicFile(File file){
        try {
            String result = postFile(POST_MUSIC_FILE,file);
            if (!TextUtils.isEmpty(result)){
                ResultBean<String> resultBean = gson.fromJson(result, new TypeToken<ResultBean<String>>() {
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
    public static String postJsonData(String httpUrl,String json) throws IOException {
        URL url = new URL(httpUrl);
        //得到connection对象。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        //连接
        connection.connect();
        OutputStream out = connection.getOutputStream();
        out.write(json.getBytes());
        out.flush();
        out.close();

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
    private final static String BOUNDARY = UUID.randomUUID().toString()
            .toLowerCase().replaceAll("-", "");// 边界标识
    private final static String PREFIX = "--";// 必须存在
    private final static String LINE_END = "\r\n";
    private static String postFile(String httpUrl,File file) throws IOException {
        URL url = new URL(httpUrl);
        //得到connection对象。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        //连接
        connection.connect();
        OutputStream out = connection.getOutputStream();
        StringBuilder requestParams = new StringBuilder();

        requestParams.append(PREFIX).append(BOUNDARY).append(LINE_END);
        requestParams.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
        requestParams.append("Content-Type: application/octet-stream; charset=UTF-8" + LINE_END);
        requestParams.append("Content-Transfer-Encoding: binary"+LINE_END);
        requestParams.append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容

        out.write(requestParams.toString().getBytes());
        FileInputStream fin = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int bytelength;
        while((bytelength=fin.read(bytes))!=-1){
            out.write(bytes, 0, bytelength);
        }
        out.write(LINE_END.getBytes());
        out.flush();

        // 请求结束标志
        String endTarget = PREFIX + BOUNDARY + PREFIX + LINE_END;
        out.write(endTarget.getBytes());
        out.flush();
        out.close();

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
