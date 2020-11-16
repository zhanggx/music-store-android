package com.example.musicplayer.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.ItemObjectArrayAdapter;
import com.example.musicplayer.databinding.ActivityAlbumEditBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ItemObject;
import com.example.musicplayer.entity.MusicTheme;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlbumEditActivity extends AppCompatActivity implements View.OnClickListener, PicturePicker.OnPicturePickedListener, AdapterView.OnItemSelectedListener {
    private ActivityAlbumEditBinding albumEditBinding;
    private Album album;
    private LocalDate mMusicDate;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private PicturePicker picturePicker;
    private String mImagePath;
    private int musicThemeId,singerId;
    private final List<MusicTheme> musicThemeList=new ArrayList<>();
    private final List<Singer> singerList=new ArrayList<>();
    private ItemObjectArrayAdapter musicThemeArrayAdapter,singerArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumEditBinding = ActivityAlbumEditBinding.inflate(getLayoutInflater());
        setContentView(albumEditBinding.getRoot());
        album=getIntent().getParcelableExtra(Constants.DATA);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(album!=null?R.string.edit_album:R.string.add_album);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        if (album!=null&&savedInstanceState==null){
            albumEditBinding.nameText.setText(album.getName());
            albumEditBinding.countText.setText(String.valueOf(album.getMusicCount()));
            albumEditBinding.descText.setText(album.getDescription());
            Glide.with(this).load(album.getPictureUrl()).into(albumEditBinding.image);
        }
        if (savedInstanceState!=null){
            mImagePath=savedInstanceState.getString(Constants.STATE_IMAGE);
            long date=savedInstanceState.getLong(Constants.STATE,0L);
            mMusicDate =LocalDate.ofEpochDay(date);
            if (!TextUtils.isEmpty(mImagePath)){
                Glide.with(this).load(mImagePath).into(albumEditBinding.image);
            }
            List<MusicTheme> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                musicThemeList.addAll(list);
            }
            List<Singer> singerList=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA2);
            if (singerList!=null){
                this.singerList.addAll(singerList);
            }
            musicThemeId=savedInstanceState.getInt(Constants.STATE_INDEX);
            singerId=savedInstanceState.getInt(Constants.STATE_INDEX2);
        }else{
            if (album!=null) {
                musicThemeId = album.getThemeId();
            }
            if (album!=null) {
                singerId = album.getSingerId();
            }
            mMusicDate =LocalDate.now();
            loadData();
        }
        picturePicker=new PicturePicker(this,this);
        albumEditBinding.publishTimeText.setText(mMusicDate.format(fmt));
        albumEditBinding.publishTimeLayout.setOnClickListener(this);
        albumEditBinding.selectPicButton.setOnClickListener(this);
        musicThemeArrayAdapter=new ItemObjectArrayAdapter(this,musicThemeList);
        singerArrayAdapter=new ItemObjectArrayAdapter(this,singerList);
        albumEditBinding.themeSpinner.setAdapter(musicThemeArrayAdapter);
        albumEditBinding.singerSpinner.setAdapter(singerArrayAdapter);
        albumEditBinding.themeSpinner.setOnItemSelectedListener(this);
        albumEditBinding.singerSpinner.setOnItemSelectedListener(this);
        if (musicThemeId>0&&!musicThemeList.isEmpty()){
            int position=getPositionInList(musicThemeList,musicThemeId);
            if (position>=0) {
                albumEditBinding.themeSpinner.setSelection(position);
            }
        }
        if (singerId>0&&!singerList.isEmpty()){
            int position=getPositionInList(singerList,singerId);
            if (position>=0) {
                albumEditBinding.singerSpinner.setSelection(position);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.STATE, mMusicDate.toEpochDay());
        if(mImagePath!=null){
            outState.putString(Constants.STATE_IMAGE, mImagePath);
        }
        if (!musicThemeList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) musicThemeList);
        }
        if (!singerList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA2, (ArrayList<? extends Parcelable>) singerList);
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
        new SaveAsyncTask(this,album).execute();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        if (viewId==R.id.publish_time_layout){
            new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
                // 绑定监听器(How the parent is notified that the date is set.)
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // 此处得到选择的时间，可以进行你想要的操作
                    mMusicDate=LocalDate.of(year,monthOfYear,dayOfMonth);
                    albumEditBinding.publishTimeText.setText(mMusicDate.format(fmt));
                }
            },mMusicDate.getYear(),mMusicDate.getMonthValue(),mMusicDate.getDayOfMonth()).show();
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
        Glide.with(this).load(mImagePath).into(albumEditBinding.image);
    }
    private void loadData(){
        new LoadDataAsyncTask(this).execute();
    }

    private void onUpdateList(List<MusicTheme> musicThemeList, List<Singer> singerList) {
        this.musicThemeList.clear();
        this.singerList.clear();
        if (musicThemeList!=null){
            this.musicThemeList.addAll(musicThemeList);
        }
        if (singerList!=null){
            this.singerList.addAll(singerList);
        }
        musicThemeArrayAdapter.notifyDataSetChanged();
        singerArrayAdapter.notifyDataSetChanged();
        if (musicThemeId>0&&!this.musicThemeList.isEmpty()){
            int position=getPositionInList(this.musicThemeList,musicThemeId);
            if (position>=0) {
                albumEditBinding.themeSpinner.setSelection(position);
            }
        }
        if (singerId>0&&!this.singerList.isEmpty()){
            int position=getPositionInList(this.singerList,singerId);
            if (position>=0) {
                albumEditBinding.singerSpinner.setSelection(position);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent==albumEditBinding.themeSpinner){
            MusicTheme musicTheme=(MusicTheme)parent.getItemAtPosition(position);
            musicThemeId=musicTheme.getId();
        }else if (parent==albumEditBinding.singerSpinner){
            Singer singer=(Singer)parent.getItemAtPosition(position);
            singerId=singer.getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent==albumEditBinding.themeSpinner){
            musicThemeId=0;
        }else if (parent==albumEditBinding.singerSpinner){
            singerId=0;
        }
    }

    private static class LoadDataAsyncTask extends AsyncTask<Void,Void, Object[]> {
        private final WeakReference<AlbumEditActivity> activityWeakReference;
        public LoadDataAsyncTask(AlbumEditActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected Object[] doInBackground(Void... voids) {
            AlbumEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            ResultBean<List<MusicTheme>> resultBean1= NetworkRequestUtils.getThemeList();
            ResultBean<List<Singer>> resultBean2= NetworkRequestUtils.getSingerList();
            List<MusicTheme> musicThemeList=null;
            List<Singer> singerList=null;
            if (resultBean1!=null&&resultBean1.isSuccess()){
                musicThemeList=resultBean1.getData();
            }
            if (resultBean2!=null&&resultBean2.isSuccess()){
                singerList=resultBean2.getData();
            }
            return new Object[]{musicThemeList,singerList};
        }

        @Override
        protected void onPostExecute(Object[] result) {
            super.onPostExecute(result);
            AlbumEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            List<MusicTheme> musicThemeList=(List<MusicTheme>)result[0];
            List<Singer> singerList=(List<Singer>)result[1];
            activity.onUpdateList(musicThemeList,singerList);
        }
    }

    private static class SaveAsyncTask extends AsyncTask<Void,Void, ResultBean> {
        private final WeakReference<AlbumEditActivity> activityWeakReference;
        private final Album album;
        public SaveAsyncTask(AlbumEditActivity activity,Album album){
            activityWeakReference=new WeakReference<>(activity);
            this.album=album;
        }
        @Override
        protected ResultBean doInBackground(Void... voids) {
            AlbumEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            //return NetworkRequestUtils.saveAlbum(album);
            return null;
        }

        @Override
        protected void onPostExecute(ResultBean resultBean) {
            super.onPostExecute(resultBean);
            AlbumEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
        }
    }

    private int getPositionInList(List<? extends ItemObject> list, int id){
        if (list.isEmpty()){
            return -1;
        }
        for(int i=0;i<list.size();i++){
            if (list.get(i).getId()==id){
                return i;
            }
        }
        return 0;
    }
}
