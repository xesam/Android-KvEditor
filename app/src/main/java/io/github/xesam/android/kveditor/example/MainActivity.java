package io.github.xesam.android.kveditor.example;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.xesam.android.kveditor.KvEditor;
import io.github.xesam.android.kveditor.storage.base.StorageManager;
import io.github.xesam.android.kveditor.storage.base.StorageType;

public class MainActivity extends AppCompatActivity {

    private TextView dataDisplayTextView;
    private String currentFile = "user_preferences";
    private Random random = new Random();
    private StorageType currentStorageType = StorageType.SHARED_PREFERENCE;
    private RadioGroup radioStorageType;
    private LinearLayout layoutSharedPrefButtons;
    private LinearLayout layoutMmkvButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置EdgeToEdge显示
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dataDisplayTextView = findViewById(R.id.tv_data_display);

        // 初始化MMKV
        MmkvDataGenerator.initializeMmkv(this);

        // 检查并生成示例数据
        if (!ExampleDataGenerator.hasExampleData(this)) {
            ExampleDataGenerator.generateExampleData(this);
        }

        // 初始化MMKV示例数据
        if (!MmkvDataGenerator.hasMmkvData(this, MmkvDataGenerator.MMKV_USER_DATA)) {
            MmkvDataGenerator.createUserDataMmkv(this);
        }

