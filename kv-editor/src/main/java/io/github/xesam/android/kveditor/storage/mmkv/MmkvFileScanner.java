package io.github.xesam.android.kveditor.storage.mmkv;

import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.xesam.android.kveditor.model.FileItem;
import io.github.xesam.android.kveditor.storage.base.FileScanner;
import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * MMKV文件扫描器
 * 用于扫描和发现应用中存在的MMKV文件
 */
public class MmkvFileScanner implements FileScanner {
    private static final String TAG = "MmkvFileScanner";
    
    @Override
    public List<FileItem> scanFiles(Context context) {
        List<FileItem> fileItems = new ArrayList<>();
        
        try {
            // 获取MMKV根目录
            String rootDir = MMKV.getRootDir();
            if (rootDir == null || rootDir.isEmpty()) {
                Log.w(TAG, "MMKV root directory is null or empty");
                return fileItems;
            }
            
            File mmkvRootDir = new File(rootDir);
            if (!mmkvRootDir.exists() || !mmkvRootDir.isDirectory()) {
                Log.w(TAG, "MMKV root directory does not exist or is not a directory: " + rootDir);
                return fileItems;
            }
            
            Log.d(TAG, "Scanning MMKV directory: " + rootDir);
            
            // 扫描MMKV目录下的所有.crc文件（MMKV文件的特征）
            File[] files = mmkvRootDir.listFiles();
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " files in MMKV root directory");
                
                // 用于存储已经处理过的文件名（去重）
                List<String> processedNames = new ArrayList<>();
                
                for (File file : files) {
                    String fileName = file.getName();
                    Log.d(TAG, "Processing file: " + fileName);
                    
                    // 处理.crc文件，提取文件名
                    if (fileName.endsWith(".crc")) {
                        // 提取MMKV ID（去掉.crc后缀）
                        String mmkvId = fileName.substring(0, fileName.length() - 4);
                        
                        // 检查是否已经处理过这个ID
                        if (!processedNames.contains(mmkvId)) {
                            processedNames.add(mmkvId);
                            
                            // 创建文件项
                            FileItem item = new FileItem();
                            item.setName(mmkvId);
                            item.setStorageType(StorageType.MMKV);
                            item.setFilePath(file.getParent());
                            item.setSize(file.length());
                            item.setLastModified(file.lastModified());
                            
                            fileItems.add(item);
                            Log.d(TAG, "Added MMKV file: " + mmkvId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning MMKV files", e);
        }
        
        return fileItems;
    }
    
    @Override
    public StorageType getStorageType() {
        return StorageType.MMKV;
    }
    
    /**
     * 扫描MMKV文件名称列表
     * @param context 上下文
     * @return MMKV文件名称列表
     */
    public static List<String> scanMmkvFiles(Context context) {
        List<String> fileNames = new ArrayList<>();
        
        try {
            // 获取MMKV根目录
            String rootDir = MMKV.getRootDir();
            if (rootDir == null || rootDir.isEmpty()) {
                Log.w(TAG, "MMKV root directory is null or empty");
                return fileNames;
            }
            
            File mmkvRootDir = new File(rootDir);
            if (!mmkvRootDir.exists() || !mmkvRootDir.isDirectory()) {
                Log.w(TAG, "MMKV root directory does not exist or is not a directory: " + rootDir);
                return fileNames;
            }
            
            Log.d(TAG, "Scanning MMKV directory: " + rootDir);
            
            // 扫描MMKV目录下的所有.crc文件（MMKV文件的特征）
            File[] files = mmkvRootDir.listFiles();
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " files in MMKV root directory");
                
                // 用于存储已经处理过的文件名（去重）
                List<String> processedNames = new ArrayList<>();
                
                for (File file : files) {
                    String fileName = file.getName();
                    Log.d(TAG, "Processing file: " + fileName);
                    
                    // 处理.crc文件，提取文件名
                    if (fileName.endsWith(".crc")) {
                        // 提取MMKV ID（去掉.crc后缀）
                        String mmkvId = fileName.substring(0, fileName.length() - 4);
                        
                        // 检查是否已经处理过这个ID
                        if (!processedNames.contains(mmkvId)) {
                            processedNames.add(mmkvId);
                            fileNames.add(mmkvId);
                            Log.d(TAG, "Added MMKV file: " + mmkvId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning MMKV files", e);
        }
        
        return fileNames;
    }
}