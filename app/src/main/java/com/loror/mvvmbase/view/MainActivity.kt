package com.loror.mvvmbase.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.loror.mvvm.adapter.SimpleRecyclerAdapter
import com.loror.mvvm.annotation.LiveDataEvent
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.core.MvvmViewModel
import com.loror.mvvmbase.R
import com.loror.mvvmbase.common.base.BaseActivity
import com.loror.mvvmbase.databinding.ActivityMainBinding
import com.loror.mvvmbase.databinding.ActivityMainTestBinding
import com.loror.mvvmbase.model.MainModel
import com.loror.mvvmbase.viewModel.MainViewModel

class MainActivity : BaseActivity() {

    @Sign
    private lateinit var binding: ActivityMainBinding

    @Sign
    private lateinit var bindingTest: ActivityMainTestBinding

    @Sign
    private lateinit var model: MainModel

    @Sign
    private lateinit var viewModel: MainViewModel

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.model = model
        model.text1.set("123")
        model.text2.setText2("456")
        bindingTest.text.text = "test"
        binding.list.adapter = Adapter(this)
        binding.net.setOnClickListener {
            viewModel.netBaidu()
        }
        binding.showBack.setOnClickListener {
            viewModel.showBack()
        }
    }

    @LiveDataEvent(MvvmViewModel.EVENT_SUCCESS)
    fun success(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @LiveDataEvent(MvvmViewModel.EVENT_FAILED)
    fun failed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    internal class Adapter(private val context: Context) :
        SimpleRecyclerAdapter<String>(context, arrayListOf()) {

        override fun onBindViewHolder(viewHolder: SimpleViewHolder, position: Int) {
            viewHolder.setVariable(R.id.name, position.toString())
            viewHolder.itemView.setOnClickListener {
                Toast.makeText(context, "item${position}点击", Toast.LENGTH_SHORT)
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
}
