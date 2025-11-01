package io.github.xesam.android.kveditor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.github.xesam.android.kveditor.R;
import io.github.xesam.android.kveditor.model.FileItem;
import io.github.xesam.android.kveditor.KvEditor;
import io.github.xesam.android.kveditor.storage.base.StorageManager;
import io.github.xesam.android.kveditor.storage.base.StorageType;
import io.github.xesam.android.kveditor.storage.base.FileScanner;
import io.github.xesam.android.kveditor.ui.KeyValueEditorActivity;

/**
 * SharedPreference文件列表界面
 */
public class FileListActivity extends Activity {

    private RecyclerView mRecyclerView;
    private FileListAdapter mAdapter;
    private List<String> mFileList;
    private TextView mStorageTypeFilterText;
    private Set<StorageType> mSelectedStorageTypes;
    private Set<StorageType> mAvailableStorageTypes; // 可用的存储类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        // 初始化RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化适配器
        mAdapter = new FileListAdapter(new ArrayList<>(), fileItem -> {
            Intent intent = new Intent(FileListActivity.this, KeyValueEditorActivity.class);
            intent.putExtra(KeyValueEditorActivity.EXTRA_FILE_NAME, fileItem.getFileName());
            intent.putExtra(KeyValueEditorActivity.EXTRA_STORAGE_TYPE, fileItem.getStorageType().getCode());
            startActivity(intent);
        });
        mRecyclerView.setAdapter(mAdapter);

        // 初始化存储类型过滤器
        mStorageTypeFilterText = findViewById(R.id.storage_type_filter_text);
        mSelectedStorageTypes = new HashSet<>();
        mAvailableStorageTypes = new HashSet<>();
        
        // 从Intent中获取支持的存储类型
        String[] supportedTypeCodes = getIntent().getStringArrayExtra(KvEditor.EXTRA_SUPPORTED_STORAGE_TYPES);
        
        if (supportedTypeCodes != null && supportedTypeCodes.length > 0) {
            // 使用配置的支持类型
            for (String typeCode : supportedTypeCodes) {
                StorageType type = StorageType.fromCode(typeCode);
                if (type != null) {
                    mAvailableStorageTypes.add(type);
                }
            }
        } else {
            // 未配置时使用所有存储类型
            for (StorageType type : StorageType.getSupportedTypes()) {
                mAvailableStorageTypes.add(type);
            }
        }
        
        // 默认选择所有可用的存储类型
        mSelectedStorageTypes.addAll(mAvailableStorageTypes);
        
        mStorageTypeFilterText.setOnClickListener(v -> {
            // 只显示可用的存储类型
            StorageTypeFilterDialog.show(this, mAvailableStorageTypes, mSelectedStorageTypes, new StorageTypeFilterDialog.OnStorageTypesSelectedListener() {
                @Override
                public void onStorageTypesSelected(Set<StorageType> selectedTypes) {
                    mSelectedStorageTypes = selectedTypes;
                    updateStorageTypeFilterText();
                    scanPreferenceFiles();
                }
            });
        });
        
        updateStorageTypeFilterText();

        // 扫描并显示文件列表
        scanPreferenceFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回此界面时刷新文件列表
        scanPreferenceFiles();
    }

    /**
     * 显示加载状态
     */
    private void showLoading(boolean show) {
        findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新空视图状态
     */
    private void updateEmptyView(boolean isEmpty) {
        findViewById(R.id.empty_view).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示提示信息
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 扫描并显示SharedPreference文件列表
     */
    private void scanPreferenceFiles() {
        showLoading(true);
        
        new Thread(() -> {
            List<FileItem> fileItems = new ArrayList<>();
            try {
                StorageManager storageManager = StorageManager.getInstance(FileListActivity.this);
                for (StorageType storageType : mSelectedStorageTypes) {
                    FileScanner scanner = storageManager.getFileScanner(storageType, FileListActivity.this);
                    if (scanner != null) {
                        List<FileItem> scannedFiles = scanner.scanFiles(FileListActivity.this);
                        fileItems.addAll(scannedFiles);
                    }
                }
                
                // 按文件名排序（兼容API 21）
                Collections.sort(fileItems, (a, b) -> a.getFileName().compareToIgnoreCase(b.getFileName()));
                
            } catch (Exception e) {
                android.util.Log.e("FileListActivity", "扫描文件失败", e);
                showToast("扫描文件失败: " + e.getMessage());
            }
            
            runOnUiThread(() -> {
                showLoading(false);
                mFileList = new ArrayList<>();
                for (FileItem item : fileItems) {
                    mFileList.add(item.getFileName());
                }
                if (mAdapter != null) {
                    mAdapter.updateData(fileItems);
                }
                updateEmptyView(fileItems.isEmpty());
            });
        }).start();
    }

    /**
     * 加载文件（兼容方法）
     */
    private void loadFiles() {
        scanPreferenceFiles();
    }



    /**
     * 更新存储类型过滤器文本
     */
    private void updateStorageTypeFilterText() {
        if (mSelectedStorageTypes.isEmpty()) {
            mStorageTypeFilterText.setText("选择存储类型");
        } else if (mSelectedStorageTypes.size() == 1) {
            for (StorageType type : mSelectedStorageTypes) {
                mStorageTypeFilterText.setText(type.getDescription());
                break;
            }
        } else {
            mStorageTypeFilterText.setText("已选择 " + mSelectedStorageTypes.size() + " 种类型");
        }
    }
}