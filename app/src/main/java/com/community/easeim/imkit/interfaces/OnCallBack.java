package com.community.easeim.imkit.interfaces;

public interface OnCallBack<T> {
    void onSuccess(T models);
    void onError(int code, String error);
}
