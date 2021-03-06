package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * @author Aaditya Deowanshi
 */
public class UbuntuButton extends Button {

    public UbuntuButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            TypeFaceProvider.setTypeFace(context, this, TypeFaceProvider.UBUNTU_REGULAR);
        }
    }

}
