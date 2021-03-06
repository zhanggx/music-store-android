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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SingerListAdapter;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBeanBase;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SingerManageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SingerListAdapter.OnSingerManageEventListener {
    private static final int REQUEST_CODE_ADD=1000;
    private static final int REQUEST_CODE_EDIT=1001;

    private final List<Singer> singerList =new ArrayList<>();
    private SingerListAdapter singerListAdapter;
    private SwipeRefreshLayout swipeRefreshView;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list);
        swipeRefreshView=findViewById(R.id.swipe_refresh_view);
        recyclerView=findViewById(R.id.list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.singer_manage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        swipeRefreshView.setOnRefreshListener(this);
        if (savedInstanceState!=null){
            List<Singer> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                singerList.addAll(list);
            }
        }else{
            loadData();
        }
        singerListAdapter=new SingerListAdapter(this, singerList,SingerListAdapter.MODE_MANAGE,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(singerListAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!singerList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) singerList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.add) {
            Intent intent=new Intent(this, SingerEditActivity.class);
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
                Singer singer=data.getParcelableExtra(Constants.DATA);
                if (singer!=null){
                    for(Singer s:singerList){
                        if (singer.getId()==s.getId()){
                            s.copyFrom(singer);
                            singerListAdapter.notifyDataSetChanged();
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
        new SingerAsyncTask(this).execute();
    }

    private void onUpdateSingerList(ResultBean<List<Singer>> resultBean) {
        swipeRefreshView.setRefreshing(false);
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            singerList.clear();
            singerList.addAll(resultBean.getData());
            singerListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRemoveSinger(Singer singer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.remind)
            .setMessage(R.string.confirm_remove_singer)
            .setPositiveButton(R.string.ok, (dialog, which) -> removeSinger(singer))
            .setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onSelectSinger(Singer singer) {
        Intent intent=new Intent(this, SingerEditActivity.class);
        intent.putExtra(Constants.DATA,singer);
        this.startActivityForResult(intent,REQUEST_CODE_EDIT);
    }
    private void removeSinger(Singer singer){
        new RemoveAsyncTask(this,singer).execute();
    }
    private void onRemoveSinger(ResultBeanBase resultBean,Singer singer){
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            singerList.remove(singer);
            singerListAdapter.notifyDataSetChanged();
            Intent intent = new Intent(Constants.ACTION_SINGER_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }

    private static class SingerAsyncTask extends AsyncTask<Void,Void, ResultBean<List<Singer>>> {
        private final WeakReference<SingerManageActivity> activityWeakReference;
        public SingerAsyncTask(SingerManageActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected ResultBean<List<Singer>> doInBackground(Void... voids) {
            SingerManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            return NetworkRequestUtils.getSingerList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Singer>> resultBean) {
            super.onPostExecute(resultBean);
            SingerManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onUpdateSingerList(resultBean);
        }
    }
    private static class RemoveAsyncTask extends AsyncTask<Void,Void, ResultBeanBase> {
        private final WeakReference<SingerManageActivity> activityWeakReference;
        private Singer singer;
        private ProgressDialog progressDialog;
        public RemoveAsyncTask(SingerManageActivity activity, Singer singer){
            activityWeakReference=new WeakReference<>(activity);
            this.singer=singer;
            try{
                progressDialog=ProgressDialog.show(activity,"歌手管理","正在删除，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBeanBase doInBackground(Void... voids) {
            SingerManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            ResultBeanBase resultBean=NetworkRequestUtils.removeSinger(singer);
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
            SingerManageActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onRemoveSinger(resultBean,singer);
        }
    }
}
