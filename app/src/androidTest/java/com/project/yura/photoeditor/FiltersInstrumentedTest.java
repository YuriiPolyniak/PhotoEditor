package com.project.yura.photoeditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.project.yura.photoeditor.Model.CustomFilters;
import com.project.yura.photoeditor.Model.IFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FiltersInstrumentedTest {
    private CustomFilters customFilter;
    private Context appContext;
    private Bitmap testImage;

    @Before
    public void initCustomFilters() {
        appContext = InstrumentationRegistry.getTargetContext();

        int width = 3, height = 3;
        int[] testImagePixels = {
                getPx(255, 0, 0), getPx(255, 255, 255), getPx(127, 127, 0  ),
                getPx(0, 255, 0), getPx(0,   0,   0  ), getPx(127, 0  , 127),
                getPx(0, 0, 255), getPx(127, 127, 127), getPx(0  , 127, 127),
        };
        testImage = Bitmap.createBitmap(testImagePixels, width, height, Bitmap.Config.ARGB_8888);

        customFilter = new CustomFilters(appContext);
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
    public void CheckFilters_isCorrect() throws Exception {
        int filtersCount = customFilter.getFilters().length;
        assertNotEquals(0, filtersCount);
    }

    @Test
    public void GrayScaleFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new GrayScaleFilter();

        assertEquals("GrayScale", filter.getName());
        int[] expectedResult = {
                -258382234, -251658241, -260013978,
                -261711514, -268435456, -260020608,
                -261724519, -256658509, -261717888
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void PolaroidFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new PolaroidFilter();

        assertEquals("Polaroid", filter.getName());
        int[] expectedResult = {
                -251723776, -251658241, -251658496,
                -268370176, -268435456, -251723521,
                -268435201, -251673345, -268369921
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void InverseFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new PolaroidFilter();

        assertEquals("Polaroid", filter.getName());
        int[] expectedResult = {
                -251723776, -251658241, -251658496,
                -268370176, -268435456, -251723521,
                -268435201, -251673345, -268369921
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void SepiaFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new SepiaFilter();

        assertEquals("Sepia", filter.getName());
        int[] expectedResult = {
                -262198237, -251658292, -260540341,
                -258816651, -268435456, -264889044,
                -267448524, -260013978, -263099819
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void BlackAndWhiteFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new BlackAndWhiteFilter();

        assertEquals("B & W", filter.getName());
        int[] expectedResult = {
                -268435456, -251658241, -268435456,
                -251658241, -268435456, -268435456,
                -268435456, -261000847, -268435456
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void BlurFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new BlurFilter();

        assertEquals("Blur", filter.getName());
        int[] expectedResult = {
                -261274789, -261470119, -261599657,
                -261864604, -261994654, -262124192,
                -262454676, -262584470, -262779545
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void EmbossFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new EmbossFilter();

        assertEquals("Emboss", filter.getName());
        int[] expectedResult = {
                -268431361, -268402433, -268435456,
                -268370161, -268435201, -268435328,
                -267452401, -251690993, -268431601
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void SharpenFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new SharpenFilter();

        assertEquals("Sharpen", filter.getName());
        int[] expectedResult = {
                -267452416, -267448561, -268402688,
                -268431616, -268435456, -267452401,
                -268435441, -267448448, -268370048
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void EdgeEnhanceFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new EdgeEnhanceFilter();

        assertEquals("Edge", filter.getName());
        int[] expectedResult = {
                -251723776, -251719921, -268435456,
                -268370176, -268435456, -251723521,
                -268435201, -251658496, -268402560,
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void GaussianBlurFilter_isCorrect() throws Exception {
        IFilter filter = customFilter.new GaussianBlurFilter();

        assertEquals("Gauss", filter.getName());
        int[] expectedResult = {
                -257337280, -257985450, -259493306,
                -262381732, -261855388, -262381732,
                -265271143, -264283257, -264805768
        };
        int[] result = getPixelsFromBitmap(filter.applyFilter(testImage, 80));
        assertArrayEquals(expectedResult, result);
    }
}
