package io.github.xesam.android.kveditor.example;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.xesam.android.kveditor.manager.PreferenceManager;
import io.github.xesam.android.kveditor.model.KvPair;

/**
 * 增强的数据管理器，提供多种SharedPreference文件和增删改查操作
 */
public class EnhancedDataManager {
    
    // 多个示例文件名
    public static final String USER_PREF_NAME = "user_preferences";
    public static final String APP_PREF_NAME = "app_settings";
    public static final String GAME_PREF_NAME = "game_data";
    public static final String CONFIG_PREF_NAME = "app_config";
    
    private static final Random RANDOM = new Random();
    
    /**
     * 创建用户偏好设置数据
     */
    public static void createUserPreferences(Context context) {
        PreferenceManager.saveKvPair(context, USER_PREF_NAME, 
                new KvPair("username", "john_doe", KvPair.DataType.STRING));
        
        PreferenceManager.saveKvPair(context, USER_PREF_NAME, 
                new KvPair("email", "john@example.com", KvPair.DataType.STRING));
        
        PreferenceManager.saveKvPair(context, USER_PREF_NAME, 
                new KvPair("age", 28, KvPair.DataType.INTEGER));
        
        PreferenceManager.saveKvPair(context, USER_PREF_NAME, 
                new KvPair("premium_user", true, KvPair.DataType.BOOLEAN));
        
        PreferenceManager.saveKvPair(context, USER_PREF_NAME, 
                new KvPair("login_count", 15, KvPair.DataType.INTEGER));
        
        PreferenceManager.saveKvPair(context, USER_PREF_NAME, 
                new KvPair("last_login", System.currentTimeMillis() - 86400000, KvPair.DataType.LONG));
    }
    
    /**
     * 创建应用设置数据
     */
    public static void createAppSettings(Context context) {
        PreferenceManager.saveKvPair(context, APP_PREF_NAME, 
                new KvPair("theme", "dark", KvPair.DataType.STRING));
        
        PreferenceManager.saveKvPair(context, APP_PREF_NAME, 
                new KvPair("notifications_enabled", true, KvPair.DataType.BOOLEAN));
        
        PreferenceManager.saveKvPair(context, APP_PREF_NAME, 
                new KvPair("font_size", 16.0f, KvPair.DataType.FLOAT));
        
        PreferenceManager.saveKvPair(context, APP_PREF_NAME, 
                new KvPair("cache_size", 1048576L, KvPair.DataType.LONG));
        
        PreferenceManager.saveKvPair(context, APP_PREF_NAME, 
                new KvPair("auto_sync", false, KvPair.DataType.BOOLEAN));
    }
    
    /**
     * 创建游戏数据
     */
    public static void createGameData(Context context) {
        PreferenceManager.saveKvPair(context, GAME_PREF_NAME, 
                new KvPair("player_level", 42, KvPair.DataType.INTEGER));
        
        PreferenceManager.saveKvPair(context, GAME_PREF_NAME, 
                new KvPair("player_name", "Hero123", KvPair.DataType.STRING));
        
        PreferenceManager.saveKvPair(context, GAME_PREF_NAME, 
                new KvPair("high_score", 98750, KvPair.DataType.INTEGER));
        
        PreferenceManager.saveKvPair(context, GAME_PREF_NAME, 
                new KvPair("sound_enabled", true, KvPair.DataType.BOOLEAN));
        
        PreferenceManager.saveKvPair(context, GAME_PREF_NAME, 
                new KvPair("music_volume", 0.8f, KvPair.DataType.FLOAT));
        
        PreferenceManager.saveKvPair(context, GAME_PREF_NAME, 
                new KvPair("total_play_time", 3600000L, KvPair.DataType.LONG));
    }
    
