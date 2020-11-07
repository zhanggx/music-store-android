package com.example.musicplayer.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {
    private int id;
    private String name;

    private int albumId;
    private String albumName;
    private int singerId;
    private String singerName;

    private String timeLengthText;

    private int timeLength;

    private String fileUrl;
    private String filePath;
    private int fileSize;
    private String fileSizeText;
    private String description;
    private String albumPictureUrl;
    private String timeStamp;

    public Music(){

    }

    protected Music(Parcel in) {
        id = in.readInt();
        name = in.readString();
        albumId = in.readInt();
        albumName = in.readString();
        singerId = in.readInt();
        singerName = in.readString();
        timeLengthText = in.readString();
        timeLength = in.readInt();
        fileUrl = in.readString();
        filePath = in.readString();
        fileSize = in.readInt();
        fileSizeText = in.readString();
        description = in.readString();
        albumPictureUrl = in.readString();
        timeStamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(albumId);
        dest.writeString(albumName);
        dest.writeInt(singerId);
        dest.writeString(singerName);
        dest.writeString(timeLengthText);
        dest.writeInt(timeLength);
        dest.writeString(fileUrl);
        dest.writeString(filePath);
        dest.writeInt(fileSize);
        dest.writeString(fileSizeText);
        dest.writeString(description);
        dest.writeString(albumPictureUrl);
        dest.writeString(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
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

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
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

    public String getTimeLengthText() {
        return timeLengthText;
    }

    public void setTimeLengthText(String timeLengthText) {
        this.timeLengthText = timeLengthText;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSizeText() {
        return fileSizeText;
    }

    public void setFileSizeText(String fileSizeText) {
        this.fileSizeText = fileSizeText;
    }

    public String getAlbumPictureUrl() {
        return albumPictureUrl;
    }

    public void setAlbumPictureUrl(String albumPictureUrl) {
        this.albumPictureUrl = albumPictureUrl;
    }
}
