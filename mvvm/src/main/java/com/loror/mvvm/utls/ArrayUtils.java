package com.loror.mvvm.utls;

/**
 * @author chenran
 */
public class ArrayUtils {

    /**
     * 获取元素在数组中位置
     *
     * @param array 数组
     * @param value 元素
     * @return 位置
     */
    public static int indexOf(Object[] array, Object value) {
        for (int i = 0; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断数组中内容是否一致
     *
     * @param source 数组
     * @param target 数组
     * @return 是否一致
     */
    public static boolean same(Object[] source, Object[] target) {
        if (source == null) {
            return target == null;
        }
        if (target == null) {
            return false;
        }
        if (source.length != target.length) {
            return false;
        }
        for (int i = 0; i < source.length; i++) {
            if (source[i] != target[i]) {
                return false;
            }
        }
        return true;
    }
}
