package io.github.xesam.android.kveditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.xesam.android.kveditor.storage.base.StorageType;
import io.github.xesam.android.kveditor.ui.FileListActivity;

public final class KvEditor {
    // 支持的存储类型配置键
    public static final String EXTRA_SUPPORTED_STORAGE_TYPES = "extra_supported_storage_types";
    /**
     * 打开KvEditor，指定支持的存储类型
     *
     * @param from           上下文
     * @param supportedTypes 支持的存储类型集合
     */
    public static void open(Context from, Set<StorageType> supportedTypes) {
        Intent intent = new Intent(from, FileListActivity.class);

        // 将支持的存储类型配置传递给FileListActivity
        if (supportedTypes != null && !supportedTypes.isEmpty()) {
            Bundle extras = new Bundle();
            Set<String> typeCodes = new HashSet<>();
            for (StorageType type : supportedTypes) {
                typeCodes.add(type.getCode());
            }
            extras.putStringArray(EXTRA_SUPPORTED_STORAGE_TYPES, typeCodes.toArray(new String[0]));
            intent.putExtras(extras);
        }

        if (!(from instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        from.startActivity(intent);
    }

    /**
     * 打开KvEditor，指定支持的存储类型
     *
     * @param from       上下文
     * @param otherTypes 其他支持的存储类型
     */
    public static void open(Context from, StorageType... otherTypes) {
        Set<StorageType> supportedTypes = new HashSet<>();
        if (otherTypes != null) {
            Collections.addAll(supportedTypes, otherTypes);
        }
        open(from, supportedTypes);
    }
}
