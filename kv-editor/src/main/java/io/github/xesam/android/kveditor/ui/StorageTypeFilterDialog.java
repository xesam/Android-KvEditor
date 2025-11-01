package io.github.xesam.android.kveditor.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import io.github.xesam.android.kveditor.R;
import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * 存储类型过滤器对话框
 */
public class StorageTypeFilterDialog extends Dialog {

    private LinearLayout mContainer;
    private TextView mSelectAllText;
    private TextView mDeselectAllText;
    private TextView mOkButton;
    
    private Set<StorageType> mSelectedTypes;
    private OnStorageTypesSelectedListener mListener;
    
    public interface OnStorageTypesSelectedListener {
        void onStorageTypesSelected(Set<StorageType> selectedTypes);
    }
    
    private Set<StorageType> mAvailableTypes;
    
    private StorageTypeFilterDialog(@NonNull Context context, Set<StorageType> availableTypes, 
                                  Set<StorageType> selectedTypes, OnStorageTypesSelectedListener listener) {
        super(context);
        mAvailableTypes = new HashSet<>(availableTypes);
        mSelectedTypes = new HashSet<>(selectedTypes);
        // 确保选中的类型都在可用类型中
        mSelectedTypes.retainAll(mAvailableTypes);
        mListener = listener;
    }
    
    /**
     * 显示存储类型过滤对话框
     * @param context 上下文
     * @param selectedTypes 已选择的存储类型
     * @param listener 选择监听器
     */
    public static void show(Context context, Set<StorageType> selectedTypes, 
                           OnStorageTypesSelectedListener listener) {
        // 兼容旧方法，使用所有存储类型作为可用类型
        Set<StorageType> availableTypes = new HashSet<>();
        for (StorageType type : StorageType.getSupportedTypes()) {
            availableTypes.add(type);
        }
        show(context, availableTypes, selectedTypes, listener);
    }
    
    /**
     * 显示存储类型过滤对话框
     * @param context 上下文
     * @param availableTypes 可用的存储类型
     * @param selectedTypes 已选择的存储类型
     * @param listener 选择监听器
     */
    public static void show(Context context, Set<StorageType> availableTypes, 
                           Set<StorageType> selectedTypes, OnStorageTypesSelectedListener listener) {
        StorageTypeFilterDialog dialog = new StorageTypeFilterDialog(context, availableTypes, selectedTypes, listener);
        dialog.show();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_storage_type_filter);
        
        // 设置对话框宽度为屏幕宽度的80%
        if (getWindow() != null) {
            android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
            getWindow().setAttributes(params);
        }
        
        mContainer = findViewById(R.id.container);
        mSelectAllText = findViewById(R.id.select_all_text);
        mDeselectAllText = findViewById(R.id.deselect_all_text);
        mOkButton = findViewById(R.id.ok_button);
        
        // 填充存储类型
        fillStorageTypes();
        
        // 设置全选按钮
        mSelectAllText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedTypes.clear();
                mSelectedTypes.addAll(mAvailableTypes);
                updateUI();
            }
        });
        
        // 设置全不选按钮
        mDeselectAllText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedTypes.clear();
                updateUI();
            }
        });
        
        // 设置确定按钮
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onStorageTypesSelected(new HashSet<>(mSelectedTypes));
                }
                dismiss();
            }
        });
    }
    
    /**
     * 更新UI状态
     */
    private void updateUI() {
        // 重新填充存储类型以更新选择状态
        fillStorageTypes();
    }
    
    private void fillStorageTypes() {
        mContainer.removeAllViews();
        
        for (final StorageType type : mAvailableTypes) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_storage_type_filter, mContainer, false);
            final CheckBox checkBox = itemView.findViewById(R.id.checkbox);
            TextView textView = itemView.findViewById(R.id.text_view);
            
            textView.setText(type.getDescription());
            checkBox.setChecked(mSelectedTypes.contains(type));
            
            // 设置checkbox选中状态变化监听
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedTypes.add(type);
                    } else {
                        mSelectedTypes.remove(type);
                    }
                }
            });
            
            // 添加整个选项行的点击事件，点击整行切换选中状态
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 切换checkbox的选中状态
                    checkBox.setChecked(!checkBox.isChecked());
                    // 不需要在这里手动更新mSelectedTypes，因为checkbox的onCheckedChanged会处理
                }
            });
            
            mContainer.addView(itemView);
        }
    }
    

}