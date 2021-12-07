package com.loror.mvvmbase.view

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.loror.mvvm.annotation.LiveDataEvent
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.core.MvvmViewModel
import com.loror.mvvmbase.R
import com.loror.mvvmbase.adapter.ListAdapter
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

    @LiveDataEvent(MvvmViewModel.EVENT_SUCCESS)
    fun success(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @LiveDataEvent(MvvmViewModel.EVENT_FAILED)
    fun failed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
