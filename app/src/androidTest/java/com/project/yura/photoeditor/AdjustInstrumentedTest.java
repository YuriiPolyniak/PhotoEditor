package com.project.yura.photoeditor;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.project.yura.photoeditor.Model.CustomAdjust;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AdjustInstrumentedTest {
    private CustomAdjust customAdjust;
    private Bitmap testImage;
    private int[] testImagePixels;

    @Before
    public void initCustomAdjust() {

        int width = 3, height = 3;
        testImagePixels = new int[]{
                getPx(255, 0, 0), getPx(255, 255, 255), getPx(127, 127, 0  ),
                getPx(0, 255, 0), getPx(0,   0,   0  ), getPx(127, 0  , 127),
                getPx(0, 0, 255), getPx(127, 127, 127), getPx(0  , 127, 127),
        };
        testImage = Bitmap.createBitmap(testImagePixels, width, height, Bitmap.Config.ARGB_8888);

        customAdjust = new CustomAdjust();
    }

    private int[] getPixelsFromBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    private int getPx(int r, int g, int b) {
        return b + (g << 8) + (r << 16) + (0xf0 << 24);
    }

    @Test
    public void adjust_isCorrect() throws Exception {
        CustomAdjust.AdjustParameters parameters = new CustomAdjust.AdjustParameters();
        parameters.update(CustomAdjust.AdjustType.BRIGHT, CustomAdjust.AdjustTypeParameter.WEIGHT, 80);

        int[] expectedResult = {
                -251700646, -251658241, -254092710,
                -262471846, -262514086, -254125350,
                -262513921, -254092582, -262481190
        };
        int[] result = getPixelsFromBitmap(customAdjust.adjustBrightness(testImage, parameters));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void contrast_isCorrect() throws Exception {
        CustomAdjust.AdjustParameters parameters = new CustomAdjust.AdjustParameters();
        parameters.update(CustomAdjust.AdjustType.CONTRAST, CustomAdjust.AdjustTypeParameter.WEIGHT, 80);

        int[] expectedResult = {
                -251723776, -251658241, -256066560,
                -268370176, -268435456, -256114500,
                -268435201, -256066372, -268387140
        };
        int[] result = getPixelsFromBitmap(customAdjust.adjustContrast(testImage, parameters));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void saturation_isCorrect() throws Exception {
        CustomAdjust.AdjustParameters parameters = new CustomAdjust.AdjustParameters();
        parameters.update(CustomAdjust.AdjustType.SATURATION, CustomAdjust.AdjustTypeParameter.WEIGHT, 80);

        int[] expectedResult = {
                -251723776, -251658241, -258566656,
                -268370176, -268435456, -251723521,
                -268435201, -260013952, -268386112
        };
        int[] result = getPixelsFromBitmap(customAdjust.adjustSaturation(testImage, parameters));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void balance_isCorrect() throws Exception {
        CustomAdjust.AdjustParameters parameters = new CustomAdjust.AdjustParameters();
        parameters.update(CustomAdjust.AdjustType.BALANCE, CustomAdjust.AdjustTypeParameter.WEIGHT, 80);
        parameters.update(CustomAdjust.AdjustType.BALANCE, CustomAdjust.AdjustTypeParameter.COLOR, -8437985);

        int[] expectedResult = {
                -251723776, -251697562, -260014080,
                -268370176, -268435456, -260046720,
                -268435201, -260033741, -268402560
        };
        int[] result = getPixelsFromBitmap(customAdjust.adjustBalance(testImage, parameters));
        assertArrayEquals(expectedResult, result);
    }
}
