package com.example.musicplayer.data;

import android.content.Context;
import android.content.SharedPreferences;

public class MusicPlayStatus {
    public static final int STATUS_NONE=0;
    public static final int STATUS_PLAYING=1;
    public static final int STATUS_ENDED=2;

    private static final String STATUS_MUSIC_ID = "status_music_id";
    private static final String STATUS_MUSIC_NAME = "status_music_name";
    private static final String STATUS_MUSIC_STATUS = "status_music_status";

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

    public void setMusic(int id,String name) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(STATUS_MUSIC_ID, id);
        editor.putString(STATUS_MUSIC_NAME, name);
        editor.putInt(STATUS_MUSIC_STATUS, STATUS_NONE);
        editor.commit();
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
