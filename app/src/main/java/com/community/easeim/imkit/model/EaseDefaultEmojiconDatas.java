package com.community.easeim.imkit.model;


import com.community.easeim.R;
import com.community.easeim.imkit.domain.EaseEmojicon;
import com.community.easeim.imkit.utils.EaseSmileUtils;

public class EaseDefaultEmojiconDatas {

    private static String[] emojis = new String[]{
            EaseSmileUtils.ee_1,
            EaseSmileUtils.ee_2,
            EaseSmileUtils.ee_3,
            EaseSmileUtils.ee_4,
            EaseSmileUtils.ee_5,
            EaseSmileUtils.ee_6,
            EaseSmileUtils.ee_7,
            EaseSmileUtils.ee_8,
            EaseSmileUtils.ee_9,
            EaseSmileUtils.ee_10,
            EaseSmileUtils.ee_11,
            EaseSmileUtils.ee_12,
            EaseSmileUtils.ee_13,
            EaseSmileUtils.ee_14,
            EaseSmileUtils.ee_15,
            EaseSmileUtils.ee_16,
            EaseSmileUtils.ee_17,
            EaseSmileUtils.ee_18,
            EaseSmileUtils.ee_19,
            EaseSmileUtils.ee_20,
            EaseSmileUtils.ee_21,
            EaseSmileUtils.ee_22,
            EaseSmileUtils.ee_23,
            EaseSmileUtils.ee_24,
            EaseSmileUtils.ee_25,
            EaseSmileUtils.ee_26,
            EaseSmileUtils.ee_27,
            EaseSmileUtils.ee_28,
            EaseSmileUtils.ee_29,
            EaseSmileUtils.ee_30,
            EaseSmileUtils.ee_31,
            EaseSmileUtils.ee_32,
            EaseSmileUtils.ee_33,
            EaseSmileUtils.ee_34,
            EaseSmileUtils.ee_35,
            EaseSmileUtils.ee_36,
            EaseSmileUtils.ee_37,
            EaseSmileUtils.ee_38,
            EaseSmileUtils.ee_39,
            EaseSmileUtils.ee_40,
            EaseSmileUtils.ee_41,
            EaseSmileUtils.ee_42,
            EaseSmileUtils.ee_43,
            EaseSmileUtils.ee_44,
            EaseSmileUtils.ee_45,
            EaseSmileUtils.ee_46,
            EaseSmileUtils.ee_47,
            EaseSmileUtils.ee_48,

    };

    private static int[] icons = new int[]{
            R.drawable.ee_1,
            R.drawable.ee_2,
            R.drawable.ee_3,
            R.drawable.ee_4,
            R.drawable.ee_5,
            R.drawable.ee_6,
            R.drawable.ee_7,
            R.drawable.ee_8,
            R.drawable.ee_9,
            R.drawable.ee_10,
            R.drawable.ee_11,
            R.drawable.ee_12,
            R.drawable.ee_13,
            R.drawable.ee_14,
            R.drawable.ee_15,
            R.drawable.ee_16,
            R.drawable.ee_17,
            R.drawable.ee_18,
            R.drawable.ee_19,
            R.drawable.ee_20,
            R.drawable.ee_21,
            R.drawable.ee_22,
            R.drawable.ee_23,
            R.drawable.ee_24,
            R.drawable.ee_25,
            R.drawable.ee_26,
            R.drawable.ee_27,
            R.drawable.ee_28,
            R.drawable.ee_29,
            R.drawable.ee_30,
            R.drawable.ee_31,
            R.drawable.ee_32,
            R.drawable.ee_33,
            R.drawable.ee_34,
            R.drawable.ee_35,
            R.drawable.ee_36,
            R.drawable.ee_37,
            R.drawable.ee_38,
            R.drawable.ee_39,
            R.drawable.ee_40,
            R.drawable.ee_41,
            R.drawable.ee_42,
            R.drawable.ee_43,
            R.drawable.ee_44,
            R.drawable.ee_45,
            R.drawable.ee_46,
            R.drawable.ee_47,
            R.drawable.ee_48,
    };


    private static final EaseEmojicon[] DATA = createData();

    private static EaseEmojicon[] createData() {
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for (int i = 0; i < icons.length; i++) {
            datas[i] = new EaseEmojicon(icons[i], emojis[i], EaseEmojicon.Type.NORMAL);
        }
        return datas;
    }

    public static EaseEmojicon[] getData() {
        return DATA;
    }
}
