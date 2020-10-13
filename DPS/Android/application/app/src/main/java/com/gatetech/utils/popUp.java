package com.gatetech.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.gatetech.cadewiclients.R;

public class popUp {

    static OnpopUpListener listener;

    static public ProgressDialog mprogressdialog =null;

    static public void setListener(OnpopUpListener listener) {
        popUp.listener=listener;
    }


    public static void WaitMessageBox(String title,String message,Activity activity) {
        mprogressdialog = new ProgressDialog(activity);
        mprogressdialog.setTitle(title);
        mprogressdialog.setMessage(message);
        mprogressdialog.setCancelable(false);
        mprogressdialog.show();


    }

    public static void CloseWaitMessageBox() {
        if (mprogressdialog != null && mprogressdialog.isShowing()) {
            mprogressdialog.hide();
            mprogressdialog.dismiss();
            mprogressdialog = null;
        }
    }


    static public void AlertDialog(final String key, String message, String title, String strbutton, Activity activity) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(strbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        listener.popUponresponse(key,dialog);
                    }
                });

        builder.create();
        builder.show();
    }

    static public void AlertDialog(String message, String title, String strbutton, Activity activity) {

        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setMessage(message)
                .setTitle(title)
                .setIcon(R.drawable.alert_circle)
                .setPositiveButton(strbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        AlertDialog_onClick("",dialog);

                    }
                });

        builder.create();
        builder.show();
    }


    public interface OnpopUpListener {
        void popUponresponse (String key, DialogInterface dialog);
    }


    static public void AlertDialog_onClick(String key, DialogInterface dialog) {
        dialog.cancel();
    }

}
