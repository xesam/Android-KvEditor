package io.github.xesam.android.kveditor.storage.mmkv;

import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.xesam.android.kveditor.storage.base.StorageAdapter;

/**
 * MMKV适配器实现
 * 适配MMKV功能到统一的StorageAdapter接口
 */
public class MmkvAdapter implements StorageAdapter {
    private static final String TAG = "MmkvAdapter";
    
    private Context context;
    private String name;
    private MMKV mmkv;
    
    @Override
    public void init(Context context, String name) {
        this.context = context;
        this.name = name;
        
        // 初始化MMKV
        String rootDir = MMKV.initialize(context);
        Log.d(TAG, "MMKV rootDir: " + rootDir);
        
        // 获取MMKV实例
        this.mmkv = MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>();
        String[] keys = mmkv.allKeys();
        if (keys != null) {
            for (String key : keys) {
                Object value = getValueByKey(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }
    
    /**
     * 根据键获取值
     */
    private Object getValueByKey(String key) {
        // MMKV不直接提供获取值类型的方法，我们需要尝试不同类型
        // 这里按照概率顺序尝试不同类型
        
        // 先尝试字符串
        String stringValue = getString(key, null);
        if (stringValue != null) {
            return stringValue;
        }
        
        // 尝试布尔值
        try {
            boolean hasBoolean = mmkv.containsKey(key + ".bool");
            if (hasBoolean) {
                return mmkv.getBoolean(key, false);
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }
        
        // 尝试整数
        try {
            boolean hasInt = mmkv.containsKey(key + ".int");
            if (hasInt) {
                return mmkv.getInt(key, 0);
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }
        
        // 尝试长整数
        try {
            boolean hasLong = mmkv.containsKey(key + ".long");
            if (hasLong) {
                return mmkv.getLong(key, 0L);
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }
        
        // 尝试浮点数
        try {
            boolean hasFloat = mmkv.containsKey(key + ".float");
            if (hasFloat) {
                return mmkv.getFloat(key, 0.0f);
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }
        
        // 尝试字符串集合
        try {
            Set<String> stringSet = getStringSet(key, null);
            if (stringSet != null) {
                return stringSet;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        return null;
    }
    
    @Override
    public String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }
    
    @Override
    public int getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }
    
    @Override
    public long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }
    
    @Override
    public float getFloat(String key, float defaultValue) {
        return mmkv.decodeFloat(key, defaultValue);
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }
    
    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return mmkv.decodeStringSet(key, defaultValue);
    }
    
    @Override
    public boolean putString(String key, String value) {
        return mmkv.encode(key, value);
    }
    
    @Override
    public boolean putInt(String key, int value) {
        return mmkv.encode(key, value);
    }
    
    @Override
    public boolean putLong(String key, long value) {
        return mmkv.encode(key, value);
    }
    
    @Override
    public boolean putFloat(String key, float value) {
        return mmkv.encode(key, value);
    }
    
    @Override
    public boolean putBoolean(String key, boolean value) {
        return mmkv.encode(key, value);
    }
    
    @Override
    public boolean putStringSet(String key, Set<String> value) {
        return mmkv.encode(key, value);
    }
    
    @Override
    public boolean contains(String key) {
        return mmkv.containsKey(key);
    }
    
    @Override
    public boolean remove(String key) {
        mmkv.remove(key);
        return true;
    }
    
    @Override
    public boolean clear() {
        mmkv.clearAll();
        return true;
    }
}