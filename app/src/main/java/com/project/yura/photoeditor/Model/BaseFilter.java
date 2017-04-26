package com.project.yura.photoeditor.Model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

public abstract class BaseFilter {
    public Bitmap applyColorMatrix (Bitmap image, ColorMatrix matrix) {
        long startTime = System.currentTimeMillis();

        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(),
                image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(new ColorMatrixColorFilter(
                matrix));
        canvas.drawBitmap(image, 0, 0, paint);

        float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
        //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
        Log.d("FILTER", "Time: " + finishTime);

        return bitmap;
    }
}
