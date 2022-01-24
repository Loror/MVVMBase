package com.loror.mvvmbase.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.loror.mvvm.adapter.HolderBaseBindingAdapter
import com.loror.mvvmbase.BR
import com.loror.mvvmbase.R

class ListAdapter(context: Context) : HolderBaseBindingAdapter<String>(context, null) {

    override fun getLayout(viewType: Int): Int {
        return R.layout.item_list
    }

    override fun onBindViewHolder(binding: ViewDataBinding, position: Int) {
        binding.setVariable(BR.listName, "item:${position}")
        binding.executePendingBindings()
    }

    override fun getCount(): Int {
        return 3
    }

}