package io.github.xesam.android.kveditor.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.xesam.android.kveditor.storage.base.StorageAdapter;

/**
 * SharedPreference适配器实现
 * 适配现有的SharedPreference功能到统一的StorageAdapter接口
 */
public class SharedPrefAdapter implements StorageAdapter {
    private static final String TAG = "SharedPrefAdapter";
    
    private Context context;
    private String name;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    @Override
    public void init(Context context, String name) {
        this.context = context;
        this.name = name;
        this.sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>();
        Map<String, ?> allData = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allData.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    @Override
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
    
    @Override
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }
    
    @Override
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }
    
    @Override
    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    
    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }
    
    @Override
    public boolean putString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }
    
    @Override
    public boolean putInt(String key, int value) {
        editor.putInt(key, value);
        return editor.commit();
    }
    
    @Override
    public boolean putLong(String key, long value) {
        editor.putLong(key, value);
        return editor.commit();
    }
    
    @Override
    public boolean putFloat(String key, float value) {
        editor.putFloat(key, value);
        return editor.commit();
    }
    
    @Override
    public boolean putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }
    
    @Override
    public boolean putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        return editor.commit();
    }
    
    @Override
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }
    
    @Override
    public boolean remove(String key) {
        editor.remove(key);
        return editor.commit();
    }
    
    @Override
    public boolean clear() {
        editor.clear();
        return editor.commit();
    }
    
    /**
     * 获取SharedPreference文件的实际路径
     * @return 文件路径
     */
    public String getFilePath() {
        File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        File prefsFile = new File(prefsDir, name + ".xml");
        return prefsFile.getAbsolutePath();
    }
}