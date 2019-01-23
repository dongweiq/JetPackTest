package com.honghe.jetpacktest.dagger;

import javax.inject.Inject;

public class Man {
    @Inject
    public Man() {
    }

    public String getDefaultMan() {
        return "defaultMan";
    }
}
