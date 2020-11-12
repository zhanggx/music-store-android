package com.example.musicplayer.service;

import com.example.musicplayer.entity.Music;

public class PlayMusicInfo {
    public static final int STATUS_NONE=0;
    public static final int STATUS_PLAYING=1;
    public static final int STATUS_COMPLETED=2;
    public static final int STATUS_STOP=3;
    public static final int STATUS_PAUSE=4;
    private Music music;
    private int duration;
    private int position;
    private int status=STATUS_NONE;
    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public boolean isPlaying() {
        return STATUS_PLAYING==status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
