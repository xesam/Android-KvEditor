package io.github.xesam.android.kveditor.storage.base;

import android.content.Context;

import java.util.List;

import io.github.xesam.android.kveditor.model.FileItem;

/**
 * 文件扫描器接口
 * 用于扫描特定类型存储的文件
 */
public interface FileScanner {
    
    /**
     * 扫描存储文件
     * @param context 上下文
     * @return 文件项列表
     */
    List<FileItem> scanFiles(Context context);
    
    /**
     * 获取支持的存储类型
     * @return 存储类型
     */
    StorageType getStorageType();
}