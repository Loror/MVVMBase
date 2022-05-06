package com.loror.mvvm.utls;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPreferenceUtil {

    private static SharedPreferences sharedPreferences;

    public static void init(@NonNull Application application) {
        if (sharedPreferences == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (sharedPreferences == null) {
                    sharedPreferences = application.getSharedPreferences("shareSaved", Context.MODE_PRIVATE);
                }
            }
        }
    }

    /**
     * 获取一个Long
     *
     * @param key 键
     * @return 返回long值，未保存返回null，错误返回{@link Long#MAX_VALUE}
     */
    public static Long getLong(String key) {
        try {
            String value = getString(key);
            if (value == null) {
                return null;
            }
            return Long.parseLong(value);
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 获取一个Boolean
     *
     * @param key 键
     * @return 返回Boolean值，未保存返回false，错误返回null
     */
    public static Boolean getBoolean(String key) {
        try {
            String value = getString(key);
            if (value == null) {
                return false;
            }
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取一个Double
     *
     * @param key 键
     * @return 返回Double值，未保存返回null，错误返回{@link Double#MAX_VALUE}
     */
    public static Double getDouble(String key) {
        try {
            String value = getString(key);
            if (value == null) {
                return null;
            }
            return Double.parseDouble(value);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    /**
     * 获取一个String
     *
     * @param key 键
     * @return 返回String值，未保存返回null
     */
    public static String getString(String key) {
        if (sharedPreferences == null) {
            return null;
        }
        return sharedPreferences.getString(key, null);
    }

    /**
     * 保存键值对
     *
     * @param key   键
     * @param value 值（可保存基本类型、字符串）
     */
    public static void save(String key, Object value) {
        save(key, value, false);
    }

    /**
     * 保存键值对
     *
     * @param key   键
     * @param value 值（可保存基本类型、字符串）
     * @param sync  是否同步执行
     */
    @SuppressLint("ApplySharedPref")
    public static void save(String key, Object value, boolean sync) {
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, String.valueOf(value));
        if (sync) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    /**
     * 移除一个数据
     *
     * @param key 键
     */
    public static void remove(String key) {
        remove(key, false);
    }

    /**
     * 移除一个数据
     *
     * @param key  键
     * @param sync 是否同步执行
     */
    @SuppressLint("ApplySharedPref")
    public static void remove(String key, boolean sync) {
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sync) {
            editor.remove(key).commit();
        } else {
            editor.remove(key).apply();
        }
    }

    /**
     * 移除所有数据
     */
    public static void clear() {
        clear(false);
    }

    /**
     * 移除所有数据
     *
     * @param sync 是否同步执行
     */
    @SuppressLint("ApplySharedPref")
    public static void clear(boolean sync) {
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sync) {
            editor.clear().commit();
        } else {
            editor.clear().apply();
        }
    }
}

