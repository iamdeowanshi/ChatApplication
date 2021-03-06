package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Aaditya Deowanshi
 */
public class UbuntuBoldTextView extends TextView {

    public UbuntuBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypeFaceProvider.setTypeFace(context, this, TypeFaceProvider.UBUNTU_BOLD);
        }
    }
}
