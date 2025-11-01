package io.github.xesam.android.kveditor.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.xesam.android.kveditor.model.KvPair;

/**
 * SharedPreference管理器，负责SharedPreference的读取和写入操作
 */
public class PreferenceManager {

    private static final String DEFAULT_PREFERENCE_NAME = "default_preference";

    /**
     * 获取指定名称的SharedPreferences实例
     */
    public static SharedPreferences getPreferences(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            name = DEFAULT_PREFERENCE_NAME;
        }
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 读取指定名称的SharedPreferences中的所有键值对
     */
    public static Map<String, ?> getAll(Context context, String name) {
        return getPreferences(context, name).getAll();
    }

    /**
     * 保存键值对到SharedPreferences
     */
    public static boolean saveKvPair(Context context, String name, KvPair kvPair) {
        SharedPreferences.Editor editor = getPreferences(context, name).edit();
        String key = kvPair.getKey();
        Object value = kvPair.getValue();
        KvPair.DataType dataType = kvPair.getDataType();

        switch (dataType) {
            case STRING:
                editor.putString(key, (String) value);
                break;
            case INTEGER:
                editor.putInt(key, (Integer) value);
                break;
            case LONG:
                editor.putLong(key, (Long) value);
                break;
            case FLOAT:
                editor.putFloat(key, (Float) value);
                break;
            case BOOLEAN:
                editor.putBoolean(key, (Boolean) value);
                break;
            case STRING_SET:
                if (value instanceof Set) {
                    editor.putStringSet(key, (Set<String>) value);
                } else if (value instanceof String) {
                    // 处理字符串形式的StringSet（简化处理）
                    Set<String> stringSet = new HashSet<>();
                    stringSet.add((String) value);
                    editor.putStringSet(key, stringSet);
                }
                break;
            default:
                return false;
        }

        return editor.commit();
    }

    /**
     * 从SharedPreferences中删除指定键
     */
    public static boolean removeKey(Context context, String name, String key) {
        SharedPreferences.Editor editor = getPreferences(context, name).edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 清除指定名称的SharedPreferences中的所有数据
     */
    public static boolean clearAll(Context context, String name) {
        SharedPreferences.Editor editor = getPreferences(context, name).edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 检查SharedPreferences中是否存在指定键
     */
    public static boolean contains(Context context, String name, String key) {
        return getPreferences(context, name).contains(key);
    }

    /**
     * 根据数据类型从SharedPreferences中获取对应的值
     */
    public static Object getValueByType(Context context, String name, String key, KvPair.DataType dataType, Object defaultValue) {
        SharedPreferences preferences = getPreferences(context, name);

        switch (dataType) {
            case STRING:
                return preferences.getString(key, defaultValue != null ? (String) defaultValue : "");
            case INTEGER:
                return preferences.getInt(key, defaultValue != null ? (Integer) defaultValue : 0);
            case LONG:
                return preferences.getLong(key, defaultValue != null ? (Long) defaultValue : 0L);
            case FLOAT:
                return preferences.getFloat(key, defaultValue != null ? (Float) defaultValue : 0.0f);
            case BOOLEAN:
                return preferences.getBoolean(key, defaultValue != null ? (Boolean) defaultValue : false);
            case STRING_SET:
                return preferences.getStringSet(key, defaultValue != null ? (Set<String>) defaultValue : new HashSet<>());
            default:
                return defaultValue;
        }
    }
}