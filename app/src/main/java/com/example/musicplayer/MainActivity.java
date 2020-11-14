package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicplayer.data.MusicDataUtils;
import com.example.musicplayer.data.MusicPlayStatus;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.fragment.AlbumListFragment;
import com.example.musicplayer.fragment.CollectListFragment;
import com.example.musicplayer.fragment.RecommendListFragment;
import com.example.musicplayer.fragment.SingerListFragment;
import com.example.musicplayer.service.PlayService;
import com.example.musicplayer.util.Constants;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final List<Fragment> fragments = new ArrayList<>();
    private String[] titles;
    private View mPlayView;
    private ImageView mImageView;
    private Animation rotation;
    private MusicPlayStatus musicPlayStatus;
    private PlayBroadcastReceiver playBroadcastReceiver=new PlayBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        titles=getResources().getStringArray(R.array.title_tabs);
        fragments.add(new RecommendListFragment());
        fragments.add(new AlbumListFragment());
        fragments.add(new SingerListFragment());
        fragments.add(new CollectListFragment());
        ViewPager viewPager=findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.setAdapter(fragmentStatePagerAdapter);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        musicPlayStatus = new MusicPlayStatus(this);

        mPlayView=View.inflate(this,R.layout.view_image, null);
        mImageView=mPlayView.findViewById(R.id.image);
        mImageView.setVisibility(View.GONE);
        mImageView.setOnClickListener(this);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_img);
        rotation.setRepeatCount(Animation.INFINITE);
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_MUSIC_PLAY);
        LocalBroadcastManager.getInstance(this).registerReceiver(playBroadcastReceiver,intentFilter);

        Music music = getIntent().getParcelableExtra(Constants.DATA);
        if (music != null) {
            getIntent().removeExtra(Constants.DATA);
            PlayActivity.startPlayActivity(this, music);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Music music =  intent.getParcelableExtra(Constants.DATA);
        if (music!=null){
            PlayActivity.startPlayActivity(this, music);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicPlayStatus.getStatus()==MusicPlayStatus.STATUS_PLAYING&&PlayService.isPlayServiceRunning(this)){
            if (mImageView.getVisibility()!=View.VISIBLE) {
                mImageView.setVisibility(View.VISIBLE);
                mImageView.startAnimation(rotation);
            }
        }else{
            mImageView.setVisibility(View.GONE);
            rotation.cancel();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayStatus.getStatus()!=MusicPlayStatus.STATUS_PLAYING){
            PlayService.stopService(this);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playBroadcastReceiver);
    }
    @Override
    public void onBackPressed() {
        if (musicPlayStatus.getStatus()!=MusicPlayStatus.STATUS_PLAYING){
            this.moveTaskToBack(true);
        }else {
            super.onBackPressed();
        }
    }
    //调用菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        //mImageView.findViewById(R.id.image).startAnimation(rotation);
        menu.findItem(R.id.play_item).setActionView(mPlayView);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.play_item) {
            if (musicPlayStatus.getStatus()==MusicPlayStatus.STATUS_PLAYING){
                Music music=musicPlayStatus.getMusic();
                if (music!=null){
                    PlayActivity.startPlayActivity(this,music);
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final FragmentStatePagerAdapter fragmentStatePagerAdapter=new FragmentStatePagerAdapter(getSupportFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    };

    @Override
    public void onClick(View v) {
        if (musicPlayStatus.getStatus()==MusicPlayStatus.STATUS_PLAYING){
            Music music=musicPlayStatus.getMusic();
            if (music!=null){
                PlayActivity.startPlayActivity(this,music);
            }
        }
    }

    private class PlayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (musicPlayStatus.getStatus()==MusicPlayStatus.STATUS_PLAYING){
                if (mImageView.getVisibility()!=View.VISIBLE) {
                    mImageView.setVisibility(View.VISIBLE);
                    mImageView.startAnimation(rotation);
                }
            }else{
                mImageView.setVisibility(View.GONE);
                rotation.cancel();
            }
        }
    }

}