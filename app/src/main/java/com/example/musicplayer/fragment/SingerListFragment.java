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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SingerListAdapter;
import com.example.musicplayer.databinding.FragmentRecyclerListBinding;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SingerListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentRecyclerListBinding fragmentRecyclerListBinding;
    private final List<Singer> singerList=new ArrayList<>();
    private SingerListAdapter singerListAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            List<Singer> list=savedInstanceState.getParcelableArrayList(Constants.LIST_DATA);
            if (list!=null){
                singerList.addAll(list);
            }
        }else{
            loadData();
        }
        singerListAdapter=new SingerListAdapter(getActivity(),singerList);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentRecyclerListBinding=FragmentRecyclerListBinding.inflate(inflater,container,false);
        fragmentRecyclerListBinding.swipeRefreshView.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        fragmentRecyclerListBinding.listView.setLayoutManager(linearLayoutManager);
        fragmentRecyclerListBinding.listView.setAdapter(singerListAdapter);
        return fragmentRecyclerListBinding.getRoot();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!singerList.isEmpty()){
            outState.putParcelableArrayList(Constants.LIST_DATA, (ArrayList<? extends Parcelable>) singerList);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
    private void loadData(){
        new MusicAsyncTask(this).execute();
    }

    private void onUpdateMusicList(ResultBean<List<Singer>> resultBean) {
        fragmentRecyclerListBinding.swipeRefreshView.setRefreshing(false);
        if (resultBean==null){
            Toast.makeText(getContext(), R.string.network_error_msg,Toast.LENGTH_SHORT).show();
        }else if (resultBean.isSuccess()){
            singerList.clear();
            singerList.addAll(resultBean.getData());
            singerListAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getContext(), resultBean.getText(),Toast.LENGTH_SHORT).show();
        }
    }
    private static class MusicAsyncTask extends AsyncTask<Void,Void,ResultBean<List<Singer>>>{
        private final WeakReference<SingerListFragment> fragmentWeakReference;
        public MusicAsyncTask(SingerListFragment singerListFragment){
            fragmentWeakReference=new WeakReference<>(singerListFragment);
        }
        @Override
        protected ResultBean<List<Singer>> doInBackground(Void... voids) {
            SingerListFragment singerListFragment=fragmentWeakReference.get();
            if (singerListFragment==null){
                return null;
            }
            return NetworkRequestUtils.getSingerList();
        }

        @Override
        protected void onPostExecute(ResultBean<List<Singer>> resultBean) {
            super.onPostExecute(resultBean);
            SingerListFragment singerListFragment=fragmentWeakReference.get();
            if (singerListFragment==null){
                return;
            }
            singerListFragment.onUpdateMusicList(resultBean);
        }
    }
}
