package com.loror.mvvmbase.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.loror.mvvmbase.R
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
        //绑定viewModel
        binding.viewModel = viewModel
        //lifecycle配置监听
        lifecycle.addObserver(viewModel)
        //liveData监听
        viewModel.liveData.observe(this,
            { t -> Toast.makeText(context, t, Toast.LENGTH_SHORT).show() })
    }

    fun onButtonClick(v: View) {
        viewModel.netBaidu()
    }
}
