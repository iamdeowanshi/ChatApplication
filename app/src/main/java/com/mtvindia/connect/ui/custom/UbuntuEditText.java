package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @author Aaditya Deowanshi
 */
public class UbuntuEditText extends EditText {

    public UbuntuEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypeFaceProvider.setTypeFace(context, this, TypeFaceProvider.UBUNTU_REGULAR);
        }
    }

}
