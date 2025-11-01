package io.github.xesam.android.kveditor.utils;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import io.github.xesam.android.kveditor.model.KvPair;

/**
 * 数据类型转换器，用于处理不同数据类型之间的转换
 */
public class DataTypeConverter {

    /**
     * 将字符串值转换为指定的数据类型
     */
    public static Object convertStringToValue(String stringValue, KvPair.DataType dataType) {
        if (TextUtils.isEmpty(stringValue)) {
            return getDefaultValue(dataType);
        }

        switch (dataType) {
            case STRING:
                return stringValue;
            case INTEGER:
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    return 0;
                }
            case LONG:
                try {
                    return Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            case FLOAT:
                try {
                    return Float.parseFloat(stringValue);
                } catch (NumberFormatException e) {
                    return 0.0f;
                }
            case BOOLEAN:
                return Boolean.parseBoolean(stringValue);
            case STRING_SET:
                // 简单实现：使用逗号分隔的字符串作为StringSet
                Set<String> stringSet = new HashSet<>();
                String[] values = stringValue.split(",");
                for (String value : values) {
                    if (!TextUtils.isEmpty(value)) {
                        stringSet.add(value.trim());
                    }
                }
                return stringSet;
            default:
                return stringValue;
        }
    }

    /**
     * 将任意值转换为字符串表示
     */
    public static String convertValueToString(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Set) {
            // 简单实现：将StringSet转换为逗号分隔的字符串
            return String.join(", ", (Set<String>) value);
        } else {
            return value.toString();
        }
    }

    /**
     * 根据对象的实际类型确定DataType
     */
    public static KvPair.DataType getDataTypeByObject(Object value) {
        if (value == null) {
            return KvPair.DataType.STRING;
        }

        if (value instanceof String) {
            return KvPair.DataType.STRING;
        } else if (value instanceof Integer) {
            return KvPair.DataType.INTEGER;
        } else if (value instanceof Long) {
            return KvPair.DataType.LONG;
        } else if (value instanceof Float) {
            return KvPair.DataType.FLOAT;
        } else if (value instanceof Boolean) {
            return KvPair.DataType.BOOLEAN;
        } else if (value instanceof Set) {
            return KvPair.DataType.STRING_SET;
        } else {
            return KvPair.DataType.STRING;
        }
    }

    /**
     * 获取指定数据类型的默认值
     */
    public static Object getDefaultValue(KvPair.DataType dataType) {
        switch (dataType) {
            case STRING:
                return "";
            case INTEGER:
                return 0;
            case LONG:
                return 0L;
            case FLOAT:
                return 0.0f;
            case BOOLEAN:
                return false;
            case STRING_SET:
                return new HashSet<String>();
            default:
                return "";
        }
    }

    /**
     * 验证字符串是否可以转换为指定的数据类型
     */
    public static boolean isValidValue(String stringValue, KvPair.DataType dataType) {
        if (TextUtils.isEmpty(stringValue) && dataType != KvPair.DataType.STRING) {
            return false;
        }

        try {
            switch (dataType) {
                case STRING:
                    return true;
                case INTEGER:
                    Integer.parseInt(stringValue);
                    return true;
                case LONG:
                    Long.parseLong(stringValue);
                    return true;
                case FLOAT:
                    Float.parseFloat(stringValue);
                    return true;
                case BOOLEAN:
                    // "true" or "false"，不区分大小写
                    return stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("false");
                case STRING_SET:
                    // 简单验证：非空即可
                    return true;
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取数据类型的可读名称
     */
    public static String getDataTypeName(KvPair.DataType dataType) {
        switch (dataType) {
            case STRING:
                return "String";
            case INTEGER:
                return "Integer";
            case LONG:
                return "Long";
            case FLOAT:
                return "Float";
            case BOOLEAN:
                return "Boolean";
            case STRING_SET:
                return "StringSet";
            default:
                return "Unknown";
        }
    }
}