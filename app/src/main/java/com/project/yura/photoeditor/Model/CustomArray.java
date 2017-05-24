package com.project.yura.photoeditor.Model;

public class CustomArray {
    public int[] array;
    private int width;
    private int height;

    public CustomArray(int[] array, int width, int height) {
        this.array = array;
        this.width = width;
        this.height = height;
    }

    public int IJ(int i, int j) {
        return array[i * width + j];
    }

    public void IJ(int i, int j, int value) {
        array[i * width + j] = value;
    }
}