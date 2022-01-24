package com.loror.mvvm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.loror.mvvm.widget.OnSafeClickListener

abstract class HolderBaseRecyclerBindingAdapter<T>(
    val context: Context,
    var data: List<T>?
) :
    RecyclerView.Adapter<HolderBaseRecyclerBindingAdapter.BindingHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    protected var onItemClickListener: AdapterView.OnItemClickListener? = null

    open fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener?): HolderBaseRecyclerBindingAdapter<T> {
        this.onItemClickListener = onItemClickListener
        return this
    }

    @LayoutRes
    abstract fun getLayout(viewType: Int): Int

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getLayout(viewType),
            parent,
            false
        )
        return BindingHolder(binding)
    }

    @CallSuper
    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(OnSafeClickListener(
                300
            ) { v: View? ->
                onItemClickListener?.onItemClick(null, v, position, 0)
            })
        }
    }

    override fun getItemCount() = data?.size ?: 0

    open class BindingHolder(var binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root)
}