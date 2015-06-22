package com.github.yatatsu.android.trydatabinding.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.databinding.FragmentDetailBinding;
import com.github.yatatsu.android.trydatabinding.model.Memo;
import com.github.yatatsu.android.trydatabinding.viewmodel.EditItemViewModel;

import org.jetbrains.annotations.NotNull;

public class EditItemFragment extends DialogFragment {

    public interface EditItemActionListener {
        void onCompleteEditItem(@NonNull Memo memo, int key);
    }

    private static final String ARGS_KEY_MEMO = "_ARG_KEY_MEMO_";
    private static final String ARGS_KEY_EDIT = "_ARG_KEY_EDIT_";
    private static final String ARGS_KEY_KEY = "_ARG_KEY_KEY_";

    public static EditItemFragment newInstance(@NonNull Memo memo, int key) {
        EditItemFragment fragment = new EditItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_MEMO, memo);
        args.putInt(ARGS_KEY_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable private EditItemActionListener listener;
    private EditItemViewModel viewModel;
    private int key = -1;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_detail, null, false);

        key = grubTargetKey(savedInstanceState);
        Memo editingMemo = grubEditingMemo(savedInstanceState, grubOriginalMemo());

        // set binding
        FragmentDetailBinding binding = FragmentDetailBinding.bind(view);
        viewModel = new EditItemViewModel(editingMemo);
        binding.setViewModel(viewModel);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.edit_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onCompleteEditItem(viewModel.editingMemo, key);
                        }
                    }
                })
                .setNegativeButton(R.string.edit_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EditItemActionListener) {
            this.listener = (EditItemActionListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGS_KEY_KEY, key);
        outState.putSerializable(ARGS_KEY_EDIT, viewModel.editingMemo);
    }

    @NonNull
    private Memo grubEditingMemo(Bundle savedInstanceState, @NonNull Memo originalMemo) {
        Memo targetMemo = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(ARGS_KEY_EDIT)) {
            targetMemo = (Memo) savedInstanceState.getSerializable(ARGS_KEY_EDIT);
        } else {
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARGS_KEY_EDIT)) {
                targetMemo = (Memo) args.getSerializable(ARGS_KEY_EDIT);
            }
        }
        return targetMemo == null ? originalMemo : targetMemo;
    }

    @NonNull
    private Memo grubOriginalMemo() {
        Memo targetMemo = null;
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_KEY_MEMO)) {
            targetMemo = (Memo) args.getSerializable(ARGS_KEY_MEMO);
        }
        return targetMemo == null ? Memo.newInstance() : targetMemo;
    }

    private int grubTargetKey(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(ARGS_KEY_KEY)) {
            return savedInstanceState.getInt(ARGS_KEY_KEY);
        } else if (getArguments() != null && getArguments().containsKey(ARGS_KEY_KEY)) {
            return getArguments().getInt(ARGS_KEY_KEY);
        }
        return -1;
    }
}
