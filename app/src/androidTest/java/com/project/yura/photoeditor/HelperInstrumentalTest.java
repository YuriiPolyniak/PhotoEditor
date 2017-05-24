package com.project.yura.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.project.yura.photoeditor.Model.Helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class HelperInstrumentalTest {
    private Context appContext;
    private Bitmap testImage;

    @Before
    public void initCustomFilters() {
        appContext = InstrumentationRegistry.getTargetContext();

        testImage = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.test_image);
    }

    @Test
    public void resizeUp_isCorrect() throws Exception {
        int width = testImage.getWidth(), height = testImage.getHeight();
        Bitmap resultImage = Helper.ResizeBitmap(testImage, width * 2, height * 2, true);

        assertEquals(width * 2, resultImage.getWidth());
        assertEquals(height * 2, resultImage.getHeight());
    }

    @Test
    public void resizeDown_isCorrect() throws Exception {
        int width = testImage.getWidth(), height = testImage.getHeight();
        Bitmap resultImage = Helper.ResizeBitmap(testImage, width / 2, height / 2, true);

        assertEquals(width / 2, resultImage.getWidth());
        assertEquals(height / 2, resultImage.getHeight());
    }

    @Test
    public void resizeToHigherNoFill_isCorrect() throws Exception {
        int width = testImage.getWidth(), height = testImage.getHeight();
        Bitmap resultImage = Helper.ResizeBitmap(testImage, width * 2, height * 4, false);

        assertEquals(height * 4, resultImage.getWidth());
        assertNotEquals(height * 4, resultImage.getHeight());
    }

    @Test
    public void resizeToHigherFill_isCorrect() throws Exception {
        int width = testImage.getWidth(), height = testImage.getHeight();
        Bitmap resultImage = Helper.ResizeBitmap(testImage, width * 2, height * 4, true);

        assertEquals(width * 4, resultImage.getWidth());
        assertEquals(height * 4, resultImage.getHeight());
    }

}
