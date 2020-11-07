package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup;

import com.example.musicplayer.fragment.AlbumListFragment;
import com.example.musicplayer.fragment.CollectListFragment;
import com.example.musicplayer.fragment.RecommendListFragment;
import com.example.musicplayer.fragment.SingerListFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Fragment> fragments = new ArrayList<>();
    private String[] titles;
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
    }
    //调用菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
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
}