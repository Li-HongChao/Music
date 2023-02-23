package com.example.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;

import java.util.ArrayList;
import java.util.List;

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.Views> {
    private List<String> list = new ArrayList<>();

    public  onClick onClick;

    public interface onClick {
        void OnClick(View v, int i);
    }

    public void setOnClick(History_Adapter.onClick onClick) {
        this.onClick = onClick;
    }

    public History_Adapter(List<String> list) {
        this.list = list;
    }

    //指定子控件
    @NonNull
    @Override
    public History_Adapter.Views onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Views(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull History_Adapter.Views holder, @SuppressLint("RecyclerView") int position) {
        //初始化
        holder.tv.setText(list.get(position));
        //把单击事件接管使,用本地方法
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.OnClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //获取子控件
    public static class Views extends RecyclerView.ViewHolder {

        private final TextView tv;

        public Views(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.history_tv);
        }
    }
}
