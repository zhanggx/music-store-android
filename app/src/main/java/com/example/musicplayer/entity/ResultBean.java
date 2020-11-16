package com.example.musicplayer.entity;

import com.google.gson.JsonObject;

public class ResultBean<T> extends ResultBeanBase {
    private T data;
    public ResultBean(){

    }
    public ResultBean(JsonObject jsonObject){
        super(jsonObject);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
