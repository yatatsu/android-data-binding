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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.yatatsu.android.trydatabinding.BR;
import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.ServiceException;
import com.github.yatatsu.android.trydatabinding.api.MemoApiClient;
import com.github.yatatsu.android.trydatabinding.databinding.ListItemBinding;
import com.github.yatatsu.android.trydatabinding.model.Memo;

import java.util.ArrayList;
import java.util.List;

/**
 * 一覧画面
 */
public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;
    private MemoAdapter memoAdapter;
    private ProgressBar progressBar;
    private MemoApiClient apiClient = new MemoApiClient();
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMemosAsync();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        memoAdapter = new MemoAdapter(this);
        recyclerView.setAdapter(memoAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchMemosAsync();
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
                progressBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
                memoAdapter.setDataSource(memos);
            }

            @Override
            public void onFailure(ServiceException e) {
                if (isFinishing()) {
                    return;
                }
                Log.d(TAG, "onFailure in API");
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(swipeContainer, e.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    static class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.BindingHolder> {

        @NonNull private final List<Memo> dataSource = new ArrayList<>();

        private final LayoutInflater layoutInflater;

        public MemoAdapter(Context context) {
            super();
            layoutInflater = LayoutInflater.from(context);
        }

        public void setDataSource(@NonNull List<Memo> dataSource) {
            this.dataSource.clear();
            this.dataSource.addAll(dataSource);
            notifyDataSetChanged();
        }

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false);
            return new BindingHolder(binding);
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            Memo memo = dataSource.get(position);
            holder.binding.setVariable(BR.memo, memo);
            holder.binding.executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return dataSource.size();
        }

        static class BindingHolder extends RecyclerView.ViewHolder {
            public final TextView titleView;
            public final TextView bodyView;
            public final TextView countView;
            public final ListItemBinding binding;

            public BindingHolder(ListItemBinding binding) {
                super(binding.getRoot());
                View view = binding.getRoot();
                this.titleView = (TextView) view.findViewById(R.id.memo_title);
                this.bodyView = (TextView) view.findViewById(R.id.memo_body);
                this.countView = (TextView) view.findViewById(R.id.memo_count);
                this.binding = binding;
            }
        }
    }
}
