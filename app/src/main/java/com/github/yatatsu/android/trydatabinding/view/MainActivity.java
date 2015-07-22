package com.github.yatatsu.android.trydatabinding.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.databinding.ActivityMainBinding;
import com.github.yatatsu.android.trydatabinding.model.Memo;
import com.github.yatatsu.android.trydatabinding.viewmodel.ItemListViewModel;

import java.util.ArrayList;

/**
 * 一覧画面
 */
public class MainActivity extends AppCompatActivity implements EditItemFragment.EditItemActionListener {

    private ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ItemListViewModel viewModel = new ItemListViewModel(this, getFragmentManager(), new SnackBarCallback() {
            @Override
            public void show(String message, int duration) {
                Snackbar.make(binding.swipeContainer, message, duration).show();
            }
        });
        binding.setViewModel(viewModel);
        binding.getViewModel().init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.getViewModel().dispose();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ItemListViewModel.ARGS_KEY_DATA,
                (ArrayList<Memo>) binding.getViewModel().getData());
    }

    @Override
    public void onCompleteEditItem(@NonNull Memo memo, int key) {
        binding.getViewModel().onCompleteEditItem(memo, key);
    }
}