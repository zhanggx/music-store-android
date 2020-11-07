package com.example.musicplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.AlbumActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;
import com.example.musicplayer.util.ScreenUtils;


import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder> implements View.OnClickListener {
    private final Activity activity;
    private final List<Album> albumList;
    private final int imageSize;
    private final boolean mSimpleMode;
    public AlbumListAdapter(Activity activity, List<Album> list){
        this(activity,list,false);
    }
    public AlbumListAdapter(Activity activity, List<Album> list,boolean simpleMode){
        this.activity= activity;
        this.albumList=list;
        imageSize= ScreenUtils.getScreenWidth()/2-2*activity.getResources().getDimensionPixelSize(R.dimen.album_layout_padding);
        this.mSimpleMode=simpleMode;
    }
    @NonNull
    @Override
    public AlbumListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AlbumListViewHolder holder=new AlbumListViewHolder(LayoutInflater.from(activity).inflate(mSimpleMode?R.layout.item_album_simple:R.layout.item_album, parent, false));
        if (!mSimpleMode) {
            ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
            layoutParams.height = imageSize;
            layoutParams.width = imageSize;
            holder.imageView.setLayoutParams(layoutParams);
        }
        holder.mainLayout.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListViewHolder holder, int position) {
        Album album=albumList.get(position);
        holder.bind(activity,album);
        Glide.with(activity).load(album.getPictureUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    @Override
    public void onClick(View v) {
        Album album=(Album)v.getTag();
        Intent intent=new Intent(activity, AlbumActivity.class);
        intent.putExtra(Constants.DATA,album);
        activity.startActivity(intent);
    }

    public static class AlbumListViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;
        private final TextView countText;
        private final TextView descText;
        private final ImageView imageView;
        private final View mainLayout;
        public AlbumListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameText=itemView.findViewById(R.id.name_text);
            this.countText=itemView.findViewById(R.id.count_text);
            this.descText=itemView.findViewById(R.id.desc_text);
            this.imageView=itemView.findViewById(R.id.image);
            this.mainLayout=itemView.findViewById(R.id.main_layout);
        }

        public void bind(Context context, Album album) {
            nameText.setText(album.getName());
            countText.setText(context.getString(R.string.music_count_string,album.getMusicCount()));
            if (descText!=null) {
                descText.setText(album.getName());
            }
            this.mainLayout.setTag(album);
        }
    }
}
