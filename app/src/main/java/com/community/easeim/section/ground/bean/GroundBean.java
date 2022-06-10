package com.community.easeim.section.ground.bean;

public class GroundBean {
    private String id;
    private String ground_id;
    private String ground_name;
    private String ground_describe;
    private String ground_cover;

    /**
     * 区长
     */
    private String ground_owner;
    /**
     * 是否运行成员邀请
     */
    private boolean isMemberCanInvite;

    /**
    * 社区属性  true 公开社区，false私密社区
    * */
    private boolean ground_type;
    private int ground_total;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroundCover() {
        return ground_cover;
    }

    public void setGround_cover(String ground_cover) {
        this.ground_cover = ground_cover;
    }

    public GroundBean() {
    }

    public GroundBean(String name, String describe, String cover) {
        this.ground_name = name;
        this.ground_describe = describe;
        this.ground_cover = cover;
    }

    public GroundBean(String name, String describe) {
        this.ground_name = name;
        this.ground_describe = describe;
    }

    public String getGroundId() {
        return ground_id;
    }

    public void setGround_id(String ground_id) {
        this.ground_id = ground_id;
    }

    public String getGroundName() {
        return ground_name;
    }

    public void setGround_name(String ground_name) {
        this.ground_name = ground_name;
    }

    public String getGroundDes() {
        return ground_describe;
    }

    public void setGround_describe(String ground_describe) {
        this.ground_describe = ground_describe;
    }


    public String getGroundOwner() {
        return ground_owner;
    }

    public void setGround_owner(String ground_owner) {
        this.ground_owner = ground_owner;
    }

    public boolean isMemberCanInvite() {
        return isMemberCanInvite;
    }

    public void setMemberCanInvite(boolean memberCanInvite) {
        isMemberCanInvite = memberCanInvite;
    }

    public boolean isGround_type() {
        return ground_type;
    }

    public void setGround_type(boolean ground_type) {
        this.ground_type = ground_type;
    }

    public int getGround_total() {
        return ground_total;
    }

    public void setGround_total(int ground_total) {
        this.ground_total = ground_total;
    }
}
