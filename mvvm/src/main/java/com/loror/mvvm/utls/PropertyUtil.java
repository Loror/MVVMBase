package com.loror.mvvm.utls;

import com.loror.lororUtil.text.TextUtil;
import com.loror.mvvm.annotation.StringScript;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author chenran
 */
public class PropertyUtil {

    /**
     * 日期、时间格式化标准
     */
    private final static String FMT_DATE_TIME_STR = "yyyy-MM-dd HH:mm:ss";
    private final static SimpleDateFormat FMT_DATE_TIME_O = new SimpleDateFormat(FMT_DATE_TIME_STR, Locale.CHINA);

    /**
     * 当前筛选
     */
    private final static ThreadLocal<Filter> CURRENT_FILTER = new ThreadLocal<>();

    /**
     * 筛选模式
     */
    public enum Mode {
        /**
         * 忽略模式
         */
        IGNORE,
        /**
         * 仅拷贝模式
         */
        ONLY
    }

    /**
     * 筛选器
     */
    private static class Filter {
        /**
         * 模式
         */
        private final Mode mode;
        /**
         * 字段
         */
        private final String[] fields;

        public Filter(Mode mode, String[] fields) {
            this.mode = mode;
            this.fields = fields;
        }
    }

    protected static Class<?> execSource;

    /**
     * 设置@StringScript查找方法class
     *
     * @param execSource obj
     */
    public static void setExecSource(Class<?> execSource) {
        PropertyUtil.execSource = execSource;
    }

    /**
     * 设置模式
     *
     * @param mode   模式
     * @param fields fields
     */
    public static void setFilter(Mode mode, String... fields) {
        if (fields != null) {
            CURRENT_FILTER.set(new Filter(mode, fields));
        }
    }

