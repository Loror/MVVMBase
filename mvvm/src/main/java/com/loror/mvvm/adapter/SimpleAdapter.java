package com.loror.mvvm.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.loror.lororUtil.image.ImageUtil;

import java.util.List;

public abstract class SimpleAdapter<T> extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<T> data;

    public SimpleAdapter(@NonNull Context context, @NonNull List<T> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(getLayout(getItemViewType(position)), parent, false);
            viewHolder = new SimpleViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SimpleViewHolder) convertView.getTag();
        }
        onBindViewHolder(viewHolder, position);
        return convertView;
    }

    public abstract void onBindViewHolder(@NonNull SimpleViewHolder viewHolder, int position);

    public abstract int getLayout(int viewType);

    public static class SimpleViewHolder {

        protected final View itemView;
        private final SparseArray<View> views = new SparseArray<>();

        public SimpleViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public <T extends View> T getView(@IdRes int id) {
            return getView(id, null);
        }

        public <T extends View> T getView(@IdRes int id, OnFirstFind<T> onFirstFind) {
            T view = (T) views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                if (view != null) {
                    if (onFirstFind != null) {
                        onFirstFind.onFirstFind(view);
                    }
                    views.put(id, view);
                }
            }
            return view;
        }

        public boolean setVariable(@IdRes int id, Object value) {
            if (value != null) {
                View view = getView(id);
                if (view instanceof TextView) {
                    ((TextView) view).setText(value instanceof Integer ? view.getContext().getString((Integer) value) : value.toString());
                    return true;
                } else if (view instanceof ImageView) {
                    ImageUtil.with(view.getContext()).from(value.toString()).loadTo((ImageView) view);
                    return true;
                }
            }
            return false;
        }
    }

    public interface OnFirstFind<T extends View> {
        void onFirstFind(T view);
    }
}
