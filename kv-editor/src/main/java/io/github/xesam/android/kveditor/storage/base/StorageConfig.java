package io.github.xesam.android.kveditor.storage.base;

/**
 * 存储配置类
 * 封装存储相关的配置信息
 */
public class StorageConfig {
    private final StorageType type;
    private final String name;
    private final boolean multiProcess;

    private StorageConfig(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.multiProcess = builder.multiProcess;
    }

    public StorageType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isMultiProcess() {
        return multiProcess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageConfig that = (StorageConfig) o;

        if (multiProcess != that.multiProcess) return false;
        if (type != that.type) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (multiProcess ? 1 : 0);
        return result;
    }

    /**
     * 构建器类
     */
    public static class Builder {
        private StorageType type;
        private String name;
        private boolean multiProcess = false;

        public Builder setType(StorageType type) {
            this.type = type;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMultiProcess(boolean multiProcess) {
            this.multiProcess = multiProcess;
            return this;
        }

        public StorageConfig build() {
            if (type == null) {
                throw new IllegalArgumentException("Storage type must be set");
            }
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Storage name must be set");
            }
            return new StorageConfig(this);
        }
    }
}