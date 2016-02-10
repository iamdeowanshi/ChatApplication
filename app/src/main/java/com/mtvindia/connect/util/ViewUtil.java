package com.mtvindia.connect.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Aaditya Deowanshi
 *
 *         Utility class which contains method to display or hide keyboard.
 */

public class ViewUtil {

    /**
     * Hides keyboard.
     *
     * @param view
     */
    public static void hideKeyboard(View view) {
        InputMethodManager inputMgr = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Shows keyboard.
     *
     * @param context
     */
    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

}
