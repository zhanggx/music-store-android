package com.example.musicplayer.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.NotificationActivity;
import com.example.musicplayer.activity.PlayActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.data.MusicPlayStatus;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.ContextUtils;
import com.example.musicplayer.util.NotificationHelper;

import java.lang.ref.WeakReference;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, PlayServiceBinder, MediaPlayer.OnPreparedListener {


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
        try {
            activity.unbindService(serviceConnection);
        }catch(Throwable tr){
            tr.printStackTrace();
        }
    }
    private Music music;
    private final MediaPlayer myMediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new LocalBinder();
    private PlayServiceCallBack mPlayServiceCallBack;
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL = "Playback";
    private MusicPlayStatus musicPlayStatus;
    private PlayHandler playHandler;
    private PlayMusicInfo playMusicInfo=new PlayMusicInfo();
    private NotificationHelper mNotificationHelper;
    private PendingIntent mNotificationAction;
    private Bitmap mBmpCover;
    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayStatus = new MusicPlayStatus(this);
        myMediaPlayer.setOnCompletionListener(this);
        myMediaPlayer.setOnErrorListener(this);
        myMediaPlayer.setOnPreparedListener(this);
        playHandler=new PlayHandler(this);
        mNotificationHelper = new NotificationHelper(this, NOTIFICATION_CHANNEL, getString(R.string.app_name));
        mNotificationAction = createNotificationAction();
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
        musicPlayStatus.setStatus(MusicPlayStatus.STATUS_PLAYING);
        sendBroadcast();
        updateNotification(false);
    }

    public class LocalBinder extends Binder {
        public PlayService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action=intent.getAction();
        if (Constants.ACTION_CLOSE_NOTIFICATION.equals(action)){
            pauseMusic();
            updateNotification(false);
            return Service.START_STICKY;
        }
        if (Constants.ACTION_TOGGLE_PLAYBACK_NOTIFICATION.equals(action)){
            if (isPlaying()){
                pauseMusic();
                updateNotification(false);
            }else{
                if (music!=null){
                    startPlay();
                }
            }
            return Service.START_STICKY;
        }
        Music music=intent.getParcelableExtra(Constants.DATA);
        if (music!=null){
            if (this.music!=null&&music.getId()==this.music.getId()){
                if (!myMediaPlayer.isPlaying()) {
                    startPlay();
                }
                return Service.START_STICKY;
            }
            this.music=music;
            playMusicInfo.setMusic(music);
            startPlay();
        }
        return Service.START_STICKY;
    }
    private void startPlay(){
        musicPlayStatus.setMusic(music);
        String url=music.getFileUrl();
        try {
            if (myMediaPlayer.isPlaying()){
                myMediaPlayer.stop();
                myMediaPlayer.reset();
            }
            myMediaPlayer.setDataSource(url);
            myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //myMediaPlayer.prepare();
            //myMediaPlayer.start();
            myMediaPlayer.prepareAsync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Glide.with(this).asBitmap().load(music.getAlbumPictureUrl()).into(new SimpleTarget<Bitmap>(360,360) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mBmpCover=resource;
                if (myMediaPlayer.isPlaying()) {
                    updateNotification(false);
                }
            }
        });
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playMusicInfo.setStatus(PlayMusicInfo.STATUS_COMPLETED);
        if (mPlayServiceCallBack!=null) {
            mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
        }
        playHandler.stopLoop();
        musicPlayStatus.setStatus(MusicPlayStatus.STATUS_ENDED);
        sendBroadcast();
        updateNotification(true);
        stopForeground(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        playMusicInfo.setStatus(PlayMusicInfo.STATUS_STOP);
        playHandler.stopLoop();
        musicPlayStatus.setStatus(MusicPlayStatus.STATUS_ENDED);
        sendBroadcast();
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
            musicPlayStatus.setStatus(MusicPlayStatus.STATUS_ENDED);
            sendBroadcast();
            updateNotification(true);
            stopForeground(true);
        }
    }

    @Override
    public void startMusic() {
        if (!myMediaPlayer.isPlaying()) {
            if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_NONE||playMusicInfo.getStatus()==PlayMusicInfo.STATUS_STOP){
                //myMediaPlayer.prepareAsync();
                //startForeground(NOTIFICATION_ID, createNotification());
                if (music!=null){
                    startPlay();
                }
            }else {
                myMediaPlayer.start();
                playMusicInfo.setStatus(PlayMusicInfo.STATUS_PLAYING);
                playMusicInfo.setDuration(myMediaPlayer.getDuration());
                playHandler.startLoop();
                if (mPlayServiceCallBack!=null) {
                    mPlayServiceCallBack.onMusicPlayStatusChanged(playMusicInfo);
                }
                musicPlayStatus.setStatus(MusicPlayStatus.STATUS_PLAYING);
                sendBroadcast();
                updateNotification(false);
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
            //musicPlayStatus.setStatus(MusicPlayStatus.STATUS_ENDED);
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
    private boolean isPlaying(){
        return myMediaPlayer.isPlaying();
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
    private void sendBroadcast(){
        Intent intent = new Intent(Constants.ACTION_MUSIC_PLAY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
    public static boolean isPlayServiceRunning(Context context){
        return ContextUtils.isServiceWork(context,PlayService.class.getName());
    }

    /**
     * Create a song notification. Call through the NotificationHelper to
     * display it.
     */
    public Notification createNotification(){
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification);
        RemoteViews expanded = new RemoteViews(getPackageName(), R.layout.notification_expanded);

        if (mBmpCover !=null&&!mBmpCover.isRecycled()) {
            views.setImageViewBitmap(R.id.cover, mBmpCover);
            expanded.setImageViewBitmap(R.id.cover, mBmpCover);
        } else {
            views.setImageViewResource(R.id.cover, R.mipmap.ic_launcher);
            expanded.setImageViewResource(R.id.cover, R.mipmap.ic_launcher);
        }

        int playButton = getPlayButtonResource(isPlaying());

        views.setImageViewResource(R.id.play_pause, playButton);
        expanded.setImageViewResource(R.id.play_pause, playButton);

        ComponentName service = new ComponentName(this, PlayService.class);

        /*Intent previous = new Intent(Constants.ACTION_PREVIOUS_SONG);
        previous.setComponent(service);
        views.setOnClickPendingIntent(R.id.previous, PendingIntent.getService(this, 0, previous, 0));
        expanded.setOnClickPendingIntent(R.id.previous, PendingIntent.getService(this, 0, previous, 0));*/

        Intent playPause = new Intent(Constants.ACTION_TOGGLE_PLAYBACK_NOTIFICATION);
        playPause.setComponent(service);
        views.setOnClickPendingIntent(R.id.play_pause, PendingIntent.getService(this, 0, playPause, 0));
        expanded.setOnClickPendingIntent(R.id.play_pause, PendingIntent.getService(this, 0, playPause, 0));
        /*
        Intent next = new Intent(Constants.ACTION_NEXT_SONG);
        next.setComponent(service);
        views.setOnClickPendingIntent(R.id.next, PendingIntent.getService(this, 0, next, 0));
        expanded.setOnClickPendingIntent(R.id.next, PendingIntent.getService(this, 0, next, 0));*/

        int closeButtonVisibility = View.VISIBLE;
        Intent close = new Intent(Constants.ACTION_CLOSE_NOTIFICATION);
        close.setComponent(service);
        views.setOnClickPendingIntent(R.id.close, PendingIntent.getService(this, 0, close, 0));
        views.setViewVisibility(R.id.close, closeButtonVisibility);
        expanded.setOnClickPendingIntent(R.id.close, PendingIntent.getService(this, 0, close, 0));
        expanded.setViewVisibility(R.id.close, closeButtonVisibility);

        views.setTextViewText(R.id.title, music.getName());
        views.setTextViewText(R.id.artist, music.getSingerName());
        expanded.setTextViewText(R.id.title, music.getName());
        expanded.setTextViewText(R.id.album, music.getAlbumName());
        expanded.setTextViewText(R.id.artist, music.getSingerName());

        Notification notification = mNotificationHelper.getNewNotification(getApplicationContext());
        notification.contentView = views;
        notification.icon = R.drawable.status_icon;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.contentIntent = mNotificationAction;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // expanded view is available since 4.1
            notification.bigContentView = expanded;
            // 4.1 also knows about notification priorities
            // HIGH is one higher than the default.
            notification.priority = Notification.PRIORITY_HIGH;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.visibility = Notification.VISIBILITY_PUBLIC;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.priority = Notification.PRIORITY_MAX;
            notification.vibrate = new long[0]; // needed to get headsup
        } else {
            notification.tickerText = music.getName();
        }
        return notification;
    }
    private void updateNotification(boolean isCancel){
        if (isCancel) {
            mNotificationHelper.cancel(NOTIFICATION_ID);
        } else {
                mNotificationHelper.notify(NOTIFICATION_ID, createNotification());
        }
    }
    public PendingIntent createNotificationAction() {
        Intent appIntent= makeIntent();//new Intent(context, NotificationActivity.class);
        appIntent.putExtra(Constants.DATA,music);
        //appIntent.setAction(Intent.ACTION_MAIN);
        //appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent contentIntent =PendingIntent.getActivity(this, 0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }
    //任务栈
    private Intent[] makeIntentStack() {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(this, MainActivity.class));
        intents[1] = new Intent(this,  PlayActivity.class);
        return intents;
    }
    private Intent makeIntent() {
        Intent intent =new Intent(this,  NotificationActivity.class);
        return intent;
    }
    private static int getPlayButtonResource(boolean playing){
        int playButton = 0;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Android >= 5.0 uses the dark version of this drawable
            playButton = playing ? R.drawable.widget_pause : R.drawable.widget_play;
        } else {
            playButton = playing ? R.drawable.pause : R.drawable.play;
        }
        return playButton;
    }
}
