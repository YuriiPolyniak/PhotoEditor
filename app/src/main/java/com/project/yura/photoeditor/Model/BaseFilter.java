package com.project.yura.photoeditor.Model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

public abstract class BaseFilter {

    public Bitmap applyColorMatrix(Bitmap image, ColorMatrix matrix) {
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

   /* Bitmap applyColorMatrix(Bitmap image, ColorMatrix matrix) {
        int height = image.getHeight();
        int width = image.getWidth();

        int[] pixels = new int[height * width]; //array for image pixels
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        CustomArray arr = new CustomArray(pixels, width, height);

        //arrays for storage current pixel of image
        int[] element = new int[4];
        int[] newElement = new int[4];

        float[] colorMatrixArray = matrix.getArray(); //make color matrix easy to use
        CustomArrayFloat colorMatrix = new CustomArrayFloat(colorMatrixArray, 5, 4);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                //put pixel of matrix to local variable
                int pix = arr.IJ(j, i);
                for (int k = 0; k < 4; k++) {
                    element[k] = (pix >> (8 * (3 - (k == 3 ? 0 : k + 1)))) & 0xff;
                }

                //apply color matrix to current pixel and save result to newElement
                for (int k = 0; k < 4; k++) {
                    float sum = 0;
                    for (int l = 0; l < 4; l++) {
                        sum += element[l] * colorMatrix.IJ(k, l);
                    }
                    sum += colorMatrix.IJ(k, 4);
                    newElement[k] = (int)sum;
                }

                //save values of array to pixel of new image
                int newPix = 0;
                for (int k = 0; k < 4; k++) {
                    newPix = newPix | (Math.max(Math.min(newElement[k], 255), 0) << (8 * (3 - (k == 3 ? 0 : k + 1))));
                }
                arr.IJ(j, i, newPix);
            }
        }

        //return new image
        return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
    }*/
}
