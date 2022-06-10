package com.community.easeim.section;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.community.easeim.DemoHelper;
import com.community.easeim.common.db.dao.InviteMessageDao;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.livedatas.SingleSourceLiveData;
import com.community.easeim.common.db.DemoDbHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.adapter.EMAConversation;

import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {
    private InviteMessageDao inviteMessageDao;
    private SingleSourceLiveData<Integer> switchObservable;
    private MutableLiveData<String> homeUnReadObservable;

    public MainViewModel(@NonNull Application application) {
        super(application);
        switchObservable = new SingleSourceLiveData<>();
        inviteMessageDao = DemoDbHelper.getInstance(application).getInviteMessageDao();
        homeUnReadObservable = new MutableLiveData<>();
    }

    public LiveData<Integer> getSwitchObservable() {
        return switchObservable;
    }

    /**
     * 设置可见的fragment
     * @param title
     */
    public void setVisibleFragment(Integer title) {
        switchObservable.setValue(title);
    }

    public LiveData<String> homeUnReadObservable() {
        return homeUnReadObservable;
    }

    public LiveDataBus messageChangeObservable() {
        return LiveDataBus.get();
    }

    public void checkUnreadMsg() {
        int unreadCount = 0;
        if(inviteMessageDao != null) {
            unreadCount = inviteMessageDao.queryUnreadCount();
        }

        int unreadMessageCount = 0;
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        for (EMConversation conversation : conversations.values()) {
            if (conversation.getType() == EMConversation.EMConversationType.Chat) {
                unreadMessageCount += conversation.getUnreadMsgCount();
            }
        }
        String count = getUnreadCount(unreadCount + unreadMessageCount);
        homeUnReadObservable.postValue(count);
    }

    /**
     * 获取未读消息数目
     * @param count
     * @return
     */
    private String getUnreadCount(int count) {
        if(count <= 0) {
            return null;
        }
        if(count > 99) {
            return "99+";
        }
        return String.valueOf(count);
    }

}
