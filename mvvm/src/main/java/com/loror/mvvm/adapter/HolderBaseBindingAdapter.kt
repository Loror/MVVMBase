package com.loror.mvvm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class HolderBaseBindingAdapter<T>(
    val context: Context,
    val data: List<T>?
) :
    BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @LayoutRes
    abstract fun getLayout(viewType: Int): Int

    abstract fun onBindViewHolder(binding: ViewDataBinding, position: Int)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val binding: ViewDataBinding
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, getLayout(getItemViewType(position)), parent, false)
            view = binding.root
        } else {
            view = convertView
            binding = DataBindingUtil.getBinding(convertView)!!
        }
        onBindViewHolder(binding, position)
        return view
    }

    override fun getCount(): Int {
        return data?.size ?: 0
    }

    override fun getItem(position: Int): T? {
        return data?.get(position)
    }

}