package com.loror.mvvm.adapter;

import android.annotation.SuppressLint;
import android.widget.AdapterView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loror.mvvm.widget.OnSafeClickListener;

import java.util.List;

public abstract class HolderBaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    private final List<T> data;
    protected AdapterView.OnItemClickListener onItemClickListener;

    public HolderBaseRecyclerAdapter(List<T> data) {
        this.data = data;
    }

    public HolderBaseRecyclerAdapter<VH, T> setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new OnSafeClickListener(300, v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(null, v, position, 0);
                }
            }));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public final void notifyDataSetChangedAnyWay() {
        notifyDataSetChanged();
    }

}

