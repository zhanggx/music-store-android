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
import com.example.musicplayer.adapter.AlbumListAdapter;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumManageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AlbumListAdapter.OnAlbumManageEventListener {
    private static final int REQUEST_CODE_ADD=1000;
    private static final int REQUEST_CODE_EDIT=1001;

    private final List<Album> albumList =new ArrayList<>();
    private AlbumListAdapter albumListAdapter;
    private SwipeRefreshLayout swipeRefreshView;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list);
        swipeRefreshView=findViewById(R.id.swipe_refresh_view);
        recyclerView=findViewById(R.id.list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.album_manage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        swipeRefreshView.setOnRefreshListener(this);
        if (savedInstanceState!=null){
            List<Album> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                albumList.addAll(list);
            }
        }else{
            loadData();
        }
        albumListAdapter=new AlbumListAdapter(this, albumList,AlbumListAdapter.MODE_MANAGE,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(albumListAdapter);
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
        if (item.getItemId() == R.id.add) {
            Intent intent=new Intent(this, AlbumEditActivity.class);
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
        new AlbumAsyncTask(this).execute();
    }

    private void onUpdateAlbumList(ResultBean<List<Album>> resultBean) {
        swipeRefreshView.setRefreshing(false);
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            albumList.clear();
            albumList.addAll(resultBean.getData());
            albumListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRemoveAlbum(Album album) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.remind)
            .setMessage(R.string.confirm_remove_album)
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
    public void onSelectAlbum(Album album) {
        Intent intent=new Intent(this, AlbumEditActivity.class);
        intent.putExtra(Constants.DATA,album);
        this.startActivityForResult(intent,REQUEST_CODE_EDIT);
    }

    private static class AlbumAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Album>>> {
        private final WeakReference<AlbumManageActivity> activityWeakReference;
        public AlbumAsyncTask(AlbumManageActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected ResultBean<List<Album>> doInBackground(Void... voids) {
            AlbumManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            return NetworkRequestUtils.getAlbumList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Album>> resultBean) {
            super.onPostExecute(resultBean);
            AlbumManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onUpdateAlbumList(resultBean);
        }
    }
}
