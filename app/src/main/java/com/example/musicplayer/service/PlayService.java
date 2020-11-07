package com.example.musicplayer.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;

import java.io.IOException;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,PlayServiceBinder {
    private Music music;
    private final MediaPlayer myMediaPlayer = new MediaPlayer();

    public static void startService(Context context,Music music){
        Intent intent=new Intent(context,PlayService.class);
        intent.putExtra(Constants.DATA,music);
        context.startService(intent);;
    }
    public static void stopService(Context context){
        Intent intent=new Intent(context,PlayService.class);
        context.stopService(intent);;
    }
    public static void bindService(Activity activity, ServiceConnection serviceConnection){
        Intent intent=new Intent(activity,PlayService.class);
        activity.bindService(intent, serviceConnection,Context.BIND_AUTO_CREATE);
    }
    public static void unbindService(Activity activity, ServiceConnection serviceConnection){
        activity.unbindService(serviceConnection);
    }
    private final IBinder mBinder = new LocalBinder();
    private PlayServiceCallBack mPlayServiceCallBack;
    @Override
    public void onCreate() {
        super.onCreate();
        myMediaPlayer.setOnCompletionListener(this);
        myMediaPlayer.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public PlayService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Music music=intent.getParcelableExtra(Constants.DATA);
        if (music!=null){
            if (this.music!=null&&music.getId()==this.music.getId()){
                return Service.START_STICKY;
            }
            this.music=music;
            startPlay();
        }
        return Service.START_STICKY;
    }
    private void startPlay(){
        Uri uri = Uri.parse(music.getFileUrl());
        try {
            if (myMediaPlayer.isPlaying()){
                myMediaPlayer.stop();
            }
            myMediaPlayer.setDataSource(this, uri);
            myMediaPlayer.prepareAsync();
            myMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
    @Override
    public void setServiceCallBack(PlayServiceCallBack callBack) {
        this.mPlayServiceCallBack=callBack;
    }

}
