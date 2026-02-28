package io.github.xesam.android.kveditor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.xesam.android.kveditor.R;
import io.github.xesam.android.kveditor.model.KvPair;
import io.github.xesam.android.kveditor.utils.DataTypeConverter;
import io.github.xesam.android.kveditor.storage.base.StorageAdapter;
import io.github.xesam.android.kveditor.storage.base.StorageConfig;
import io.github.xesam.android.kveditor.storage.base.StorageManager;
import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * 键值对编辑界面
 */
public class KeyValueEditorActivity extends Activity {

    public static final String EXTRA_FILE_NAME = "extra_file_name";
    public static final String EXTRA_STORAGE_TYPE = "extra_storage_type";

    private String mFileName;
    private RecyclerView mRecyclerView;
    private KeyValueAdapter mAdapter;
    private List<KvPair> mKvPairList;
    private List<KvPair> mFilteredKvPairList; // 过滤后的列表
    private StorageType mCurrentStorageType;
    private TextView mSortText;
    private EditText mSearchEditText;

    // 排序方式枚举
    private enum SortOption {
        KEY_ASC("键名升序", "a-z"),
        KEY_DESC("键名降序", "z-a"),
        TYPE_ASC("类型升序", "类型A-Z"),
        TYPE_DESC("类型降序", "类型Z-A");

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

    private SortOption mCurrentSortOption = SortOption.KEY_ASC; // 默认按键名升序

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_value_editor);

        // 获取文件名
        mFileName = getIntent().getStringExtra(EXTRA_FILE_NAME);
        if (mFileName == null) {
            finish();
            return;
        }

        // 获取存储类型
        String storageTypeCode = getIntent().getStringExtra(EXTRA_STORAGE_TYPE);
        if (storageTypeCode == null) {
            storageTypeCode = StorageType.SHARED_PREFERENCE.getCode();
        }
        mCurrentStorageType = StorageType.fromCode(storageTypeCode);

        // 初始化RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化列表
        mKvPairList = new ArrayList<>();
        mFilteredKvPairList = new ArrayList<>();

