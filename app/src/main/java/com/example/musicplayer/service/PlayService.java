package com.example.musicplayer.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
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
    private PlayHandler playHandler;
    private PlayMusicInfo playMusicInfo=new PlayMusicInfo();
    @Override
    public void onCreate() {
        super.onCreate();
        myMediaPlayer.setOnCompletionListener(this);
        myMediaPlayer.setOnErrorListener(this);
        myMediaPlayer.setOnPreparedListener(this);
        playHandler=new PlayHandler(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playHandler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        playMusicInfo.setStatus(PlayMusicInfo.STATUS_PLAYING);
        playMusicInfo.setDuration(mp.getDuration());
        playHandler.startLoop();
        if (mPlayServiceCallBack!=null) {
            mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
        }
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
            playMusicInfo.setMusic(music);
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
        playMusicInfo.setStatus(PlayMusicInfo.STATUS_COMPLETED);
        if (mPlayServiceCallBack!=null) {
            mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
        }
        playHandler.stopLoop();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        playMusicInfo.setStatus(PlayMusicInfo.STATUS_STOP);
        playHandler.stopLoop();
        return false;
    }

    @Override
    public void stopMusic() {
        if (myMediaPlayer.isPlaying()){
            playMusicInfo.setPosition(myMediaPlayer.getCurrentPosition());
            playMusicInfo.setStatus(PlayMusicInfo.STATUS_STOP);
            myMediaPlayer.stop();
            playHandler.stopLoop();
            if (mPlayServiceCallBack!=null) {
                mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
            }
        }
    }

    @Override
    public void startMusic() {
        if (!myMediaPlayer.isPlaying()) {
            if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_NONE||playMusicInfo.getStatus()==PlayMusicInfo.STATUS_STOP){
                myMediaPlayer.prepareAsync();
            }else {
                myMediaPlayer.start();
                playMusicInfo.setStatus(PlayMusicInfo.STATUS_PLAYING);
                playMusicInfo.setDuration(myMediaPlayer.getDuration());
                playHandler.startLoop();
                if (mPlayServiceCallBack!=null) {
                    mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
                }
            }
        }
    }

    @Override
    public void pauseMusic() {
        if (myMediaPlayer.isPlaying()){
            playMusicInfo.setStatus(PlayMusicInfo.STATUS_PAUSE);
            myMediaPlayer.pause();
            playHandler.stopLoop();
            if (mPlayServiceCallBack!=null) {
                mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
            }
        }
    }

    @Override
    public PlayMusicInfo getCurrentMusicInfo() {
        if (myMediaPlayer.isPlaying()){
            playMusicInfo.setPosition(myMediaPlayer.getCurrentPosition());
        }
        return playMusicInfo;
    }

    private boolean onMusicPlayProgress() {
        if (myMediaPlayer.isPlaying()){
            playMusicInfo.setPosition(myMediaPlayer.getCurrentPosition());
            mPlayServiceCallBack.onMusicPlayProgress(playMusicInfo);
            return true;
        }
        return false;
    }

    @Override
    public void setServiceCallBack(PlayServiceCallBack callBack) {
        this.mPlayServiceCallBack=callBack;
    }

    @Override
    public void seekTo(int progress) {
        if (myMediaPlayer.isPlaying()){
            myMediaPlayer.seekTo(progress);
            playMusicInfo.setPosition(myMediaPlayer.getCurrentPosition());
            mPlayServiceCallBack.onMusicPlayProgress(playMusicInfo);
        }
    }

    private static class PlayHandler extends Handler{
        private final WeakReference<PlayService> playServiceWeakReference;
        public PlayHandler(PlayService service){
            super();
            playServiceWeakReference=new WeakReference<>(service);
        }
        void startLoop(){
            if (this.hasMessages(0)){
               this.removeMessages(0);
            }
            this.sendEmptyMessageDelayed(0,1000);
        }
        void stopLoop(){
            this.removeMessages(0);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            PlayService playService=playServiceWeakReference.get();
            if (playService==null){
                return;
            }
            if (playService.onMusicPlayProgress()){
                this.sendEmptyMessageDelayed(0,1000);
            }
        }
    }
}
