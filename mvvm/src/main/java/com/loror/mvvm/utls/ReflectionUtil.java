package com.loror.mvvm.utls;

import com.loror.lororUtil.text.TextUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenran
 */
public class ReflectionUtil {

    /**
     * 通过方法名获取方法对象
     *
     * @param type       源
     * @param methodName 方法名
     * @param paramCount 方法参数数量
     * @return 方法
     */
    public static Method findStaticMethodByName(Class<?> type, String methodName, int paramCount) {
        if (TextUtil.isEmpty(methodName)) {
            return null;
        }
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(methodName) && method.getParameterTypes().length == paramCount) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过方法名获取方法对象
     *
     * @param type       源
     * @param methodName 方法名
     * @param paramCount 方法参数数量
     * @return 方法
     */
    public static Method findMethodByName(Class<?> type, String methodName, int paramCount) {
        if (TextUtil.isEmpty(methodName)) {
            return null;
        }
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == paramCount) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过方法名获取方法对象
     *
     * @param type      源
     * @param fieldName 变量名
     * @return 方法
     */
    public static Field findFieldByName(Class<?> type, String fieldName) {
        if (TextUtil.isEmpty(fieldName)) {
            return null;
        }
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * 通过变量名获取getter方法
     *
     * @param type      源
     * @param fieldName 变量名
     * @return 方法
     */
    public static Method findGetterMethodByFieldName(Class<?> type, String fieldName) {
        if (TextUtil.isEmpty(fieldName)) {
            return null;
        }
        fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0) {
                if (method.getName().equals("get" + fieldName) || method.getName().equals("is" + fieldName)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获取getter方法
     *
     * @param type 源
     * @return 方法
     */
    public static Method[] findAllGetterMethod(Class<?> type) {
        Method[] methods = type.getMethods();
        List<Method> results = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0) {
                if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                    results.add(method);
                }
            }
        }
        return results.toArray(new Method[0]);
    }

    /**
     * 通过变量名获取setter方法
     *
     * @param type      源
     * @param fieldName 变量名
     * @return 方法
     */
    public static Method findSetterMethodByFieldName(Class<?> type, String fieldName) {
        if (TextUtil.isEmpty(fieldName)) {
            return null;
        }
        fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return findMethodByName(type, "set" + fieldName, 1);
    }

    /**
     * 获取setter方法
     *
     * @param type 源
     * @return 方法
     */
    public static Method[] findAllSetterMethod(Class<?> type) {
        Method[] methods = type.getMethods();
        List<Method> results = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1 && method.getName().startsWith("set")) {
                results.add(method);
            }
        }
        return results.toArray(new Method[0]);
    }

    /**
     * 获取默认值
     *
     * @param as 类型
     */
    public static Object getDefaultValue(Class<?> as) {
        if (as == String.class) {
            return "";
        } else if (as == Byte.class || as == Byte.TYPE) {
            return (byte) 0;
        } else if (as == Short.class || as == Short.TYPE) {
            return (short) 0;
        } else if (as == Integer.class || as == Integer.TYPE) {
            return 0;
        } else if (as == Long.class || as == Long.TYPE) {
            return 0L;
        } else if (as == Float.class || as == Float.TYPE) {
            return 0F;
        } else if (as == Double.class || as == Double.TYPE) {
            return 0.0;
        } else if (as == Boolean.class || as == Boolean.TYPE) {
            return false;
        }
        return null;
    }

    /**
     * 转换value类型
     *
     * @param value 数据
     * @param as    类型
     */
    public static Object convertValue(Object value, Class<?> as, String exec) {
        if (as == String.class) {
            return StringScriptUtil.exec(value.toString(), exec);
        } else if (as == Byte.class || as == Byte.TYPE) {
            return value instanceof Byte ? value : Byte.parseByte(value.toString());
        } else if (as == Short.class || as == Short.TYPE) {
            return value instanceof Short ? value : Short.parseShort(value.toString());
        } else if (as == Integer.class || as == Integer.TYPE) {
            return value instanceof Integer ? value : (value instanceof Double ? ((Double) value).intValue() : (value instanceof Float ? ((Float) value).intValue() : Integer.parseInt(value.toString())));
        } else if (as == Long.class || as == Long.TYPE) {
            return value instanceof Long ? value : (value instanceof Double ? ((Double) value).longValue() : (value instanceof Float ? ((Float) value).longValue() : Long.parseLong(value.toString())));
        } else if (as == Float.class || as == Float.TYPE) {
            return value instanceof Float ? value : (value instanceof Double ? ((Double) value).floatValue() : Float.parseFloat(value.toString()));
        } else if (as == Double.class || as == Double.TYPE) {
            return value instanceof Double ? value : (value instanceof Float ? ((Float) value).doubleValue() : Double.parseDouble(value.toString()));
        } else if (as == Boolean.class || as == Boolean.TYPE) {
            return value instanceof Boolean ? value : Boolean.parseBoolean(value.toString());
        }
        return null;
    }

    /**
     * 通过字段名搜集字段
     *
     * @param source    源
     * @param fieldName 字段
     * @return 结果
     */
    public static List<Object> collectField(List<?> source, String fieldName) {
        List<Object> result = new ArrayList<>();
        for (Object o : source) {
            result.add(getValueByField(o, fieldName));
        }
        return result;
    }

    /**
     * 通过字段名搜集字段
     *
     * @param source 源
     * @param target 目的
     * @return 结果
     */
    public static List<String> collectSameGetterField(Class<?> source, Class<?> target) {
        Method[] sourceGetter = findAllGetterMethod(source);
        Method[] targetGetter = findAllGetterMethod(target);
        List<String> fields = new ArrayList<>();
        for (Method getter : sourceGetter) {
            for (Method getter1 : targetGetter) {
                String name = getter.getName();
                if (name.equals(getter1.getName())) {
                    fields.add(name.startsWith("get") ? name.substring(3) : name.substring(2));
                    break;
                }
            }
        }
        return fields;
    }

    /**
     * 通过字段名获取值
     *
     * @param source    源
     * @param fieldName 字段
     * @return 结果
     */
    public static Object getValueByField(Object source, String fieldName) {
        if (source == null) {
            return null;
        }
        Class<?> type = source.getClass();
        try {
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(source);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否为可拆箱类型
     */
    public static boolean isPrimitive(Type type) {
        return type == Byte.class || type == Byte.TYPE || type == Short.class || type == Short.TYPE
                || type == Integer.class || type == Integer.TYPE || type == Long.class || type == Long.TYPE
                || type == Float.class || type == Float.TYPE || type == Double.class || type == Double.TYPE
                || type == Boolean.class || type == Boolean.TYPE || type == Character.class || type == Character.TYPE;
    }
}
