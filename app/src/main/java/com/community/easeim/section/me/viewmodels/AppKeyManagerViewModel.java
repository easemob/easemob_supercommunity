package com.community.easeim.section.me.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.community.easeim.common.livedatas.SingleSourceLiveData;
import com.community.easeim.common.net.Resource;
import com.community.easeim.common.repositories.EMClientRepository;

public class AppKeyManagerViewModel extends AndroidViewModel {
    private EMClientRepository repository;
    private SingleSourceLiveData<Resource<Boolean>> logoutObservable;

    public AppKeyManagerViewModel(@NonNull Application application) {
        super(application);
        repository = new EMClientRepository();
        logoutObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getLogoutObservable() {
        return logoutObservable;
    }

    public void logout(boolean unbindDeviceToken) {
        logoutObservable.setSource(repository.logout(unbindDeviceToken));
    }
}

