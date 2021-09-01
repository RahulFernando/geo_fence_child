package com.rahul.child;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

class LoadinDialog {
    private Activity activity;
    private AlertDialog dialog;

    LoadinDialog(Activity activity){
        this.activity = activity;
    }

    public void loadingAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
