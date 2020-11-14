package com.example.musicplayer.activity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityAlbumEditBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SongEditActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAlbumEditBinding albumEditBinding;
    private Album album;
    private LocalDate mMusicDate;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumEditBinding = ActivityAlbumEditBinding.inflate(getLayoutInflater());
        setContentView(albumEditBinding.getRoot());
        album=getIntent().getParcelableExtra(Constants.DATA);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_album);
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
            long date=savedInstanceState.getLong(Constants.STATE,0L);
            mMusicDate =LocalDate.ofEpochDay(date);
        }else{
            if (album!=null){

            }else{

            }
            mMusicDate =LocalDate.now();
        }
        albumEditBinding.publishTimeText.setText(mMusicDate.format(fmt));
        albumEditBinding.publishTimeLayout.setOnClickListener(this);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.STATE, mMusicDate.toEpochDay());
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

        }
    }


    private static class SaveAsyncTask extends AsyncTask<Void,Void, ResultBean> {
        private final WeakReference<SongEditActivity> activityWeakReference;
        private final Album album;
        public SaveAsyncTask(SongEditActivity activity, Album album){
            activityWeakReference=new WeakReference<>(activity);
            this.album=album;
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
}
