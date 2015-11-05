package com.mtvindia.connect.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.app.di.Injector;

import javax.inject.Inject;

/**
 * Created by Sibi on 05/11/15.
 */
public class DialogUtil extends BaseActivity {
    @Inject
    NetworkUtil networkUtil;

    public DialogUtil() {
        Injector.instance().inject(this);
    }

    public void displayInternetAlert(final Activity activity) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Connection Failed")
                .setMessage("Please Check Your Internet Connection")
                .setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!networkUtil.isOnline()) {
                            displayInternetAlert(activity);
                        }
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();

        dialog.show();
    }
}