package io.github.xesam.android.kveditor.storage.base;

import android.content.Context;
import java.util.Map;
import java.util.Set;

/**
 * 通用存储适配器接口
 * 定义了所有存储类型（SharedPreference、MMKV等）的统一操作接口
 */
public interface StorageAdapter {
    
    /**
     * 初始化存储适配器
     * @param context 上下文
     * @param name 存储名称
     */
    void init(Context context, String name);
    
    /**
     * 获取存储名称
     * @return 存储名称
     */
    String getName();
    
    /**
     * 获取所有数据
     * @return 包含所有键值对的Map
     */
    Map<String, Object> getAll();
    
    /**
     * 获取字符串值
     * @param key 键
     * @param defaultValue 默认值
     * @return 字符串值
     */
    String getString(String key, String defaultValue);
    
    /**
     * 获取整数值
     * @param key 键
     * @param defaultValue 默认值
     * @return 整数值
     */
    int getInt(String key, int defaultValue);
    
    /**
     * 获取长整数值
     * @param key 键
     * @param defaultValue 默认值
     * @return 长整数值
     */
    long getLong(String key, long defaultValue);
    
    /**
     * 获取浮点数值
     * @param key 键
     * @param defaultValue 默认值
     * @return 浮点数值
     */
    float getFloat(String key, float defaultValue);
    
    /**
     * 获取布尔值
     * @param key 键
     * @param defaultValue 默认值
     * @return 布尔值
     */
    boolean getBoolean(String key, boolean defaultValue);
    
    /**
     * 获取字符串集合
     * @param key 键
     * @param defaultValue 默认值
     * @return 字符串集合
     */
    Set<String> getStringSet(String key, Set<String> defaultValue);
    
    /**
     * 存储字符串值
     * @param key 键
     * @param value 值
     * @return 是否存储成功
     */
    boolean putString(String key, String value);
    
    /**
     * 存储整数值
     * @param key 键
     * @param value 值
     * @return 是否存储成功
     */
    boolean putInt(String key, int value);
    
    /**
     * 存储长整数值
     * @param key 键
     * @param value 值
     * @return 是否存储成功
     */
    boolean putLong(String key, long value);
    
    /**
     * 存储浮点数值
     * @param key 键
     * @param value 值
     * @return 是否存储成功
     */
    boolean putFloat(String key, float value);
    
    /**
     * 存储布尔值
     * @param key 键
     * @param value 值
     * @return 是否存储成功
     */
    boolean putBoolean(String key, boolean value);
    
    /**
     * 存储字符串集合
     * @param key 键
     * @param value 值
     * @return 是否存储成功
     */
    boolean putStringSet(String key, Set<String> value);
    
    /**
     * 检查键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean contains(String key);
    
    /**
     * 移除指定的键
     * @param key 键
     * @return 是否移除成功
     */
    boolean remove(String key);
    
    /**
     * 清空所有数据
     * @return 是否清空成功
     */
    boolean clear();
}