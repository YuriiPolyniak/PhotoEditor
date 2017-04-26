package com.project.yura.photoeditor.Model;

import android.graphics.Bitmap;

public interface IFilter {
    String getName();
    Bitmap applyFilter(Bitmap image, int weight);
    boolean hasWeight();
}
