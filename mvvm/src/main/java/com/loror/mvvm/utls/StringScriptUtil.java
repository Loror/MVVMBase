package com.loror.mvvm.utls;

import com.loror.lororUtil.text.TextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenran
 */
public class StringScriptUtil {

    /**
     * 字符串预处理
     *
     * @param source 数据源
     * @param exec   执行语句
     * @return 执行结果
     */
    public static String exec(String source, String exec) {
        if (TextUtil.isEmpty(exec)) {
            return source;
        }
        return exec(splitData(exec, ';', true), 0, source);
    }

    /**
     * 字符串预处理
     *
     * @param source 数据源
     * @param words  词法
     * @param index  词位
     * @return 执行结果
     */
    private static String exec(String[] words, int index, String source) {
        if (index < words.length) {
            String exec = words[index];
            int cut = exec.indexOf(":");
            if (cut != -1) {
                String cmd = exec.substring(0, cut).trim();
                if (source == null && !"cover".equals(cmd)) {
                    return null;
                }
                String[] data = splitData(exec.substring(cut + 1), ',', false);
                switch (cmd) {
                    case "cover": {
                        if (data.length == 1) {
                            return exec(words, index + 1, data[0]);
                        }
                    }
                    break;
                    case "split": {
                        if (data.length == 2 && TextUtil.isNumber(data[1])) {
                            String[] result = source.split(data[0]);
                            return exec(words, index + 1, result[Integer.parseInt(data[1])]);
                        }
                    }
                    break;
                    case "sub":
                        if (data.length == 2 && TextUtil.isNumber(data[0]) && TextUtil.isNumber(data[1])) {
                            int start = Integer.parseInt(data[0]);
                            int end = Integer.parseInt(data[1]);
                            start = start < 0 ? (source.length() + start) : start;
                            end = end < 0 ? (source.length() + end) : end;
                            return exec(words, index + 1, source.substring(start, end));
                        }
                        break;
                    case "replace":
                        if (data.length == 2) {
                            return exec(words, index + 1, source.replace(data[0], data[1]));
                        }
                        break;
                    case "add":
                        if (data.length == 1) {
                            return exec(words, index + 1, source + data[0]);
                        }
                        break;
                    case "insert":
                        if (data.length == 2 && TextUtil.isNumber(data[1])) {
                            int where = Integer.parseInt(data[1]);
                            where = where < 0 ? (source.length() - where) : where;
                            return exec(words, index + 1, where == 0 ? (data[0] + source) :
                                    (where >= data.length ? (source + data[0]) : (source.substring(0, where + 1) + data[0] + source.substring(where + 1))));
                        }
                        break;
                    case "remove":
                        if (data.length == 2 && TextUtil.isNumber(data[0]) && TextUtil.isNumber(data[1])) {
                            int start = Integer.parseInt(data[0]);
                            int end = Integer.parseInt(data[1]);
                            start = start < 0 ? (source.length() + start) : start;
                            end = end < 0 ? (source.length() + end) : end;
                            return exec(words, index + 1, start == 0 ? source.substring(end) :
                                    (end == source.length() ? source.substring(0, start) : (source.substring(0, start) + source.substring(end))));
                        }
                        break;
                    case "func":
                        if (data.length == 1 || data.length == 2) {
                            Class<?> type = PropertyUtil.execSource;
                            if (data.length == 2) {
                                try {
                                    type = Class.forName(data[1]);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (type != null) {
                                Method method = ReflectionUtil.findStaticMethodByName(type, data[0], 1);
                                if (method != null && method.getParameterTypes()[0] == String.class) {
                                    method.setAccessible(true);
                                    try {
                                        Object result = method.invoke(type, source);
                                        return exec(words, index + 1, result == null ? source : result.toString());
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            return exec(words, index + 1, source);
        }
        return source;
    }

    /**
     * 拆分数据,example:` `,1
     *
     * @param keep 保留`
     */
    private static String[] splitData(String data, char split, boolean keep) {
        if (data.length() == 0) {
            return new String[]{};
        }
        List<String> list = new ArrayList<>(3);
        String item = null;
        boolean tag = false;
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (i == data.length() - 1) {
                if (item == null) {
                    item = "";
                }
                if (tag && '`' == c && !keep) {
                    list.add(item);
                } else {
                    list.add(item + c);
                }
                continue;
            } else if ('`' == c) {
                if (!keep) {
                    if (!tag) {
                        tag = true;
                        continue;
                    } else {
                        tag = false;
                        list.add(item);
                        item = null;
                        continue;
                    }
                } else {
                    tag = !tag;
                }
            } else if (split == c) {
                if (!tag) {
                    if (item != null) {
                        list.add(item);
                        item = null;
                    }
                    continue;
                }
            }
            item = (item == null ? "" : item) + c;
        }
        return list.toArray(new String[0]);
    }
}
