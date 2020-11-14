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
import com.example.musicplayer.adapter.SingerListAdapter;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

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
    public void onSelectSinger(Singer singer) {
        Intent intent=new Intent(this, SingerEditActivity.class);
        intent.putExtra(Constants.DATA,singer);
        this.startActivityForResult(intent,REQUEST_CODE_EDIT);
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
}
