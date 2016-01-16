package com.mtvindia.connect.util;

import android.content.pm.PackageManager;

import com.mtvindia.connect.app.di.Injector;

/**
 * Created by Sibi on 14/01/16.
 */

/**
 * Utility class that wraps access to the runtime permissions API in M and provides basic helper
 * methods.
 */
public class PermissionUtil {

    public PermissionUtil() {
        Injector.instance().inject(this);
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
