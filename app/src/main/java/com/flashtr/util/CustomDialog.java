package com.flashtr.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flashtr.R;

public class CustomDialog {
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private static CustomDialog instance;

    private CustomDialog() {
    }

    public static CustomDialog getInstance() {
        if (instance == null) {
            instance = new CustomDialog();
        }
        return instance;
    }

    public void show(Context mContext, String loaderMessage) {
        dialog = new Dialog(mContext, R.style.DialogTheme);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.item_progress);

        TextView mTxtLoading = (TextView) dialog.findViewById(R.id.tv_txtDialogMessage);

        if(!loaderMessage.equalsIgnoreCase("")) {
            mTxtLoading.setText(loaderMessage);
        }else{
            mTxtLoading.setVisibility(View.GONE);
        }

        try {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        try {
            if (dialog != null) {
                dialog.cancel();
                dialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isDialogShowing() {
        if (dialog != null && dialog.isShowing()) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }



}