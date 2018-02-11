package com.project.yura.photoeditor;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.support.test.runner.AndroidJUnit4;

import com.project.yura.photoeditor.processing.BaseFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ColorMatrixInstrumentedTest {
    private Bitmap testImage;
    private int[] testImagePixels;

    @Before
    public void initImage() {
        int width = 3, height = 3;
        testImagePixels = new int[]{
                getPx(255, 0, 0), getPx(255, 255, 255), getPx(127, 127, 0),
                getPx(0, 255, 0), getPx(0, 0, 0), getPx(127, 0, 127),
                getPx(0, 0, 255), getPx(127, 127, 127), getPx(0, 127, 127),
        };
        testImage = Bitmap.createBitmap(testImagePixels, width, height, Bitmap.Config.ARGB_8888);
    }

    private int[] getPixelsFromBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    private int getPx(int r, int g, int b) {
        return b + (g << 8) + (r << 16) + (0xff << 24);
    }

    @Test
    public void defaultMatrix_isCorrect() throws Exception {
        BaseFilter baseFilter = new BaseFilter(){
        };

        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0,
        });

        int[] expectedResult = testImagePixels;
        int[] result = getPixelsFromBitmap(baseFilter.applyColorMatrix(testImage, colorMatrix));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void halfMatrix_isCorrect() throws Exception {
        BaseFilter baseFilter = new BaseFilter(){
        };

        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                0.5f, 0, 0, 0, 0,
                0, 0.5f, 0, 0, 0,
                0, 0, 0.5f, 0, 0,
                0, 0, 0, 1, 0,
        });

        int[] expectedResult = new int[]{
                -8454144, -8421505, -12632320,
                -16744704, -16777216, -12648385,
                -16777089, -12632257, -16761025
        };

        int[] result = getPixelsFromBitmap(baseFilter.applyColorMatrix(testImage, colorMatrix));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void swapColorMatrix_isCorrect() throws Exception {
        BaseFilter baseFilter = new BaseFilter(){
        };

        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                0, 0, 1, 0, 0,
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 0, 1, 0,
        });

        int[] expectedResult = new int[]{
                -16711936, -1, -16744577,
                -16776961, -16777216, -8421632,
                -65536, -8421505, -8454017
        };

        int[] result = getPixelsFromBitmap(baseFilter.applyColorMatrix(testImage, colorMatrix));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void fullMatrix_isCorrect() throws Exception {
        BaseFilter baseFilter = new BaseFilter(){
        };

        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                1, 1, 1, 0, 0,
                1, 1, 1, 0, 0,
                1, 1, 1, 0, 0,
                0, 0, 0, 1, 0,
        });

        int[] expectedResult = new int[]{
                -1, -1, -65794,
                -1, -16777216, -65794,
                -1, -1, -65794
        };

        int[] result = getPixelsFromBitmap(baseFilter.applyColorMatrix(testImage, colorMatrix));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void fullDifferentMatrix_isCorrect() throws Exception {
        BaseFilter baseFilter = new BaseFilter(){
        };

        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                1, 0.5f, 0.25f, 0, 0,
                0.25f, 1, 0.5f, 0, 0,
                0.5f, 0.25f, 1, 0, 0,
                0, 0, 0, 1, 0,
        });

        int[] expectedResult = new int[]{
                -49025, -1, -4284577,
                -8388800, -16777216, -6332482,
                -12550145, -2171170, -10502497
        };

        int[] result = getPixelsFromBitmap(baseFilter.applyColorMatrix(testImage, colorMatrix));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void emptyMatrix_isCorrect() throws Exception {
        BaseFilter baseFilter = new BaseFilter(){
        };

        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 1, 0,
        });

        int[] expectedResult = new int[]{
                -16777216, -16777216, -16777216,
                -16777216, -16777216, -16777216,
                -16777216, -16777216, -16777216
        };

        int[] result = getPixelsFromBitmap(baseFilter.applyColorMatrix(testImage, colorMatrix));
        assertArrayEquals(expectedResult, result);
    }
}
