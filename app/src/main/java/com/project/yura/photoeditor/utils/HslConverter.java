package com.project.yura.photoeditor.utils;

public class HslConverter {
    public enum ColorPosition{
        LEFT,
        CENTER,
        RIGHT
    }

    //radius 0..100, hue 0..360
    public static float getHueForPosition(ColorPosition position, float hue, float radius) {
        radius *= 0.5f;
        switch (position) {
            case LEFT:
                return (360 + hue - radius) % 360;
            case CENTER:
                return hue;
            case RIGHT:
                return (hue + radius) % 360;
            default:
                return -1;
        }
    }
}
