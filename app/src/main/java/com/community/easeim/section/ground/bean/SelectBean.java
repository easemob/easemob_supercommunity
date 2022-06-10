package com.community.easeim.section.ground.bean;

public class SelectBean {
    private int titleSrc;
    private String name;

    public SelectBean(int title, String name) {
        this.titleSrc = title;
        this.name = name;
    }

    public int getTitle() {
        return titleSrc;
    }

    public void setTitle(int title) {
        this.titleSrc = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
