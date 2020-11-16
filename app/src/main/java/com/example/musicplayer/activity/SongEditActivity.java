package com.example.musicplayer.activity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AlbumArrayAdapter;
import com.example.musicplayer.adapter.ItemObjectArrayAdapter;
import com.example.musicplayer.databinding.ActivitySongEditBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ItemObject;
import com.example.musicplayer.entity.Music;
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

public class SongEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivitySongEditBinding songEditBinding;
    private Music music;

    private int albumId;
    private final List<Album> albumList=new ArrayList<>();
    private AlbumArrayAdapter albumArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songEditBinding = ActivitySongEditBinding.inflate(getLayoutInflater());
        setContentView(songEditBinding.getRoot());
        music=getIntent().getParcelableExtra(Constants.DATA);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(music!=null?R.string.edit_song:R.string.add_song);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        if (music!=null&&savedInstanceState==null){
            songEditBinding.nameText.setText(music.getName());
            songEditBinding.lengthText.setText(String.valueOf(music.getTimeLength()));
            songEditBinding.descText.setText(music.getDescription());
        }
        if (savedInstanceState!=null){
            List<Album> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                albumList.addAll(list);
            }
            albumId=savedInstanceState.getInt(Constants.STATE_INDEX);
        }else{
            if (music!=null) {
                albumId = music.getAlbumId();
            }
            loadData();
        }
        albumArrayAdapter =new AlbumArrayAdapter(this,albumList);
        songEditBinding.albumSpinner.setAdapter(albumArrayAdapter);
        songEditBinding.albumSpinner.setOnItemSelectedListener(this);
        if (albumId>0&&!albumList.isEmpty()){
            int position=getPositionInList(albumList,albumId);
            if (position>=0) {
                songEditBinding.albumSpinner.setSelection(position);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!albumList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) albumList);
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
    private void saveData(){
        new SaveAsyncTask(this,music).execute();
    }


    private void loadData(){
        new LoadDataAsyncTask(this).execute();
    }

    private void onUpdateList(List<Album> albumList) {
        this.albumList.clear();
        if (albumList!=null){
            this.albumList.addAll(albumList);
        }
        albumArrayAdapter.notifyDataSetChanged();
        if (albumId>0&&!this.albumList.isEmpty()){
            int position=getPositionInList(this.albumList,albumId);
            if (position>=0) {
                songEditBinding.albumSpinner.setSelection(position);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent==songEditBinding.albumSpinner){
            Album album=(Album)parent.getItemAtPosition(position);
            albumId=album.getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent==songEditBinding.albumSpinner){
            albumId=0;
        }
    }

    private static class LoadDataAsyncTask extends AsyncTask<Void,Void,List<Album>> {
        private final WeakReference<SongEditActivity> activityWeakReference;
        public LoadDataAsyncTask(SongEditActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected List<Album> doInBackground(Void... voids) {
            SongEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            ResultBean<List<Album>> resultBean= NetworkRequestUtils.getAlbumList();

            if (resultBean!=null&&resultBean.isSuccess()){
                return resultBean.getData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Album> result) {
            super.onPostExecute(result);
            SongEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onUpdateList(result);
        }
    }

    private static class SaveAsyncTask extends AsyncTask<Void,Void, ResultBean> {
        private final WeakReference<SongEditActivity> activityWeakReference;
        private final Music music;
        public SaveAsyncTask(SongEditActivity activity,Music music){
            activityWeakReference=new WeakReference<>(activity);
            this.music=music;
        }
        @Override
        protected ResultBean doInBackground(Void... voids) {
            SongEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            //return NetworkRequestUtils.saveAlbum(album);
            return null;
        }

        @Override
        protected void onPostExecute(ResultBean resultBean) {
            super.onPostExecute(resultBean);
            SongEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
        }
    }

    private int getPositionInList(List<Album> list, int id){
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
