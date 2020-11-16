package com.example.musicplayer.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivitySingerEditBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.util.Constants;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SingerEditActivity extends AppCompatActivity implements View.OnClickListener, PicturePicker.OnPicturePickedListener {
    private ActivitySingerEditBinding singerEditBinding;
    private Singer singer;
    private LocalDate mBirthdayDate;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private PicturePicker picturePicker;
    private String mImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singerEditBinding = ActivitySingerEditBinding.inflate(getLayoutInflater());
        setContentView(singerEditBinding.getRoot());
        singer=getIntent().getParcelableExtra(Constants.DATA);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(singer!=null?R.string.edit_singer:R.string.add_singer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        if (singer!=null&&savedInstanceState==null){
            singerEditBinding.nameText.setText(singer.getName());
            singerEditBinding.descText.setText(singer.getDescription());
            Glide.with(this).load(singer.getPictureUrl()).into(singerEditBinding.image);
        }
        if (savedInstanceState!=null){
            mImagePath=savedInstanceState.getString(Constants.STATE_IMAGE);
            long date=savedInstanceState.getLong(Constants.STATE,0L);
            mBirthdayDate =LocalDate.ofEpochDay(date);
            if (!TextUtils.isEmpty(mImagePath)){
                Glide.with(this).load(mImagePath).into(singerEditBinding.image);
            }
        }else{

            mBirthdayDate =LocalDate.now();
        }
        picturePicker=new PicturePicker(this,this);
        singerEditBinding.birthdayText.setText(mBirthdayDate.format(fmt));
        singerEditBinding.birthdayLayout.setOnClickListener(this);
        singerEditBinding.selectPicButton.setOnClickListener(this);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.STATE, mBirthdayDate.toEpochDay());
        if(mImagePath!=null){
            outState.putString(Constants.STATE_IMAGE, mImagePath);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            saveData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        picturePicker.onRequestPermissionsResult(requestCode, permissions,grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        picturePicker.onActivityResultOk(requestCode, resultCode,data);
        if (resultCode== Activity.RESULT_OK) {
        }
    }

    private void saveData(){
        //new SaveAsyncTask(this,album).execute();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        if (viewId==R.id.birthday_layout){
            new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
                // 绑定监听器(How the parent is notified that the date is set.)
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // 此处得到选择的时间，可以进行你想要的操作
                    mBirthdayDate=LocalDate.of(year,monthOfYear,dayOfMonth);
                    singerEditBinding.birthdayText.setText(mBirthdayDate.format(fmt));
                }
            },mBirthdayDate.getYear(),mBirthdayDate.getMonthValue(),mBirthdayDate.getDayOfMonth()).show();
            return;
        }
        if (viewId==R.id.select_pic_button){
            picturePicker.showPickDialog();
            return;
        }
    }

    @Override
    public void onPicturePicked(String imagePath) {
        this.mImagePath=imagePath;
        Glide.with(this).load(mImagePath).into(singerEditBinding.image);
    }


    private static class SaveAsyncTask extends AsyncTask<Void,Void, ResultBean> {
        private final WeakReference<SingerEditActivity> activityWeakReference;
        private final Album album;
        public SaveAsyncTask(SingerEditActivity activity, Album album){
            activityWeakReference=new WeakReference<>(activity);
            this.album=album;
        }
        @Override
        protected ResultBean doInBackground(Void... voids) {
            SingerEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            //return NetworkRequestUtils.saveAlbum(album);
            return null;
        }

        @Override
        protected void onPostExecute(ResultBean resultBean) {
            super.onPostExecute(resultBean);
            SingerEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
        }
    }
}
