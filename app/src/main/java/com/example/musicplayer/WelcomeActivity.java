package com.example.musicplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.activity.SingerEditActivity;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ResultBean;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new WelcomeAsyncTask(this).execute();
    }
    private void startMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }
    private static class WelcomeAsyncTask extends AsyncTask<Void,Void, Void> {
        private final WeakReference<WelcomeActivity> activityWeakReference;
        public WelcomeAsyncTask(WelcomeActivity activity){
            activityWeakReference=new WeakReference<>(activity);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            WelcomeActivity activity =activityWeakReference.get();
            if (activity ==null){
                return null;
            }
            long startTimeStamp=System.currentTimeMillis();
            File fileDir=activity.getExternalCacheDir();
            File[] files=fileDir.listFiles((dir, name) -> {
                if (name.startsWith("tmp_image_")&&name.endsWith(".jpg")){
                    return true;
                }
                return false;
            });
            if (files!=null&&files.length>0){
                for(File file:files){
                    try {
                        file.delete();
                    }catch (Throwable tr){
                        tr.printStackTrace();
                    }
                }
            }
            long time=System.currentTimeMillis()-startTimeStamp;
            if (time<2000L){
                try {
                    Thread.sleep(2000L - time);
                }catch(Throwable tr){
                    tr.printStackTrace();
                }
            }
            //return NetworkRequestUtils.saveAlbum(album);
            return null;
        }

        @Override
        protected void onPostExecute(Void resultBean) {
            super.onPostExecute(resultBean);
            WelcomeActivity activity =activityWeakReference.get();
            if (activity ==null){
                return;
            }
            activity.startMainActivity();
        }
    }
}