        // 获取UI控件
        mSortText = findViewById(R.id.sort_text);
        mSearchEditText = findViewById(R.id.search_edit_text);

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
                filterKvPairs(s.toString());
            }
        });

        updateSortText();

        // 加载键值对数据
        loadKvPairs();

        // 设置添加按钮点击事件
        findViewById(R.id.add_button).setOnClickListener(v -> {
            showAddEditDialog(null);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回此界面时刷新数据
        loadKvPairs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 加载键值对数据
     */
    private void loadKvPairs() {
        // 显示加载状态
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        // 根据存储类型加载数据
        Map<String, ?> all = null;

        // 使用StorageManager获取存储适配器
        StorageConfig config = new StorageConfig.Builder()
                .setName(mFileName)
                .setType(mCurrentStorageType)
                .build();
        StorageAdapter storageAdapter = StorageManager.getInstance(this).getAdapter(config);

        try {
            all = storageAdapter.getAll();
        } catch (Exception e) {
            android.util.Log.e("KeyValueEditorActivity", "加载数据失败", e);
            showToast("加载数据失败: " + e.getMessage());
            all = null;
        }

        mKvPairList = new ArrayList<>();

        if (all != null) {
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                KvPair.DataType dataType = DataTypeConverter.getDataTypeByObject(value);
                mKvPairList.add(new KvPair(key, value, dataType));
            }
        }

        // 应用搜索过滤
        String searchText = mSearchEditText.getText().toString().trim();
        filterKvPairs(searchText);
    }

    /**
     * 过滤键值对（根据搜索关键词）
     */
    private void filterKvPairs(String searchText) {
        // 隐藏加载状态
        findViewById(R.id.progress_bar).setVisibility(View.GONE);

        if (searchText == null || searchText.trim().isEmpty()) {
            mFilteredKvPairList = new ArrayList<>(mKvPairList);
        } else {
            String query = searchText.toLowerCase(Locale.ROOT);
            mFilteredKvPairList = new ArrayList<>();
            for (KvPair pair : mKvPairList) {
                String key = pair.getKey().toLowerCase(Locale.ROOT);
                String valueStr = DataTypeConverter.convertValueToString(pair.getValue()).toLowerCase(Locale.ROOT);
                if (key.contains(query) || valueStr.contains(query)) {
                    mFilteredKvPairList.add(pair);
                }
            }
        }

        // 应用排序
        sortKvPairs(mFilteredKvPairList, mCurrentSortOption);

        // 更新UI
        updateAdapter();
    }

    /**
     * 更新适配器
     */
    private void updateAdapter() {
        if (mFilteredKvPairList.isEmpty()) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            // 创建或更新适配器
            if (mAdapter == null) {
                mAdapter = new KeyValueAdapter(mFilteredKvPairList, new KeyValueAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(KvPair kvPair) {
                        showAddEditDialog(kvPair);
                    }

                    @Override
                    public void onItemDelete(KvPair kvPair) {
                        // 显示删除确认对话框
                        new AlertDialog.Builder(KeyValueEditorActivity.this)
                                .setTitle("确认删除")
                                .setMessage("确定要删除键 '" + kvPair.getKey() + "' 吗？")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    deleteKvPair(kvPair);
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.updateData(mFilteredKvPairList);
            }
        }
    }

    /**
     * 根据排序选项对键值对列表进行排序
     */
    private void sortKvPairs(List<KvPair> kvPairs, SortOption sortOption) {
        if (kvPairs == null || kvPairs.isEmpty()) {
            return;
        }

        Comparator<KvPair> comparator;
        switch (sortOption) {
            case KEY_ASC:
                comparator = (a, b) -> a.getKey().compareToIgnoreCase(b.getKey());
                break;
            case KEY_DESC:
                comparator = (a, b) -> b.getKey().compareToIgnoreCase(a.getKey());
                break;
            case TYPE_ASC:
                comparator = (a, b) -> a.getDataType().name().compareTo(b.getDataType().name());
                break;
            case TYPE_DESC:
                comparator = (a, b) -> b.getDataType().name().compareTo(a.getDataType().name());
                break;
            default:
                comparator = (a, b) -> a.getKey().compareToIgnoreCase(b.getKey());
                break;
        }

        Collections.sort(kvPairs, comparator);
    }

    /**
     * 显示排序选择对话框
     */
    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            filterKvPairs(mSearchEditText.getText().toString().trim());
            dialog.dismiss();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 更新排序文本
     */
    private void updateSortText() {
        mSortText.setText(mCurrentSortOption.getShortName());
    }

    /**
     * 显示添加/编辑对话框
     */
    private void showAddEditDialog(KvPair kvPair) {
        AddEditDialog dialog = new AddEditDialog(this, kvPair, new AddEditDialog.OnSaveListener() {
            @Override
            public void onSave(KvPair newKvPair) {
                saveKvPair(newKvPair);
            }
        });
        dialog.show();
    }

    /**
     * 保存键值对
     */
    private void saveKvPair(KvPair kvPair) {
        boolean success = false;

        try {
            // 使用StorageManager获取存储适配器
            StorageConfig config = new StorageConfig.Builder()
                    .setName(mFileName)
                    .setType(mCurrentStorageType)
                    .build();
            StorageAdapter adapter = StorageManager.getInstance(this).getAdapter(config);

            // 根据数据类型调用对应的存储方法
            switch (kvPair.getDataType()) {
                case STRING:
                    adapter.putString(kvPair.getKey(), (String) kvPair.getValue());
                    break;
                case INTEGER:
                    adapter.putInt(kvPair.getKey(), (Integer) kvPair.getValue());
                    break;
                case LONG:
                    adapter.putLong(kvPair.getKey(), (Long) kvPair.getValue());
                    break;
                case FLOAT:
                    adapter.putFloat(kvPair.getKey(), (Float) kvPair.getValue());
                    break;
                case BOOLEAN:
                    adapter.putBoolean(kvPair.getKey(), (Boolean) kvPair.getValue());
                    break;
                case STRING_SET:
                    adapter.putStringSet(kvPair.getKey(), (java.util.Set<String>) kvPair.getValue());
                    break;
            }
            success = true;
        } catch (Exception e) {
            android.util.Log.e("KeyValueEditorActivity", "保存失败", e);
            success = false;
        }

        if (success) {
            showToast("保存成功");
            loadKvPairs(); // 重新加载数据
        } else {
            showToast("保存失败");
        }
    }

    /**
     * 删除键值对
     */
    private void deleteKvPair(KvPair kvPair) {
        boolean success = false;

        try {
            // 使用StorageManager获取存储适配器
            StorageConfig config = new StorageConfig.Builder()
                    .setName(mFileName)
                    .setType(mCurrentStorageType)
                    .build();
            StorageAdapter adapter = StorageManager.getInstance(this).getAdapter(config);
            adapter.remove(kvPair.getKey());
            success = true;
        } catch (Exception e) {
            android.util.Log.e("KeyValueEditorActivity", "删除失败", e);
            success = false;
        }

        if (success) {
            showToast("删除成功");
            loadKvPairs(); // 重新加载数据
        } else {
            showToast("删除失败");
        }
    }

    /**
     * 显示Toast提示
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}