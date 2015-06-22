package com.github.yatatsu.android.trydatabinding.viewmodel;

import android.app.FragmentManager;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.yatatsu.android.trydatabinding.BR;
import com.github.yatatsu.android.trydatabinding.ServiceException;
import com.github.yatatsu.android.trydatabinding.api.MemoApiClient;
import com.github.yatatsu.android.trydatabinding.model.Memo;
import com.github.yatatsu.android.trydatabinding.view.EditItemFragment;
import com.github.yatatsu.android.trydatabinding.view.MemoListAdapter;

import java.util.ArrayList;
import java.util.List;


public class ItemListViewModel extends BaseObservable {

    private final Context context;
    private final FragmentManager fragmentManager;
    private final MemoApiClient apiClient;
    public final MemoListAdapter adapter;
    private final Snackbar snackbar;
    private boolean destroyed;
    private boolean visibleProgressBar;
    private boolean refreshing;

    private List<Memo> data;

    public static final String ARGS_KEY_DATA = "ARGS_KEY_DATA";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static final String TAG = "ItemListViewModel";

    public ItemListViewModel(Context context, FragmentManager fragmentManager, Snackbar snackbar) {
        super();
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.apiClient = new MemoApiClient();
        this.adapter = new MemoListAdapter(context);
        this.snackbar = snackbar;
        this.destroyed = false;
    }

    public void init(Bundle savedInstanceState) {
        adapter.setOnClickItemViewListener(new MemoListAdapter.OnClickItemViewListener() {
            @Override
            public void onClickItemView(View view, Memo memo, int key) {
                EditItemFragment.newInstance(memo, key).show(fragmentManager, FRAGMENT_TAG);
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(ARGS_KEY_DATA)) {
            data = savedInstanceState.getParcelableArrayList(ARGS_KEY_DATA);
        }
        if (data == null || data.isEmpty()) {
            setVisibleProgressBar(true);
            fetchMemosAsync();
        } else {
            adapter.setDataSource(data);
        }
    }

    public void dispose() {
        destroyed = true;
    }

    public List<Memo> getData() {
        return data;
    }

    // --------------------------------------
    // binding
    // --------------------------------------

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMemosAsync();
            }
        };
    }

    public int[] getColorSchemeResources() {
        return new int[] {
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        };
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(context);
    }

    @Bindable
    public boolean isRefreshing() {
        return refreshing;
    }

    private void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }

    @Bindable
    public boolean isVisibleProgressBar() {
        return visibleProgressBar;
    }

    private void setVisibleProgressBar(boolean visibleProgressBar) {
        this.visibleProgressBar = visibleProgressBar;
        notifyPropertyChanged(BR.visibleProgressBar);
    }

    // --------------------------------------
    // logic
    // --------------------------------------

    @VisibleForTesting
    void fetchMemosAsync() {
        apiClient.get(10, new MemoApiClient.Callback() {
            @Override
            public void onSuccess(List<Memo> memos) {
                if (destroyed) {
                    return;
                }
                Log.d(TAG, "onSuccess in API");
                data = memos;
                setRefreshing(false);
                setVisibleProgressBar(false);
                adapter.setDataSource(memos);
            }

            @Override
            public void onFailure(ServiceException e) {
                if (destroyed) {
                    return;
                }
                Log.d(TAG, "onFailure in API");
                setRefreshing(false);
                setVisibleProgressBar(false);
                adapter.setDataSource(new ArrayList<Memo>());
                snackbar.setText(e.getMessage()).show();
            }
        });
    }

    public void onCompleteEditItem(@NonNull Memo memo, int key) {
        Log.d(TAG, "onCompleteEditItem key:" + key + " memo:" + memo.getTitle() + "/" + memo.getBody());
        if (key >= 0) {
            adapter.getDataSource().put(key, memo);
            adapter.notifyItemChanged(key);
        }
    }

}
