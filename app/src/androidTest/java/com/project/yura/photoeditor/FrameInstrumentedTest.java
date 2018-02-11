package com.project.yura.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.project.yura.photoeditor.processing.CustomFrameFilters;
import com.project.yura.photoeditor.processing.IFilter;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FrameInstrumentedTest {
    private CustomFrameFilters customFrames;
    private Context appContext;
    private Bitmap testImage;

    @Before
    public void initCustomFilters() {
        appContext = InstrumentationRegistry.getTargetContext();

        testImage = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.test_image);
        customFrames = new CustomFrameFilters(appContext);
    }

    private int[] getPixelsFromBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    @Test
    public void CheckFilters_isCorrect() throws Exception {
        int filtersCount = customFrames.getFilters().length;
        assertNotEquals(0, filtersCount);
    }

    @Test
    public void BlackFilter_isCorrect() throws Exception {
        IFilter filter = customFrames.new FrameFilter1();

        assertEquals("Black 1", filter.getName());

        Bitmap image = filter.applyFilter(testImage, 80);
        int width = image.getWidth(), height = image.getHeight();
        int[] result = getPixelsFromBitmap(image);
        for (int i = 0; i < width; i++) {
            assertEquals(-16777216, result[i]);
        }
        for (int i = height - 1; i < width; i++) {
            assertEquals(-16777216, result[width * (height - 1) + i]);
        }
        for (int i = 0; i < height; i++) {
            assertEquals(-16777216, result[width * i]);
        }
        for (int i = 0; i < height; i++) {
            assertEquals(-16777216, result[width * i + width - 1]);
        }
    }

    @Test
    public void WhiteFilter_isCorrect() throws Exception {
        IFilter filter = customFrames.new FrameFilter2();

        assertEquals("White 1", filter.getName());

        Bitmap image = filter.applyFilter(testImage, 80);
        int width = image.getWidth(), height = image.getHeight();
        int[] result = getPixelsFromBitmap(image);
        for (int i = 0; i < width; i++) {
            assertEquals(-1, result[i]);
        }
        for (int i = height - 1; i < width; i++) {
            assertEquals(-1, result[width * (height - 1) + i]);
        }
        for (int i = 0; i < height; i++) {
            assertEquals(-1, result[width * i]);
        }
        for (int i = 0; i < height; i++) {
            assertEquals(-1, result[width * i + width - 1]);
        }
    }
}