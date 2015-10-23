package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Sibi on 23/10/15.
 */
public class UbuntuEditText extends EditText {

    public UbuntuEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypeFaceProvider.setTypeFace(context, this, TypeFaceProvider.UBUNTU_REGULAR);
        }
    }

}
