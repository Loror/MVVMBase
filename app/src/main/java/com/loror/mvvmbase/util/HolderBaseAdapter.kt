package com.loror.mvvmbase.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Created by Loror on 2017/7/6.
 */
abstract class HolderBaseAdapter(val context: Context) : BaseAdapter() {
    val inflater: LayoutInflater
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @LayoutRes
    abstract fun getLayout(position: Int): Int
    abstract fun bindView(binding: ViewDataBinding, position: Int)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val binding: ViewDataBinding
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, getLayout(position), parent, false)
            view = binding.root
        } else {
            view = convertView
            binding = DataBindingUtil.getBinding(convertView)!!
        }
        bindView(binding, position)
        return view
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}