    /**
     * 拷贝对象属性，支持BaseEnum、LocalDate、LocalDateTime
     *
     * @param source 数据源
     * @param target 数据目的
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        if (source.getClass() == Object.class || target.getClass() == Object.class) {
            return;
        }
        Filter filter = CURRENT_FILTER.get();
        if (filter == null) {
            copyWithIgnore(source, target);
        } else {
            CURRENT_FILTER.remove();
            if (filter.mode == Mode.IGNORE) {
                copyWithIgnore(source, target, filter.fields);
            } else {
                copyOnly(source, target, filter.fields);
            }
        }
    }

    /**
     * 拷贝对象属性，支持BaseEnum、LocalDate、LocalDateTime
     *
     * @param source 数据源
     * @param target 数据目的
     * @param fields 忽略字段
     */
    private static void copyWithIgnore(Object source, Object target, String... fields) {
        Class<?> sourceType = source.getClass();
        Class<?> targetType = target.getClass();
        if (fields != null && fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                String ignore = fields[i];
                if (ignore.contains("_")) {
                    fields[i] = TextUtil.underlineLowercaseToHump(ignore);
                }
            }
        }
        if (source instanceof Map) {
            Map<Object, Object> sourceMap = (Map<Object, Object>) source;
            if (target instanceof Map) {
                Map<Object, Object> targetMap = (Map<Object, Object>) target;
                Set<? extends Map.Entry<Object, Object>> entrySet = sourceMap.entrySet();
                for (Map.Entry<Object, Object> item : entrySet) {
                    targetMap.put(item.getKey(), item.getValue());
                }
            } else {
                Set<? extends Map.Entry<Object, Object>> entrySet = sourceMap.entrySet();
                for (Map.Entry<Object, Object> item : entrySet) {
                    String fieldName = TextUtil.underlineLowercaseToHump(String.valueOf(item.getKey()));
                    Method method = ReflectionUtil.findSetterMethodByFieldName(targetType, fieldName);
                    if (method != null) {
                        try {
                            copyToField(item.getValue(), null, target, method);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            Method[] methods = ReflectionUtil.findAllGetterMethod(sourceType);
            for (Method sourceMethod : methods) {
                if (sourceMethod.getParameterTypes().length != 0) {
                    continue;
                }
                try {
                    sourceMethod.setAccessible(true);
                    String sourceMethodName = sourceMethod.getName();
                    if ("getClass".equals(sourceMethodName)) {
                        continue;
                    }
                    sourceMethodName = sourceMethodName.startsWith("get") ? sourceMethodName.substring(3) : sourceMethodName.substring(2);
                    if (sourceMethodName.length() == 0) {
                        continue;
                    }
                    if (fields != null && fields.length > 0) {
                        if (ArrayUtils.indexOf(fields, Character.toLowerCase(sourceMethodName.charAt(0)) + (sourceMethodName.length() > 1 ? sourceMethodName.substring(1) : "")) != -1) {
                            continue;
                        }
                    }
                    Object sourceValue = sourceMethod.invoke(source);
                    if (sourceValue != null) {
                        Method targetMethod = ReflectionUtil.findSetterMethodByFieldName(targetType, sourceMethodName);
                        if (targetMethod != null) {
                            targetMethod.setAccessible(true);
                            copyToField(sourceValue, sourceMethod, target, targetMethod);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拷贝对象属性，支持BaseEnum、LocalDate、LocalDateTime
     *
     * @param source 数据源
     * @param target 数据目的
     * @param fields 拷贝字段
     */
    private static void copyOnly(Object source, Object target, String... fields) {
        if (fields == null) {
            return;
        }
        Class<?> sourceType = source.getClass();
        Class<?> targetType = target.getClass();
        if (fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                if (field.contains("_")) {
                    fields[i] = TextUtil.underlineLowercaseToHump(field);
                }
            }
        }
        for (String fieldName : fields) {
            String upperName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method sourceMethod = ReflectionUtil.findGetterMethodByFieldName(sourceType, upperName);
            if (sourceMethod != null) {
                sourceMethod.setAccessible(true);
                Method targetMethod = ReflectionUtil.findSetterMethodByFieldName(targetType, upperName);
                if (targetMethod != null) {
                    try {
                        Object sourceValue = sourceMethod.invoke(source);
                        targetMethod.setAccessible(true);
                        copyToField(sourceValue, sourceMethod, target, targetMethod);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 拷贝到target
     *
     * @param sourceValue  数据源值
     * @param sourceMethod 数据源方法(getter)
     * @param target       数据目的
     * @param targetMethod 目的方法(setter)
     */
    protected static void copyToField(Object sourceValue, Method sourceMethod, Object target, Method targetMethod) throws InvocationTargetException, IllegalAccessException {
        if (sourceValue == null) {
            return;
        }

        Class<?> sourceValueType = sourceValue.getClass();
        Class<?> targetFieldType = targetMethod.getParameterTypes()[0];

        String exec = null;
        if (sourceMethod != null && targetFieldType == String.class) {
            String sourceName = sourceMethod.getName();
            sourceName = sourceName.startsWith("get") ? sourceName.substring(3) : sourceName.substring(2);
            sourceName = Character.toLowerCase(sourceName.charAt(0)) + sourceName.substring(1);
            Field targetField = ReflectionUtil.findFieldByName(targetMethod.getDeclaringClass(), sourceName);
            if (targetField != null) {
                StringScript stringScript = targetField.getAnnotation(StringScript.class);
                if (stringScript != null) {
                    exec = stringScript.value();
                }
            }
        }

        if (targetFieldType == sourceValueType || targetFieldType.isAssignableFrom(sourceValueType)) {
            if (targetFieldType == String.class) {
                targetMethod.invoke(target, StringScriptUtil.exec(sourceValue.toString(), exec));
            } else {
                targetMethod.invoke(target, sourceValue);
            }
        } else if (sourceValueType == Date.class) {
            if (targetFieldType == String.class) {
                targetMethod.invoke(target, StringScriptUtil.exec(((SimpleDateFormat) FMT_DATE_TIME_O.clone()).format((Date) sourceValue), exec));
            } else if (targetFieldType == Long.class || targetFieldType == Long.TYPE) {
                targetMethod.invoke(target, ((Date) sourceValue).getTime());
            }
        } else {
            Object targetValue = ReflectionUtil.convertValue(sourceValue, targetFieldType, exec);
            if (targetValue != null) {
                targetMethod.invoke(target, targetValue);
            } else {
                String fieldName = targetMethod.getName();
                Method method = ReflectionUtil.findGetterMethodByFieldName(targetMethod.getDeclaringClass(), fieldName.substring(3));
                if (method != null) {
                    method.setAccessible(true);
                    targetValue = method.invoke(target);
                    if (targetValue != null) {
                        copyProperties(sourceValue, targetValue);
                    }
                }
            }
        }
    }

    /**
     * 比较对象字段并获取compare不同字段
     * 规则：source与compare字段值不同则返回，source为null、compare不为null不返回
     *
     * @param source  数据源
     * @param compare 比较
     * @return 不同字段
     */
    public static Map<String, List<Object>> compareObjectField(Object source, Object compare) {
        if (source == null || compare == null) {
            return null;
        }
        Map<String, List<Object>> different = new HashMap<>(10);
        Class<?> sourceType = source.getClass();
        Class<?> targetType = compare.getClass();
        Method[] methods = ReflectionUtil.findAllGetterMethod(sourceType);
        for (Method sourceMethod : methods) {
            if (sourceMethod.getParameterTypes().length != 0) {
                continue;
            }
            sourceMethod.setAccessible(true);
            String sourceMethodName = sourceMethod.getName();
            if ("getClass".equals(sourceMethodName)) {
                continue;
            }
            try {
                String fieldName = sourceMethodName.startsWith("get") ? sourceMethodName.substring(3) : sourceMethodName.substring(2);
                if (fieldName.length() == 0) {
                    continue;
                }
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                Method targetMethod = targetType.getMethod(sourceMethodName);
                targetMethod.setAccessible(true);
                Object sourceValue = sourceMethod.invoke(source);
                if (sourceValue == null) {
                    continue;
                }
                Object targetValue = targetMethod.invoke(compare);
                if (sourceMethod.getReturnType() != targetMethod.getReturnType()) {
                    continue;
                }
                List<Object> change = new ArrayList<>(2);
                if (!sourceValue.equals(targetValue)) {
                    change.add(sourceValue);
                    change.add(targetValue);
                    different.put(fieldName, change);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                if (e instanceof InvocationTargetException) {
                    e.printStackTrace();
                }
            }
        }
        return different;
    }

}
