package com.example.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class Singer implements Parcelable,ItemObject {
    private int id;
    private String name;;
    private String picturePath;
    private String pictureUrl;

    private String birthday;
    private String description;
    /**
     * 创建时间
     */
    private String timeStamp;

    public Singer(){

    }
    public Singer(Singer singer){
        id = singer.id;
        copyFrom(singer);
    }

    protected Singer(Parcel in) {
        id = in.readInt();
        name = in.readString();
        picturePath = in.readString();
        pictureUrl = in.readString();
        birthday = in.readString();
        description = in.readString();
        timeStamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(picturePath);
        dest.writeString(pictureUrl);
        dest.writeString(birthday);
        dest.writeString(description);
        dest.writeString(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Singer> CREATOR = new Creator<Singer>() {
        @Override
        public Singer createFromParcel(Parcel in) {
            return new Singer(in);
        }

        @Override
        public Singer[] newArray(int size) {
            return new Singer[size];
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void copyFrom(Singer singer) {
        name = singer.name;
        picturePath = singer.picturePath;
        pictureUrl = singer.pictureUrl;
        birthday = singer.birthday;
        description = singer.description;
        timeStamp = singer.timeStamp;
    }
}
