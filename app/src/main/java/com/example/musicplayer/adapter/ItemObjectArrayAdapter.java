package com.example.musicplayer.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicplayer.R;
import com.example.musicplayer.entity.ItemObject;

import java.util.List;

public class  ItemObjectArrayAdapter<T extends ItemObject> extends ArrayAdapter<T> {
    private final Context context;
    private final List<T> list;
    public ItemObjectArrayAdapter(@NonNull Context context, @NonNull List<T> list) {
        super(context, R.layout.item_spinner, list);
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=super.getView(position, convertView, parent);

        TextView textView=(TextView)view;
        textView.setText(list.get(position).getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=super.getDropDownView(position, convertView, parent);
        TextView textView=(TextView)view;
        textView.setText(list.get(position).getName());
        return textView;
    }
}
