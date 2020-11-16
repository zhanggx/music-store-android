package com.example.musicplayer.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class MusicTheme implements Parcelable,ItemObject {
    private int id;
    private String code;
    private String name;
    public MusicTheme(){

    }
    protected MusicTheme(Parcel in) {
        id = in.readInt();
        code = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicTheme> CREATOR = new Creator<MusicTheme>() {
        @Override
        public MusicTheme createFromParcel(Parcel in) {
            return new MusicTheme(in);
        }

        @Override
        public MusicTheme[] newArray(int size) {
            return new MusicTheme[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
