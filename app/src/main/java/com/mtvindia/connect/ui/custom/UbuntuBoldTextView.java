package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Sibi on 26/11/15.
 */
public class UbuntuBoldTextView extends TextView {

    public UbuntuBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypeFaceProvider.setTypeFace(context, this, TypeFaceProvider.UBUNTU_BOLD);
        }
    }
}
