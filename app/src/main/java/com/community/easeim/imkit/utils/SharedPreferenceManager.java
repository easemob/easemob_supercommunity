package com.community.easeim.imkit.utils;

public class SharedPreferenceManager {
    private SharedPreferenceManager() {
    }

    private static SharedPreferenceManager instance;

    public static SharedPreferenceManager getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceManager();
        }
        return instance;
    }
}
