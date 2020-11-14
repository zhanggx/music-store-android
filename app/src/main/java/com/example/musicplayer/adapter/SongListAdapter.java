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
import com.example.musicplayer.PlayActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.SingerActivity;
import com.example.musicplayer.entity.Music;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.util.Constants;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListViewHolder> implements View.OnClickListener {
    private final Activity activity;
    private final List<Music> musicList;
    private final boolean mSimpleMode;
    public SongListAdapter(Activity activity, List<Music> list){
        this(activity,list,false);
    }
    public SongListAdapter(Activity activity, List<Music> list,boolean simpleMode){
        this.activity= activity;
        this.musicList=list;
        this.mSimpleMode=simpleMode;
    }
    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SongListViewHolder holder= new SongListViewHolder(LayoutInflater.from(activity).inflate(mSimpleMode?R.layout.item_song_simple:R.layout.item_song, parent, false));
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
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onClick(View v) {
        Music music=(Music)v.getTag();
        PlayActivity.startPlayActivity(activity,music);
    }

    public static class SongListViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;
        private final TextView singerText;
        private final TextView albumText;
        private final TextView lenText;
        private final ImageView imageView;
        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameText=itemView.findViewById(R.id.name_text);
            this.singerText=itemView.findViewById(R.id.signer_text);
            this.albumText=itemView.findViewById(R.id.album_text);
            this.lenText=itemView.findViewById(R.id.len_text);
            this.imageView=itemView.findViewById(R.id.image);
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
}
