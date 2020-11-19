package com.example.musicplayer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AlbumArrayAdapter;
import com.example.musicplayer.databinding.ActivitySongEditBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.ResultBeanBase;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.ContextUtils;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SongEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int REQUEST_CODE_PICK=10001;
    private ActivitySongEditBinding songEditBinding;
    private Music music;

    private int albumId;
    private final List<Album> albumList=new ArrayList<>();
    private AlbumArrayAdapter albumArrayAdapter;
    private String mMusicFilePath;
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
        if (music!=null){
            if (savedInstanceState==null) {
                songEditBinding.nameText.setText(music.getName());
                songEditBinding.lengthText.setText(String.valueOf(music.getTimeLength()));
                songEditBinding.descText.setText(music.getDescription());
                String fileName=music.getFilePath();
                if (fileName!=null) {
                    int index = music.getFilePath().lastIndexOf("/");
                    if (index > 0) {
                        fileName = music.getFilePath().substring(index);
                    }
                    songEditBinding.fileText.setText(fileName);
                }
            }else{
                mMusicFilePath =savedInstanceState.getString(Constants.STATE_FILE);
            }
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
        songEditBinding.fileLayout.setOnClickListener(this);
        if (mMusicFilePath!=null) {
            File file=new File(mMusicFilePath);
            songEditBinding.fileText.setText(file.getName());
        }
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
        if(mMusicFilePath !=null){
            outState.putString(Constants.STATE_FILE, mMusicFilePath);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode== Activity.RESULT_OK&&requestCode==REQUEST_CODE_PICK) {
            Uri uri = data.getData();
            handleMusicFile(uri);
        }
    }

    private void saveData(){
        String name=songEditBinding.nameText.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"请输入名称",Toast.LENGTH_SHORT).show();
            return;
        }
        if(albumId<=0){
            Toast.makeText(this,"请选择专辑",Toast.LENGTH_SHORT).show();
            return;
        }
        String lenStr=songEditBinding.lengthText.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"请输入歌曲长度",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!TextUtils.isDigitsOnly(lenStr)){
            Toast.makeText(this,"请输入正确的歌曲长度",Toast.LENGTH_SHORT).show();
            return;
        }
        ContextUtils.hideSoftInput(this,songEditBinding.nameText);
        String description=songEditBinding.descText.getText().toString().trim();
        Music music;
        if (this.music==null) {
            music = new Music();
        }else {
            music=new Music(this.music);
        }
        music.setName(name);
        music.setAlbumId(albumId);
        music.setTimeLength(Integer.parseInt(lenStr));
        music.setDescription(description);
        File file=null;
        if (!TextUtils.isEmpty(mMusicFilePath)){
            file=new File(mMusicFilePath);
        }
        new SaveAsyncTask(this,music,file).execute();
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

    private void onSaveMusic(ResultBeanBase resultBean,Music music) {
        if (resultBean==null){
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            if (this.music!=null) {
                Intent intent=new Intent();
                intent.putExtra(Constants.DATA,music);
                this.setResult(Activity.RESULT_OK,intent);
            }else{
                this.setResult(Activity.RESULT_OK);
            }
            this.finish();
        }else{
            Toast.makeText(this, resultBean.getText(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        if (viewId==R.id.file_layout) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            this.startActivityForResult(intent, REQUEST_CODE_PICK);
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

    private static class SaveAsyncTask extends AsyncTask<Void,Void, ResultBeanBase> {
        private final WeakReference<SongEditActivity> activityWeakReference;
        private Music music;
        private ProgressDialog progressDialog;
        private final File file;
        public SaveAsyncTask(SongEditActivity activity,Music music,File file){
            activityWeakReference=new WeakReference<>(activity);
            this.music=music;
            this.file=file;
            try{
                progressDialog=ProgressDialog.show(activity,"歌曲管理","正在保存，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBeanBase doInBackground(Void... voids) {
            SongEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            if (file!=null) {
                ResultBean<String> resultBean=NetworkRequestUtils.uploadMusicFile(file);
                if (resultBean!=null&&resultBean.isSuccess()){
                    music.setFilePath(resultBean.getData());
                    music.setFileSize((int)file.length());
                }
            }
            ResultBeanBase resultBean=NetworkRequestUtils.saveMusic(music);
            if (resultBean!=null&&resultBean.isSuccess()){
                if (music.getId()>0) {
                    ResultBean<Music> musicResultBean=NetworkRequestUtils.getMusicById(music.getId());
                    if (musicResultBean!=null&&musicResultBean.isSuccess()){
                        this.music=musicResultBean.getData();
                    }
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
            SongEditActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.onSaveMusic(resultBean,music);
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
    private void handleMusicFile(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Audio.Media._ID + "=" + id;
                imagePath = getMusicFilePath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getMusicFilePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getMusicFilePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        File file=new File(imagePath);
        mMusicFilePath =imagePath;
        songEditBinding.fileText.setText(file.getName());
        //displayImage(imagePath);
    }
    private String getMusicFilePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = this.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
