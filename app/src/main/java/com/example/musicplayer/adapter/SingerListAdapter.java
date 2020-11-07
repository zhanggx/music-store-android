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
import com.example.musicplayer.AlbumActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.SingerActivity;
import com.example.musicplayer.entity.Album;
import com.example.musicplayer.entity.Singer;
import com.example.musicplayer.util.Constants;

import java.util.List;

public class SingerListAdapter extends RecyclerView.Adapter<SingerListAdapter.SingerListViewHolder> implements View.OnClickListener {
    private final Activity activity;
    private final List<Singer> singerList;
    public SingerListAdapter(Activity activity, List<Singer> list){
        this.activity= activity;
        this.singerList=list;
    }
    @NonNull
    @Override
    public SingerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingerListViewHolder holder= new SingerListViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_singer, parent, false));
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingerListViewHolder holder, int position) {
        Singer singer=singerList.get(position);
        holder.bind(singer);
        Glide.with(activity).load(singer.getPictureUrl()).into(holder.imageView);
        holder.itemView.setTag(singer);
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }

    @Override
    public void onClick(View v) {
        Singer singer=(Singer)v.getTag();
        Intent intent=new Intent(activity, SingerActivity.class);
        intent.putExtra(Constants.DATA,singer);
        activity.startActivity(intent);
    }

    public static class SingerListViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;
        private final TextView themeText;
        private final TextView descText;
        private final ImageView imageView;
        public SingerListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameText=itemView.findViewById(R.id.name_text);
            this.themeText=itemView.findViewById(R.id.theme_text);
            this.descText=itemView.findViewById(R.id.desc_text);
            this.imageView=itemView.findViewById(R.id.image);
        }

        public void bind(Singer singer) {
            nameText.setText(singer.getName());
            themeText.setText(singer.getBirthday());
            descText.setText(singer.getDescription());

        }
    }
}
