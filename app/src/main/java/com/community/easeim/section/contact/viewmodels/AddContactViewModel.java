package com.community.easeim.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.community.easeim.common.livedatas.SingleSourceLiveData;
import com.community.easeim.common.net.Resource;
import com.community.easeim.common.repositories.EMContactManagerRepository;

public class AddContactViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<Boolean>> addContactObservable;

    public AddContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        addContactObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getAddContact() {
        return addContactObservable;
    }

    public void addContact(String username, String reason) {
        addContactObservable.setSource(mRepository.addContact(username, reason));
    }

}
