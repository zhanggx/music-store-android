package com.example.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class Album implements Parcelable {
    private int id;
    private String name;;
    private String picturePath;
    private int musicCount;
    private String description;
    private int singerId;
    private String singerName;
    private int themeId;
    private String themeName;

    private String pictureUrl;
    private String publishTime;
    /**
     * 创建时间
     */
    private String timeStamp;

    public Album(){

    }

    public Album(Album album){
        id = album.id;
        copyFrom(album);
    }
    protected Album(Parcel in) {
        id = in.readInt();
        name = in.readString();
        picturePath = in.readString();
        musicCount = in.readInt();
        description = in.readString();
        singerId = in.readInt();
        singerName = in.readString();
        themeId = in.readInt();
        themeName = in.readString();
        pictureUrl = in.readString();
        publishTime = in.readString();
        timeStamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(picturePath);
        dest.writeInt(musicCount);
        dest.writeString(description);
        dest.writeInt(singerId);
        dest.writeString(singerName);
        dest.writeInt(themeId);
        dest.writeString(themeName);
        dest.writeString(pictureUrl);
        dest.writeString(publishTime);
        dest.writeString(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(int musicCount) {
        this.musicCount = musicCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public void copyFrom(Album album) {
        name = album.name;
        picturePath = album.picturePath;
        musicCount = album.musicCount;
        description = album.description;
        singerId = album.singerId;
        singerName = album.singerName;
        themeId = album.themeId;
        themeName = album.themeName;
        pictureUrl = album.pictureUrl;
        publishTime = album.publishTime;
        timeStamp = album.timeStamp;
    }
}
