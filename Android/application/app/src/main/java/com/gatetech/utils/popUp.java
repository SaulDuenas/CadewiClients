package com.gatetech.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public final class popUp {


    static public ProgressDialog mprogressdialog =null;

    public static void WaitMessageBox(String title,String message,Activity activity) throws InterruptedException {
        mprogressdialog = new ProgressDialog(activity);
        mprogressdialog.setTitle(title);
        mprogressdialog.setMessage(message);
        mprogressdialog.setCancelable(false);
        mprogressdialog.show();
        Thread.sleep(100);

    }

    public static void CloseWaitMessageBox() {
        if (mprogressdialog != null && mprogressdialog.isShowing()) {
            mprogressdialog.hide();
            mprogressdialog.dismiss();
            mprogressdialog = null;
        }
    }

    public static void AlertDialog(String message, String title, String strbutton, Activity activity){
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(strbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.create();
        builder.show();
    }

}
