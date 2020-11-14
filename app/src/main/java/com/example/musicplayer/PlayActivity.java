package com.example.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.musicplayer.data.MusicDataUtils;
import com.example.musicplayer.data.MusicPlayStatus;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.service.PlayMusicInfo;
import com.example.musicplayer.service.PlayService;
import com.example.musicplayer.service.PlayServiceBinder;
import com.example.musicplayer.service.PlayServiceCallBack;
import com.example.musicplayer.util.Constants;

public class PlayActivity extends AppCompatActivity implements PlayServiceCallBack, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final void startPlayActivity(Activity activity,Music music){

        Intent intent=new Intent(activity, PlayActivity.class);
        intent.putExtra(Constants.DATA,music);
        activity.startActivity(intent);
    }
    private Music music;
    private MusicPlayStatus musicPlayStatus;
    private PlayServiceBinder playServiceBinder;
    private ImageButton btnPlay,btnStop;
    private SeekBar seekBarProgress;
    private TextView tvProgress,tvDuration;
    private ImageView mImageView;
    private Animation rotation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        music = getIntent().getParcelableExtra(Constants.DATA);
        toolbar.setTitle(music.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        btnPlay=findViewById(R.id.play_button);
        btnStop=findViewById(R.id.stop_button);
        mImageView=findViewById(R.id.image);
        Glide.with(this).load(music.getAlbumPictureUrl()).into(mImageView);
        tvProgress=findViewById(R.id.progress_text);
        tvDuration=findViewById(R.id.duration_text);
        seekBarProgress=findViewById(R.id.seekBar);
        seekBarProgress.setOnSeekBarChangeListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStop.setVisibility(View.GONE);
        musicPlayStatus = new MusicPlayStatus(this);
        PlayService.startService(this, music);
        PlayService.bindService(this, mConnection);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_play);
        rotation.setRepeatCount(Animation.INFINITE);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Music music =  intent.getParcelableExtra(Constants.DATA);
        if (music!=null&&music.getId()!=this.music.getId()){
            this.music=music;
            PlayService.startService(this, music);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play,menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayService.unbindService(this,mConnection);
        if (musicPlayStatus.getStatus()!=MusicPlayStatus.STATUS_PLAYING){
            PlayService.stopService(this);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.collect) {
            new Thread(() -> {
                long result=MusicDataUtils.insertOrUpdate(PlayActivity.this,music);
                if (result>0L){
                    Intent intent = new Intent(Constants.ACTION_MUSIC_DATA_CHANGED);
                    LocalBroadcastManager.getInstance(PlayActivity.this).sendBroadcast(intent);
                    runOnUiThread(()->Toast.makeText(PlayActivity.this, "成功收藏歌曲",Toast.LENGTH_SHORT).show());
                }else{
                    runOnUiThread(()->Toast.makeText(PlayActivity.this, "收藏歌曲失败",Toast.LENGTH_SHORT).show());
                }
            }).start();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //android.util.Log.e(TAG, "onServiceConnected#className=" + className);
            PlayService.LocalBinder binder = (PlayService.LocalBinder) service;
            playServiceBinder = binder.getService();
            playServiceBinder.setServiceCallBack(PlayActivity.this);
            PlayMusicInfo playMusicInfo= playServiceBinder.getCurrentMusicInfo();
            if (playMusicInfo.isPlaying()){
                btnPlay.setImageResource(R.drawable.widget_pause);
                seekBarProgress.setMax(playMusicInfo.getDuration());
                seekBarProgress.setProgress(playMusicInfo.getPosition());
                tvDuration.setText(getTimeStr(playMusicInfo.getDuration()));
                tvProgress.setText(getTimeStr(playMusicInfo.getPosition()));
                mImageView.startAnimation(rotation);
                btnStop.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if (playServiceBinder != null) {
                playServiceBinder.setServiceCallBack(null);
            }
            playServiceBinder = null;
        }
    };

    @Override
    public void onMusicPlayProgress(PlayMusicInfo playMusicInfo) {
        seekBarProgress.setProgress(playMusicInfo.getPosition());
        tvProgress.setText(getTimeStr(playMusicInfo.getPosition()));
    }

    @Override
    public void onMusicPlayStatusChanged(PlayMusicInfo playMusicInfo) {
        seekBarProgress.setMax(playMusicInfo.getDuration());
        if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_COMPLETED||playMusicInfo.getStatus()==PlayMusicInfo.STATUS_STOP) {
            btnPlay.setImageResource(R.drawable.widget_play);
            seekBarProgress.setProgress(0);
            tvProgress.setText(getTimeStr(0));
            rotation.cancel();
            btnStop.setVisibility(View.GONE);
        }else if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_PLAYING){
            btnPlay.setImageResource(R.drawable.widget_pause);
            seekBarProgress.setProgress(playMusicInfo.getPosition());
            tvDuration.setText(getTimeStr(playMusicInfo.getDuration()));
            tvProgress.setText(getTimeStr(playMusicInfo.getPosition()));
            mImageView.startAnimation(rotation);
            btnStop.setVisibility(View.VISIBLE);
        }else if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_PAUSE){
            btnPlay.setImageResource(R.drawable.widget_play);
            rotation.cancel();
        }
    }
    @Override
    public void onClick(View v) {
        if(playServiceBinder==null){
            return;
        }
        int viewId=v.getId();
        if (viewId==R.id.play_button) {
            PlayMusicInfo playMusicInfo = playServiceBinder.getCurrentMusicInfo();
            if (playMusicInfo.isPlaying()) {
                playServiceBinder.pauseMusic();
                btnPlay.setImageResource(R.drawable.widget_play);
                rotation.cancel();
            } else {
                playServiceBinder.startMusic();
            }
        }else if (viewId==R.id.stop_button) {
            playServiceBinder.stopMusic();
            btnPlay.setImageResource(R.drawable.widget_play);
            seekBarProgress.setProgress(0);
            tvProgress.setText(getTimeStr(0));
            rotation.cancel();
            btnStop.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            PlayMusicInfo playMusicInfo = playServiceBinder.getCurrentMusicInfo();
            if (playMusicInfo.isPlaying()) {
                playServiceBinder.seekTo(progress);
                tvProgress.setText(getTimeStr(progress));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    private String getTimeStr(int milliSeconds){
        int second=milliSeconds/1000;
        if (second==0){
            return "00:00";
        }
        if (second<10){
            return "00:0"+second;
        }
        if (second<60){
            return "00:"+second;
        }
        int hours=second/3600;
        int minute=second/60;
        second=second%60;
        StringBuilder stringBuilder=new StringBuilder();
        if (hours>0){
            if (hours<10){
                stringBuilder.append("0");
            }
            stringBuilder.append(hours);
            stringBuilder.append(":");
        }
        if (minute<10){
            stringBuilder.append("0");
        }
        stringBuilder.append(minute);
        stringBuilder.append(":");
        if (second<10){
            stringBuilder.append("0");
        }
        stringBuilder.append(second);
        return stringBuilder.toString();
    }
}
