package com.example.musicplayer.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivitySingerEditBinding;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.ResultBeanBase;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.ContextUtils;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.io.File;
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
            mImagePath=savedInstanceState.getString(Constants.STATE_FILE);
            long date=savedInstanceState.getLong(Constants.STATE,0L);
            if (date>0L) {
                mBirthdayDate = LocalDate.ofEpochDay(date);
            }
            if (!TextUtils.isEmpty(mImagePath)){
                Glide.with(this).load(mImagePath).into(singerEditBinding.image);
            }
        }else{
            if (singer!=null){
                if (!TextUtils.isEmpty(singer.getBirthday())) {
                    mBirthdayDate = LocalDate.parse(singer.getBirthday());
                }
            }else{
                mBirthdayDate=LocalDate.now();
            }
        }
        picturePicker=new PicturePicker(this,this);
        if (mBirthdayDate!=null) {
            singerEditBinding.birthdayText.setText(mBirthdayDate.format(fmt));
        }
        singerEditBinding.birthdayLayout.setOnClickListener(this);
        singerEditBinding.selectPicButton.setOnClickListener(this);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBirthdayDate!=null) {
            outState.putLong(Constants.STATE, mBirthdayDate.toEpochDay());
        }
        if(mImagePath!=null){
            outState.putString(Constants.STATE_FILE, mImagePath);
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
        String name=singerEditBinding.nameText.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"请输入名称",Toast.LENGTH_SHORT).show();
            return;
        }
        ContextUtils.hideSoftInput(this,singerEditBinding.nameText);
        String description=singerEditBinding.descText.getText().toString().trim();
        Singer singer;
        if (this.singer==null) {
            singer = new Singer();
        }else {
            singer=new Singer(this.singer);
        }
        singer.setName(name);
        if (mBirthdayDate!=null) {
            singer.setBirthday(mBirthdayDate.format(fmt));
        }else{
            singer.setBirthday(null);
        }
        singer.setDescription(description);
        File file=null;
        if (!TextUtils.isEmpty(mImagePath)){
            file=new File(mImagePath);
        }
        new SaveAsyncTask(this,singer,file).execute();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        if (viewId==R.id.birthday_layout){
            LocalDate localDate=mBirthdayDate;
            if (localDate==null){
                localDate=LocalDate.now();
            }
            int month=localDate.getMonthValue();
            new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
                // 绑定监听器(How the parent is notified that the date is set.)
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // 此处得到选择的时间，可以进行你想要的操作
                    mBirthdayDate=LocalDate.of(year,monthOfYear+1,dayOfMonth);
                    singerEditBinding.birthdayText.setText(mBirthdayDate.format(fmt));
                }
            },localDate.getYear(),month-1,localDate.getDayOfMonth()).show();
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

    private void onSaveSinger(ResultBeanBase resultBean,Singer singer) {
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            if (this.singer!=null) {
                Intent intent=new Intent();
                intent.putExtra(Constants.DATA,singer);
                this.setResult(Activity.RESULT_OK,intent);
            }else{
                this.setResult(Activity.RESULT_OK);
            }
            Intent intent = new Intent(Constants.ACTION_SINGER_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            this.finish();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }

    private static class SaveAsyncTask extends AsyncTask<Void,Void, ResultBeanBase> {
        private final WeakReference<SingerEditActivity> activityWeakReference;
        private Singer singer;
        private final File file;
        private ProgressDialog progressDialog;
        public SaveAsyncTask(SingerEditActivity activity, Singer singer,File file){
            activityWeakReference=new WeakReference<>(activity);
            this.singer=singer;
            this.file=file;
            try{
                progressDialog=ProgressDialog.show(activity,"歌手管理","正在保存，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBeanBase doInBackground(Void... voids) {
            SingerEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            if (file!=null) {
                ResultBean<String> resultBean=NetworkRequestUtils.uploadImageFile(file);
                if (resultBean!=null&&resultBean.isSuccess()){
                    singer.setPicturePath(resultBean.getData());
                }
            }
            ResultBeanBase resultBean=NetworkRequestUtils.saveSinger(singer);
            if (resultBean!=null&&resultBean.isSuccess()){
                if (singer.getId()>0) {
                    ResultBean<Singer> singerResultBean = NetworkRequestUtils.getSingerById(singer.getId());
                    if (singerResultBean != null && singerResultBean.isSuccess()) {
                        this.singer = singerResultBean.getData();
                    }
                }
            }
            return resultBean;
        }

        @Override
        protected void onPostExecute(ResultBeanBase resultBean) {
            super.onPostExecute(resultBean);
            try {
                progressDialog.dismiss();
            }catch(Throwable tr){
                tr.printStackTrace();
            }
            SingerEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onSaveSinger(resultBean,singer);
        }
    }
}
