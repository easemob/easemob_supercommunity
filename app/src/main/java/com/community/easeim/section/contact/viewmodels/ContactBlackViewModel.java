package com.community.easeim.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.community.easeim.common.livedatas.SingleSourceLiveData;
import com.community.easeim.common.net.Resource;
import com.community.easeim.common.repositories.EMContactManagerRepository;
import com.community.easeim.imkit.domain.EaseUser;

import java.util.List;

public class ContactBlackViewModel extends AndroidViewModel {
    private EMContactManagerRepository repository;
    private SingleSourceLiveData<Resource<List<EaseUser>>> blackObservable;
    private SingleSourceLiveData<Resource<Boolean>> resultObservable;

    public ContactBlackViewModel(@NonNull Application application) {
        super(application);
        repository = new EMContactManagerRepository();
        blackObservable = new SingleSourceLiveData<>();
        resultObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> blackObservable() {
        return blackObservable;
    }

    public void getBlackList() {
        blackObservable.setSource(repository.getBlackContactList());
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return resultObservable;
    }

    public void removeUserFromBlackList(String username) {
        resultObservable.setSource(repository.removeUserFromBlackList(username));
    }

}
