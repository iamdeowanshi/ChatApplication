package com.mtvindia.connect.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Ref: https://github.com/JakeWharton/u2020/blob/master/src/main/java/com/jakewharton/u2020/ui/transform/CircleStrokeTransformation.java
 *
 * @author Jake Wharton
 */
public class CircleStrokeTransformation implements Transformation {

    private final float strokeWidth;
    private final Paint strokePaint;
    private final int strokeColor;

    public CircleStrokeTransformation(Context context, int strokeColor, int strokeWidthDp) {
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidthDp * context.getResources().getDisplayMetrics().density;

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        int size = bitmap.getWidth();
        Bitmap rounded = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounded);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaderPaint.setShader(shader);

        RectF rect = new RectF(0, 0, size, size);
        float radius = size / 2f;
        canvas.drawRoundRect(rect, radius, radius, shaderPaint);

        strokePaint.setStrokeWidth(strokeWidth);

        float strokeInset = strokeWidth / 2f;
        rect.inset(strokeInset, strokeInset);
        float strokeRadius = radius - strokeInset;
        canvas.drawRoundRect(rect, strokeRadius, strokeRadius, strokePaint);

        bitmap.recycle();
        return rounded;
    }

    @Override
    public String key() {
        return "circle_stroke(" + strokeColor + "," + strokeWidth + ")";
    }

}