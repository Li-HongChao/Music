package com.example.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.entity.Music;

import java.util.List;
import java.util.Objects;

public class Music_Adapter extends RecyclerView.Adapter<Music_Adapter.views> {
    List<Music> list;
    //创建回调对象
    OnClickItem onClickItem;
    OnItemLongClickItem onItemLongClickItem;

    //声明了一个接口，接收点击事件
    public interface OnClickItem {
        void onClickItem(View v, int i);
    }

    public interface OnItemLongClickItem {
        boolean onItemLongClickItem(View view, int position);
    }

    public void setList(List<Music> list) {
        this.list = list;
    }

    public void setOnItemLongClickItem(OnItemLongClickItem onItemLongClickItem) {
        this.onItemLongClickItem = onItemLongClickItem;
    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public Music_Adapter(List<Music> list) {
        Log.e(TAG, "List: " + list);
        this.list = list;
    }

    @NonNull
    @Override
    public views onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new views(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list, parent, false));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull views holder, @SuppressLint("RecyclerView") int position) {
        //判断歌曲是否可用，是否删除选中
        if (list.get(position).getFileUrl().isEmpty()||Objects.equals(list.get(position).getType(), "delete")) {
            holder.itemLayoutMusiclist.setBackgroundResource(R.color.unEnable);
        } else {
            holder.itemLayoutMusiclist.setBackgroundResource(R.drawable.list_bg);
        }

        holder.nameTvMusiclist.setText(list.get(position).getFileName());
        holder.timeTvMusiclist.setText(list.get(position).getDuration());
        holder.singerTvMusiclist.setText(list.get(position).getSinger() + "\t|\t" + list.get(position).getAlbum());
        holder.orderTvMusicList.setText(String.valueOf(position + 1));
        holder.itemLayoutMusiclist.setOnClickListener(view -> onClickItem.onClickItem(view, position));
        holder.itemLayoutMusiclist.setOnLongClickListener(view -> onItemLongClickItem.onItemLongClickItem(view, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class views extends RecyclerView.ViewHolder {
        public TextView nameTvMusiclist;
        public TextView singerTvMusiclist;
        public TextView timeTvMusiclist;
        public RelativeLayout itemLayoutMusiclist;
        public TextView orderTvMusicList;

        public views(@NonNull View itemView) {
            super(itemView);

            orderTvMusicList = itemView.findViewById(R.id.order_tv_musiclist);
            nameTvMusiclist = itemView.findViewById(R.id.name_tv_musiclist);
            singerTvMusiclist = itemView.findViewById(R.id.singer_tv_musiclist);
            timeTvMusiclist = itemView.findViewById(R.id.time_tv_musiclist);
            itemLayoutMusiclist = itemView.findViewById(R.id.item_layout_musiclist);
        }
    }
}
