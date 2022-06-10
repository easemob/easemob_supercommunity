package com.community.easeim.section.ground.bean;

import com.hyphenate.chat.EMGroupManager;

import java.io.Serializable;

public class UserGroundBean implements Serializable {
    /**
     * 用户Id
     */
    private String userId;


    /**
     * 用户在社区的昵称
     */
    private String userGName;
    /**
     * 社区ID
     */
    private String groundId;
    /**
     * 社区名称
     */
    private String groundName;

    /**
     * 社区主
     */
    private String groundOwner;
    /**
     * 社区简介
     */
    private String groundDes;

    /**
     * 社区简介
     */
    private String groundCover;
    /**
     * 是否运行成员邀请
     */
    private boolean isMemberCanInvite;
    /**
     * 社区属性
     * */
    EMGroupManager.EMGroupStyle style;

    /**
     * 是否随便逛逛
     */
    private boolean isStroll;

    private String objId;

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserGName() {
        return userGName;
    }

    public void setUserGName(String userGName) {
        this.userGName = userGName;
    }

    public String getGroundId() {
        return groundId;
    }

    public void setGroundId(String groundId) {
        this.groundId = groundId;
    }

    public String getGroundName() {
        return groundName;
    }

    public void setGroundName(String groundName) {
        this.groundName = groundName;
    }

    public String getGroundDes() {
        return groundDes;
    }

    public void setGroundDes(String groundDes) {
        this.groundDes = groundDes;
    }

    public String getGroundCover() {
        return groundCover;
    }

    public void setGroundCover(String groundCover) {
        this.groundCover = groundCover;
    }

    public String getGroundOwner() {
        return groundOwner;
    }

    public void setGroundOwner(String groundOwner) {
        this.groundOwner = groundOwner;
    }

    public boolean isMemberCanInvite() {
        return isMemberCanInvite;
    }

    public void setMemberCanInvite(boolean memberCanInvite) {
        isMemberCanInvite = memberCanInvite;
    }

    public EMGroupManager.EMGroupStyle getStyle() {
        return style;
    }

    public void setStyle(EMGroupManager.EMGroupStyle style) {
        this.style = style;
    }

    public boolean isStroll() {
        return isStroll;
    }

    public void setStroll(boolean stroll) {
        isStroll = stroll;
    }
}
