package com.honghe.jetpacktest.dagger;

import com.honghe.jetpacktest.MainActivity;

import dagger.Component;

@Component
public interface MainActivityComponent {
    void inject(MainActivity activity);
}
