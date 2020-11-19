package com.example.musicplayer.activity;

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
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.ResultBeanBase;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecommendManageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SongListAdapter.OnSongManageEventListener {
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
        toolbar.setTitle(R.string.song_recommend);
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
        return super.onOptionsItemSelected(item);
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
        popupMenu.getMenuInflater().inflate(R.menu.menu_song_recommend, popupMenu.getMenu());
        if (songList.size() <= 1) {
            popupMenu.getMenu().findItem(R.id.move_prev).setEnabled(false);
            popupMenu.getMenu().findItem(R.id.move_next).setEnabled(false);
        } else {
            int index = songList.indexOf(music);
            if (index == 0) {
                popupMenu.getMenu().findItem(R.id.move_prev).setEnabled(false);
            } else if (index == songList.size() - 1) {
                popupMenu.getMenu().findItem(R.id.move_next).setEnabled(false);
            }
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.cancel) {
                    onCancelRecommend(music);
                } else if (item.getItemId() == R.id.move_prev) {
                    int index = songList.indexOf(music);
                    if (index>0){
                        moveRecommend(music,songList.get(index-1));
                    }
                } else if (item.getItemId() == R.id.move_next) {
                    int index = songList.indexOf(music);
                    if (index<songList.size()-1){
                        moveRecommend(music,songList.get(index+1));
                    }
                }
                return true;
            }
        });
    }
    private void onCancelRecommend(Music music){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.remind)
            .setMessage(R.string.confirm_cancel_recommend)
            .setPositiveButton(R.string.ok, (dialog, which) -> cancelRecommend(music))
            .setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onSelectSong(Music music) {

    }

    private static class SongAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Music>>> {
        private final WeakReference<RecommendManageActivity> activityWeakReference;
        public SongAsyncTask(RecommendManageActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected ResultBean<List<Music>> doInBackground(Void... voids) {
            RecommendManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            return NetworkRequestUtils.getRecommendMusicList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Music>> resultBean) {
            super.onPostExecute(resultBean);
            RecommendManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onUpdateSongList(resultBean);
        }
    }

    private void cancelRecommend(Music music){
        new CancelAsyncTask(this,music).execute();
    }
    private void onCancelRecommendResult(ResultBeanBase resultBean, Music music){
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            songList.remove(music);
            songListAdapter.notifyDataSetChanged();
            Intent intent = new Intent(Constants.ACTION_RECOMMEND_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class CancelAsyncTask extends AsyncTask<Void,Void, ResultBeanBase> {
        private final WeakReference<RecommendManageActivity> activityWeakReference;
        private Music music;
        private ProgressDialog progressDialog;
        public CancelAsyncTask(RecommendManageActivity activity, Music music){
            activityWeakReference=new WeakReference<>(activity);
            this.music=music;
            try{
                progressDialog=ProgressDialog.show(activity,"歌曲管理","正在取消推荐，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBeanBase doInBackground(Void... voids) {
            RecommendManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            ResultBeanBase resultBean=NetworkRequestUtils.cancelMusicRecommend(music);
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
            RecommendManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onCancelRecommendResult(resultBean,music);
        }
    }


    private void moveRecommend(Music src,Music dst){
        new MoveAsyncTask(this,src,dst).execute();
    }
    private void onMoveRecommendResult(ResultBean<List<Music>> resultBean){
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            songList.clear();
            songList.addAll(resultBean.getData());
            songListAdapter.notifyDataSetChanged();
            Intent intent = new Intent(Constants.ACTION_RECOMMEND_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class MoveAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Music>>> {
        private final WeakReference<RecommendManageActivity> activityWeakReference;
        private Music musicSrc, musicDst;
        private ProgressDialog progressDialog;

        public MoveAsyncTask(RecommendManageActivity activity, Music musicSrc, Music musicDst) {
            activityWeakReference = new WeakReference<>(activity);
            this.musicSrc = musicSrc;
            this.musicDst = musicDst;
            try {
                progressDialog = ProgressDialog.show(activity, "歌曲管理", "正在处理，请稍候...");
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }

        @Override
        protected ResultBean<List<Music>> doInBackground(Void... voids) {
            RecommendManageActivity activity = activityWeakReference.get();
            if (activity == null) {
                return null;
            }
            ResultBean<List<Music>> resultBean = NetworkRequestUtils.moveMusicRecommend(musicSrc, musicDst);
            if (resultBean != null && resultBean.isSuccess()) {
                ResultBean<List<Music>> listResultBean = NetworkRequestUtils.getRecommendMusicList();
                if (listResultBean != null && listResultBean.isSuccess()) {
                    resultBean.setData(listResultBean.getData());
                }
            }
            return resultBean;
        }
        @Override
        protected void onPostExecute(ResultBean<List<Music>> resultBean) {
            super.onPostExecute(resultBean);
            try {
                progressDialog.dismiss();
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
            RecommendManageActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.onMoveRecommendResult(resultBean);
        }
    }
}
