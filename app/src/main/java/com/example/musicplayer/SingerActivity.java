package com.example.musicplayer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicplayer.adapter.AlbumListAdapter;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.databinding.ActivitySingerBinding;
import com.example.musicplayer.databinding.ActivitySingerBinding;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;
import com.example.musicplayer.util.ScreenUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SingerActivity extends AppCompatActivity {
    private ActivitySingerBinding activitySingerBinding;
    private final List<Album> albumList=new ArrayList<>();
    private AlbumListAdapter albumListAdapter;
    private Singer singer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singer=getIntent().getParcelableExtra(Constants.DATA);
        activitySingerBinding = ActivitySingerBinding.inflate(getLayoutInflater());
        setContentView(activitySingerBinding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(singer.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        if (savedInstanceState!=null){
            List<Album> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                albumList.addAll(list);
            }
        }else{
            loadData();
        }
        albumListAdapter=new AlbumListAdapter(this,albumList,true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        activitySingerBinding.listView.setLayoutManager(linearLayoutManager);
        activitySingerBinding.listView.setAdapter(albumListAdapter);
        int screenWidth= ScreenUtils.getScreenWidth();
        ViewGroup.LayoutParams layoutParams=activitySingerBinding.image.getLayoutParams();
        layoutParams.height=screenWidth;
        activitySingerBinding.image.setLayoutParams(layoutParams);
        Glide.with(this).load(singer.getPictureUrl()).into(activitySingerBinding.image);
        activitySingerBinding.nameText.setText(singer.getName());
        activitySingerBinding.descText.setText(singer.getDescription());
        activitySingerBinding.countText.setText(getString(R.string.album_count_string,albumList.size()));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadData(){
        new SingerActivity.AlbumAsyncTask(this,singer).execute();
    }

    private void onUpdateAlbumList(ResultBean<List<Album>> resultBean) {
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            albumList.clear();
            albumList.addAll(resultBean.getData());
            albumListAdapter.notifyDataSetChanged();
            activitySingerBinding.countText.setText(getString(R.string.album_count_string,albumList.size()));
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class AlbumAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Album>>> {
        private final WeakReference<SingerActivity> singerActivityWeakReference;
        private final Singer singer;
        public AlbumAsyncTask(SingerActivity singerActivity,Singer singer){
            singerActivityWeakReference=new WeakReference<>(singerActivity);
            this.singer=singer;
        }
        @Override
        protected ResultBean<List<Album>> doInBackground(Void... voids) {
            SingerActivity singerActivity=singerActivityWeakReference.get();
            if (singerActivity==null){
                return null;
            }
            return NetworkRequestUtils.getAlbumListBySingerId(singer.getId());
        }

        @Override
        protected void onPostExecute(ResultBean<List<Album>> resultBean) {
            super.onPostExecute(resultBean);
            SingerActivity singerActivity=singerActivityWeakReference.get();
            if (singerActivity==null){
                return;
            }
            singerActivity.onUpdateAlbumList(resultBean);
        }
    }
}
