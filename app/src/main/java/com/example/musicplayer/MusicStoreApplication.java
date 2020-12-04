package com.example.musicplayer;

import android.app.Application;

public class MusicStoreApplication extends Application {
    private static volatile MusicStoreApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        //应用程序初始化
        this.instance=this;
    }

    public MusicStoreApplication getInstance(){
        return instance;
    }
}
