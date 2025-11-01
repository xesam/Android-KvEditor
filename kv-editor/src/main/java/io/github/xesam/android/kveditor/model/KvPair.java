package io.github.xesam.android.kveditor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 键值对数据模型类，用于存储SharedPreference中的键值数据
 */
public class KvPair implements Parcelable {
    private String key;
    private Object value;
    private DataType dataType;

    public enum DataType {
        STRING, INTEGER, LONG, FLOAT, BOOLEAN, STRING_SET
    }

    public KvPair(String key, Object value, DataType dataType) {
        this.key = key;
        this.value = value;
        this.dataType = dataType;
    }

    protected KvPair(Parcel in) {
        key = in.readString();
        int typeOrdinal = in.readInt();
        dataType = DataType.values()[typeOrdinal];
        
        switch (dataType) {
            case STRING:
                value = in.readString();
                break;
            case INTEGER:
                value = in.readInt();
                break;
            case LONG:
                value = in.readLong();
                break;
            case FLOAT:
                value = in.readFloat();
                break;
            case BOOLEAN:
                value = in.readByte() != 0;
                break;
            case STRING_SET:
                // 这里简化处理，实际应该读取StringSet
                value = in.readString();
                break;
        }
    }

    public static final Creator<KvPair> CREATOR = new Creator<KvPair>() {
        @Override
        public KvPair createFromParcel(Parcel in) {
            return new KvPair(in);
        }

        @Override
        public KvPair[] newArray(int size) {
            return new KvPair[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeInt(dataType.ordinal());
        
        switch (dataType) {
            case STRING:
                dest.writeString((String) value);
                break;
            case INTEGER:
                dest.writeInt((Integer) value);
                break;
            case LONG:
                dest.writeLong((Long) value);
                break;
            case FLOAT:
                dest.writeFloat((Float) value);
                break;
            case BOOLEAN:
                dest.writeByte((byte) ((Boolean) value ? 1 : 0));
                break;
            case STRING_SET:
                // 这里简化处理，实际应该写入StringSet
                dest.writeString(value.toString());
                break;
        }
    }

    @Override
    public String toString() {
        return "KvPair{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", dataType=" + dataType +
                '}';
    }
}