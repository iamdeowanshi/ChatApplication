package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * @author Aaditya Deowanshi
 *
 *         Typeface provide class for custom fonts.
 */
public class TypeFaceProvider {

    public static final String TYPEFACE_FOLDER = "fonts";
    public static final String TYPEFACE_EXTENSION = ".ttf";

    public final static String UBUNTU_BOLD = "Ubuntu-B";
    public final static String UBUNTU_REGULAR = "Ubuntu-L";
    public final static String UBUNTU_MEDIUM = "Ubuntu-M";


    private static Hashtable<String, Typeface> sTypeFaces
            = new Hashtable<String, Typeface>(3);

    public static Typeface getTypeFace(Context context, String fontName) {
        Typeface tempTypeface = sTypeFaces.get(fontName);

        if (tempTypeface == null) {
            String fontPath = new StringBuilder(TYPEFACE_FOLDER).append('/').append(fontName).append(TYPEFACE_EXTENSION).toString();
            tempTypeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            sTypeFaces.put(fontName, tempTypeface);
        }

        return tempTypeface;
    }

    public static void setTypeFace(Context context, TextView textView, String fontName) {
        textView.setTypeface(getTypeFace(context, fontName));
    }

}
