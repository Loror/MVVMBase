package com.loror.mvvmbase.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.loror.mvvm.annotation.LiveDataEvent
import com.loror.mvvm.core.BaseViewModel
import com.loror.mvvmbase.R
import com.loror.mvvmbase.adapter.ListAdapter
import com.loror.mvvmbase.databinding.ActivityMainBinding
import com.loror.mvvmbase.util.BaseActivity
import com.loror.mvvmbase.viewModel.MainViewModel

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel//绑定viewModel
        viewModel.attachView(this)
        viewModel.listenLifeCycle(this)
        initView()
    }

    private fun initView() {
        binding.adapter = ListAdapter(this)//绑定adapter
        binding.net.setOnClickListener {
            viewModel.netBaidu()
        }
        binding.showBack.setOnClickListener {
            viewModel.showBack()
        }
    }

    @LiveDataEvent(MainViewModel.EVENT_SHOW_BACK)
    fun show(message: String) {
        binding.showBack.text = message
    }

    @LiveDataEvent(BaseViewModel.EVENT_SUCCESS)
    fun success(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @LiveDataEvent(BaseViewModel.EVENT_FAILED)
    fun failed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
