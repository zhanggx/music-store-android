package com.example.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.util.Constants;

public class PicturePreviewActivity extends AppCompatActivity implements View.OnClickListener {
    public static void startActivity(Activity activity,String imagePath){
        Intent intent=new Intent(activity,PicturePreviewActivity.class);
        intent.putExtra(Constants.DATA,imagePath);
        activity.startActivity(intent);
    }
    private String mImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_picture_preview);
        this.findViewById(R.id.back_button).setOnClickListener(this);
        mImagePath=getIntent().getStringExtra(Constants.DATA);
        ImageView imageView=findViewById(R.id.image);
        if (!TextUtils.isEmpty(mImagePath)){
            Glide.with(this).load(mImagePath).into(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch (viewId){
            case R.id.back_button:{
                this.finish();
                break;
            }
        }
    }
}
