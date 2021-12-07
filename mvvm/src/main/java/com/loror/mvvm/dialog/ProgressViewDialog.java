package com.loror.mvvm.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.loror.lororUtil.text.TextUtil;
import com.loror.mvvm.R;
import com.loror.mvvm.core.MvvmDialog;

public class ProgressViewDialog extends MvvmDialog implements ProgressDialog{

    private TextView dialogText;
    private String text;
    private AnimationDrawable animationDrawable;

    public ProgressViewDialog(Context context) {
        super(context, R.style.ProgressDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!viewIntercept(savedInstanceState)) {
            setContentView(R.layout.dialog_progress_view);
            ImageView mProgressBar = findViewById(R.id.progress_dialog);
            dialogText = findViewById(R.id.progress_text);
            animationDrawable = new AnimationDrawable();
            int[] drawables = {R.mipmap.loading_01, R.mipmap.loading_02, R.mipmap.loading_03, R.mipmap.loading_04,
                    R.mipmap.loading_05, R.mipmap.loading_06, R.mipmap.loading_07, R.mipmap.loading_08,
                    R.mipmap.loading_09, R.mipmap.loading_10, R.mipmap.loading_11, R.mipmap.loading_12};
            for (int drawable : drawables) {
                animationDrawable.addFrame(ResourcesCompat.getDrawable(getContext().getResources(), drawable, null), 150);
            }
            animationDrawable.setOneShot(false);
            mProgressBar.setBackground(animationDrawable);
            if (!TextUtil.isEmpty(text)) {
                dialogText.setText(text);
                dialogText.setVisibility(View.VISIBLE);
            } else {
                dialogText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 界面事件拦截，返回true时拦截
     */
    protected boolean viewIntercept(Bundle savedInstanceState) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (animationDrawable != null) {
            animationDrawable.start();
        }
    }

    @Override
    protected void onStop() {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        super.onStop();
    }

    public ProgressViewDialog setText(String text) {
        this.text = text;
        if (dialogText != null) {
            if (!TextUtil.isEmpty(text)) {
                dialogText.setText(text);
                dialogText.setVisibility(View.VISIBLE);
                if (isShowing()) {
                    showAtCenter(0.6);
                }
            } else {
                dialogText.setVisibility(View.GONE);
                if (isShowing()) {
                    showAtCenter(0.3);
                }
            }
        }
        return this;
    }

    @Override
    public void show() {
        setCanceledOnTouchOutside(false);
        super.show();
        showAtCenter(TextUtil.isEmpty(text) ? 0.3 : 0.4);
    }

}
