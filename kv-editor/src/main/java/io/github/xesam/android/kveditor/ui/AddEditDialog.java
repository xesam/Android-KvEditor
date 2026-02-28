package io.github.xesam.android.kveditor.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import io.github.xesam.android.kveditor.R;
import io.github.xesam.android.kveditor.model.KvPair;
import io.github.xesam.android.kveditor.storage.base.StorageType;
import io.github.xesam.android.kveditor.utils.DataTypeConverter;

/**
 * 添加/编辑键值对对话框
 */
public class AddEditDialog extends AlertDialog {

    private EditText mKeyEditText;
    private Spinner mTypeSpinner;
    private Button mSaveButton;
    
    // 不同数据类型的输入控件
    private EditText mStringValueEditText;
    private EditText mIntegerValueEditText;
    private TextView mIntegerErrorText;
    private LinearLayout mIntegerValueContainer;
    private EditText mLongValueEditText;
    private TextView mLongErrorText;
    private LinearLayout mLongValueContainer;
    private EditText mFloatValueEditText;
    private TextView mFloatErrorText;
    private LinearLayout mFloatValueContainer;
    private RadioGroup mBooleanValueRadioGroup;
    private RadioButton mBooleanTrueRadio;
    private RadioButton mBooleanFalseRadio;
    private EditText mStringSetItemEditText;
    
    private KvPair mCurrentKvPair;
    private OnSaveListener mSaveListener;
    private KvPair.DataType mSelectedDataType = KvPair.DataType.STRING;

    public interface OnSaveListener {
        void onSave(KvPair kvPair);
    }

