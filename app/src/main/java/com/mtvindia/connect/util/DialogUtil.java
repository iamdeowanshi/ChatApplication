package com.mtvindia.connect.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

/**
 * Created by Sibi on 05/11/15.
 */

/**
 * Utility class that provides Alert Dialog box to display pop up and set actions for each
 * buttons.
 */
public class DialogUtil {

    /**
     * Returns alert dialog.
     * @param activity
     * @param title
     * @param message
     * @param positiveMessage
     * @param negativeMessage
     * @return
     */
    public Dialog createAlertDialog(final Activity activity, String title, String message,String positiveMessage, String negativeMessage) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(negativeMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();

        return dialog;
    }

}
