package com.github.yatatsu.android.trydatabinding.activity;

import android.content.Context;
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

import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.ServiceException;
import com.github.yatatsu.android.trydatabinding.api.MemoApiClient;
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

    static class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Memo memo = dataSource.get(position);
            holder.titleView.setText(memo.getTitle());
            holder.bodyView.setText(memo.getBody());
            holder.countView.setText("閲覧回数 " + memo.getViews() + "回");
        }

        @Override
        public int getItemCount() {
            return dataSource.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView titleView;
            public final TextView bodyView;
            public final TextView countView;

            public ViewHolder(View view) {
                super(view);
                this.titleView = (TextView) view.findViewById(R.id.memo_title);
                this.bodyView = (TextView) view.findViewById(R.id.memo_body);
                this.countView = (TextView) view.findViewById(R.id.memo_count);
            }
        }
    }
}
