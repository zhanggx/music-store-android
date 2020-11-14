package com.example.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.data.MusicDataUtils;
import com.example.musicplayer.databinding.FragmentRecyclerListBinding;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CollectListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentRecyclerListBinding fragmentRecyclerListBinding;
    private final List<Music> musicList=new ArrayList<>();
    private SongListAdapter songListAdapter;
    private CollectionBroadcastReceiver mReceiver = new CollectionBroadcastReceiver();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            List<Music> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                musicList.addAll(list);
            }
        }else{
            loadData();
        }
        songListAdapter=new SongListAdapter(getActivity(),musicList);
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_MUSIC_DATA_CHANGED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver,intentFilter);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentRecyclerListBinding=FragmentRecyclerListBinding.inflate(inflater,container,false);
        fragmentRecyclerListBinding.swipeRefreshView.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        fragmentRecyclerListBinding.listView.setLayoutManager(linearLayoutManager);
        fragmentRecyclerListBinding.listView.setAdapter(songListAdapter);
        return fragmentRecyclerListBinding.getRoot();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }


    private void loadData(){
        new MusicAsyncTask(this).execute();
    }

    private class CollectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CollectListFragment.this.isAdded()){
                loadData();
            }
        }
    }

    private void onUpdateMusicList(List<Music> list) {
        fragmentRecyclerListBinding.swipeRefreshView.setRefreshing(false);
        if (list==null){
            Toast.makeText(getContext(), R.string.load_data_error_msg,Toast.LENGTH_SHORT).show();
        }else{
            musicList.clear();
            musicList.addAll(list);
            songListAdapter.notifyDataSetChanged();
        }
    }
    private static class MusicAsyncTask extends AsyncTask<Void,Void,List<Music>> {
        private final WeakReference<CollectListFragment> fragmentWeakReference;
        public MusicAsyncTask(CollectListFragment collectListFragment){
            fragmentWeakReference=new WeakReference<>(collectListFragment);
        }
        @Override
        protected List<Music> doInBackground(Void... voids) {
            CollectListFragment collectListFragment =fragmentWeakReference.get();
            if (collectListFragment ==null){
                return null;
            }
            return MusicDataUtils.listAll(collectListFragment.getContext());
        }

        @Override
        protected void onPostExecute(List<Music> list) {
            super.onPostExecute(list);
            CollectListFragment collectListFragment =fragmentWeakReference.get();
            if (collectListFragment ==null){
                return;
            }
            collectListFragment.onUpdateMusicList(list);
        }
    }
}
