package com.community.easeim.common.leancloud;

import androidx.annotation.NonNull;

import com.community.easeim.voice.Callback;

import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class GroundMemberManager {
    private GroundMemberManager() {
    }

    private static GroundMemberManager instance;

    public GroundMemberManager getInstance() {
        if (instance == null) {
            instance = new GroundMemberManager();
        }
        return instance;
    }

    private static final String OBJ_KEY = "GROUND_MEMBER_MANAGER";
    private static final String GROUND_ID = "GROUND_ID";
    private static final String USER_ID = "USER_ID";
    private static final String NICKNAME = "NICKNAME";

    public void setNicknameInGround(String groundId, String userId, String nickname, Callback<String> callback) {
        LCObject lcObject = new LCObject(OBJ_KEY);
        lcObject.put(GROUND_ID, groundId);
        lcObject.put(NICKNAME, nickname);
        lcObject.put(USER_ID, userId);
        lcObject.saveInBackground().subscribe(new Observer<LCObject>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull LCObject object) {
                callback.onSuccess(object.getObjectId());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void getNicknameInGround(String groundId, String userId, Callback<String> callback) {
        LCQuery<LCObject> query = LCQuery.getQuery(OBJ_KEY);
        query.whereEqualTo(GROUND_ID, groundId).whereEqualTo(USER_ID, userId);
        query.getFirstInBackground().subscribe(new Observer<LCObject>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull LCObject lcObjects) {
                callback.onSuccess(lcObjects.getString(NICKNAME));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
} 