    /**
     * 创建应用配置数据
     */
    public static void createAppConfig(Context context) {
        PreferenceManager.saveKvPair(context, CONFIG_PREF_NAME, 
                new KvPair("api_endpoint", "https://api.example.com", KvPair.DataType.STRING));
        
        PreferenceManager.saveKvPair(context, CONFIG_PREF_NAME, 
                new KvPair("api_version", "v2.0", KvPair.DataType.STRING));
        
        PreferenceManager.saveKvPair(context, CONFIG_PREF_NAME, 
                new KvPair("debug_mode", false, KvPair.DataType.BOOLEAN));
        
        PreferenceManager.saveKvPair(context, CONFIG_PREF_NAME, 
                new KvPair("request_timeout", 30, KvPair.DataType.INTEGER));
        
        PreferenceManager.saveKvPair(context, CONFIG_PREF_NAME, 
                new KvPair("retry_count", 3, KvPair.DataType.INTEGER));
    }
    
    /**
     * 添加随机数据到指定文件
     */
    public static void addRandomData(Context context, String prefName) {
        String key = "random_" + System.currentTimeMillis();
        Object value;
        KvPair.DataType type;
        
        int typeIndex = RANDOM.nextInt(6);
        switch (typeIndex) {
            case 0:
                value = "RandomString_" + RANDOM.nextInt(1000);
                type = KvPair.DataType.STRING;
                break;
            case 1:
                value = RANDOM.nextInt(1000);
                type = KvPair.DataType.INTEGER;
                break;
            case 2:
                value = RANDOM.nextBoolean();
                type = KvPair.DataType.BOOLEAN;
                break;
            case 3:
                value = RANDOM.nextFloat() * 100;
                type = KvPair.DataType.FLOAT;
                break;
            case 4:
                value = RANDOM.nextLong();
                type = KvPair.DataType.LONG;
                break;
            default:
                value = "DefaultString";
                type = KvPair.DataType.STRING;
                break;
        }
        
        PreferenceManager.saveKvPair(context, prefName, new KvPair(key, value, type));
    }
    
    /**
     * 更新现有数据
     */
    public static void updateExistingData(Context context, String prefName, String key) {
        Map<String, ?> allData = PreferenceManager.getAll(context, prefName);
        if (allData.containsKey(key)) {
            Object currentValue = allData.get(key);
            
            // 根据当前值类型生成新值
            if (currentValue instanceof String) {
                String newValue = currentValue + "_updated";
                PreferenceManager.saveKvPair(context, prefName, 
                        new KvPair(key, newValue, KvPair.DataType.STRING));
            } else if (currentValue instanceof Integer) {
                Integer newValue = (Integer) currentValue + 1;
                PreferenceManager.saveKvPair(context, prefName, 
                        new KvPair(key, newValue, KvPair.DataType.INTEGER));
            } else if (currentValue instanceof Boolean) {
                Boolean newValue = !(Boolean) currentValue;
                PreferenceManager.saveKvPair(context, prefName, 
                        new KvPair(key, newValue, KvPair.DataType.BOOLEAN));
            } else if (currentValue instanceof Float) {
                Float newValue = (Float) currentValue + 0.1f;
                PreferenceManager.saveKvPair(context, prefName, 
                        new KvPair(key, newValue, KvPair.DataType.FLOAT));
            } else if (currentValue instanceof Long) {
                Long newValue = (Long) currentValue + 1;
                PreferenceManager.saveKvPair(context, prefName, 
                        new KvPair(key, newValue, KvPair.DataType.LONG));
            }
        }
    }
    
    /**
     * 删除指定键的数据
     */
    public static void deleteData(Context context, String prefName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }
    
    /**
     * 清空指定文件的所有数据
     */
    public static void clearAllData(Context context, String prefName) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * 获取所有数据用于显示
     */
    public static Map<String, Object> getAllData(Context context, String prefName) {
        Map<String, ?> prefsData = PreferenceManager.getAll(context, prefName);
        Map<String, Object> result = new HashMap<>();
        result.putAll(prefsData);
        return result;
    }
    
    /**
     * 检查指定文件是否存在数据
     */
    public static boolean hasData(Context context, String prefName) {
        return !PreferenceManager.getAll(context, prefName).isEmpty();
    }
}