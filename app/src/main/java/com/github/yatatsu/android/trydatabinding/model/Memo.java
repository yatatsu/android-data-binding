package com.github.yatatsu.android.trydatabinding.model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.yatatsu.android.trydatabinding.BR;

import java.io.Serializable;

/**
 * メモのモデル
 */
public class Memo extends BaseObservable implements Serializable, Parcelable {

    public enum Status {
        NotYet,
        Archived,
        ;
    }

    public enum Operation {
        Create,
        Update,
        Delete,
        ;
    }

    public Memo() {
        this.status = Status.NotYet;
        this.views = 0;
    }

    protected Memo(Parcel in) {
        title = in.readString();
        body = in.readString();
        status = Status.values()[in.readInt()];
        views = in.readInt();
    }

    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(@NonNull Parcel in) {
            return new Memo(in);
        }

        @NonNull
        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };

    @NonNull
    public static Memo newInstance() {
        return newInstance(null, null);
    }

    @NonNull
    public static Memo newInstance(String title, String body) {
        Memo item = new Memo();
        item.setTitle(title);
        item.setBody(body);
        return item;
    }

    private String title;
    private String body;
    private Status status;
    private int views;

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Bindable
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Bindable
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public boolean isInvalid() {
        return TextUtils.isEmpty(title) || TextUtils.isEmpty(body);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flag) {
        out.writeString(title);
        out.writeString(body);
        out.writeInt(status.ordinal());
        out.writeInt(views);
    }
}
