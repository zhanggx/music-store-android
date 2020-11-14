package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG) {
            android.util.Log.e("NotificationActivity", "onCreate#this=" + this);
        }
        Music music = getIntent().getParcelableExtra(Constants.DATA);

        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra(Constants.DATA,music);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }
}
