package com.community.easeim.section.ground.bean;

public class MemberBean {
    private int onLineState;
    private String name;
    private int role;
    private String id ;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final int ONLINE_ALL = 0;
    public static final int ONLINE_ONLINE = 1;
    public static final int ONLINE_BLACK = 2;

    public static final int ROLE_OWNER = 0;
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_MEMBER = 2;

    public MemberBean() {
    }

    public MemberBean(String name) {
        this.name = name;
    }

    public MemberBean(int onLineState, String id, int role) {
        this.onLineState = onLineState;
        this.id = id;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public MemberBean(int onLineState) {
        this.onLineState = onLineState;
    }

    public int getOnLineState() {
        return onLineState;
    }

    public void setOnLineState(int onLineState) {
        this.onLineState = onLineState;
    }
}
