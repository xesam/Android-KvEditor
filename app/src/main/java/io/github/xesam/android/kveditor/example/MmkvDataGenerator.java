package io.github.xesam.android.kveditor.example;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.xesam.android.kveditor.storage.base.StorageManager;
import io.github.xesam.android.kveditor.storage.base.StorageType;
import io.github.xesam.android.kveditor.model.KvPair;

/**
 * MMKV数据生成器，用于创建演示用的MMKV文件
 */
public class MmkvDataGenerator {
    
    // MMKV示例文件名
    public static final String MMKV_USER_DATA = "mmkv_user_data";
    public static final String MMKV_APP_CACHE = "mmkv_app_cache";
    public static final String MMKV_GAME_PROGRESS = "mmkv_game_progress";
    public static final String MMKV_SYSTEM_CONFIG = "mmkv_system_config";
    
    /**
     * 初始化MMKV库
     */
    public static void initializeMmkv(Context context) {
        String rootDir = MMKV.initialize(context);
        System.out.println("MMKV initialized, rootDir: " + rootDir);
    }
    
    /**
     * 创建用户数据MMKV文件
     */
    public static void createUserDataMmkv(Context context) {
        StorageManager storageManager = StorageManager.getInstance(context);
        
        // 使用StorageManager获取MMKV适配器
        try {
            // 创建String类型数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putString("user_id", "user_12345");
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putString("nickname", "超级用户");
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putString("avatar_url", "https://example.com/avatar.jpg");
            
            // 创建Integer类型数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putInt("login_count", 128);
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putInt("level", 15);
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putInt("experience", 3250);
            
            // 创建Boolean类型数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putBoolean("is_vip", true);
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putBoolean("email_verified", true);
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putBoolean("phone_bound", false);
            
            // 创建Float类型数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putFloat("account_balance", 1234.56f);
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putFloat("rating", 4.8f);
            
            // 创建Long类型数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putLong("register_time", System.currentTimeMillis() - 86400000L * 30);
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putLong("last_login_time", System.currentTimeMillis() - 3600000L);
            
            // 创建StringSet类型数据
            Set<String> permissions = new HashSet<>();
            permissions.add("read");
            permissions.add("write");
            permissions.add("admin");
            storageManager.getAdapter(StorageType.MMKV, MMKV_USER_DATA).putStringSet("permissions", permissions);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 创建应用缓存MMKV文件
     */
    public static void createAppCacheMmkv(Context context) {
        StorageManager storageManager = StorageManager.getInstance(context);
        
        try {
            // 缓存相关String数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putString("last_api_response", "{\"status\":\"success\",\"data\":{}}");
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putString("theme_mode", "dark");
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putString("language", "zh_CN");
            
            // 缓存相关Integer数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putInt("cache_version", 2);
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putInt("api_call_count", 456);
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putInt("image_cache_size", 1024);
            
            // 缓存相关Boolean数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putBoolean("is_cache_enabled", true);
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putBoolean("auto_refresh", false);
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putBoolean("wifi_only", true);
            
            // 缓存相关Float数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putFloat("cache_hit_rate", 0.85f);
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putFloat("compression_ratio", 0.7f);
            
            // 缓存相关Long数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putLong("cache_expire_time", System.currentTimeMillis() + 7200000L);
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putLong("last_cleanup_time", System.currentTimeMillis() - 86400000L);
            
            // 缓存相关StringSet数据
            Set<String> cachedUrls = new HashSet<>();
            cachedUrls.add("https://api1.example.com/data");
            cachedUrls.add("https://api2.example.com/info");
            cachedUrls.add("https://cdn.example.com/images");
            storageManager.getAdapter(StorageType.MMKV, MMKV_APP_CACHE).putStringSet("cached_urls", cachedUrls);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 创建游戏进度MMKV文件
     */
    public static void createGameProgressMmkv(Context context) {
        StorageManager storageManager = StorageManager.getInstance(context);
        
        try {
            // 游戏进度String数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putString("current_level", "level_3_5");
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putString("player_name", "勇者");
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putString("save_slot", "slot_1");
            
            // 游戏进度Integer数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putInt("score", 98765);
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putInt("coins", 1250);
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putInt("lives", 3);
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putInt("level_number", 15);
            
            // 游戏进度Boolean数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putBoolean("is_boss_defeated", true);
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putBoolean("has_secret_key", false);
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putBoolean("tutorial_completed", true);
            
            // 游戏进度Float数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putFloat("player_speed", 5.5f);
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putFloat("difficulty_multiplier", 1.2f);
            
            // 游戏进度Long数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putLong("play_time", 3600000L * 2); // 2小时
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putLong("last_save_time", System.currentTimeMillis());
            
            // 游戏进度StringSet数据
            Set<String> unlockedAchievements = new HashSet<>();
            unlockedAchievements.add("first_blood");
            unlockedAchievements.add("speed_runner");
            unlockedAchievements.add("coin_collector");
            storageManager.getAdapter(StorageType.MMKV, MMKV_GAME_PROGRESS).putStringSet("unlocked_achievements", unlockedAchievements);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 创建系统配置MMKV文件
     */
    public static void createSystemConfigMmkv(Context context) {
        StorageManager storageManager = StorageManager.getInstance(context);
        
        try {
            // 系统配置String数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putString("device_id", "device_abcdef123456");
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putString("os_version", "Android 12");
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putString("app_version", "1.2.3");
            
            // 系统配置Integer数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putInt("screen_width", 1080);
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putInt("screen_height", 1920);
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putInt("dpi", 480);
            
            // 系统配置Boolean数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putBoolean("is_rooted", false);
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putBoolean("is_emulator", false);
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putBoolean("first_launch", false);
            
            // 系统配置Float数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putFloat("brightness", 0.7f);
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putFloat("volume", 0.8f);
            
            // 系统配置Long数据
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putLong("install_time", System.currentTimeMillis() - 86400000L * 7);
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putLong("last_update_time", System.currentTimeMillis() - 86400000L);
            
            // 系统配置StringSet数据
            Set<String> supportedLanguages = new HashSet<>();
            supportedLanguages.add("en");
            supportedLanguages.add("zh");
            supportedLanguages.add("ja");
            supportedLanguages.add("ko");
            storageManager.getAdapter(StorageType.MMKV, MMKV_SYSTEM_CONFIG).putStringSet("supported_languages", supportedLanguages);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 检查MMKV文件是否存在数据
     */
    public static boolean hasMmkvData(Context context, String fileName) {
        try {
            StorageManager storageManager = StorageManager.getInstance(context);
            Map<String, ?> allData = storageManager.getAdapter(StorageType.MMKV, fileName).getAll();
            return allData != null && !allData.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取所有MMKV数据用于显示
     */
    public static Map<String, ?> getAllMmkvData(Context context, String fileName) {
        try {
            StorageManager storageManager = StorageManager.getInstance(context);
            return storageManager.getAdapter(StorageType.MMKV, fileName).getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    /**
     * 清空指定的MMKV文件
     */
    public static void clearMmkvData(Context context, String fileName) {
        try {
            StorageManager storageManager = StorageManager.getInstance(context);
            storageManager.getAdapter(StorageType.MMKV, fileName).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}