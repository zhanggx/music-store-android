package com.example.musicplayer.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.musicplayer.entity.Music;
import com.google.gson.Gson;

public class MusicPlayStatus {
    public static final int STATUS_NONE=0;
    public static final int STATUS_PLAYING=1;
    public static final int STATUS_ENDED=2;
    private static final Gson gson=new Gson();
    private static final String STATUS_MUSIC_ID = "status_music_id";
    private static final String STATUS_MUSIC_NAME = "status_music_name";
    private static final String STATUS_MUSIC_STATUS = "status_music_status";;
    private static final String STATUS_MUSIC = "status_music";

    protected final SharedPreferences preferences;
    protected final Context context;
    private final String PREFERENCE_NAME = "status";

    public MusicPlayStatus(Context context) {
        this.preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }
    public int getMusicId() {
        return preferences.getInt(STATUS_MUSIC_ID,0);
    }
    public String getMusicName() {
        return preferences.getString(STATUS_MUSIC_STATUS,null);
    }

    public void setMusic(Music music) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(STATUS_MUSIC_ID, music.getId());
        editor.putString(STATUS_MUSIC_NAME, music.getName());
        editor.putInt(STATUS_MUSIC_STATUS, STATUS_NONE);
        editor.putString(STATUS_MUSIC, gson.toJson(music));
        editor.commit();
    }
    public Music getMusic() {
        int id= preferences.getInt(STATUS_MUSIC_ID,0);
        if (id==0){
            return null;
        }
        String json=preferences.getString(STATUS_MUSIC,null);
        if (!TextUtils.isEmpty(json)){
            return gson.fromJson(json,Music.class);
        }
        return null;
    }
    public int getStatus() {
        return preferences.getInt(STATUS_MUSIC_STATUS,STATUS_NONE);
    }
    public void setStatus(int status) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(STATUS_MUSIC_STATUS, status);
        editor.apply();
    }
}
