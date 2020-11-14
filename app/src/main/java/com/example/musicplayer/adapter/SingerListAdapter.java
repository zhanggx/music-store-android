package com.example.musicplayer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.SingerActivity;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.util.Constants;

import java.util.List;

public class SingerListAdapter extends RecyclerView.Adapter<SingerListAdapter.SingerListViewHolder> implements View.OnClickListener {
    public static final int MODE_NORMAL=0;
    public static final int MODE_MANAGE=1;
    private final Activity activity;
    private final List<Singer> singerList;
    private final int mMode;
    private final OnSingerManageEventListener singerManageEventListener;
    public SingerListAdapter(Activity activity, List<Singer> list){
        this(activity,list,MODE_NORMAL,null);
    }
    public SingerListAdapter(Activity activity, List<Singer> list,int mode,OnSingerManageEventListener listener){
        this.activity= activity;
        this.singerList=list;
        this.mMode=mode;
        this.singerManageEventListener=listener;
    }
    @NonNull
    @Override
    public SingerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingerListViewHolder holder= new SingerListViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_singer, parent, false));
        holder.vwRightIcon.setVisibility(mMode==MODE_NORMAL?View.VISIBLE:View.GONE);
        holder.vwDelButton.setVisibility(mMode==MODE_MANAGE?View.VISIBLE:View.GONE);
        if (mMode==MODE_MANAGE) {
            holder.vwDelButton.setOnClickListener(this);
        }
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingerListViewHolder holder, int position) {
        Singer singer=singerList.get(position);
        holder.bind(singer);
        Glide.with(activity).load(singer.getPictureUrl()).into(holder.imageView);
        holder.itemView.setTag(singer);
        holder.vwDelButton.setTag(singer);
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }

    @Override
    public void onClick(View v) {
        Singer singer=(Singer)v.getTag();
        int id=v.getId();
        if (id==R.id.del_button){
            if (singerManageEventListener!=null){
                singerManageEventListener.onRemoveSinger(singer);
            }
        }else {
            if (mMode==MODE_MANAGE) {
                if (singerManageEventListener!=null){
                    singerManageEventListener.onSelectSinger(singer);
                }
            }else {
                Intent intent=new Intent(activity, SingerActivity.class);
                intent.putExtra(Constants.DATA, singer);
                activity.startActivity(intent);
            }
        }
    }

    public static class SingerListViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;
        private final TextView themeText;
        private final TextView descText;
        private final ImageView imageView;
        private final View vwRightIcon,vwDelButton;
        public SingerListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameText=itemView.findViewById(R.id.name_text);
            this.themeText=itemView.findViewById(R.id.theme_text);
            this.descText=itemView.findViewById(R.id.desc_text);
            this.imageView=itemView.findViewById(R.id.image);
            this.vwRightIcon=itemView.findViewById(R.id.right_icon);
            this.vwDelButton=itemView.findViewById(R.id.del_button);
        }

        public void bind(Singer singer) {
            nameText.setText(singer.getName());
            themeText.setText(singer.getBirthday());
            descText.setText(singer.getDescription());
        }
    }
    public interface OnSingerManageEventListener {
        void onRemoveSinger(Singer singer);
        void onSelectSinger(Singer singer);
    }
}
