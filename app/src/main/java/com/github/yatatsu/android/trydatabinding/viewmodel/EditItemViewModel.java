package com.github.yatatsu.android.trydatabinding.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.github.yatatsu.android.trydatabinding.BR;
import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.ServiceApplication;
import com.github.yatatsu.android.trydatabinding.model.Memo;
import com.github.yatatsu.android.trydatabinding.view.SimpleTextWatcher;


public class EditItemViewModel extends BaseObservable {

    @Bindable
    public Memo editingMemo;

    public EditItemViewModel(Memo memo) {
        super();
        this.editingMemo = memo;
    }

    @Bindable
    public String getTitleError() {
        return validate(editingMemo.getTitle());
    }

    @Bindable
    public String getBodyError() {
        return validate(editingMemo.getBody());
    }

    private String validate(String text) {
        if (TextUtils.isEmpty(text)) {
            return ServiceApplication.getAppContext().getString(R.string.empty_text_warning);
        }
        return null;
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

    public View.OnFocusChangeListener getTitleFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    editingMemo.notifyPropertyChanged(BR.title);
                    notifyPropertyChanged(BR.titleError);
                }
            }
        };
    }

    public View.OnFocusChangeListener getBodyFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    editingMemo.notifyPropertyChanged(BR.body);
                    notifyPropertyChanged(BR.bodyError);
                }
            }
        };
    }
}
