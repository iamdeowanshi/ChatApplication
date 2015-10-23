package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Sibi on 23/10/15.
 */
public class UbuntuButton extends Button {


    public UbuntuButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            TypeFaceProvider.setTypeFace(context, this, TypeFaceProvider.UBUNTU_REGULAR);
        }
    }

}
