package com.community.easeim.section.me.headImage;

import android.text.TextUtils;
import android.widget.ImageView;

import com.community.easeim.R;

import java.util.Random;

public class CustomImageUtil {
    private static final CustomImageUtil ourInstance = new CustomImageUtil();

    public static CustomImageUtil getInstance() {
        return ourInstance;
    }

    private CustomImageUtil() {
    }

    private static final int[] coverIds = new int[]{R.drawable.cover01, R.drawable.cover02, R.drawable.cover03, R.drawable.cover04, R.drawable.cover05, R.drawable.cover06, R.drawable.cover07, R.drawable.cover08};

    private static final int[] headIds = new int[]{R.drawable.portrait01, R.drawable.portrait02, R.drawable.portrait03, R.drawable.portrait04, R.drawable.portrait05, R.drawable.portrait06, R.drawable.portrait07, R.drawable.portrait08, R.drawable.portrait09, R.drawable.portrait10
            , R.drawable.portrait11, R.drawable.portrait12, R.drawable.portrait13, R.drawable.portrait14, R.drawable.portrait15, R.drawable.portrait16, R.drawable.portrait17, R.drawable.portrait18, R.drawable.portrait19, R.drawable.portrait20
            , R.drawable.portrait21, R.drawable.portrait22, R.drawable.portrait23, R.drawable.portrait24, R.drawable.portrait25, R.drawable.portrait26, R.drawable.portrait27, R.drawable.portrait28, R.drawable.portrait29, R.drawable.portrait30
            , R.drawable.portrait31, R.drawable.portrait32, R.drawable.portrait33, R.drawable.portrait34, R.drawable.portrait35, R.drawable.portrait36, R.drawable.portrait37, R.drawable.portrait38, R.drawable.portrait39, R.drawable.portrait40
            , R.drawable.portrait41, R.drawable.portrait42, R.drawable.portrait43, R.drawable.portrait44, R.drawable.portrait45, R.drawable.portrait46, R.drawable.portrait47, R.drawable.portrait48, R.drawable.portrait49, R.drawable.portrait50};

    public int[] getHeadIds() {
        return headIds;
    }

    public int[] getCoverIds() {
        return coverIds;
    }

    public int getCoverResId(String pos) {
        if (!TextUtils.isEmpty(pos) && pos.length() < 3) {
            return coverIds[Integer.parseInt(pos)];
        }
        return R.drawable.ease_default_avatar;
    }

    public int getHeadResId(String pos) {
        if (!TextUtils.isEmpty(pos) && pos.length() < 3) {
            return headIds[Integer.parseInt(pos)];
        }
        return R.drawable.ease_default_avatar;
    }

    public void setCoverView(ImageView imageView, String pos) {
        if (!TextUtils.isEmpty(pos) && !TextUtils.equals(pos, "-1")) {
            imageView.setImageResource(getCoverResId(pos));
        } else {
            imageView.setImageResource(R.drawable.ease_default_avatar);
        }
    }

    public void setHeadView(ImageView imageView, String pos) {
        if (!TextUtils.isEmpty(pos) && !TextUtils.equals(pos, "-1")) {
            imageView.setImageResource(getHeadResId(pos));
        } else {
            imageView.setImageResource(R.drawable.ease_default_avatar);
        }
    }

    public String getHeadNum() {
        return getNum(0, headIds.length);
    }

    public String getCoverNum() {
        return getNum(0, coverIds.length);
    }

    public String getNum(int startNum, int endNum) {
        if (endNum > startNum) {
            Random random = new Random();
            int i = random.nextInt(endNum - startNum) + startNum;
            return i + "";
        }
        return "-1";
    }

    public String setRandomCover(ImageView imageView) {
        String num = getNum(0, 8);
        setCoverView(imageView, num);
        return num;
    }
}
