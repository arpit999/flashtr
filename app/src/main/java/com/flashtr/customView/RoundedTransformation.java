package com.flashtr.customView;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by admin3 on 16-01-2016.
 */
public class RoundedTransformation implements Transformation {

    private int mBorderSize = 4;
    private int mColor = Color.WHITE;


    public RoundedTransformation(int mBorderSize, int mColor) {
        this.mBorderSize = mBorderSize;
        this.mColor = mColor;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        // TODO Auto-generated method stub
//        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
//        final RectF rectF = new RectF(rect);
//        final float roundPx = mCornerRadius;
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(source, rect, rect, paint);
//
//        // draw border
//        paint.setColor(mColor);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth((float) mBorderSize);
//        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
//        //-------------------
//
//        if(source != output) source.recycle();
//        return output;
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;

        // Prepare the background
        Paint paintBg = new Paint();
        paintBg.setColor(mColor);
        paintBg.setAntiAlias(true);

        // Draw the background circle
        canvas.drawCircle(r, r, r, paintBg);

        // Draw the image smaller than the background so a little border will be seen
        canvas.drawCircle(r, r, r - mBorderSize, paint);

        squaredBitmap.recycle();
        return bitmap;

    }

    @Override
    public String key() {
        // TODO Auto-generated method stub
        return "grayscaleTransformation()";
    }

}
