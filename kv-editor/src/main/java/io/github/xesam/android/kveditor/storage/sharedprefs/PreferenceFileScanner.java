package io.github.xesam.android.kveditor.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final String XML_EXTENSION = ".xml";
    private static final int MIN_FILE_NAME_LENGTH = 5; // "a.xml" = 5 chars

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
            File[] files = prefsDir.listFiles((dir, name) -> name.endsWith(XML_EXTENSION));
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " XML files in SharedPreferences directory");

                // 按修改时间排序（最新的在前）
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

                for (File file : files) {
                    String fileName = file.getName();

                    // 验证文件名长度，避免边缘情况（如 ".xml" 或非常短的文件名）
                    if (fileName.length() < MIN_FILE_NAME_LENGTH) {
                        Log.w(TAG, "Skipping file with invalid name length: " + fileName);
                        continue;
                    }

                    // 去掉.xml后缀
                    String preferenceName = fileName.substring(0, fileName.length() - XML_EXTENSION.length());

                    // 验证 preference 名称不为空且有效
                    if (preferenceName == null || preferenceName.trim().isEmpty()) {
                        Log.w(TAG, "Skipping file with empty preference name: " + fileName);
                        continue;
                    }

                    // 验证文件有效性 - 尝试读取文件以确保可以正常使用
                    if (!isValidPreferenceFile(context, preferenceName)) {
                        Log.w(TAG, "Skipping invalid or corrupted SharedPreferences file: " + preferenceName);
                        continue;
                    }

                    // 创建文件项
                    FileItem item = new FileItem();
                    item.setName(preferenceName);
                    item.setStorageType(StorageType.SHARED_PREFERENCE);
                    item.setFilePath(file.getAbsolutePath());
                    item.setSize(file.length());
                    item.setLastModified(file.lastModified());

                    fileItems.add(item);
                    Log.d(TAG, "Added valid SharedPreferences file: " + preferenceName);
                }

                Log.d(TAG, "Successfully added " + fileItems.size() + " valid SharedPreferences files");
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
            File[] files = prefsDir.listFiles((dir, name) -> name.endsWith(XML_EXTENSION));
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " XML files in SharedPreferences directory");

                // 按修改时间排序（最新的在前）
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

                for (File file : files) {
                    String fileName = file.getName();

                    // 验证文件名长度
                    if (fileName.length() < MIN_FILE_NAME_LENGTH) {
                        continue;
                    }

                    // 去掉.xml后缀
                    String preferenceName = fileName.substring(0, fileName.length() - XML_EXTENSION.length());

                    // 验证 preference 名称不为空且有效
                    if (preferenceName == null || preferenceName.trim().isEmpty()) {
                        continue;
                    }

                    // 验证文件有效性
                    if (!isValidPreferenceFile(context, preferenceName)) {
                        continue;
                    }

                    fileNames.add(preferenceName);
                    Log.d(TAG, "Added valid SharedPreferences file: " + preferenceName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning SharedPreferences files", e);
        }

        return fileNames;
    }

    /**
     * 验证 SharedPreferences 文件的有效性
     * 通过尝试读取文件来验证其是否可正常使用
     * @param context 上下文
     * @param preferenceName SharedPreferences 名称
     * @return 如果文件有效返回 true，否则返回 false
     */
    private static boolean isValidPreferenceFile(Context context, String preferenceName) {
        try {
            // 使用 MODE_PRIVATE 以最小权限读取文件
            SharedPreferences prefs = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            // 尝试获取所有键值对来验证文件可读性
            prefs.getAll();
            return true;
        } catch (Exception e) {
            // 文件可能损坏或格式不正确
            Log.w(TAG, "Invalid SharedPreferences file: " + preferenceName + ", error: " + e.getMessage());
            return false;
        }
    }
}