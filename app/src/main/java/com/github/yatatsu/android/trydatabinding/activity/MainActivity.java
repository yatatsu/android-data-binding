package com.github.yatatsu.android.trydatabinding.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.yatatsu.android.trydatabinding.BR;
import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.ServiceException;
import com.github.yatatsu.android.trydatabinding.api.MemoApiClient;
import com.github.yatatsu.android.trydatabinding.databinding.ActivityMainBinding;
import com.github.yatatsu.android.trydatabinding.databinding.ListItemBinding;
import com.github.yatatsu.android.trydatabinding.fragment.EditItemFragment;
import com.github.yatatsu.android.trydatabinding.model.Memo;

import java.util.ArrayList;
import java.util.List;

/**
 * 一覧画面
 */
public class MainActivity extends AppCompatActivity implements EditItemFragment.EditItemActionListener {

    private MemoAdapter memoAdapter;
    private List<Memo> data;
    private ActivityMainBinding binding;
    private MemoApiClient apiClient = new MemoApiClient();
    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static final String ARGS_KEY_DATA = "ARGS_KEY_DATA";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMemosAsync();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.recyclerView.getContext()));
        memoAdapter = new MemoAdapter(this);
        memoAdapter.setOnClickItemViewListener(new MemoAdapter.OnClickItemViewListener() {
            @Override
            public void onClickItemView(View view, Memo memo, int key) {
                EditItemFragment.newInstance(memo, key).show(getFragmentManager(), FRAGMENT_TAG);
            }
        });
        binding.recyclerView.setAdapter(memoAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(ARGS_KEY_DATA)) {
            data = savedInstanceState.getParcelableArrayList(ARGS_KEY_DATA);
        }
        if (data == null || data.isEmpty()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            fetchMemosAsync();
        } else {
            memoAdapter.setDataSource(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARGS_KEY_DATA, (ArrayList<Memo>) data);
    }

    private void fetchMemosAsync() {
        Log.d(TAG, "fetchMemosAsync");
        apiClient.get(10, new MemoApiClient.Callback() {
            @Override
            public void onSuccess(List<Memo> memos) {
                if (isFinishing()) {
                    return;
                }
                Log.d(TAG, "onSuccess in API");
                data = memos;
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeContainer.setRefreshing(false);
                memoAdapter.setDataSource(memos);
            }

            @Override
            public void onFailure(ServiceException e) {
                if (isFinishing()) {
                    return;
                }
                Log.d(TAG, "onFailure in API");
                binding.swipeContainer.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
                memoAdapter.setDataSource(new ArrayList<Memo>());
                Snackbar.make(binding.swipeContainer, e.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onCompleteEditItem(@NonNull Memo memo, int key) {
        Log.d(TAG, "onCompleteEditItem");
        if (memoAdapter == null) {
            return;
        }
        if (key > 0) {
            memoAdapter.getDataSource().put(key, memo);
            memoAdapter.notifyItemChanged(key);
        }
    }

    static class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.BindingHolder> {

        interface OnClickItemViewListener {
            void onClickItemView(View view, Memo memo, int key);
        }

        @NonNull private final SparseArray<Memo> dataSource = new SparseArray<>();
        private final LayoutInflater layoutInflater;
        private OnClickItemViewListener listener;

        public MemoAdapter(Context context) {
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
                    Log.d(TAG, "onClick");
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
}
