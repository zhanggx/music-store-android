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
import com.example.musicplayer.activity.AlbumActivity;
import com.example.musicplayer.activity.PlayActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.util.Constants;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListViewHolder> implements View.OnClickListener {
    public static final int MODE_NORMAL=0;
    public static final int MODE_SIMPLE=1;
    public static final int MODE_MANAGE=2;
    private final Activity activity;
    private final List<Music> musicList;
    private final int mMode;
    private final OnSongManageEventListener songManageEventListener;
    public SongListAdapter(Activity activity, List<Music> list){
        this(activity,list,MODE_NORMAL,null);
    }
    public SongListAdapter(Activity activity, List<Music> list,int mode){
        this(activity,list,mode,null);
    }
    public SongListAdapter(Activity activity, List<Music> list,int mode, OnSongManageEventListener listener){
        this.activity= activity;
        this.musicList=list;
        this.mMode=mode;
        this.songManageEventListener=listener;
    }
    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SongListViewHolder holder= new SongListViewHolder(LayoutInflater.from(activity).inflate(mMode==MODE_SIMPLE?R.layout.item_song_simple:R.layout.item_song, parent, false));
        if (mMode!=MODE_SIMPLE) {
            holder.vwRightIcon.setVisibility(mMode==MODE_NORMAL?View.VISIBLE:View.GONE);
            holder.vwMenuButton.setVisibility(mMode==MODE_MANAGE?View.VISIBLE:View.GONE);
        }
        if (mMode==MODE_MANAGE) {
            holder.vwMenuButton.setOnClickListener(this);
        }
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int position) {
        Music music=musicList.get(position);
        holder.bind(music);
        if (holder.imageView!=null) {
            Glide.with(activity).load(music.getAlbumPictureUrl()).into(holder.imageView);
        }
        holder.itemView.setTag(music);
        if (holder.vwMenuButton!=null) {
            holder.vwMenuButton.setTag(music);
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onClick(View v) {
        Music music=(Music)v.getTag();

        int id=v.getId();
        if (id==R.id.menu_button){
            if (songManageEventListener!=null){
                songManageEventListener.onSongMoreButtonClick(v,music);
            }
        }else {
            if (mMode==MODE_MANAGE) {
                if (songManageEventListener!=null){
                    songManageEventListener.onSelectSong(music);
                }
            }else {
                PlayActivity.startPlayActivity(activity,music);
            }
        }
    }

    public static class SongListViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;
        private final TextView singerText;
        private final TextView albumText;
        private final TextView lenText;
        private final ImageView imageView;
        private final View vwRightIcon,vwMenuButton;
        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameText=itemView.findViewById(R.id.name_text);
            this.singerText=itemView.findViewById(R.id.signer_text);
            this.albumText=itemView.findViewById(R.id.album_text);
            this.lenText=itemView.findViewById(R.id.len_text);
            this.imageView=itemView.findViewById(R.id.image);
            this.vwRightIcon=itemView.findViewById(R.id.right_icon);
            this.vwMenuButton=itemView.findViewById(R.id.menu_button);
        }

        public void bind(Music music) {
            nameText.setText(music.getName());
            if (singerText!=null) {
                singerText.setText(music.getSingerName());
            }
            if (albumText!=null) {
                albumText.setText(music.getAlbumName());
            }
            lenText.setText(music.getTimeLengthText());
        }
    }
    public interface OnSongManageEventListener {
        void onSongMoreButtonClick(View anchorView,Music music);
        void onSelectSong(Music music);
    }
}
