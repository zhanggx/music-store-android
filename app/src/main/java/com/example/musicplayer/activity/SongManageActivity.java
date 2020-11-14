package com.example.musicplayer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
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
    public void onRemoveSong(Music music) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.remind)
            .setMessage(R.string.confirm_remove_song)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    //Toast.makeText(MainActivity.this, "positive: " + which, Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(R.string.cancel, null);
        builder.show();
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
            //return NetworkRequestUtils.getMusicList();
            return null;
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
}
