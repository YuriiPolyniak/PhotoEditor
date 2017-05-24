package com.project.yura.photoeditor.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.util.Log;

public class CustomAdjust extends BaseFilter {
   // private Context context = null;

    public enum AdjustType {
        BRIGHT,
        CONTRAST,
        BALANCE,
        SATURATION,
        CROP
    }

    public enum AdjustTypeParameter {
        WEIGHT,
        COLOR,
        RADIUS
    }

    public static class AdjustParameters {
        int bright;
        int contrast;
        int balance;
        int saturation;
        int balanceColor;
        int balanceRadius;

        public AdjustParameters() {
            balance = 50;
            bright = 50;
            contrast = 50;
            saturation = 50;
            balanceColor = Color.BLUE;
            balanceRadius = 50;
        }

        public void update(AdjustType type, AdjustTypeParameter typeParameter, int value) {
            switch (type) {
                case BRIGHT:
                    bright = value;
                    break;
                case CONTRAST:
                    contrast = value;
                    break;
                case BALANCE:
                    switch(typeParameter){
                        case WEIGHT:
                            balance = value;
                            break;
                        case COLOR:
                            balanceColor = value;
                            break;
                        case RADIUS:
                            balanceRadius = value;
                            break;
                    }
                    break;
                case SATURATION:
                    saturation = value;
                    break;
            }
        }

        public int get(AdjustType type, AdjustTypeParameter typeParameter) {
            switch (type) {
                case BRIGHT:
                    return bright;
                case CONTRAST:
                    return contrast;
                case BALANCE:
                    switch(typeParameter){
                        case WEIGHT:
                            return balance;
                        case COLOR:
                            return balanceColor;
                        case RADIUS:
                            return balanceRadius;
                    }
                case SATURATION:
                    return saturation;
                default:
                    return -1;
            }
        }

        public void update(AdjustType type, int value) {
            update(type, AdjustTypeParameter.WEIGHT, value);
        }

        public int get(AdjustType type) {
            return get(type, AdjustTypeParameter.WEIGHT);
        }
    }

    public Bitmap adjustBrightness(Bitmap image, AdjustParameters weight) {
        float fWeight = weight.bright * 3 - 150;

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
                1, 0, 0, 0, fWeight,
                0, 1, 0, 0, fWeight,
                0, 0, 1, 0, fWeight,
                0, 0, 0, 1, 0
        });

        return applyColorMatrix(image, colorMatrix);
    }

    public Bitmap adjustContrast(Bitmap image, AdjustParameters weight) {
        float c = weight.contrast / 100.0f * 1.6f + 0.2f;
        float t = (1.0f - c) / 2.0f;
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
                c, 0, 0, 0, t,
                0, c, 0, 0, t,
                0, 0, c, 0, t,
                0, 0, 0, 1, 0
        });

        return applyColorMatrix(image, colorMatrix);
    }

    public Bitmap adjustBalance(Bitmap image, AdjustParameters parameters) {
        final float weight = parameters.balance / 50f - 1;
        final float radius = parameters.balanceRadius;
        float[] hsv = new float[3];
        Color.colorToHSV(parameters.balanceColor, hsv);
        final float C = hsv[0]; //selected color
        final float A = HSL_Helper.getHueForPosition(HSL_Helper.ColorPosition.LEFT, C, radius);
        final float B = HSL_Helper.getHueForPosition(HSL_Helper.ColorPosition.RIGHT, C, radius);
        long startTime = System.currentTimeMillis();

//        float fWeight = weight * 3.6f - 180;
//        if (fWeight < 0) {
//            fWeight = 360 + fWeight;
//        }

        final int height = image.getHeight();
        final int width = image.getWidth();
        int[] pixels = new int[height * width];

        image.getPixels(pixels, 0, width, 0, 0, width, height);

        final CustomArray arr = new CustomArray(pixels, width, height);

        final int length = 6;
        final Thread[] threads = new Thread[length];

        for (int i = 0; i < length; i++) {
            int startRow, endRow;
            startRow = i * height/length;
            endRow = (i + 1)* height/length;

            //final float finalFWeight = fWeight;
            threads[i] = new Thread() {
                private int startRow, endRow;

                Thread init(int startRow, int endRow) {
                    this.startRow = startRow;
                    this.endRow = endRow;
                    return this;
                }

                @Override
                public void run() {
                    float[] hsv = new float[3];
                    for (int j = startRow; j < endRow; j++) {
                        for (int i = 0; i < width; i++) {
                            int pix = arr.IJ(j, i);
                            int alpha = Color.alpha(pix);

                            Color.colorToHSV(pix, hsv);
                            float X = hsv[0];
                            if ((A < B && A < X && X < B) ||
                                    (A > C && (X > A || X < B)) ||
                                    (C > B && (A < X || X < B))){
                                //hsv[0] = (hsv[0] + weight) % 360;
                                hsv[1] = Math.max(Math.min(hsv[1] + weight, 1), 0);
                                int newPix = Color.HSVToColor(alpha, hsv);
                                arr.IJ(j, i, newPix);
                            }
                        }
                    }
                }
            }.init(startRow, endRow);

            threads[i].start();
        }

        try {
            for (Thread thread :threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
        Log.d("FILTER", "Time: " + finishTime);

        return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);

        /*        float fWeight = parameters.balance / 100.0f;

        ColorMatrix colorMatrix = new ColorMatrix();

        ColorMatrix grayFilter = new ColorMatrix(new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0,
        });
        colorMatrix.postConcat(grayFilter);

        return applyColorMatrix(image, colorMatrix);*/
    }

    public Bitmap adjustSaturation(Bitmap image, AdjustParameters weight) {
        float fWeight = weight.saturation / 50.0f ;

        if (fWeight > 1) {
            fWeight = fWeight + 3 * (fWeight - 1);
        }
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(fWeight);

        return applyColorMatrix(image, colorMatrix);
    }
}
