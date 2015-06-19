package com.github.yatatsu.android.trydatabinding.api;


import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.github.yatatsu.android.trydatabinding.R;
import com.github.yatatsu.android.trydatabinding.ServiceApplication;
import com.github.yatatsu.android.trydatabinding.ServiceException;
import com.github.yatatsu.android.trydatabinding.model.Memo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MemoApiClient {

    public interface Callback {
        void onSuccess(List<Memo> memos);
        void onFailure(ServiceException e);
    }

    public void get(@Size(min = 0) int count, @NonNull final Callback callback) {

        new ApiAsyncTask(new ApiAsyncTask.Listener() {
            @Override
            public void onFinish(List<Memo> result) {
                if (possibleFailure(50)) {
                    callback.onFailure(new ServiceException("通信エラーが発生しました。"));
                } else {
                    callback.onSuccess(result);
                }
            }
        }, TimeUnit.SECONDS.toMillis(3), count).execute();
    }

    private boolean possibleFailure(int rate) {
        Random rand = new Random();
        return rand.nextInt(100) < rate;
    }

    static class ApiAsyncTask extends AsyncTask<Void, Void, List<Memo>> {

        interface Listener {
            void onFinish(List<Memo> result);
        }

        private Listener listener;
        private long delay;
        private int size;

        ApiAsyncTask(Listener listener, long delay, int size) {
            super();
            this.listener = listener;
            this.delay = delay;
            this.size = size;
        }

        @Override
        protected List<Memo> doInBackground(Void ... Voids) {
            SystemClock.sleep(delay);
            int itr = 0;
            List<Memo> list = new ArrayList<>();
            while (itr < size) {
                Memo item = Memo.newInstance(
                        ServiceApplication.getAppContext().getString(R.string.memo_title_ipsum) + itr,
                        ServiceApplication.getAppContext().getString(R.string.memo_body_ipsum)
                );
                list.add(item);
                itr++;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Memo> result) {
            if (!isCancelled()) {
                listener.onFinish(result);
            }
        }
    }
}
