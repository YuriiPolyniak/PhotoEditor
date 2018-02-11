package com.project.yura.photoeditor;

import com.project.yura.photoeditor.processing.model.CustomArray;

import org.junit.Test;

import static org.junit.Assert.*;

public class CustomArrayTest {
    @Test
    public void CustomArray_isCorrect() throws Exception {
        int width = 4, height = 5;
        int[] arrayToTest = new int[] {
                11, 12, 13, 14,
                15, 16, 17, 18,
                19, 20, 21, 22,
                23, 24, 25, 26,
                27, 28, 29, 30
        };
        CustomArray array = new CustomArray(arrayToTest, width, height);
        assertEquals(29, array.IJ(4, 2));
        assertEquals(22, array.IJ(2, 3));

        array.IJ(4, 2, -44);
        assertEquals(-44, array.IJ(4, 2));
    }
}
