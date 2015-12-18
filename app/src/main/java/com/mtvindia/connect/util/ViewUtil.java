package com.mtvindia.connect.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Farhan Ali
 */
public class ViewUtil {

    public static void hideKeyboard(View view) {
        InputMethodManager inputMgr = (InputMethodManager)view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public static void hide(Context context) {
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
