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
}
