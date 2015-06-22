package com.github.yatatsu.android.trydatabinding.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.yatatsu.android.trydatabinding.BR;
import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.databinding.ListItemBinding;
import com.github.yatatsu.android.trydatabinding.model.Memo;

import java.util.List;


public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.BindingHolder> {

    public interface OnClickItemViewListener {
        void onClickItemView(View view, Memo memo, int key);
    }

    @NonNull private final SparseArray<Memo> dataSource = new SparseArray<>();
    private final LayoutInflater layoutInflater;
    private OnClickItemViewListener listener;

    public MemoListAdapter(Context context) {
        super();
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnClickItemViewListener(OnClickItemViewListener listener) {
        this.listener = listener;
    }

    public void setDataSource(@NonNull List<Memo> dataSource) {
        this.dataSource.clear();
        int i = 0;
        for (Memo item : dataSource) {
            this.dataSource.append(i, item);
            i++;
        }
        notifyDataSetChanged();
    }

    @NonNull
    public SparseArray<Memo> getDataSource() {
        return dataSource;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        final Memo memo = dataSource.get(position);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                if (listener != null) {
                    listener.onClickItemView(view, memo, position);
                }

            }
        });
        holder.binding.setVariable(BR.memo, memo);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    static class BindingHolder extends RecyclerView.ViewHolder {
        public final ListItemBinding binding;

        public BindingHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
