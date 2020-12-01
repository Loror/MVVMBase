package com.loror.mvvmbase.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.loror.mvvmbase.BR
import com.loror.mvvmbase.R
import com.loror.mvvmbase.util.HolderBaseAdapter

class ListAdapter(context: Context) : HolderBaseAdapter(context) {
    override fun getLayout(position: Int): Int {
        return R.layout.item_list
    }

    override fun bindView(binding: ViewDataBinding, position: Int) {
        binding.setVariable(BR.listName, "item:${position}")
        binding.executePendingBindings()
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(i: Int): Any {
        return i
    }
}