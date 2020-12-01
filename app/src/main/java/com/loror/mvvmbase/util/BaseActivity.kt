package com.loror.mvvmbase.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    protected lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        Log.e("ACTIVITY_", javaClass.simpleName)
    }

    /*
    * 动态申请权限
    */
    open fun requestPermissions(permission: Array<String?>) {
        requestPermissions(permission, false)
    }

    /**
     * 动态申请权限
     */
    open fun requestPermission(permission: String?, requestAnyway: Boolean) {
        requestPermissions(arrayOf(permission), requestAnyway)
    }

    /**
     * 动态申请权限
     */
    open fun requestPermissions(permission: Array<String?>, requestAnyway: Boolean) {
        val requests: MutableList<String?> = ArrayList()
        for (i in permission.indices) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission[i]!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 权限申请曾经被用户拒绝
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission[i]!!)) {
                    if (requestAnyway) {
                        requests.add(permission[i])
                    } else {
                        onPermissionsResult(permission[i], false)
                    }
                } else {
                    requests.add(permission[i])
                }
            } else {
                onPermissionsResult(permission[i], true)
            }
        }
        if (requests.size > 0) {
            // 进行权限请求
            ActivityCompat.requestPermissions(this, requests.toTypedArray(), requestCode)
            requestCode++
        }
    }

    /**
     * 是否有权限
     */
    open fun hasPermission(permission: String?): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 请求权限回调
     */
    open fun onPermissionsResult(permission: String?, success: Boolean) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            val permission = permissions[i]
            var success = false
            // 如果请求被拒绝，那么通常grantResults数组为空
            if (grantResults.size > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                success = true
            }
            onPermissionsResult(permission, success)
        }
    }

    fun goAppDetailSettingIntent(context: Context) {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", context.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName(
                "com.android.settings",
                "com.android.setting.InstalledAppDetails"
            )
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
        }
        startActivity(localIntent)
    }

    //扩展方法
    fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    fun onBack(v: View) {
        finish()
    }
}