    public AddEditDialog(Context context, @Nullable KvPair kvPair, OnSaveListener saveListener) {
        super(context);
        mCurrentKvPair = kvPair;
        mSaveListener = saveListener;
        
        // 设置取消监听器
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 标记对话框已取消
                mIsDialogCanceled = true;
            }
        });
        
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit, null);
        setView(view);

        mKeyEditText = view.findViewById(R.id.key_edit_text);
        mTypeSpinner = view.findViewById(R.id.type_spinner);
        mSaveButton = view.findViewById(R.id.save_button);
        
        // 初始化不同数据类型的输入控件
        mStringValueEditText = view.findViewById(R.id.string_value_edit_text);
        mIntegerValueEditText = view.findViewById(R.id.integer_value_edit_text);
        mIntegerErrorText = view.findViewById(R.id.integer_error_text);
        mIntegerValueContainer = view.findViewById(R.id.integer_value_container);
        mLongValueEditText = view.findViewById(R.id.long_value_edit_text);
        mLongErrorText = view.findViewById(R.id.long_error_text);
        mLongValueContainer = view.findViewById(R.id.long_value_container);
        mFloatValueEditText = view.findViewById(R.id.float_value_edit_text);
        mFloatErrorText = view.findViewById(R.id.float_error_text);
        mFloatValueContainer = view.findViewById(R.id.float_value_container);
        mBooleanValueRadioGroup = view.findViewById(R.id.boolean_value_radio_group);
        mBooleanTrueRadio = view.findViewById(R.id.boolean_true_radio);
        mBooleanFalseRadio = view.findViewById(R.id.boolean_false_radio);
        mStringSetItemEditText = view.findViewById(R.id.string_set_item_edit_text);

        // 设置对话框标题
        setTitle(mCurrentKvPair == null ? "添加键值对" : "编辑键值对");

        // 初始化数据类型选择器
        initTypeSpinner();

        // 如果是编辑模式，填充现有数据
        if (mCurrentKvPair != null) {
            mKeyEditText.setText(mCurrentKvPair.getKey());
            mKeyEditText.setEnabled(false); // 编辑模式下不允许修改键名
            
            // 设置数据类型
            for (int i = 0; i < KvPair.DataType.values().length; i++) {
                if (KvPair.DataType.values()[i] == mCurrentKvPair.getDataType()) {
                    mTypeSpinner.setSelection(i);
                    break;
                }
            }
            
            // 根据数据类型设置不同输入控件的值
            setValueToControl(mCurrentKvPair);
        }

        // 添加文本变化监听，检查保存按钮是否可用
        mKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 为所有文本输入框添加变化监听
        addTextChangeListener(mStringValueEditText);
        addTextChangeListener(mIntegerValueEditText);
        addTextChangeListener(mLongValueEditText);
        addTextChangeListener(mFloatValueEditText);
        addTextChangeListener(mStringSetItemEditText);
        
        // 为数值输入框添加输入过滤器
        setupInputFilters();
        
        // 为Boolean类型的RadioGroup添加变化监听
        mBooleanValueRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateSaveButtonState();
        });

        // 设置保存按钮点击事件
        mSaveButton.setOnClickListener(v -> saveKvPair());
        
        // 设置取消按钮点击事件
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        // 初始更新保存按钮状态
        updateSaveButtonState();
        
        // 初始化显示对应的数据类型输入控件
        updateValueControlVisibility();
    }

    private boolean mIsDialogCanceled = false;

    private void initTypeSpinner() {
        // 准备数据类型名称数组
        String[] typeNames = new String[KvPair.DataType.values().length];
        for (int i = 0; i < KvPair.DataType.values().length; i++) {
            typeNames[i] = DataTypeConverter.getDataTypeName(KvPair.DataType.values()[i]);
        }

        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, typeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);

        // 设置选择监听
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedDataType = KvPair.DataType.values()[position];
                updateSaveButtonState();
                
                // 检查对话框是否已取消
            if (!mIsDialogCanceled) {
                // 根据选择的数据类型显示对应的输入控件
                updateValueControlVisibility();
                
                // 清除当前可见输入框的焦点，以便后续重新验证
                clearCurrentFocus();
            }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateSaveButtonState() {
        // 检查对话框是否已取消且视图是否仍然可用
        if (mIsDialogCanceled || mKeyEditText == null || mSaveButton == null ||
                !mKeyEditText.isAttachedToWindow() || !mSaveButton.isAttachedToWindow()) {
            return;
        }
        
        String key = mKeyEditText.getText().toString().trim();

        // 键名不能为空
        if (TextUtils.isEmpty(key)) {
            mSaveButton.setEnabled(false);
            return;
        }

        // 根据数据类型验证值
        if (!isValidValueForCurrentType()) {
            mSaveButton.setEnabled(false);
            return;
        }

        mSaveButton.setEnabled(true);
    }

    /**
     * 根据数据类型设置不同输入控件的值
     */
    private void setValueToControl(KvPair kvPair) {
        if (kvPair == null) return;
        
        Object value = kvPair.getValue();
        String valueStr = DataTypeConverter.convertValueToString(value);
        
        switch (kvPair.getDataType()) {
            case STRING:
                mStringValueEditText.setText(valueStr);
                break;
            case INTEGER:
                mIntegerValueEditText.setText(valueStr);
                break;
            case LONG:
                mLongValueEditText.setText(valueStr);
                break;
            case FLOAT:
                mFloatValueEditText.setText(valueStr);
                break;
            case BOOLEAN:
                if (value instanceof Boolean) {
                    boolean booleanValue = (Boolean) value;
                    if (booleanValue) {
                        mBooleanTrueRadio.setChecked(true);
                    } else {
                        mBooleanFalseRadio.setChecked(true);
                    }
                }
                break;
            case STRING_SET:
                mStringSetItemEditText.setText(valueStr);
                break;
        }
        
        // 显示对应的控件
        updateValueControlVisibility();
    }
    
    /**
     * 根据选择的数据类型显示对应的输入控件
     */
    private void updateValueControlVisibility() {
        // 隐藏所有控件
        mStringValueEditText.setVisibility(View.GONE);
        mIntegerValueContainer.setVisibility(View.GONE);
        mLongValueContainer.setVisibility(View.GONE);
        mFloatValueContainer.setVisibility(View.GONE);
        mBooleanValueRadioGroup.setVisibility(View.GONE);
        ((LinearLayout)mStringSetItemEditText.getParent()).setVisibility(View.GONE);
        
        // 隐藏所有错误提示
        mIntegerErrorText.setVisibility(View.GONE);
        mLongErrorText.setVisibility(View.GONE);
        mFloatErrorText.setVisibility(View.GONE);
        
        // 显示当前类型对应的控件
        switch (mSelectedDataType) {
            case STRING:
                mStringValueEditText.setVisibility(View.VISIBLE);
                break;
            case INTEGER:
                mIntegerValueContainer.setVisibility(View.VISIBLE);
                break;
            case LONG:
                mLongValueContainer.setVisibility(View.VISIBLE);
                break;
            case FLOAT:
                mFloatValueContainer.setVisibility(View.VISIBLE);
                break;
            case BOOLEAN:
                mBooleanValueRadioGroup.setVisibility(View.VISIBLE);
                break;
            case STRING_SET:
                ((LinearLayout)mStringSetItemEditText.getParent()).setVisibility(View.VISIBLE);
                break;
        }
    }
    
    /**
     * 清除当前可见输入框的焦点
     */
    private void clearCurrentFocus() {
        switch (mSelectedDataType) {
            case STRING:
                mStringValueEditText.clearFocus();
                break;
            case INTEGER:
                mIntegerValueEditText.clearFocus();
                break;
            case LONG:
                mLongValueEditText.clearFocus();
                break;
            case FLOAT:
                mFloatValueEditText.clearFocus();
                break;
            case BOOLEAN:
                // Boolean 类型没有输入框，无需处理
                break;
            case STRING_SET:
                mStringSetItemEditText.clearFocus();
                break;
        }
    }
    
    /**
     * 为EditText添加文本变化监听
     */
    private void addTextChangeListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    /**
     * 设置输入过滤器
     */
    private void setupInputFilters() {
        // Integer类型：只允许数字和负号
        mIntegerValueEditText.setFilters(new InputFilter[]{
            (source, start, end, dest, dstart, dend) -> {
                String input = dest.toString() + source.toString();
                // 允许空字符串、负号、或者有效的整数
                if (input.isEmpty() || input.equals("-") || input.matches("-?\\d+")) {
                    return null; // 接受输入
                }
                return ""; // 拒绝输入
            }
        });
        
        // Long类型：只允许数字和负号
        mLongValueEditText.setFilters(new InputFilter[]{
            (source, start, end, dest, dstart, dend) -> {
                String input = dest.toString() + source.toString();
                // 允许空字符串、负号、或者有效的长整数
                if (input.isEmpty() || input.equals("-") || input.matches("-?\\d+")) {
                    return null; // 接受输入
                }
                return ""; // 拒绝输入
            }
        });
        
        // Float类型：允许数字、负号和小数点
        mFloatValueEditText.setFilters(new InputFilter[]{
            (source, start, end, dest, dstart, dend) -> {
                String input = dest.toString() + source.toString();
                // 允许空字符串、负号、小数点、或者有效的浮点数
                if (input.isEmpty() || input.equals("-") || input.equals("-.") || 
                    input.matches("-?\\d+") || input.matches("-?\\d+\\.\\d*") || input.matches("-?\\.\\d+")) {
                    return null; // 接受输入
                }
                return ""; // 拒绝输入
            }
        });
    }
    
    /**
     * 根据当前选择的数据类型验证值是否有效
     */
    private boolean isValidValueForCurrentType() {
        // 隐藏所有错误提示
        mIntegerErrorText.setVisibility(View.GONE);
        mLongErrorText.setVisibility(View.GONE);
        mFloatErrorText.setVisibility(View.GONE);
        
        switch (mSelectedDataType) {
            case STRING:
                // String类型允许空值
                return true;
            case INTEGER:
                String intValue = mIntegerValueEditText.getText().toString().trim();
                if (TextUtils.isEmpty(intValue)) return false;
                try {
                    Integer.parseInt(intValue);
                    return true;
                } catch (NumberFormatException e) {
                    mIntegerErrorText.setVisibility(View.VISIBLE);
                    return false;
                }
            case LONG:
                String longValue = mLongValueEditText.getText().toString().trim();
                if (TextUtils.isEmpty(longValue)) return false;
                try {
                    Long.parseLong(longValue);
                    return true;
                } catch (NumberFormatException e) {
                    mLongErrorText.setVisibility(View.VISIBLE);
                    return false;
                }
            case FLOAT:
                String floatValue = mFloatValueEditText.getText().toString().trim();
                if (TextUtils.isEmpty(floatValue)) return false;
                try {
                    Float.parseFloat(floatValue);
                    return true;
                } catch (NumberFormatException e) {
                    mFloatErrorText.setVisibility(View.VISIBLE);
                    return false;
                }
            case BOOLEAN:
                // Boolean类型总是有效的（true或false）
                return true;
            case STRING_SET:
                // StringSet类型允许空值
                return true;
            default:
                return false;
        }
    }
    
    /**
     * 获取当前输入控件的值
     */
    private Object getCurrentValue() {
        switch (mSelectedDataType) {
            case STRING:
                return mStringValueEditText.getText().toString().trim();
            case INTEGER:
                try {
                    return Integer.parseInt(mIntegerValueEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            case LONG:
                try {
                    return Long.parseLong(mLongValueEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    return 0L;
                }
            case FLOAT:
                try {
                    return Float.parseFloat(mFloatValueEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    return 0.0f;
                }
            case BOOLEAN:
                return mBooleanTrueRadio.isChecked();
            case STRING_SET:
                String input = mStringSetItemEditText.getText().toString().trim();
                java.util.Set<String> stringSet = new java.util.HashSet<>();
                if (!TextUtils.isEmpty(input)) {
                    String[] values = input.split(",");
                    for (String value : values) {
                        if (!TextUtils.isEmpty(value)) {
                            stringSet.add(value.trim());
                        }
                    }
                }
                return stringSet;
            default:
                return null;
        }
    }
    
    /**
     * 处理取消按钮点击事件 - 用于XML布局中的android:onClick属性
     */
    public void cancel(View view) {
        dismiss();
    }
    
    /**
     * 处理保存按钮点击事件 - 用于XML布局中的android:onClick属性
     */
    public void save(View view) {
        saveKvPair();
    }
    
    /**
     * 保存键值对
     */
    private void saveKvPair() {
        String key = mKeyEditText.getText().toString().trim();

        if (TextUtils.isEmpty(key)) {
            Toast.makeText(getContext(), "键名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 获取当前数据类型对应的值
            Object value = getCurrentValue();

            if (mCurrentKvPair != null) {
                // 编辑模式 - 需要同时更新 value 和 dataType
                mCurrentKvPair.setValue(value);
                mCurrentKvPair.setDataType(mSelectedDataType); // 🔧 修复：更新数据类型
                if (mSaveListener != null) {
                    mSaveListener.onSave(mCurrentKvPair);
                }
            } else {
                // 添加模式
                // 创建新的KvPair对象
                KvPair newKvPair = new KvPair(key, value, mSelectedDataType);

                // 回调保存监听器
                if (mSaveListener != null) {
                    mSaveListener.onSave(newKvPair);
                }
            }

            // 关闭对话框
            dismiss();
        } catch (Exception e) {
            android.util.Log.e("AddEditDialog", "保存失败", e);
            Toast.makeText(getContext(), "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}