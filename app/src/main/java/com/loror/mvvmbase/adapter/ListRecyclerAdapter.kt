package com.loror.mvvmbase.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.loror.mvvm.adapter.HolderBaseBindingAdapter
import com.loror.mvvm.adapter.HolderBaseRecyclerBindingAdapter
import com.loror.mvvmbase.BR
import com.loror.mvvmbase.R

class ListRecyclerAdapter(context: Context) :
    HolderBaseRecyclerBindingAdapter<String>(context, null) {

    override fun getLayout(viewType: Int): Int {
        return R.layout.item_list
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.listName, "item:${position}")
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return 3
    }

}