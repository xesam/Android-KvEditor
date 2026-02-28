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
     * MMKV内部编码了类型信息，需要通过尝试所有类型的get方法来正确读取
     */
    private Object getValueByKey(String key) {
        // MMKV不提供直接获取值类型的方法
        // 我们需要按特定顺序尝试所有类型的读取方法

        // 关键改进：优先尝试布尔类型，避免 decodeInt/decodeString 的类型混淆

        // 1. 先尝试布尔值（优先处理，避免其他类型误判）
        try {
            boolean boolValue = mmkv.decodeBool(key, Boolean.FALSE); // 使用特定值标记未检测到的情况
            if (boolValue != Boolean.FALSE) {
                // 检测到布尔值，返回
                Log.d(TAG, "Key " + key + " detected as Boolean: " + boolValue);
                return boolValue;
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }

        // 2. 尝试字符串集合
        try {
            Set<String> stringSet = mmkv.decodeStringSet(key, null);
            if (stringSet != null) {
                return stringSet;
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }

        // 3. 尝试字符串（放在后面，避免误判布尔值）
        if (mmkv.contains(key)) {
            String stringValue = mmkv.decodeString(key, null);
            // 忽略空字符串和 "true"/"false"，这些可能是布尔值
            if (stringValue != null && !stringValue.isEmpty() &&
                    !stringValue.equals("true") && !stringValue.equals("false")) {
                // 进一步验证是否真的是StringSet类型
                return detectAndReturnCorrectType(key, stringValue);
            }
        }

        // 4. 尝试整数（通过对比默认值判断）
        try {
            int intValue = mmkv.decodeInt(key, Integer.MIN_VALUE);
            // 检查是否确实存储了整数（通过对比默认值）
            // 排除 0 和 1，这些可能是布尔值
            if (mmkv.contains(key) && intValue != Integer.MIN_VALUE && intValue != 0 && intValue != 1) {
                return intValue;
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }

        // 5. 尝试长整数
        try {
            long longValue = mmkv.decodeLong(key, Long.MIN_VALUE);
            if (mmkv.contains(key) && longValue != Long.MIN_VALUE && longValue != 0L && longValue != 1L) {
                return longValue;
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }

        // 6. 尝试浮点数
        try {
            float floatValue = mmkv.decodeFloat(key, Float.NaN);
            if (mmkv.contains(key) && !Float.isNaN(floatValue)) {
                return floatValue;
            }
        } catch (Exception e) {
            // 忽略异常，继续尝试其他类型
        }

        // 如果都失败了，返回null
        Log.w(TAG, "Failed to decode value for key: " + key);
        return null;
    }

    /**
     * 检测并返回正确的类型
     * 处理MMKV可能将Set当作String读取的情况
     */
    private Object detectAndReturnCorrectType(String key, String stringValue) {
        // 如果字符串看起来像逗号分隔的列表，尝试解析为Set
        // 这是一个启发式方法，不是100%准确

        // 检查字符串是否包含逗号
        if (stringValue != null && stringValue.contains(",")) {
            try {
                // 尝试用decodeStringSet读取
                Set<String> stringSet = mmkv.decodeStringSet(key, null);
                if (stringSet != null && !stringSet.isEmpty()) {
                    Log.d(TAG, "Key " + key + " detected as StringSet: " + stringSet);
                    return stringSet;
                }
            } catch (Exception e) {
                // 不是Set类型，返回字符串
                Log.d(TAG, "Key " + key + " is String, not Set");
            }
        }

        // 默认返回字符串
        Log.d(TAG, "Key " + key + " detected as String: " + stringValue);
        return stringValue;
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