        setupButtons();
        updateDataDisplay();
    }

    private void setupButtons() {
        findViewById(R.id.btn_open_default).setOnClickListener(v -> {
            KvEditor.open(MainActivity.this);
        });
        findViewById(R.id.btn_open_sharedpref).setOnClickListener(v -> {
            KvEditor.open(MainActivity.this, StorageType.SHARED_PREFERENCE);
        });
        findViewById(R.id.btn_open_mmkv).setOnClickListener(v -> {
            KvEditor.open(MainActivity.this, StorageType.MMKV);
        });

        // 初始化存储类型相关控件
        radioStorageType = findViewById(R.id.radio_storage_type);
        layoutSharedPrefButtons = findViewById(R.id.layout_shared_pref_buttons);
        layoutMmkvButtons = findViewById(R.id.layout_mmkv_buttons);

        // 存储类型切换监听
        radioStorageType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_shared_pref) {
                currentStorageType = StorageType.SHARED_PREFERENCE;
                layoutSharedPrefButtons.setVisibility(View.VISIBLE);
                layoutMmkvButtons.setVisibility(View.GONE);
                currentFile = "user_preferences";
            } else if (checkedId == R.id.radio_mmkv) {
                currentStorageType = StorageType.MMKV;
                layoutSharedPrefButtons.setVisibility(View.GONE);
                layoutMmkvButtons.setVisibility(View.VISIBLE);
                currentFile = MmkvDataGenerator.MMKV_USER_DATA;
            }
            updateDataDisplay();
        });
        // 初始化数据按钮
        findViewById(R.id.btn_init_user).setOnClickListener(v -> {
            EnhancedDataManager.createUserPreferences(this);
            currentFile = "user_preferences";
            updateDataDisplay();
            Toast.makeText(this, "用户偏好数据已创建", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_init_app).setOnClickListener(v -> {
            EnhancedDataManager.createAppSettings(this);
            currentFile = "app_settings";
            updateDataDisplay();
            Toast.makeText(this, "应用设置数据已创建", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_init_game).setOnClickListener(v -> {
            EnhancedDataManager.createGameData(this);
            currentFile = "game_data";
            updateDataDisplay();
            Toast.makeText(this, "游戏数据已创建", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_init_config).setOnClickListener(v -> {
            EnhancedDataManager.createAppConfig(this);
            currentFile = "app_config";
            updateDataDisplay();
            Toast.makeText(this, "应用配置已创建", Toast.LENGTH_SHORT).show();
        });

        // 数据操作按钮
        findViewById(R.id.btn_add_random).setOnClickListener(v -> {
            addRandomData();
            updateDataDisplay();
        });

        findViewById(R.id.btn_update_data).setOnClickListener(v -> {
            updateExistingData();
            updateDataDisplay();
        });

        findViewById(R.id.btn_delete_data).setOnClickListener(v -> {
            deleteRandomData();
            updateDataDisplay();
        });

        findViewById(R.id.btn_clear_file).setOnClickListener(v -> {
            if (currentStorageType == StorageType.SHARED_PREFERENCE) {
                SharedPreferences prefs = getSharedPreferences(currentFile, MODE_PRIVATE);
                prefs.edit().clear().apply();
            } else {
                // 清空MMKV数据
                StorageManager.getInstance(this).getAdapter(StorageType.MMKV, currentFile).clear();
            }
            updateDataDisplay();
            Toast.makeText(this, "文件已清空", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_refresh_display).setOnClickListener(v -> {
            updateDataDisplay();
            Toast.makeText(this, "显示已刷新", Toast.LENGTH_SHORT).show();
        });

        // MMKV初始化按钮
        findViewById(R.id.btn_init_mmkv_user).setOnClickListener(v -> {
            // 初始化MMKV
            MmkvDataGenerator.initializeMmkv(this);
            MmkvDataGenerator.createUserDataMmkv(this);
            currentFile = MmkvDataGenerator.MMKV_USER_DATA;
            updateDataDisplay();
            Toast.makeText(this, "MMKV用户数据已创建", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_init_mmkv_cache).setOnClickListener(v -> {
            // 初始化MMKV
            MmkvDataGenerator.initializeMmkv(this);
            MmkvDataGenerator.createAppCacheMmkv(this);
            currentFile = MmkvDataGenerator.MMKV_APP_CACHE;
            updateDataDisplay();
            Toast.makeText(this, "MMKV应用缓存已创建", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_init_mmkv_game).setOnClickListener(v -> {
            // 初始化MMKV
            MmkvDataGenerator.initializeMmkv(this);
            MmkvDataGenerator.createGameProgressMmkv(this);
            currentFile = MmkvDataGenerator.MMKV_GAME_PROGRESS;
            updateDataDisplay();
            Toast.makeText(this, "MMKV游戏进度已创建", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_init_mmkv_system).setOnClickListener(v -> {
            // 初始化MMKV
            MmkvDataGenerator.initializeMmkv(this);
            MmkvDataGenerator.createSystemConfigMmkv(this);
            currentFile = MmkvDataGenerator.MMKV_SYSTEM_CONFIG;
            updateDataDisplay();
            Toast.makeText(this, "MMKV系统配置已创建", Toast.LENGTH_SHORT).show();
        });

        // 存储类型测试按钮
        findViewById(R.id.btn_test_storage).setOnClickListener(v -> {
            runStorageTests();
        });
    }

    private void addRandomData() {
        if (currentStorageType == StorageType.SHARED_PREFERENCE) {
            SharedPreferences prefs = getSharedPreferences(currentFile, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String[] keys = {"random_key_" + random.nextInt(1000), "test_" + System.currentTimeMillis(), "auto_" + random.nextInt(100)};
            String key = keys[random.nextInt(keys.length)];

            switch (random.nextInt(4)) {
                case 0:
                    editor.putString(key, "随机字符串_" + random.nextInt(1000));
                    break;
                case 1:
                    editor.putInt(key, random.nextInt(10000));
                    break;
                case 2:
                    editor.putBoolean(key, random.nextBoolean());
                    break;
                case 3:
                    editor.putFloat(key, random.nextFloat() * 100);
                    break;
            }

            editor.apply();
            Toast.makeText(this, "已添加随机数据: " + key, Toast.LENGTH_SHORT).show();
        } else {
            // MMKV数据
            String key = "test_key_" + random.nextInt(1000);
            StorageManager.getInstance(this).getAdapter(StorageType.MMKV, currentFile)
                    .putString(key, "test_value_" + random.nextInt(1000));
            Toast.makeText(this, "已添加随机数据: " + key, Toast.LENGTH_SHORT).show();
        }
        updateDataDisplay();
    }

    private void updateExistingData() {
        if (currentStorageType == StorageType.SHARED_PREFERENCE) {
            SharedPreferences prefs = getSharedPreferences(currentFile, MODE_PRIVATE);
            Map<String, ?> allData = prefs.getAll();

            if (allData.isEmpty()) {
                Toast.makeText(this, "当前文件为空，无法更新数据", Toast.LENGTH_SHORT).show();
                return;
            }

            // 随机选择一个键进行更新
            List<String> keys = new ArrayList<>(allData.keySet());
            String randomKey = keys.get(random.nextInt(keys.size()));

            SharedPreferences.Editor editor = prefs.edit();
            Object value = allData.get(randomKey);

            if (value instanceof String) {
                editor.putString(randomKey, "更新后的字符串_" + System.currentTimeMillis());
            } else if (value instanceof Integer) {
                editor.putInt(randomKey, random.nextInt(10000));
            } else if (value instanceof Boolean) {
                editor.putBoolean(randomKey, !((Boolean) value));
            } else if (value instanceof Float) {
                editor.putFloat(randomKey, random.nextFloat() * 100);
            }

            editor.apply();
        } else {
            // MMKV数据更新
            Map<String, ?> allData = StorageManager.getInstance(this)
                    .getAdapter(StorageType.MMKV, currentFile).getAll();
            if (allData.isEmpty()) {
                Toast.makeText(this, "当前文件为空，无法更新数据", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> keys = new ArrayList<>(allData.keySet());
            String randomKey = keys.get(random.nextInt(keys.size()));
            StorageManager.getInstance(this).getAdapter(StorageType.MMKV, currentFile)
                    .putString(randomKey, "更新后的字符串_" + System.currentTimeMillis());
        }

        updateDataDisplay();
        Toast.makeText(this, "已更新数据", Toast.LENGTH_SHORT).show();
    }

    private void deleteRandomData() {
        if (currentStorageType == StorageType.SHARED_PREFERENCE) {
            SharedPreferences prefs = getSharedPreferences(currentFile, MODE_PRIVATE);
            Map<String, ?> allData = prefs.getAll();

            if (allData.isEmpty()) {
                Toast.makeText(this, "当前文件为空，无法删除数据", Toast.LENGTH_SHORT).show();
                return;
            }

            // 随机选择一个键进行删除
            List<String> keys = new ArrayList<>(allData.keySet());
            String randomKey = keys.get(random.nextInt(keys.size()));

            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(randomKey);
            editor.apply();
        } else {
            // MMKV数据删除
            Map<String, ?> allData = StorageManager.getInstance(this)
                    .getAdapter(StorageType.MMKV, currentFile).getAll();
            if (allData.isEmpty()) {
                Toast.makeText(this, "当前文件为空，无法删除数据", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> keys = new ArrayList<>(allData.keySet());
            String randomKey = keys.get(random.nextInt(keys.size()));
            StorageManager.getInstance(this).getAdapter(StorageType.MMKV, currentFile).remove(randomKey);
        }

        updateDataDisplay();
        Toast.makeText(this, "已删除数据", Toast.LENGTH_SHORT).show();
    }

    private void updateDataDisplay() {
        StringBuilder display = new StringBuilder();
        display.append("存储类型: ").append(currentStorageType == StorageType.SHARED_PREFERENCE ? "SharedPreferences" : "MMKV").append("\n");
        display.append("当前文件: ").append(currentFile).append("\n");

        if (currentStorageType == StorageType.SHARED_PREFERENCE) {
            SharedPreferences prefs = getSharedPreferences(currentFile, MODE_PRIVATE);
            Map<String, ?> allData = prefs.getAll();

            display.append("数据总数: ").append(allData.size()).append("\n\n");

            for (Map.Entry<String, ?> entry : allData.entrySet()) {
                display.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }

            if (allData.isEmpty()) {
                display.append("暂无数据");
            }
        } else {
            // MMKV数据显示
            Map<String, ?> allData = StorageManager.getInstance(this)
                    .getAdapter(StorageType.MMKV, currentFile).getAll();

            display.append("数据总数: ").append(allData.size()).append("\n\n");

            for (Map.Entry<String, ?> entry : allData.entrySet()) {
                display.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }

            if (allData.isEmpty()) {
                display.append("暂无数据");
            }
        }

        dataDisplayTextView.setText(display.toString());
    }

    private void runStorageTests() {
        StringBuilder testResult = new StringBuilder();
        testResult.append("=== 存储类型测试 ===\n\n");

        // 测试SharedPreferences
        testResult.append("【SharedPreferences测试】\n");
        SharedPreferences sharedPrefs = getSharedPreferences("test_shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        // 添加测试数据
        editor.putString("test_string", "Hello SharedPreferences");
        editor.putInt("test_int", 12345);
        editor.putBoolean("test_bool", true);
        editor.putFloat("test_float", 3.14f);
        editor.putLong("test_long", System.currentTimeMillis());
        editor.apply();

        // 读取测试
        Map<String, ?> sharedData = sharedPrefs.getAll();
        testResult.append("数据数量: ").append(sharedData.size()).append("\n");
        for (Map.Entry<String, ?> entry : sharedData.entrySet()) {
            testResult.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }

        testResult.append("\n【MMKV测试】\n");
        try {
            // 测试MMKV
            StorageManager.getInstance(this).getAdapter(StorageType.MMKV, "test_mmkv")
                    .putString("mmkv_string", "Hello MMKV");
            StorageManager.getInstance(this).getAdapter(StorageType.MMKV, "test_mmkv")
                    .putInt("mmkv_int", 67890);
            StorageManager.getInstance(this).getAdapter(StorageType.MMKV, "test_mmkv")
                    .putBoolean("mmkv_bool", false);

            Map<String, ?> mmkvData = StorageManager.getInstance(this)
                    .getAdapter(StorageType.MMKV, "test_mmkv").getAll();
            testResult.append("数据数量: ").append(mmkvData.size()).append("\n");
            for (Map.Entry<String, ?> entry : mmkvData.entrySet()) {
                testResult.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }

            testResult.append("\n✅ 两种存储类型测试完成！");
        } catch (Exception e) {
            testResult.append("❌ MMKV测试失败: ").append(e.getMessage());
        }

        dataDisplayTextView.setText(testResult.toString());
        Toast.makeText(this, "存储类型测试完成", Toast.LENGTH_SHORT).show();
    }
}