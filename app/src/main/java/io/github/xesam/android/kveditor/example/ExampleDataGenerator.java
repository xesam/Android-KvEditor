package io.github.xesam.android.kveditor.example;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import io.github.xesam.android.kveditor.manager.PreferenceManager;
import io.github.xesam.android.kveditor.model.KvPair;

/**
 * 示例数据生成器，用于创建演示用的SharedPreference文件
 */
public class ExampleDataGenerator {

    // 示例文件名
    public static final String EXAMPLE_PREF_NAME = "demo_preference";

    /**
     * 生成示例SharedPreference文件
     * 
     * @param context 上下文
     * @return 是否创建成功
     */
    public static boolean generateExampleData(Context context) {
        boolean success = true;
        
        // 添加String类型数据
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("app_name", "Android KvEditor", KvPair.DataType.STRING));
        
        // 添加Integer类型数据
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("version_code", 100, KvPair.DataType.INTEGER));
        
        // 添加Boolean类型数据
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("is_first_run", false, KvPair.DataType.BOOLEAN));
        
        // 添加Float类型数据
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("app_rating", 4.5f, KvPair.DataType.FLOAT));
        
        // 添加Long类型数据
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("last_login_time", System.currentTimeMillis(), KvPair.DataType.LONG));
        
        // 添加StringSet类型数据
        Set<String> favoriteColors = new HashSet<>();
        favoriteColors.add("Red");
        favoriteColors.add("Blue");
        favoriteColors.add("Green");
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("favorite_colors", favoriteColors, KvPair.DataType.STRING_SET));
        
        // 添加用户信息相关数据
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("user_name", "demo_user", KvPair.DataType.STRING));
        
        success &= PreferenceManager.saveKvPair(context, EXAMPLE_PREF_NAME, 
                new KvPair("user_age", 25, KvPair.DataType.INTEGER));
        
        return success;
    }
    
    /**
     * 检查示例文件是否已存在
     * 
     * @param context 上下文
     * @return 是否存在
     */
    public static boolean hasExampleData(Context context) {
        return !PreferenceManager.getAll(context, EXAMPLE_PREF_NAME).isEmpty();
    }
}