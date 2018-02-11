package com.project.yura.photoeditor.processing.model;

class CustomArrayFloat {
    public float[] array;
    private int width;
    private int height;

    public CustomArrayFloat(float[] array, int width, int height) {
        this.array = array;
        this.width = width;
        this.height = height;
    }

    float IJ(int i, int j) {
        return array[i * width + j];
    }

    void IJ(int i, int j, float value) {
        array[i * width + j] = value;
    }
}