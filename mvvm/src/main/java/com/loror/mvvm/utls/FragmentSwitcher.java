package com.loror.mvvm.utls;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

/**
 * Created by Loror on 2017/11/1.
 */

public class FragmentSwitcher {

    private Fragment fragmentShow;
    private final FragmentManager manager;
    private final int parent;
    private boolean switchByReplace;

    public FragmentSwitcher(@NonNull AppCompatActivity context, @IdRes int parent) {
        this.manager = context.getSupportFragmentManager();
        this.parent = parent;
    }

    public FragmentSwitcher(@NonNull Fragment fragment, @IdRes int parent) {
        this.manager = fragment.getFragmentManager();
        this.parent = parent;
    }

    /**
     * 设置切换模式是否为replace
     */
    public void setSwitchByReplace(boolean switchByReplace) {
        this.switchByReplace = switchByReplace;
    }

    /**
     * 准备首个Fragment并显示
     */
    public void prepare(@NonNull Fragment home) {
        FragmentTransaction transaction = manager.beginTransaction();
        try {
            List<Fragment> fragments = manager.getFragments();
            if (fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    transaction.remove(fragment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (home != null && !home.isAdded()) {
            fragmentShow = home;
            transaction.add(parent, home).commit();
        } else {
            throw new IllegalStateException("home fragment is not useful");
        }
    }

    public void switchContent(@NonNull final Fragment to) {
        switchContent(null, to);
    }

    /**
     * 判断是否被add过
     * add过  隐藏当前的fragment，add下一个到Activity中
     * 否则   隐藏当前的fragment，显示下一个
     */
    public void switchContent(@Nullable Fragment from, @NonNull final Fragment to) {
        if (fragmentShow != to) {
            if (from == null) {
                from = fragmentShow;
            }
            fragmentShow = to;
            FragmentTransaction transaction = manager.beginTransaction();
            if (switchByReplace) {
                transaction.replace(parent, to).commitAllowingStateLoss();
            } else {
                if (!to.isAdded()) {
                    transaction.hide(from).add(parent, to).commitAllowingStateLoss();
                } else {
                    transaction.hide(from).show(to).commitAllowingStateLoss();
                }
            }
        }
    }
}
