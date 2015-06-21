package com.github.yatatsu.android.trydatabinding.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.github.yatatsu.android.trydatabinding.BR;
import com.github.yatatsu.android.trydatabinding.model.Memo;


public class EditItemViewModel extends BaseObservable {

    @Bindable
    public Memo editingMemo;

    public EditItemViewModel(Memo memo) {
        super();
        this.editingMemo = memo;
    }

    public TextWatcher getTitleWatcher() {
        return new SimpleTextWatcher() {
            @Override
            public void onTextChanged(@NonNull CharSequence charSequence, int i, int i1, int i2) {
                editingMemo.setTitle(charSequence.toString());
            }
        };
    }

    public TextWatcher getBodyWatcher() {
        return new SimpleTextWatcher() {
            @Override
            public void onTextChanged(@NonNull CharSequence charSequence, int i, int i1, int i2) {
                editingMemo.setBody(charSequence.toString());
            }
        };
    }

    public View.OnFocusChangeListener getFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("vm", "onFocusChange");
                    editingMemo.notifyPropertyChanged(BR.title);
                    editingMemo.notifyPropertyChanged(BR.body);
                }
            }
        };
    }

    static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(@NonNull CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
