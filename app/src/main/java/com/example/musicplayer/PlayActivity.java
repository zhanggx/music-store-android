package com.example.musicplayer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.musicplayer.data.MusicPlayStatus;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.service.PlayMusicInfo;
import com.example.musicplayer.service.PlayService;
import com.example.musicplayer.service.PlayServiceBinder;
import com.example.musicplayer.service.PlayServiceCallBack;
import com.example.musicplayer.util.Constants;

public class PlayActivity extends AppCompatActivity implements PlayServiceCallBack, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Music music;
    private MusicPlayStatus musicPlayStatus;
    private PlayServiceBinder playServiceBinder;
    private Button btnStart;
    private SeekBar seekBarProgress;
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
        btnStart=findViewById(R.id.start_button);
        seekBarProgress=findViewById(R.id.seekBar);
        seekBarProgress.setOnSeekBarChangeListener(this);
        btnStart.setOnClickListener(this);
        musicPlayStatus = new MusicPlayStatus(this);
        PlayService.startService(this, music);
        PlayService.bindService(this, mConnection);
        if (savedInstanceState == null) {
            PlayService.startService(this, music);
            musicPlayStatus.setMusic(music.getId(), music.getName());
        } else {
            //如果不是首次启动（从后台判断，需要判断音乐是否结束.
            if (musicPlayStatus.getMusicId() != music.getId()) {
                PlayService.startService(this, music);
                musicPlayStatus.setMusic(music.getId(), music.getName());
            } else {
                if (musicPlayStatus.getStatus() != MusicPlayStatus.STATUS_ENDED) {
                    PlayService.startService(this, music);
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
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
                btnStart.setText("停止");
                seekBarProgress.setMax(playMusicInfo.getDuration());
                seekBarProgress.setProgress(playMusicInfo.getPosition());
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
    }

    @Override
    public void onMusicPlayStatusChanged(PlayMusicInfo playMusicInfo) {
        seekBarProgress.setMax(playMusicInfo.getDuration());
        if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_COMPLETED||playMusicInfo.getStatus()==PlayMusicInfo.STATUS_STOP) {
            btnStart.setText("开始");
            seekBarProgress.setProgress(0);
        }else if (playMusicInfo.getStatus()==PlayMusicInfo.STATUS_PLAYING){
            btnStart.setText("停止");
            seekBarProgress.setProgress(playMusicInfo.getPosition());
        }
    }
    @Override
    public void onClick(View v) {
        if(playServiceBinder==null){
            return;
        }
        PlayMusicInfo playMusicInfo= playServiceBinder.getCurrentMusicInfo();
        if (playMusicInfo.isPlaying()){
            playServiceBinder.stopMusic();
        }else{
            playServiceBinder.startMusic();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            PlayMusicInfo playMusicInfo = playServiceBinder.getCurrentMusicInfo();
            if (playMusicInfo.isPlaying()) {
                playServiceBinder.seekTo(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
