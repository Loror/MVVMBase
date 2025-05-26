package com.loror.mvvmbase.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.loror.mvvm.annotation.LiveDataEvent
import com.loror.mvvm.core.MvvmFragment
import com.loror.mvvmbase.databinding.FragmentMainBinding
import com.loror.mvvmbase.viewModel.MainViewModel

class MainFragment : MvvmFragment() {

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.attachView(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMainBinding.inflate(inflater, container, false).root
    }

    @LiveDataEvent(MainViewModel.EVENT_CROSS)
    fun success(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}