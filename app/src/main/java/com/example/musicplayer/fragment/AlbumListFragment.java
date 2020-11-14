package com.example.musicplayer.fragment;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AlbumListAdapter;
import com.example.musicplayer.databinding.FragmentRecyclerListBinding;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentRecyclerListBinding fragmentRecyclerListBinding;
    private final List<Album> albumList=new ArrayList<>();
    private AlbumListAdapter albumListAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            List<Album> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                albumList.addAll(list);
            }
        }else{
            loadData();
        }
        albumListAdapter=new AlbumListAdapter(getActivity(),albumList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentRecyclerListBinding=FragmentRecyclerListBinding.inflate(inflater,container,false);
        fragmentRecyclerListBinding.swipeRefreshView.setBackgroundColor(getResources().getColor(R.color.list_background));
        fragmentRecyclerListBinding.swipeRefreshView.setOnRefreshListener(this);
        fragmentRecyclerListBinding.listView.setLayoutManager(new GridLayoutManager(getContext(),2));

        fragmentRecyclerListBinding.listView.setAdapter(albumListAdapter);
        return fragmentRecyclerListBinding.getRoot();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!albumList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) albumList);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
    private void loadData(){
        new MusicAsyncTask(this).execute();
    }

    private void onUpdateMusicList(ResultBean<List<Album>> resultBean) {
        fragmentRecyclerListBinding.swipeRefreshView.setRefreshing(false);
        if (resultBean==null){
            Toast.makeText(getContext(), R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            albumList.clear();
            albumList.addAll(resultBean.getData());
            albumListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getContext(), resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class MusicAsyncTask extends AsyncTask<Void,Void,ResultBean<List<Album>>>{
        private final WeakReference<AlbumListFragment> fragmentWeakReference;
        public MusicAsyncTask(AlbumListFragment albumListFragment){
            fragmentWeakReference=new WeakReference<>(albumListFragment);
        }
        @Override
        protected ResultBean<List<Album>> doInBackground(Void... voids) {
            AlbumListFragment albumListFragment=fragmentWeakReference.get();
            if (albumListFragment==null){
                return null;
            }
            return NetworkRequestUtils.getAlbumList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Album>> resultBean) {
            super.onPostExecute(resultBean);
            AlbumListFragment albumListFragment=fragmentWeakReference.get();
            if (albumListFragment==null){
                return;
            }
            albumListFragment.onUpdateMusicList(resultBean);
        }
    }
}
