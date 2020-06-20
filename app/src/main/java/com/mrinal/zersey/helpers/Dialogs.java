package com.mrinal.zersey.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Dialogs {

    public static ProgressDialog showProgressDialog(Context context, ProgressDialog progressDialog, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    public static void showErrorDialog(Context context, ProgressDialog progressDialog, String title, String message) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}