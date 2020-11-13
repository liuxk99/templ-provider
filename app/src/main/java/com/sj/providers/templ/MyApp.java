package com.sj.providers.templ;

import android.app.Application;
import android.util.Log;

import com.sj4a.utils.MyConf;

public class MyApp extends Application {

    private static final String TAG = MyApp.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        super.onCreate();
        MyConf.getInstance().initConf(this);
    }
}
