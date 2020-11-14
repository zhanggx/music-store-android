package com.example.musicplayer.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.databinding.ActivityAlbumBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;
import com.example.musicplayer.util.ScreenUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private ActivityAlbumBinding activityAlbumBinding;
    private final List<Music> musicList=new ArrayList<>();
    private SongListAdapter songListAdapter;
    private Album album;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        album=getIntent().getParcelableExtra(Constants.DATA);
        activityAlbumBinding = ActivityAlbumBinding.inflate(getLayoutInflater());
        setContentView(activityAlbumBinding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(album.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        if (savedInstanceState!=null){
            List<Music> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                musicList.addAll(list);
            }
        }else{
            loadData();
        }
        songListAdapter=new SongListAdapter(this,musicList,SongListAdapter.MODE_SIMPLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        activityAlbumBinding.listView.setLayoutManager(linearLayoutManager);
        activityAlbumBinding.listView.setAdapter(songListAdapter);
        int screenWidth= ScreenUtils.getScreenWidth();
        ViewGroup.LayoutParams layoutParams=activityAlbumBinding.image.getLayoutParams();
        layoutParams.height=screenWidth;
        activityAlbumBinding.image.setLayoutParams(layoutParams);
        Glide.with(this).load(album.getPictureUrl()).into(activityAlbumBinding.image);
        activityAlbumBinding.signerText.setText(album.getSingerName());
        activityAlbumBinding.nameText.setText(album.getName());
        activityAlbumBinding.descText.setText(album.getDescription());
        activityAlbumBinding.countText.setText(getString(R.string.music_count_string,album.getMusicCount()));
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
        new MusicAsyncTask(this,album).execute();
    }

    private void onUpdateMusicList(ResultBean<List<Music>> resultBean) {
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            musicList.clear();
            musicList.addAll(resultBean.getData());
            songListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class MusicAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Music>>> {
        private final WeakReference<AlbumActivity> albumActivityWeakReference;
        private final Album album;
        public MusicAsyncTask(AlbumActivity albumActivity,Album album){
            albumActivityWeakReference=new WeakReference<>(albumActivity);
            this.album=album;
        }
        @Override
        protected ResultBean<List<Music>> doInBackground(Void... voids) {
            AlbumActivity albumActivity=albumActivityWeakReference.get();
            if (albumActivity==null){
                return null;
            }
            return NetworkRequestUtils.getMusicListByAlbumId(album.getId());
        }

        @Override
        protected void onPostExecute(ResultBean<List<Music>> resultBean) {
            super.onPostExecute(resultBean);
            AlbumActivity albumActivity=albumActivityWeakReference.get();
            if (albumActivity==null){
                return;
            }
            albumActivity.onUpdateMusicList(resultBean);
        }
    }
}
