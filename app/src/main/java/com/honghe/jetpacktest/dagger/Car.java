package com.honghe.jetpacktest.dagger;

import javax.inject.Inject;


public class Car {
    @Inject
    public Car() {
    }

    public String getName() {
        return "testCar";
    }

}
