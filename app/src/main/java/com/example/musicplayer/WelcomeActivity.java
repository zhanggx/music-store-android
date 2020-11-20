package com.example.musicplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.util.LoginUtils;

import java.io.File;
import java.lang.ref.WeakReference;

public class WelcomeActivity extends AppCompatActivity {
    private LoginUtils loginUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        loginUtils=new LoginUtils(this);
        new WelcomeAsyncTask(this).execute();
    }
    private void startMainActivity(){
        String account=loginUtils.getLoginUserAccount();
        Intent intent;
        if (TextUtils.isEmpty(account)) {
            intent = new Intent(this, LoginActivity.class);
        }else{
            intent = new Intent(this, MainActivity.class);
        }
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
