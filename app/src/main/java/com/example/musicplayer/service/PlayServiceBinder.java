package com.example.musicplayer.service;

import com.example.musicplayer.entity.Music;

public interface PlayServiceBinder {
    void stopMusic();
    void startMusic();
    void pauseMusic();
    PlayMusicInfo getCurrentMusicInfo();

    void setServiceCallBack(PlayServiceCallBack callBack);
    void seekTo(int progress);
}
