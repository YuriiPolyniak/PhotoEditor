package com.project.yura.photoeditor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.slider.OnValueChangedListener;
import com.project.yura.photoeditor.CustomViews.RadiusSlider;
import com.project.yura.photoeditor.Model.HSL_Helper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorPickerDialog extends DialogFragment {
    //DialogInterface.OnClickListener positiveListener;
    //int initialColor;
    int color, radius;
    private IUpdate iUpdate;
    @BindView(R.id.color_picker_view) ColorPickerView colorPickerView;
    //@BindView(R.id.v_lightness_slider) LightnessSlider lightnessSlider;
    //@BindView(R.id.radius_seek_bar) SeekBar radiusSeekBar;
    @BindView(R.id.v_radius_slider) RadiusSlider radiusSlider;
    @BindView(R.id.color_picker_left_preview) ImageView leftPreview;
    @BindView(R.id.color_picker_center_preview) ImageView centerPreview;
    @BindView(R.id.color_picker_right_preview) ImageView rightPreview;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iUpdate = (IUpdate) context;
    }

    public static ColorPickerDialog getDialog(IUpdate context, int initialColor, int initialRadius){
        ColorPickerDialog newDialog =  new ColorPickerDialog();
        //newDialog.positiveListener = positiveListener;
        newDialog.iUpdate = context;
        newDialog.color = initialColor;
        newDialog.radius = initialRadius;
       // newDialog.radius = 50;
        return newDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        ButterKnife.bind(this, view);

        colorPickerView.setInitialColor(color, false);
        //radiusSeekBar.setProgress(50);
        buildPreviewColors();

        /*LinearGradient test = new LinearGradient(0.f, 0.f, 300.f, 0.0f,

                new int[] { 0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(test);
        radiusSeekBar.setProgressDrawable( (Drawable)shape );*/
        /*GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.RED, Color.BLACK});*/
        //radiusSeekBar.setBackgroundResource(R.color.colorPrimary);
        //radiusSeekBar.setProgressDrawable(gradient);
        //radiusSeekBar.setProgressDrawable(new ColorDrawable(Color.CYAN));

//        int seekBarColor = getResources().getColor(R.color.colorPrimaryDark);
//        radiusSeekBar.getProgressDrawable().setColorFilter(seekBarColor, PorterDuff.Mode.SRC_IN);
//        radiusSeekBar.getThumb().setColorFilter(seekBarColor, PorterDuff.Mode.SRC_IN);
//
//        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                radius = seekBar.getProgress();
//                buildPreviewColors();
//            }
//        });

        radiusSlider.setOnValueChangedListener(new OnValueChangedListener() {
            @Override
            public void onValueChanged(float v) {
                radius = (int)(v * 100);
                buildPreviewColors();
            }
        });

        colorPickerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (colorPickerView.getSelectedColor() != color) {
                    color = colorPickerView.getSelectedColor();
                    radius = 50;
                    buildPreviewColors();
                }
                return false;
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Choose color")
                .setView(view)
                //.setView(R.layout.dialog_color_picker)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        iUpdate.UpdateBalance(color, radius);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.darkOrange));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorText));
            }
        });
        return dialog;
    }

    void buildPreviewColors(){
        float hsvRadius = radius;
        float centerHue;
        float[] hsv = new float[3];
        int size = (int)getResources().getDimension(R.dimen.radius_color_preview_size);

        Color.colorToHSV(color, hsv);
        centerHue = hsv[0];
        centerPreview.setImageDrawable(drawCircle(getActivity(), size, size, color));


        hsv[0] = HSL_Helper.getHueForPosition(HSL_Helper.ColorPosition.LEFT, centerHue, hsvRadius);
        leftPreview.setImageDrawable(drawCircle(getActivity(), size, size, Color.HSVToColor(hsv)));

        hsv[0] = HSL_Helper.getHueForPosition(HSL_Helper.ColorPosition.RIGHT, centerHue, hsvRadius);
        rightPreview.setImageDrawable(drawCircle(getActivity(), size, size, Color.HSVToColor(hsv)));

       /* Color.colorToHSV(color, hsv);
        centerHue = hsv[0];

        centerPreview.setImageDrawable(drawCircle(getActivity(), size, size, color));

        hsv[0] = (360 + centerHue - hsvRadius) % 360;
        leftPreview.setImageDrawable(drawCircle(getActivity(), size, size, Color.HSVToColor(hsv)));

        hsv[0] = (centerHue + hsvRadius) % 360;
        rightPreview.setImageDrawable(drawCircle(getActivity(), size, size, Color.HSVToColor(hsv)));*/

        //centerPreview.setColorFilter(color);
        //leftPreview.setColorFilter(Color.HSVToColor(hsv));
        //rightPreview.setColorFilter(Color.HSVToColor(hsv));

        radiusSlider.setRadius(radius);
    }

//    float getFractionalPart(float digit) {
//        return digit - (int) digit;
//    }

    public static GradientDrawable drawCircle (Context context, int width, int height, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(10, Color.BLACK);
        drawable.setSize(width, height);
        return drawable;
        /*ShapeDrawable oval = new ShapeDrawable (new OvalShape());
        oval.setIntrinsicHeight (height);
        oval.setIntrinsicWidth (width);
        oval.getPaint().setColor (color);
        return oval;*/
        /*
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke((int)cET.dpToPx(2), Color.parseColor("#EEEEEE"));
        drawable.setSize((int)cET.dpToPx(240), (int)cET.dpToPx(240));
         */
    }

    public interface IUpdate {
        void UpdateBalance(int color, int radius);
    }
}
