package io.github.xesam.android.kveditor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
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
    private StorageType mCurrentStorageType;

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
        
        all = storageAdapter.getAll();

        mKvPairList = new ArrayList<>();

        if (all != null) {
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                KvPair.DataType dataType = DataTypeConverter.getDataTypeByObject(value);
                mKvPairList.add(new KvPair(key, value, dataType));
            }
        }

        // 更新UI
        findViewById(R.id.progress_bar).setVisibility(View.GONE);

        if (mKvPairList.isEmpty()) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            // 创建或更新适配器
            if (mAdapter == null) {
                mAdapter = new KeyValueAdapter(mKvPairList, new KeyValueAdapter.OnItemClickListener() {
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
                mAdapter.updateData(mKvPairList);
            }
        }
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