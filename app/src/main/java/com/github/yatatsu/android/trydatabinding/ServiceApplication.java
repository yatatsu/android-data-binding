package com.github.yatatsu.android.trydatabinding;

import android.app.Application;
import android.content.Context;

/**
 * アプリケーションクラス
 */
public class ServiceApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
