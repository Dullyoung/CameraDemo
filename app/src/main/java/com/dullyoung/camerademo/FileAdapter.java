package com.dullyoung.camerademo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dullyoung.camerademo.databinding.ItemFileBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * @author Dullyoung   2021/9/22
 */
public class FileAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {

    public FileAdapter(@Nullable List<FileInfo> fileInfos) {
        super(R.layout.item_file,fileInfos);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FileInfo fileInfo) {
        Glide.with(holder.itemView.getContext())
                .load(fileInfo.getPath())
                .into((ImageView) holder.getView(R.id.iv_preview));
        holder.setText(R.id.tv_file_name,fileInfo.getName());

        holder.getView(R.id.btn_delete).setOnClickListener(v -> {
            OnItemChildClickListener onItemChildClickListener=getOnItemChildClickListener();
            if (onItemChildClickListener != null) {
                onItemChildClickListener.onItemChildClick(this,v,holder.getLayoutPosition());
            }
        });

    }


}
