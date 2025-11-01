package io.github.xesam.android.kveditor.model;

import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * 文件项数据模型
 * 用于统一表示不同存储类型的文件
 */
public class FileItem {
    private String fileName;
    private StorageType storageType;
    private String displayName;
    private String filePath;
    private long size;
    private long lastModified;

    public FileItem() {
    }

    public FileItem(String fileName, StorageType storageType) {
        this.fileName = fileName;
        this.storageType = storageType;
        this.displayName = fileName;
    }

    public FileItem(String fileName, StorageType storageType, String displayName) {
        this.fileName = fileName;
        this.storageType = storageType;
        this.displayName = displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setName(String name) {
        this.fileName = name;
        if (this.displayName == null) {
            this.displayName = name;
        }
    }

    public String getName() {
        return this.fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileItem fileItem = (FileItem) o;
        return java.util.Objects.equals(fileName, fileItem.fileName) &&
                storageType == fileItem.storageType;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(fileName, storageType);
    }
}