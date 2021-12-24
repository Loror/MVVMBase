package com.loror.mvvm.core;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loror.mvvm.R;
import com.loror.mvvm.dialog.ProgressDialog;
import com.loror.mvvm.dialog.ProgressViewDialog;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.SignUtil;

public class MvvmFragment extends Fragment {

    private boolean firstShow = true;
    private ProgressDialog progressViewDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigUtil.config(this);
        if (!(this instanceof MvvmSignFragment)) {
            SignUtil.signConfig(this);
        }
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
        Context context = getContext();
        if (context == null) {
            return;
        }
        if (progressViewDialog == null) {
            ProgressDialog dialog = ConfigUtil.progressDialogForFragment(this);
            progressViewDialog = dialog != null ? dialog : new ProgressViewDialog(context);
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onShow(firstShow);
            if (firstShow) {
                firstShow = false;
            }
        }
    }

    /**
     * 显示时回调
     */
    public void onShow(boolean first) {

    }
}
