package com.community.easeim.imkit.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.community.easeim.R;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.domain.EaseAvatarOptions;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.provider.EaseUserProfileProvider;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.voice.Callback;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;

import java.util.HashMap;
import java.util.Map;


public class EaseUserUtils {

    static {
        // TODO: 2019/12/30 0030 how to provide userProfileProvider
//        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        EaseUserProfileProvider provider = EaseIM.getInstance().getUserProvider();
        return provider == null ? null : provider.getUser(username);
    }

    /**
     * set user's avatar style
     * @param imageView
     */
    public static void setUserAvatarStyle(EaseImageView imageView) {
        EaseAvatarOptions avatarOptions = EaseIM.getInstance().getAvatarOptions();
        if (avatarOptions == null || imageView == null) {
            return;
        }
        if (avatarOptions.getAvatarShape() != 0)
            imageView.setShapeType(avatarOptions.getAvatarShape());
        if (avatarOptions.getAvatarBorderWidth() != 0)
            imageView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
        if (avatarOptions.getAvatarBorderColor() != 0)
            imageView.setBorderColor(avatarOptions.getAvatarBorderColor());
        if (avatarOptions.getAvatarRadius() != 0)
            imageView.setRadius(avatarOptions.getAvatarRadius());
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(String username, ImageView imageView) {

        EaseUser user = getUserInfo(username);
        if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
            CustomImageUtil.getInstance().setHeadView(imageView, user.getAvatar());
        } else {
            if (mUserMap.containsKey(username)) {
                CustomImageUtil.getInstance().setHeadView(imageView, mUserMap.get(username).getAvatar());
            } else {
                LeanCloudManager.getInstance().getUserInfo(username, new EMValueCallBack<EaseUser>() {
                    @Override
                    public void onSuccess(EaseUser value) {
                        CustomImageUtil.getInstance().setHeadView(imageView, value.getAvatar());
                        mUserMap.put(username, value);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {

                    }
                });
            }
        }
    }

    public static void getUserInfo(String username, Callback<EaseUser> callback) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getNickname() != null) {
            callback.onSuccess(user);
        } else {
            if (mUserMap.containsKey(username)) {
                callback.onSuccess(mUserMap.get(username));
            } else {
                LeanCloudManager.getInstance().getUserInfo(username, new EMValueCallBack<EaseUser>() {
                    @Override
                    public void onSuccess(EaseUser value) {
                        callback.onSuccess(value);
                        mUserMap.put(username, value);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callback.onError(errorMsg);
                    }
                });
            }
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNickname() != null) {
                textView.setText(user.getNickname());
            } else {
                if (mUserMap.containsKey(username)) {
                    textView.setText(mUserMap.get(username).getNickname());
                } else {
                    LeanCloudManager.getInstance().getUserInfo(username, new EMValueCallBack<EaseUser>() {
                        @Override
                        public void onSuccess(EaseUser value) {
                            textView.setText(value.getNickname());
                            mUserMap.put(username, value);
                        }

                        @Override
                        public void onError(int error, String errorMsg) {

                        }
                    });
                }
            }
        }
    }

    private static Map<String, EaseUser> mUserMap = new HashMap<>();

}
