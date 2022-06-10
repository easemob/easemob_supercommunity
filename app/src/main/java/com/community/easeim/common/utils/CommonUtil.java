package com.community.easeim.common.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class CommonUtil {
    private static final CommonUtil ourInstance = new CommonUtil();

    public static CommonUtil getInstance() {
        return ourInstance;
    }

    private CommonUtil() {
    }


    public String[] list2Array(List<String> members) {
        if (members == null || members.isEmpty()) {
            return new String[1];
        }
        String[] arr = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            arr[i] = members.get(i);
        }
        return arr;
    }

    public void setShowNum(TextView textView,String size){
        if (TextUtils.isEmpty(size)){
            textView.setVisibility(View.GONE);
            return;
        }
        int num = Integer.parseInt(size);
        if (num == 0){
            textView.setVisibility(View.GONE);
        }else if (num < 10){
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(num));
        }else if (num <=99){
            textView.setVisibility(View.VISIBLE);
            textView.setText(" " + num + " ");
        }else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(" 99+ ");
        }
    }

    public void setShowNum(TextView textView,int num){
        if (num == 0){
            textView.setVisibility(View.GONE);
        }else if (num < 10){
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(num));
        }else if (num <=99){
            textView.setVisibility(View.VISIBLE);
            textView.setText(" " + num + " ");
        }else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(" 99+ ");
        }
    }
}
