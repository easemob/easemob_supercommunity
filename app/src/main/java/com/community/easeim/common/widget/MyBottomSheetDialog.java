package com.community.easeim.common.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.community.easeim.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MyBottomSheetDialog extends BottomSheetDialog {
    public MyBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView ivClose = findViewById(R.id.iv_close);
        if (ivClose != null) {
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowing()) dismiss();
                }
            });
        }

        TextView tvCancel = findViewById(R.id.tv_cancel);
        if (tvCancel != null){
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowing()) dismiss();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        View view = findViewById(R.id.design_bottom_sheet);
        view.setBackgroundColor(Color.BLACK);

        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
