package io.github.xesam.android.kveditor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
    private List<FileItem> mAllFileItems; // 所有扫描到的文件
    private List<FileItem> mFilteredFileItems; // 过滤后的文件
    private TextView mStorageTypeFilterText;
    private TextView mSortText;
    private EditText mSearchEditText;
    private Set<StorageType> mSelectedStorageTypes;
    private Set<StorageType> mAvailableStorageTypes; // 可用的存储类型

    // 排序方式枚举
    private enum SortOption {
        NAME_ASC("名称升序", "a-z"),
        NAME_DESC("名称降序", "z-a"),
        TIME_ASC("时间升序", "旧到新"),
        TIME_DESC("时间降序", "新到旧"),
        SIZE_ASC("大小升序", "小到大"),
        SIZE_DESC("大小降序", "大到小");

        private final String displayName;
        private final String shortName;

        SortOption(String displayName, String shortName) {
            this.displayName = displayName;
            this.shortName = shortName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getShortName() {
            return shortName;
        }
    }

    private SortOption mCurrentSortOption = SortOption.TIME_DESC; // 默认按时间降序

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        // 初始化RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化数据列表
        mAllFileItems = new ArrayList<>();
        mFilteredFileItems = new ArrayList<>();

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
        mSortText = findViewById(R.id.sort_text);
        mSearchEditText = findViewById(R.id.search_edit_text);

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

        // 排序按钮点击事件
        mSortText.setOnClickListener(v -> {
            showSortDialog();
        });

        // 搜索框文本变化监听
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterFiles(s.toString());
            }
        });

        updateStorageTypeFilterText();
        updateSortText();

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

                // 按当前排序方式排序
                sortFileItems(fileItems, mCurrentSortOption);

            } catch (Exception e) {
                android.util.Log.e("FileListActivity", "扫描文件失败", e);
                runOnUiThread(() -> showToast("扫描文件失败: " + e.getMessage()));
            }

            List<FileItem> finalFileItems = fileItems;
            runOnUiThread(() -> {
                showLoading(false);
                mAllFileItems = finalFileItems;
                mFileList = new ArrayList<>();
                for (FileItem item : finalFileItems) {
                    mFileList.add(item.getFileName());
                }
                // 应用搜索过滤
                String searchText = mSearchEditText.getText().toString().trim();
                filterFiles(searchText);
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
     * 过滤文件（根据搜索关键词）
     */
    private void filterFiles(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            mFilteredFileItems = new ArrayList<>(mAllFileItems);
        } else {
            String query = searchText.toLowerCase(Locale.ROOT);
            mFilteredFileItems = new ArrayList<>();
            for (FileItem item : mAllFileItems) {
                String fileName = item.getFileName().toLowerCase(Locale.ROOT);
                String displayName = item.getDisplayName() != null ?
                        item.getDisplayName().toLowerCase(Locale.ROOT) : fileName;
                if (fileName.contains(query) || displayName.contains(query)) {
                    mFilteredFileItems.add(item);
                }
            }
        }
        // 应用排序
        sortFileItems(mFilteredFileItems, mCurrentSortOption);
        // 更新适配器
        if (mAdapter != null) {
            mAdapter.updateData(mFilteredFileItems);
        }
        updateEmptyView(mFilteredFileItems.isEmpty());
    }

    /**
     * 根据排序选项对文件列表进行排序
     */
    private void sortFileItems(List<FileItem> fileItems, SortOption sortOption) {
        if (fileItems == null || fileItems.isEmpty()) {
            return;
        }

        Comparator<FileItem> comparator;
        switch (sortOption) {
            case NAME_ASC:
                comparator = (a, b) -> a.getFileName().compareToIgnoreCase(b.getFileName());
                break;
            case NAME_DESC:
                comparator = (a, b) -> b.getFileName().compareToIgnoreCase(a.getFileName());
                break;
            case TIME_ASC:
                comparator = (a, b) -> Long.compare(a.getLastModified(), b.getLastModified());
                break;
            case TIME_DESC:
                comparator = (a, b) -> Long.compare(b.getLastModified(), a.getLastModified());
                break;
            case SIZE_ASC:
                comparator = (a, b) -> Long.compare(a.getSize(), b.getSize());
                break;
            case SIZE_DESC:
                comparator = (a, b) -> Long.compare(b.getSize(), a.getSize());
                break;
            default:
                comparator = (a, b) -> Long.compare(b.getLastModified(), a.getLastModified());
                break;
        }

        Collections.sort(fileItems, comparator);
    }

    /**
     * 显示排序选择对话框
     */
    private void showSortDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择排序方式");

        SortOption[] options = SortOption.values();
        String[] optionNames = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            optionNames[i] = options[i].getDisplayName();
        }

        int checkedIndex = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i] == mCurrentSortOption) {
                checkedIndex = i;
                break;
            }
        }

        builder.setSingleChoiceItems(optionNames, checkedIndex, (dialog, which) -> {
            mCurrentSortOption = options[which];
            updateSortText();
            filterFiles(mSearchEditText.getText().toString().trim());
            dialog.dismiss();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
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

    /**
     * 更新排序文本
     */
    private void updateSortText() {
        mSortText.setText(mCurrentSortOption.getShortName());
    }
}