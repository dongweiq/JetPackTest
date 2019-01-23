package com.honghe.jetpacktest.lifecycle;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.Log;

public class Java8Observer implements DefaultLifecycleObserver {
    private static final String TAG = Java8Observer.class.getName();

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onCreate: ");
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onStart: ");
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onDestroy: ");
    }
}
