package com.community.easeim.imkit.manager;

import com.community.easeim.imkit.adapter.EaseAdapterDelegate;
import com.community.easeim.imkit.adapter.EaseBaseDelegateAdapter;
import com.community.easeim.imkit.delegate.EaseCustomAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseExpressionAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseFileAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseImageAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseLocationAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseTextAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseVideoAdapterDelegate;
import com.community.easeim.imkit.delegate.EaseVoiceAdapterDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EaseMessageTypeSetManager {
    private static EaseMessageTypeSetManager mInstance;
    private EaseAdapterDelegate<?,?> defaultDelegate = new EaseTextAdapterDelegate();
    private Class<? extends EaseAdapterDelegate<?,?>> defaultDelegateCls;
    private Set<Class<? extends EaseAdapterDelegate<?, ?>>> delegates = new HashSet<>();
    private List<Class<? extends EaseAdapterDelegate<?, ?>>> delegateList = new ArrayList<>();
    private boolean hasConsistItemType;

    private EaseMessageTypeSetManager(){}

    public static EaseMessageTypeSetManager getInstance() {
        if(mInstance == null) {
            synchronized (EaseMessageTypeSetManager.class) {
                if(mInstance == null) {
                    mInstance = new EaseMessageTypeSetManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 是否使用自定义的item ViewType
     * @param hasConsistItemType
     * @return
     */
    public EaseMessageTypeSetManager setConsistItemType(boolean hasConsistItemType) {
        this.hasConsistItemType = hasConsistItemType;
        return this;
    }

    public EaseMessageTypeSetManager addMessageType(Class<? extends EaseAdapterDelegate<?, ?>> cls) {
        int size = delegates.size();
        delegates.add(cls);
        if(delegates.size() > size) {
            delegateList.add(cls);
        }
        return this;
    }

    /**
     * 设置默认的对话类型
     * @param cls
     * @return
     */
    public EaseMessageTypeSetManager setDefaultMessageType(Class<? extends EaseAdapterDelegate<?, ?>> cls) {
        this.defaultDelegateCls = cls;
        return this;
    }

    /**
     * 注册消息类型
     * @param adapter
     */
    public void registerMessageType(EaseBaseDelegateAdapter adapter) throws InstantiationException, IllegalAccessException{
        if(adapter == null) {
            return;
        }
        //如果没有注册聊天类型，则使用默认的
        if(delegateList.size() <= 0) {
            addMessageType(EaseExpressionAdapterDelegate.class)       //自定义表情
            .addMessageType(EaseFileAdapterDelegate.class)             //文件
            .addMessageType(EaseImageAdapterDelegate.class)            //图片
            .addMessageType(EaseLocationAdapterDelegate.class)         //定位
            .addMessageType(EaseVideoAdapterDelegate.class)            //视频
            .addMessageType(EaseVoiceAdapterDelegate.class)            //声音
            .addMessageType(EaseCustomAdapterDelegate.class)           //自定义消息
            .setDefaultMessageType(EaseTextAdapterDelegate.class);       //文本
        }
        for (Class<? extends EaseAdapterDelegate<?, ?>> cls : delegateList) {
            EaseAdapterDelegate delegate = cls.newInstance();
            adapter.addDelegate(delegate);
        }

        if(defaultDelegateCls == null) {
            defaultDelegate = new EaseTextAdapterDelegate();
        }else {
            defaultDelegate = defaultDelegateCls.newInstance();
        }
        adapter.setFallbackDelegate(defaultDelegate);
    }

    public boolean hasConsistItemType() {
        return this.hasConsistItemType;
    }

    public void release() {
        defaultDelegate = null;
    }

}
