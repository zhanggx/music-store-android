package com.example.musicplayer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.data.MusicPlayStatus;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.service.PlayService;
import com.example.musicplayer.service.PlayServiceBinder;
import com.example.musicplayer.service.PlayServiceCallBack;
import com.example.musicplayer.util.Constants;

public class PlayActivity extends AppCompatActivity implements PlayServiceCallBack {
    private Music music;
    private MusicPlayStatus musicPlayStatus;
    private PlayServiceBinder playServiceBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        music = getIntent().getParcelableExtra(Constants.DATA);
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

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //android.util.Log.e(TAG, "onServiceConnected#className=" + className);
            PlayService.LocalBinder binder = (PlayService.LocalBinder) service;
            playServiceBinder = binder.getService();
            playServiceBinder.setServiceCallBack(PlayActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if (playServiceBinder != null) {
                playServiceBinder.setServiceCallBack(null);
            }
            playServiceBinder = null;
        }
    };
}
