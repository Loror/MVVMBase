package com.loror.mvvmbase.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.loror.mvvm.core.MvvmSignActivity

abstract class BaseActivity : MvvmSignActivity() {

    protected lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        Log.e("ACTIVITY_", javaClass.simpleName)
    }

    //扩展方法
    fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    fun onBack(v: View) {
        finish()
    }
}