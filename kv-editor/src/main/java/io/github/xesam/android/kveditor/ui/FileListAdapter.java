package io.github.xesam.android.kveditor.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import io.github.xesam.android.kveditor.R;
import io.github.xesam.android.kveditor.model.FileItem;
import io.github.xesam.android.kveditor.storage.base.StorageType;

/**
 * SharedPreference文件列表适配器
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private List<FileItem> mFileList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(FileItem fileItem);
    }

    public FileListAdapter(List<FileItem> fileList, OnItemClickListener listener) {
        mFileList = fileList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = mFileList.get(position);
        holder.fileNameTextView.setText(fileItem.getDisplayName());
        
        // 设置存储类型标签，添加强制大写和方括号（使用Locale.ROOT避免本地化问题）
        String storageTypeLabel = "[" + fileItem.getStorageType().getDescription().toUpperCase(Locale.ROOT) + "]";
        holder.storageTypeTextView.setText(storageTypeLabel);
        
        // 根据存储类型设置不同的颜色
        try {
            int color = Color.parseColor(fileItem.getStorageType().getColor());
            holder.storageTypeTextView.setTextColor(color);
        } catch (IllegalArgumentException e) {
            // 如果颜色解析失败，使用默认颜色
            holder.storageTypeTextView.setTextColor(Color.parseColor("#757575"));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(fileItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList != null ? mFileList.size() : 0;
    }

    /**
     * 更新数据
     */
    public void updateData(List<FileItem> fileList) {
        mFileList = fileList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        TextView storageTypeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.file_name_text_view);
            storageTypeTextView = itemView.findViewById(R.id.storage_type_text_view);
        }
    }
}