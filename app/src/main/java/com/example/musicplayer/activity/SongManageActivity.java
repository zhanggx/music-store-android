package com.example.musicplayer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.ResultBeanBase;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SongManageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SongListAdapter.OnSongManageEventListener {
    private static final int REQUEST_CODE_ADD=1000;
    private static final int REQUEST_CODE_EDIT=1001;

    private final List<Music> songList =new ArrayList<>();
    private SongListAdapter songListAdapter;
    private SwipeRefreshLayout swipeRefreshView;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list);
        swipeRefreshView=findViewById(R.id.swipe_refresh_view);
        recyclerView=findViewById(R.id.list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.song_manage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        swipeRefreshView.setOnRefreshListener(this);
        if (savedInstanceState!=null){
            List<Music> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                songList.addAll(list);
            }
        }else{
            loadData();
        }
        songListAdapter=new SongListAdapter(this, songList,SongListAdapter.MODE_MANAGE,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songListAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!songList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) songList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.add) {
            Intent intent=new Intent(this, SongEditActivity.class);
            this.startActivityForResult(intent,REQUEST_CODE_ADD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode== Activity.RESULT_OK) {
            if (requestCode==REQUEST_CODE_EDIT){
                Music music=data.getParcelableExtra(Constants.DATA);
                if (music!=null){
                    for(Music m:songList){
                        if (music.getId()==m.getId()){
                            m.copyFrom(music);
                            songListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }else{
                loadData();
            }
        }
    }
    @Override
    public void onRefresh() {
        loadData();
    }
    private void loadData(){
        new SongAsyncTask(this).execute();
    }

    private void onUpdateSongList(ResultBean<List<Music>> resultBean) {
        swipeRefreshView.setRefreshing(false);
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            songList.clear();
            songList.addAll(resultBean.getData());
            songListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onSongMoreButtonClick(View anchorView, Music music) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.menu_song_manage, popupMenu.getMenu());
        if (music.getRecommendIndex()>0) {
            popupMenu.getMenu().findItem(R.id.recommend).setTitle("取消推荐");
        }else{
            popupMenu.getMenu().findItem(R.id.recommend).setTitle("推荐歌曲");
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.remove){
                    onRemoveSong(music);
                }else if(item.getItemId()==R.id.recommend){
                    onRecommendSong(music);
                }
                return true;
            }
        });
    }
    private void onRemoveSong(Music music){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.remind)
                .setMessage(R.string.confirm_remove_song)
                .setPositiveButton(R.string.ok, (dialog, which) -> removeMusic(music))
                .setNegativeButton(R.string.cancel, null);
        builder.show();
    }
    private void onRecommendSong(Music music){
        if (music.getRecommendIndex()>0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.remind)
                    .setMessage(R.string.confirm_cancel_recommend)
                    .setPositiveButton(R.string.ok, (dialog, which) -> recommendSong(music))
                    .setNegativeButton(R.string.cancel, null);
            builder.show();
        }else{
            recommendSong(music);
        }
    }
    @Override
    public void onSelectSong(Music music) {
        Intent intent=new Intent(this, SongEditActivity.class);
        intent.putExtra(Constants.DATA,music);
        this.startActivityForResult(intent,REQUEST_CODE_EDIT);
    }

    private static class SongAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Music>>> {
        private final WeakReference<SongManageActivity> activityWeakReference;
        public SongAsyncTask(SongManageActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected ResultBean<List<Music>> doInBackground(Void... voids) {
            SongManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            return NetworkRequestUtils.getMusicList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Music>> resultBean) {
            super.onPostExecute(resultBean);
            SongManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onUpdateSongList(resultBean);
        }
    }
    private void removeMusic(Music music){
        new RemoveAsyncTask(this,music).execute();
    }
    private void onRemoveMusic(ResultBeanBase resultBean,Music music){
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            songList.remove(music);
            songListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class RemoveAsyncTask extends AsyncTask<Void,Void, ResultBeanBase> {
        private final WeakReference<SongManageActivity> activityWeakReference;
        private Music music;
        private ProgressDialog progressDialog;
        public RemoveAsyncTask(SongManageActivity activity, Music music){
            activityWeakReference=new WeakReference<>(activity);
            this.music=music;
            try{
                progressDialog=ProgressDialog.show(activity,"歌曲管理","正在删除，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBeanBase doInBackground(Void... voids) {
            SongManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            ResultBeanBase resultBean=NetworkRequestUtils.removeMusic(music);
            if (resultBean!=null&&resultBean.isSuccess()){

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
            SongManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onRemoveMusic(resultBean,music);
        }
    }
    private void recommendSong(Music music){
        new RecommendAsyncTask(this,music).execute();
    }
    private void onRecommendSongResult(ResultBeanBase resultBean,Music music){
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            Intent intent = new Intent(Constants.ACTION_RECOMMEND_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            for(Music m:songList){
                if (music.getId()==m.getId()){
                    m.copyFrom(music);
                    songListAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class RecommendAsyncTask extends AsyncTask<Void,Void, ResultBeanBase> {
        private final WeakReference<SongManageActivity> activityWeakReference;
        private Music music;
        private ProgressDialog progressDialog;
        public RecommendAsyncTask(SongManageActivity activity, Music music){
            activityWeakReference=new WeakReference<>(activity);
            this.music=music;
            try{
                progressDialog=ProgressDialog.show(activity,"歌曲管理",music.getRecommendIndex()>0?"正在取消推荐，请稍候...":"正在推荐，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBeanBase doInBackground(Void... voids) {
            SongManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            ResultBeanBase resultBean;
            if (music.getRecommendIndex()>0) {
                resultBean = NetworkRequestUtils.cancelMusicRecommend(music);
            }else{
                resultBean = NetworkRequestUtils.addMusicRecommend(music);
            }
            if (resultBean!=null&&resultBean.isSuccess()){
                ResultBean<Music> musicResultBean=NetworkRequestUtils.getMusicById(music.getId());
                if (musicResultBean!=null&&musicResultBean.isSuccess()){
                    this.music=musicResultBean.getData();
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
            SongManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onRecommendSongResult(resultBean,music);
        }
    }

}
