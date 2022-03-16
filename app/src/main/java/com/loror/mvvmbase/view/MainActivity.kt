package com.loror.mvvmbase.view

import android.os.Bundle
import android.widget.Toast
import com.loror.mvvm.adapter.SimpleRecyclerAdapter
import com.loror.mvvm.annotation.AllowExact
import com.loror.mvvm.annotation.LiveDataEvent
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.core.MvvmViewModel
import com.loror.mvvmbase.R
import com.loror.mvvmbase.databinding.ActivityMainBinding
import com.loror.mvvmbase.util.BaseActivity
import com.loror.mvvmbase.viewModel.MainViewModel

class MainActivity : BaseActivity() {

    @Sign
    private lateinit var binding: ActivityMainBinding

    @Sign
    private lateinit var viewModel: MainViewModel

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.adapter = object : SimpleRecyclerAdapter<String>(this, arrayListOf()) {

            override fun onBindViewHolder(viewHolder: SimpleViewHolder, position: Int) {
                viewHolder.setVariable(R.id.name, position.toString())
                viewHolder.itemView.setOnClickListener {
                    Toast.makeText(this@MainActivity, "item${position}点击", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun getLayout(viewType: Int): Int {
                return R.layout.item_list_simple
            }

            override fun getItemCount(): Int {
                return 10
            }
        }
        binding.net.setOnClickListener {
            viewModel.netBaidu()
        }
        binding.showBack.setOnClickListener {
            viewModel.showBack()
        }
    }

    @AllowExact(script = "insert:`回显次数：`,0")
    @LiveDataEvent(MainViewModel.EVENT_SHOW_BACK)
    fun show(message: String) {
        binding.showBack.text = message
    }

    @LiveDataEvent(MvvmViewModel.EVENT_SUCCESS)
    fun success(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @LiveDataEvent(MvvmViewModel.EVENT_FAILED)
    fun failed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
