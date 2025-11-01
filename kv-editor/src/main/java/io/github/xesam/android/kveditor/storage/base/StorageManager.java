package io.github.xesam.android.kveditor.storage.base;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.github.xesam.android.kveditor.storage.mmkv.MmkvAdapter;
import io.github.xesam.android.kveditor.storage.sharedprefs.SharedPrefAdapter;

/**
 * 存储管理器
 * 统一管理不同类型的存储适配器，提供统一的访问接口
 */
public class StorageManager {
    private static final String TAG = "StorageManager";
    
    private static StorageManager instance;
    private final Map<String, StorageAdapter> adapterCache;
    private final Context context;
    private StorageType defaultStorageType = StorageType.SHARED_PREFERENCE;
    
    /**
     * 私有构造函数
     * @param context 上下文
     */
    private StorageManager(Context context) {
        this.context = context.getApplicationContext();
        this.adapterCache = new HashMap<>();
    }
    
    /**
     * 获取存储管理器实例
     * @param context 上下文
     * @return 存储管理器实例
     */
    public static synchronized StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new StorageManager(context);
        }
        return instance;
    }
    
    /**
     * 获取存储适配器
     * @param config 存储配置
     * @return 存储适配器
     */
    public StorageAdapter getAdapter(StorageConfig config) {
        String cacheKey = generateCacheKey(config);
        
        // 检查缓存
        if (adapterCache.containsKey(cacheKey)) {
            return adapterCache.get(cacheKey);
        }
        
        // 创建新的适配器
        StorageAdapter adapter = createAdapter(config);
        if (adapter != null) {
            adapter.init(context, config.getName());
            adapterCache.put(cacheKey, adapter);
        }
        
        return adapter;
    }
    
    /**
     * 获取存储适配器
     * @param type 存储类型
     * @param name 存储名称
     * @return 存储适配器
     */
    public StorageAdapter getAdapter(StorageType type, String name) {
        StorageConfig config = new StorageConfig.Builder()
                .setType(type)
                .setName(name)
                .build();
        return getAdapter(config);
    }
    
    /**
     * 创建存储适配器
     * @param config 存储配置
     * @return 存储适配器
     */
    private StorageAdapter createAdapter(StorageConfig config) {
        switch (config.getType()) {
            case SHARED_PREFERENCE:
                return new SharedPrefAdapter();
            case MMKV:
                return new MmkvAdapter();
            default:
                Log.w(TAG, "Unsupported storage type: " + config.getType());
                return null;
        }
    }
    
    /**
     * 生成缓存键
     * @param config 存储配置
     * @return 缓存键
     */
    private String generateCacheKey(StorageConfig config) {
        return config.getType().getCode() + ":" + config.getName();
    }
    
    /**
     * 设置默认存储类型
     * @param type 存储类型
     */
    public void setDefaultStorageType(StorageType type) {
        this.defaultStorageType = type;
    }
    
    /**
     * 获取默认存储类型
     * @return 默认存储类型
     */
    public StorageType getDefaultStorageType() {
        return defaultStorageType;
    }
    
    /**
     * 清除缓存的适配器
     */
    public void clearCache() {
        adapterCache.clear();
    }
    
    /**
     * 获取文件扫描器
     * @param type 存储类型
     * @param context 上下文
     * @return 文件扫描器
     */
    public FileScanner getFileScanner(StorageType type, Context context) {
        switch (type) {
            case SHARED_PREFERENCE:
                return new io.github.xesam.android.kveditor.storage.sharedprefs.PreferenceFileScanner();
            case MMKV:
                return new io.github.xesam.android.kveditor.storage.mmkv.MmkvFileScanner();
            default:
                Log.w(TAG, "No file scanner for storage type: " + type);
                return null;
        }
    }
}