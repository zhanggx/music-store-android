package com.example.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.data.MusicPlayStatus;
import com.example.musicplayer.databinding.FragmentRecyclerListBinding;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.service.PlayService;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecommendListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentRecyclerListBinding fragmentRecyclerListBinding;
    private final List<Music> musicList=new ArrayList<>();
    private SongListAdapter songListAdapter;
    private RecommendChangedBroadcastReceiver recommendChangedBroadcastReceiver=new RecommendChangedBroadcastReceiver();
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
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_RECOMMEND_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(recommendChangedBroadcastReceiver,intentFilter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(recommendChangedBroadcastReceiver);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!musicList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) musicList);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
    private void loadData(){
        new MusicAsyncTask(this).execute();
    }

    private void onUpdateMusicList(ResultBean<List<Music>> resultBean) {
        fragmentRecyclerListBinding.swipeRefreshView.setRefreshing(false);
        if (resultBean==null){
            Toast.makeText(getContext(), R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            musicList.clear();
            musicList.addAll(resultBean.getData());
            songListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getContext(), resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class MusicAsyncTask extends AsyncTask<Void,Void,ResultBean<List<Music>>>{
        private final WeakReference<RecommendListFragment> fragmentWeakReference;
        public MusicAsyncTask(RecommendListFragment recommendListFragment){
            fragmentWeakReference=new WeakReference<>(recommendListFragment);
        }
        @Override
        protected ResultBean<List<Music>> doInBackground(Void... voids) {
            RecommendListFragment recommendListFragment =fragmentWeakReference.get();
            if (recommendListFragment ==null){
                return null;
            }
            return NetworkRequestUtils.getRecommendMusicList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Music>> resultBean) {
            super.onPostExecute(resultBean);
            RecommendListFragment recommendListFragment =fragmentWeakReference.get();
            if (recommendListFragment ==null){
                return;
            }
            recommendListFragment.onUpdateMusicList(resultBean);
        }
    }
    private class RecommendChangedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()){
                loadData();
            }
        }
    }

}
