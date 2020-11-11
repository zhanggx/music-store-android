package com.example.musicplayer.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;

import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;

import java.lang.ref.WeakReference;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, PlayServiceBinder, MediaPlayer.OnPreparedListener {
    private Music music;
    private final MediaPlayer myMediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new LocalBinder();
    private PlayServiceCallBack mPlayServiceCallBack;

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

    @Override
    public void onCreate() {
        super.onCreate();
        myMediaPlayer.setOnCompletionListener(this);
        myMediaPlayer.setOnErrorListener(this);
        myMediaPlayer.setOnPreparedListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
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
        String url=music.getFileUrl();
        try {
            if (myMediaPlayer.isPlaying()){
                myMediaPlayer.stop();
            }
            myMediaPlayer.setDataSource(url);
            myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //myMediaPlayer.prepare();
            //myMediaPlayer.start();
            myMediaPlayer.prepareAsync();
        } catch (Throwable e) {
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
    private static class PlayHandler extends Handler{
        private final WeakReference<PlayService> playServiceWeakReference;
        public PlayHandler(PlayService service){
            playServiceWeakReference=new WeakReference<>(service);
        }
    }
}
