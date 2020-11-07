package com.example.musicplayer.entity;

import com.google.gson.JsonObject;

public class ResultBean<T> {
    private boolean success;
    private int errorCode;
    private String text;
    private T data;
    public ResultBean(){

    }
    public ResultBean(JsonObject jsonObject){
        success=jsonObject.get("success").getAsBoolean();
        errorCode=jsonObject.get("errorCode").getAsInt();
        text=jsonObject.get("text").getAsString();
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
