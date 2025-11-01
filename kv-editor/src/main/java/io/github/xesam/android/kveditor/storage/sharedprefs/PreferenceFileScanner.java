package io.github.xesam.android.kveditor.storage.sharedprefs;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.github.xesam.android.kveditor.model.FileItem;
import io.github.xesam.android.kveditor.storage.base.FileScanner;
import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * SharedPreferences文件扫描器
 * 用于扫描和发现应用中存在的SharedPreferences文件
 */
public class PreferenceFileScanner implements FileScanner {
    private static final String TAG = "PreferenceFileScanner";
    
    @Override
    public List<FileItem> scanFiles(Context context) {
        List<FileItem> fileItems = new ArrayList<>();
        
        try {
            // 获取SharedPreferences目录
            File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
            if (!prefsDir.exists() || !prefsDir.isDirectory()) {
                Log.w(TAG, "SharedPreferences directory does not exist or is not a directory: " + prefsDir.getAbsolutePath());
                return fileItems;
            }
            
            Log.d(TAG, "Scanning SharedPreferences directory: " + prefsDir.getAbsolutePath());
            
            // 列出目录中的所有XML文件
            File[] files = prefsDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " SharedPreferences files");
                
                // 按修改时间排序（最新的在前）
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                
                for (File file : files) {
                    String fileName = file.getName();
                    // 去掉.xml后缀
                    String preferenceName = fileName.substring(0, fileName.length() - 4);
                    
                    // 创建文件项
                    FileItem item = new FileItem();
                    item.setName(preferenceName);
                    item.setStorageType(StorageType.SHARED_PREFERENCE);
                    item.setFilePath(file.getAbsolutePath());
                    item.setSize(file.length());
                    item.setLastModified(file.lastModified());
                    
                    fileItems.add(item);
                    Log.d(TAG, "Added SharedPreferences file: " + preferenceName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning SharedPreferences files", e);
        }
        
        return fileItems;
    }
    
    @Override
    public StorageType getStorageType() {
        return StorageType.SHARED_PREFERENCE;
    }
    
    /**
     * 扫描SharedPreferences文件名称列表
     * @param context 上下文
     * @return SharedPreferences文件名称列表
     */
    public static List<String> scanPreferenceFiles(Context context) {
        List<String> fileNames = new ArrayList<>();
        
        try {
            // 获取SharedPreferences目录
            File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
            if (!prefsDir.exists() || !prefsDir.isDirectory()) {
                Log.w(TAG, "SharedPreferences directory does not exist or is not a directory: " + prefsDir.getAbsolutePath());
                return fileNames;
            }
            
            Log.d(TAG, "Scanning SharedPreferences directory: " + prefsDir.getAbsolutePath());
            
            // 列出目录中的所有XML文件
            File[] files = prefsDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " SharedPreferences files");
                
                // 按修改时间排序（最新的在前）
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                
                for (File file : files) {
                    String fileName = file.getName();
                    // 去掉.xml后缀
                    String preferenceName = fileName.substring(0, fileName.length() - 4);
                    
                    fileNames.add(preferenceName);
                    Log.d(TAG, "Added SharedPreferences file: " + preferenceName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning SharedPreferences files", e);
        }
        
        return fileNames;
    }
}