package com.project.yura.photoeditor.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.Utils;
import com.flask.colorpicker.builder.PaintBuilder;
import com.flask.colorpicker.slider.AbsCustomSlider;


public class RadiusSlider extends AbsCustomSlider {

    private int color = Color.BLACK;
    private final Paint barPaint = PaintBuilder.newPaint().build();
    private final Paint solid = PaintBuilder.newPaint().build();
    private final Paint clearingStroke;
    private ColorPickerView colorPicker;

    public RadiusSlider(Context context) {
        super(context);
        this.clearingStroke = PaintBuilder.newPaint().color(-1).xPerMode(PorterDuff.Mode.CLEAR).build();
    }

    public RadiusSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.clearingStroke = PaintBuilder.newPaint().color(-1).xPerMode(PorterDuff.Mode.CLEAR).build();
    }

    public RadiusSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.clearingStroke = PaintBuilder.newPaint().color(-1).xPerMode(PorterDuff.Mode.CLEAR).build();
    }

    protected void drawBar(Canvas barCanvas) {
        int width = barCanvas.getWidth();
        int height = barCanvas.getHeight();
        float[] hsv = new float[3];
        Color.colorToHSV(this.color, hsv);
        int l = Math.max(2, width / 256);

        for(int x = 0; x <= width; x += l) {
            // hsv[2] = (float)x / (float)(width - 1);
            this.barPaint.setColor(Color.HSVToColor(hsv));
            barCanvas.drawRect((float)x, 0.0F, (float)(x + l), (float)height, this.barPaint);
        }

    }

    protected void onValueChanged(float value) {
//        if(this.colorPicker != null) {
//             this.colorPicker.setLightness(value);
//        }
    }

    protected void drawHandle(Canvas canvas, float x, float y) {
        //this.solid.setColor(Utils.colorAtLightness(this.color, this.value));
        this.solid.setColor(this.color);
        canvas.drawCircle(x, y, (float)this.handleRadius, this.clearingStroke);
        canvas.drawCircle(x, y, (float)this.handleRadius * 0.75F, this.solid);
    }

    public void setColorPicker(ColorPickerView colorPicker) {
        this.colorPicker = colorPicker;
    }

    public void setRadius(int radius) {
        this.value = radius / 100f;
        if(this.bar != null) {
            this.updateBar();
            this.invalidate();
        }
    }

    public void setColor(int color) {
        this.color = color;
        this.value = Utils.lightnessOfColor(color);
        if(this.bar != null) {
            this.updateBar();
            this.invalidate();
        }

    }
}
