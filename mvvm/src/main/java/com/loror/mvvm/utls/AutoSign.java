package com.loror.mvvm.utls;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AutoSign {

    /**
     * 自动赋值初始值
     * 若类型为Byte、Short、Integer、Long、Float、Double、Boolean、Character且为null将赋值初始值
     * 若类型为List且为null将赋值空ArrayList，若不为空时检查item：item为以上类型且为null将赋值初始值，item非以上类型且为null将从list中移除
     */
    public static void sign(Object obj) {
        try {
            if (obj == null) {
                return;
            }
            Class<?> type = obj.getClass();
            if (type.isPrimitive() || type.isArray()) {
                return;
            }
            if (canSign(type)) {
                return;
            }

            if (obj instanceof List) {
                Iterator iterator = ((List) obj).iterator();
                while (iterator.hasNext()) {
                    Object v = iterator.next();
                    if (v == null) {
                        iterator.remove();
                    } else {
                        sign(v);
                    }
                }
            } else {
                try {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object item = field.get(obj);
                        type = field.getType();
                        if (type.isPrimitive() || type.isArray()) {
                            continue;
                        }
                        if (canSign(type)) {
                            if (item == null) {
                                field.set(obj, getDefaultValue(type));
                            }
                        } else if (type == List.class) {
                            Type genericType = field.getGenericType();
                            if (item == null) {
                                field.set(obj, new ArrayList<>());
                            }
                            if (genericType instanceof ParameterizedType) {
                                Type itemType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                                if (canSign(itemType)) {
                                    checkList((List) item, itemType);
                                    continue;
                                }
                            }
                            sign(item);
                        } else if (item != null) {
                            sign(item);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查基本类型List值
     */
    private static void checkList(List list, Type rawType) {
        for (int i = 0; i < list.size(); i++) {
            Object str = list.get(i);
            if (str == null) {
                list.set(i, getDefaultValue(rawType));
            }
        }
    }

    /**
     * 是否可赋初始值
     */
    private static boolean canSign(Type type) {
        return type == String.class || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class
                || type == Float.class || type == Double.class || type == Boolean.class || type == Character.class;
    }

    /**
     * 获取默认值
     */
    private static Object getDefaultValue(Type type) {
        if (type == String.class) {
            return "";
        } else if (type == Byte.class) {
            return (byte) 0;
        } else if (type == Short.class) {
            return (short) 0;
        } else if (type == Integer.class) {
            return 0;
        } else if (type == Long.class) {
            return 0L;
        } else if (type == Float.class) {
            return 0F;
        } else if (type == Double.class) {
            return 0.0;
        } else if (type == Boolean.class) {
            return false;
        } else if (type == Character.class) {
            return ' ';
        } else {
            return null;
        }
    }
}
