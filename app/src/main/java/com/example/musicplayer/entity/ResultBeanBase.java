package com.example.musicplayer.entity;

import com.google.gson.JsonObject;

public class ResultBeanBase {
    private boolean success;
    private int errorCode;
    private String text;
    public ResultBeanBase(){

    }
    public ResultBeanBase(JsonObject jsonObject){
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
