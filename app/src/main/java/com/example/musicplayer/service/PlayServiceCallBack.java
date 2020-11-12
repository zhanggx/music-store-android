package com.example.musicplayer.service;

public interface PlayServiceCallBack {
    void onMusicPlayStatusChanged(PlayMusicInfo playMusicInfo);
    void onMusicPlayProgress(PlayMusicInfo playMusicInfo);
}
