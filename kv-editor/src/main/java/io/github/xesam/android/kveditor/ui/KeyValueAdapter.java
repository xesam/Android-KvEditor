package io.github.xesam.android.kveditor.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.xesam.android.kveditor.R;
import io.github.xesam.android.kveditor.model.KvPair;
import io.github.xesam.android.kveditor.utils.DataTypeConverter;

/**
 * 键值对列表适配器
 */
public class KeyValueAdapter extends RecyclerView.Adapter<KeyValueAdapter.ViewHolder> {

    private List<KvPair> mKvPairList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(KvPair kvPair);
        void onItemDelete(KvPair kvPair);
    }

    public KeyValueAdapter(List<KvPair> kvPairList, OnItemClickListener listener) {
        mKvPairList = kvPairList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_key_value, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KvPair kvPair = mKvPairList.get(position);
        
        // 设置键名
        holder.keyTextView.setText(kvPair.getKey());
        
        // 设置数据类型
        String typeName = DataTypeConverter.getDataTypeName(kvPair.getDataType());
        holder.typeTextView.setText("类型: " + typeName);
        
        // 设置值
        String valueStr = DataTypeConverter.convertValueToString(kvPair.getValue());
        holder.valueTextView.setText("值: " + valueStr);
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(kvPair);
            }
        });
        
        // 设置删除按钮点击事件
        holder.deleteButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemDelete(kvPair);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKvPairList != null ? mKvPairList.size() : 0;
    }

    /**
     * 更新数据
     */
    public void updateData(List<KvPair> kvPairList) {
        mKvPairList = kvPairList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView keyTextView;
        TextView typeTextView;
        TextView valueTextView;
        ImageView deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.key_text_view);
            typeTextView = itemView.findViewById(R.id.type_text_view);
            valueTextView = itemView.findViewById(R.id.value_text_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}