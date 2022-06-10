package com.community.easeim.section.me.headImage;

import android.graphics.Bitmap;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/16/2021
 */

public class HeadImageInfo {
    private String url;
    private Bitmap bitmap;
    private int resId;

    public HeadImageInfo(String url) {
        this.url = url;
    }

    public HeadImageInfo(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getUrl() { return url;}

    public void setUrl(String url) { this.url = url;}

    public Bitmap getBitmap() { return bitmap; }

    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}
}
