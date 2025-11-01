package io.github.xesam.android.kveditor.storage.base;

/**
 * 存储类型枚举
 * 定义支持的存储类型及其特性
 */
public enum StorageType {

    /**
     * SharedPreference存储
     * 传统的Android键值存储，基于XML文件
     */
    SHARED_PREFERENCE("SharedPreference", "SharedPreference", true, true, "#4CAF50"),

    /**
     * MMKV存储
     * 腾讯开源的高性能键值存储，基于内存映射
     */
    MMKV("MMKV", "MMKV", true, true, "#2196F3"),

    ;

    private final String code;
    private final String description;
    private final boolean supported;
    private final boolean enabled;
    private final String color;

    StorageType(String code, String description, boolean supported, boolean enabled, String color) {
        this.code = code;
        this.description = description;
        this.supported = supported;
        this.enabled = enabled;
        this.color = color;
    }

    StorageType(String code, String description, boolean supported, boolean enabled) {
        this(code, description, supported, enabled, "#757575"); // 默认灰色
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSupported() {
        return supported;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getColor() {
        return color;
    }

    /**
     * 根据代码获取存储类型
     * @param code 存储类型代码
     * @return 存储类型枚举实例
     */
    public static StorageType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (StorageType type : StorageType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 获取所有支持的存储类型
     * @return 支持的存储类型数组
     */
    public static StorageType[] getSupportedTypes() {
        return StorageType.values();
    }
}