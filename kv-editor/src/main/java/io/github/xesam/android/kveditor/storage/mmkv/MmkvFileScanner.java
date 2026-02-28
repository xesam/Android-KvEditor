package io.github.xesam.android.kveditor.storage.mmkv;

import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.xesam.android.kveditor.model.FileItem;
import io.github.xesam.android.kveditor.storage.base.FileScanner;
import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * MMKV文件扫描器
 * 用于扫描和发现应用中存在的MMKV文件
 */
public class MmkvFileScanner implements FileScanner {
    private static final String TAG = "MmkvFileScanner";
    private static final String CRC_SUFFIX = ".crc";
    private static final String META_SUFFIX = ".meta";

    // MMKV 默认实例的文件名
    private static final String DEFAULT_MMKV_ID = "mMKV";

    @Override
    public List<FileItem> scanFiles(Context context) {
        List<FileItem> fileItems = new ArrayList<>();

        try {
            // 确保 MMKV 已初始化
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

            // 扫描MMKV目录下的所有文件
            File[] files = mmkvRootDir.listFiles();
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " files in MMKV root directory");

                // 用于存储已经处理过的文件ID（使用 Set 提高查找效率）
                Set<String> processedIds = new HashSet<>();
                // 存储找到的所有文件信息，用于获取最新的文件信息
                Set<String> foundIds = new HashSet<>();

                for (File file : files) {
                    String fileName = file.getName();
                    String mmkvId = extractMmkvId(fileName);

                    if (mmkvId != null && !mmkvId.isEmpty()) {
                        foundIds.add(mmkvId);
                    }
                }

                // 处理所有找到的 MMKV ID
                for (String mmkvId : foundIds) {
                    // 跳过已经处理过的 ID
                    if (processedIds.contains(mmkvId)) {
                        continue;
                    }

                    // 验证 MMKV 文件有效性
                    if (!isValidMmkvFile(mmkvId)) {
                        Log.w(TAG, "Skipping invalid or corrupted MMKV file: " + mmkvId);
                        continue;
                    }

                    // 获取主文件的文件信息（优先使用主文件，如果没有则使用 .crc 文件）
                    FileItem item = createFileItem(mmkvRootDir, mmkvId, context);
                    if (item != null) {
                        processedIds.add(mmkvId);
                        fileItems.add(item);
                        Log.d(TAG, "Added valid MMKV file: " + mmkvId);
                    }
                }

                Log.d(TAG, "Successfully added " + fileItems.size() + " valid MMKV files");
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
            // 确保 MMKV 已初始化
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

            // 扫描MMKV目录下的所有文件
            File[] files = mmkvRootDir.listFiles();
            if (files != null) {
                Log.d(TAG, "Found " + files.length + " files in MMKV root directory");

                // 用于存储已经处理过的文件ID（使用 Set 提高查找效率）
                Set<String> processedIds = new HashSet<>();
                Set<String> foundIds = new HashSet<>();

                for (File file : files) {
                    String fileName = file.getName();
                    String mmkvId = extractMmkvId(fileName);

                    if (mmkvId != null && !mmkvId.isEmpty()) {
                        foundIds.add(mmkvId);
                    }
                }

                // 处理所有找到的 MMKV ID
                for (String mmkvId : foundIds) {
                    // 跳过已经处理过的 ID
                    if (processedIds.contains(mmkvId)) {
                        continue;
                    }

                    // 验证 MMKV 文件有效性
                    if (!isValidMmkvFile(mmkvId)) {
                        Log.w(TAG, "Skipping invalid or corrupted MMKV file: " + mmkvId);
                        continue;
                    }

                    processedIds.add(mmkvId);
                    fileNames.add(mmkvId);
                    Log.d(TAG, "Added valid MMKV file: " + mmkvId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning MMKV files", e);
        }

        return fileNames;
    }

    /**
     * 从文件名提取 MMKV ID
     * MMKV 为每个实例创建以下文件：
     * - {name}       主数据文件（无后缀）
     * - {name}.crc   CRC 校验文件
     * - {name}.meta 元数据文件（可选）
     * @param fileName 文件名
     * @return MMKV ID，如果文件名不符合 MMKV 格式则返回 null
     */
    private static String extractMmkvId(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        // 检查 .crc 文件
        if (fileName.endsWith(CRC_SUFFIX) && fileName.length() > CRC_SUFFIX.length()) {
            return fileName.substring(0, fileName.length() - CRC_SUFFIX.length());
        }

        // 检查 .meta 文件
        if (fileName.endsWith(META_SUFFIX) && fileName.length() > META_SUFFIX.length()) {
            return fileName.substring(0, fileName.length() - META_SUFFIX.length());
        }

        // 检查主数据文件（无后缀）
        // 主数据文件通常没有扩展名，但也可能有一些特殊情况
        // 这里我们假设没有已知后缀且不以点开头的文件可能是主数据文件
        if (!fileName.contains(".")) {
            return fileName;
        }

        return null;
    }

    /**
     * 验证 MMKV 文件的有效性
     * 通过尝试读取文件来验证其是否可正常使用
     * @param mmkvId MMKV ID
     * @return 如果文件有效返回 true，否则返回 false
     */
    private static boolean isValidMmkvFile(String mmkvId) {
        try {
            MMKV mmkv = MMKV.mmkvWithID(mmkvId);
            // 尝试获取所有键来验证文件可读性
            String[] allKeys = mmkv.allKeys();
            return true;
        } catch (Exception e) {
            // 文件可能损坏或格式不正确
            Log.w(TAG, "Invalid MMKV file: " + mmkvId + ", error: " + e.getMessage());
            return false;
        }
    }

    /**
     * 创建 FileItem，优先使用主数据文件的信息
     * @param rootDir MMKV 根目录
     * @param mmkvId MMKV ID
     * @param context 上下文
     * @return FileItem 对象，如果创建失败则返回 null
     */
    private static FileItem createFileItem(File rootDir, String mmkvId, Context context) {
        try {
            File mainFile = new File(rootDir, mmkvId);
            File crcFile = new File(rootDir, mmkvId + CRC_SUFFIX);

            // 优先使用主文件信息，如果不存在则使用 .crc 文件
            File targetFile = mainFile.exists() ? mainFile : crcFile;

            if (!targetFile.exists()) {
                return null;
            }

            FileItem item = new FileItem();
            item.setName(mmkvId);
            item.setStorageType(StorageType.MMKV);
            item.setFilePath(targetFile.getParent());
            item.setSize(targetFile.length());
            item.setLastModified(targetFile.lastModified());

            return item;
        } catch (Exception e) {
            Log.e(TAG, "Error creating FileItem for MMKV ID: " + mmkvId, e);
            return null;
        }
    }
}