package com.example.sapr.service;

import com.example.sapr.payload.Constructor;

public enum Storage {
    INSTANCE;
    private Constructor constructor;


    public Constructor getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor constructor) {
        this.constructor = constructor;
    }
}
