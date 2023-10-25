package com.loror.mvvm.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.loror.mvvm.R;
import com.loror.mvvm.dialog.ProgressDialog;
import com.loror.mvvm.dialog.ProgressViewDialog;
import com.loror.mvvm.utls.ActivityUtil;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.ScreenUtil;
import com.loror.mvvm.utls.SignUtil;

import java.util.ArrayList;
import java.util.List;

public class MvvmActivity extends AppCompatActivity {

    protected Context context;
    private int requestCode = 0;

    private ProgressDialog progressViewDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setCustomDensity(this);
        ConfigUtil.config(this);
        this.context = this;
        if (!(this instanceof MvvmSignActivity)) {
            SignUtil.signConfig(this);
        }
    }

    /**
     * 亮色状态栏
     */
    protected boolean lightStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return true;
        }
        return false;
    }

    /**
     * 透明状态栏，6.0以上可同时配置使用亮色状态栏
     */
    protected boolean transparentStatus(boolean lightStatus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = this.getWindow();
            if (lightStatus) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return true;
    }

    /**
     * 显示loading弹窗
     */
    protected void showProgress() {
        showProgress(getString(R.string.loading));
    }

    /**
     * 显示loading弹窗
     */
    protected void showProgress(String message) {
        if (progressViewDialog == null) {
            ProgressDialog dialog = ConfigUtil.progressDialogForActivity(this);
            progressViewDialog = dialog != null ? dialog : new ProgressViewDialog(this);
        }
        progressViewDialog.setText(message);
        progressViewDialog.show();
    }

    /**
     * 关闭loading弹窗
     */
    protected void dismissProgress() {
        if (progressViewDialog != null) {
            progressViewDialog.dismiss();
        }
    }

    /**
     * 注销loading弹窗
     */
    protected void disposeProgress() {
        progressViewDialog = null;
    }

    /*
     * 动态申请权限
     */
    protected void requestPermissions(String... permission) {
        requestPermissions(false, permission);
    }

    /**
     * 动态申请权限
     */
    protected void requestPermission(boolean requestAnyway, String... permission) {
        requestPermissions(requestAnyway, permission);
    }

    /**
     * 动态申请权限
     */
    protected void requestPermissions(boolean requestAnyway, String[] permission) {
        List<String> requests = new ArrayList<>();
        for (String s : permission) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                // 权限申请曾经被用户拒绝
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {
                    if (requestAnyway) {
                        requests.add(s);
                    } else {
                        onPermissionsResult(s, false);
                    }
                } else {
                    requests.add(s);
                }
            } else {
                onPermissionsResult(s, true);
            }
        }
        if (requests.size() > 0) {
            // 进行权限请求
            ActivityCompat.requestPermissions(this, requests.toArray(new String[0]), requestCode);
            requestCode++;
        }
    }

    /**
     * 是否有权限
     */
    protected boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限回调
     */
    protected void onPermissionsResult(String permission, boolean success) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            boolean success = false;
            // 如果请求被拒绝，那么通常grantResults数组为空
            if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                success = true;
            }
            onPermissionsResult(permission, success);
        }
    }

    /**
     * 跳转设置界面
     */
    protected void goAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        startActivity(localIntent);
    }

    /**
     * 获取屏幕宽度像素
     */
    protected int getScreenWidth() {
        return ScreenUtil.getScreenWidth(this);
    }

    /**
     * 获取屏幕宽度像素
     */
    protected int getScreenHeight() {
        return ScreenUtil.getScreenHeight(this);
    }

}
