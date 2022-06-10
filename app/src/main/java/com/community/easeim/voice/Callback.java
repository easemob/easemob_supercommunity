package com.community.easeim.voice;

public interface Callback<T> {
    void onSuccess(T t);
    void onError(String err);
}
