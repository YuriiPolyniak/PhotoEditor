package com.project.yura.photoeditor.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.project.yura.photoeditor.ui.dialog.ColorPickerDialog;
import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.processing.model.CustomAdjust;
import com.project.yura.photoeditor.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyAdjustActivity extends AppCompatActivity
implements ColorPickerDialog.IUpdate {

    //region Definitions
    private Bitmap editedBitmap = null;
    private Bitmap balanceEditedBitmap = null;
    private Bitmap scaledOriginalBitmap = null;
    //private Bitmap realEditedBitmap = null;
    private CurrentSession currentSession;
    private CustomAdjust customAdjust;
    private CustomAdjust.AdjustParameters adjustParameters;
    private CustomAdjust.AdjustType adjustCurrentType;

    private View lastSelectedView;

    @BindView(R.id.imageToEdit) ImageView imageView;
    @BindView(R.id.preview_button) ImageView previewButton;
    @BindView(R.id.resize_button) ImageView resizeButton;
    @BindView(R.id.rotate_left_button) ImageView rotateLeftButton;
    @BindView(R.id.rotate_right_button) ImageView rotateRightButton;
    @BindView(R.id.color_picker_button) ImageView colorPickerButton;
    @BindView(R.id.layout_to_hide) ViewGroup barToHide;
    @BindView(R.id.seek_bar) SeekBar seekBar;
    @BindView(R.id.seek_bar_balance) SeekBar seekBarBalance;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    //@BindView(R.id.activity_apply_adjust) LinearLayout workspaceLayout;
    @BindView(R.id.crop_image_view) CropImageView cropImageView;

    private boolean displayOriginal;
    //endregion

    //region Override methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_adjust);

        ButterKnife.bind(this);

        currentSession = CurrentSession.GetInstance();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        scaledOriginalBitmap = currentSession.currentBitmap;/*Helper.ResizeBitmap(
                currentSession.currentBitmap, displayMetrics.widthPixels, displayMetrics.heightPixels, false);*/
        editedBitmap = scaledOriginalBitmap; //currentSession.currentBitmap;
        //imageView = (ImageView)findViewById(R.id.imageToEdit);
        imageView.setImageBitmap(scaledOriginalBitmap); //currentSession.currentBitmap
        //previewButton = (ImageView)findViewById(R.id.preview_button);
        //resizeButton = (ImageView)findViewById(R.id.resize_button);
        //barToHide = (ViewGroup)findViewById(R.id.layout_to_hide);
        //workspaceLayout = (LinearLayout) findViewById(R.id.activity_apply_adjust);
        //seekBar = (SeekBar)findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startImageProcessing();
                adjustParameters.update(adjustCurrentType, seekBar.getProgress());
                if (displayOriginal) {
                    previewClick(null);
                }

                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        applyFilter(false);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        imageView.setImageBitmap(editedBitmap);
                        stopImageProcessing();
                    }
                };
                task.execute();

            }
        });

        seekBarBalance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startImageProcessing();
                adjustParameters.update(adjustCurrentType, seekBar.getProgress());
                if (displayOriginal) {
                    previewClick(null);
                }

                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        applyFilter(true);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        imageView.setImageBitmap(editedBitmap);
                        stopImageProcessing();
                    }
                };
                task.execute();
                //applyFilter(true);
            }
        });

        customAdjust = new CustomAdjust();
        adjustParameters = new CustomAdjust.AdjustParameters();

        seekBarBalance.getProgressDrawable().setColorFilter(adjustParameters.get(CustomAdjust.AdjustType.BALANCE, CustomAdjust.AdjustTypeParameter.COLOR), PorterDuff.Mode.SRC_IN);
        seekBarBalance.getThumb().setColorFilter(adjustParameters.get(CustomAdjust.AdjustType.BALANCE, CustomAdjust.AdjustTypeParameter.COLOR), PorterDuff.Mode.SRC_IN);

        cropImageView.setImageBitmap(editedBitmap);
    }

    @Override
    public void onBackPressed() {
        if (barToHide.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_down_to, R.anim.slide_down_from);
        } else {
            resizeClick(resizeButton);
        }
    }
    //endregion

    //region OnClick listeners

    // show image without change (and hide)
    @OnClick(R.id.preview_button)
    void previewClick(View view) {
        if (editedBitmap != null) {
            if (displayOriginal) {
                imageView.setImageBitmap(editedBitmap);
                previewButton.setImageResource(R.drawable.preview_button_dark);
                displayOriginal = false;
            } else {
                imageView.setImageBitmap(scaledOriginalBitmap);
                previewButton.setImageResource(R.drawable.preview_button_light);
                displayOriginal = true;
            }
        }
    }

    // hide action bar
    @OnClick(R.id.resize_button)
    void resizeClick(View view) {
        if (barToHide.getVisibility() == View.VISIBLE) {
            barToHide.setVisibility(View.GONE);
            resizeButton.setImageResource(R.drawable.resize_big);
            if (adjustCurrentType == CustomAdjust.AdjustType.BALANCE) {
                colorPickerButton.setVisibility(View.GONE);
            }
        } else {
            barToHide.setVisibility(View.VISIBLE);
            resizeButton.setImageResource(R.drawable.resize_small);
            if (adjustCurrentType == CustomAdjust.AdjustType.BALANCE) {
                colorPickerButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.color_picker_button)
    public void openColorPicker(View view) {
        ColorPickerDialog dialog = ColorPickerDialog.getDialog(
                this,
                adjustParameters.get(adjustCurrentType,
                        CustomAdjust.AdjustTypeParameter.COLOR),
                adjustParameters.get(adjustCurrentType,
                        CustomAdjust.AdjustTypeParameter.RADIUS));
        dialog.show(getFragmentManager(), "");
    }

    @OnClick(R.id.bright_adjust_select)
    public void brightAdjustSelect(View view) {
        selectAdjust(CustomAdjust.AdjustType.BRIGHT, view);
    }

    @OnClick(R.id.contrast_adjust_select)
    public void contrastAdjustSelect(View view) {
        selectAdjust(CustomAdjust.AdjustType.CONTRAST, view);
    }

    @OnClick(R.id.balance_adjust_select)
    public void balanceAdjustSelect(View view) {
        selectAdjust(CustomAdjust.AdjustType.BALANCE, view);
    }

    @OnClick(R.id.saturation_adjust_select)
    public void saturationAdjustSelect(View view) {
        selectAdjust(CustomAdjust.AdjustType.SATURATION, view);
    }

    @OnClick(R.id.crop_adjust_select)
    public void cropAdjustSelect(View view) {
        selectAdjust(CustomAdjust.AdjustType.CROP, view);
    }

    @OnClick(R.id.rotate_left_button)
    public void rotateLeft(View view){
        //cropImageView.rotateImage(90);
        Matrix m = new Matrix();
        m.postRotate(270);

        editedBitmap = Bitmap.createBitmap(editedBitmap, 0, 0,
                editedBitmap.getWidth(),
                editedBitmap.getHeight(), m, true);
        cropImageView.setImageBitmap(editedBitmap);
    }

    @OnClick(R.id.rotate_right_button)
    public void rotateRight(View view){
       //cropImageView.rotateImage(-90);
        Matrix m = new Matrix();
        m.postRotate(90);

        editedBitmap = Bitmap.createBitmap(editedBitmap, 0, 0,
                editedBitmap.getWidth(),
                editedBitmap.getHeight(), m, true);
        cropImageView.setImageBitmap(editedBitmap);
    }

    //endregion

    //region Additional methods
    private void startImageProcessing() {
        seekBar.setEnabled(false);
        seekBarBalance.setEnabled(false);
        colorPickerButton.setEnabled(false);
        rotateLeftButton.setEnabled(false);
        rotateRightButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopImageProcessing() {
        seekBar.setEnabled(true);
        seekBarBalance.setEnabled(true);
        colorPickerButton.setEnabled(true);
        rotateLeftButton.setEnabled(true);
        rotateRightButton.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

    public void returnBack(View view) {
        onBackPressed();
    }

    public void saveResult(View view) {
        if (editedBitmap != null) {
//            editedBitmap = customAdjust.AdjustBrightness(currentSession.currentBitmap, adjustParameters);
//            editedBitmap = customAdjust.AdjustContrast(editedBitmap, adjustParameters);
//            editedBitmap = customAdjust.AdjustBalance(editedBitmap, adjustParameters);
//            editedBitmap = customAdjust.AdjustSaturation(editedBitmap, adjustParameters);
            if (adjustCurrentType == CustomAdjust.AdjustType.CROP) {
                currentSession.currentBitmap = cropImageView.getCroppedImage();
            } else {
                currentSession.currentBitmap = editedBitmap;
            }
            //currentSession.currentBitmap = editedBitmap;
            /*selectedFilter.getFilter().applyFilter(
                    currentSession.currentBitmap,
                    seekBar.getProgress());*/
        }
        onBackPressed();
    }

    void selectAdjust(CustomAdjust.AdjustType type, View view) {
        if (type == CustomAdjust.AdjustType.BALANCE) {
            seekBarBalance.setVisibility(View.VISIBLE);
           // adjustParameters.update(type, 50);
            seekBarBalance.setProgress(adjustParameters.get(type));
            seekBar.setVisibility(View.GONE);
            colorPickerButton.setVisibility(View.VISIBLE);
            cropImageView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(editedBitmap);
            previewButton.setVisibility(View.VISIBLE);
            resizeButton.setVisibility(View.VISIBLE);
            rotateLeftButton.setVisibility(View.GONE);
            rotateRightButton.setVisibility(View.GONE);

        } else if (type == CustomAdjust.AdjustType.CROP) {
            seekBarBalance.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            colorPickerButton.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            cropImageView.setVisibility(View.VISIBLE);
            cropImageView.setImageBitmap(editedBitmap);
            previewButton.setVisibility(View.GONE);
            resizeButton.setVisibility(View.GONE);
            rotateLeftButton.setVisibility(View.VISIBLE);
            rotateRightButton.setVisibility(View.VISIBLE);

        } else {
            seekBarBalance.setVisibility(View.GONE);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(adjustParameters.get(type));
            colorPickerButton.setVisibility(View.GONE);
            cropImageView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(editedBitmap);
            previewButton.setVisibility(View.VISIBLE);
            resizeButton.setVisibility(View.VISIBLE);
            rotateLeftButton.setVisibility(View.GONE);
            rotateRightButton.setVisibility(View.GONE);

        }

        if (displayOriginal) {
            previewClick(null);
        }

        int imageRes = -1;
        if (lastSelectedView != null) {
            switch (adjustCurrentType) {
                case BRIGHT:
                    imageRes = R.drawable.adjust_bright_icon;
                    break;
                case CONTRAST:
                    imageRes = R.drawable.adjust_contrast_icon;
                    break;
                case BALANCE:
                    imageRes = R.drawable.adjust_balance_icon;
                    break;
                case SATURATION:
                    imageRes = R.drawable.adjust_black_white_icon;
                    break;
                case CROP:
                    imageRes = R.drawable.adjust_crop_icon;
                    break;
            }
            ((ImageView)(lastSelectedView.findViewById(R.id.adjust_icon))).setImageResource(imageRes);
            ((TextView)(lastSelectedView.findViewById(R.id.adjust_text)))
                    .setTextColor(getResources().getColor(R.color.darkOrange));
        }

        lastSelectedView = view;
        adjustCurrentType = type;

        if (lastSelectedView != null) {
            switch (adjustCurrentType) {
                case BRIGHT:
                    imageRes = R.drawable.adjust_selected_bright_icon;
                    break;
                case CONTRAST:
                    imageRes = R.drawable.adjust_selected_contrast_icon;
                    break;
                case BALANCE:
                    imageRes = R.drawable.adjust_selected_balance_icon;
                    break;
                case SATURATION:
                    imageRes = R.drawable.adjust_selected_black_white_icon;
                    break;
                case CROP:
                    imageRes = R.drawable.adjust_selected_crop_icon;
                    break;
            }
            ((ImageView)(lastSelectedView.findViewById(R.id.adjust_icon))).setImageResource(imageRes);
            ((TextView)(lastSelectedView.findViewById(R.id.adjust_text)))
                    .setTextColor(getResources().getColor(R.color.selectedItem));
        }
    }

    private boolean lastEditWasBalance = false;
    private int lastBalanceColor = 0;
    void applyFilter(boolean balance) {
        if (balance) {
            if (!lastEditWasBalance) {
                lastEditWasBalance = true;
                balanceEditedBitmap = editedBitmap;

                //reset all parameter (except balance)
                adjustParameters.update(CustomAdjust.AdjustType.BRIGHT, 50);
                adjustParameters.update(CustomAdjust.AdjustType.CONTRAST, 50);
                adjustParameters.update(CustomAdjust.AdjustType.SATURATION, 50);
            }

            //save adjust for different colors
            int currentColor = adjustParameters.get(CustomAdjust.AdjustType.BALANCE,
                    CustomAdjust.AdjustTypeParameter.COLOR);
            if (currentColor != lastBalanceColor) {
                lastBalanceColor = currentColor;
                balanceEditedBitmap = editedBitmap;
            }

            //apply adjust
            editedBitmap = customAdjust.adjustBalance(balanceEditedBitmap, adjustParameters);
        } else {
            if (lastEditWasBalance) {
                lastEditWasBalance = false;
                //now we will edit edited (by balance) bitmap
                scaledOriginalBitmap = editedBitmap;

                adjustParameters.update(CustomAdjust.AdjustType.BALANCE, 50);
            }

            editedBitmap = customAdjust.adjustBrightness(scaledOriginalBitmap, adjustParameters);
            editedBitmap = customAdjust.adjustContrast(editedBitmap, adjustParameters);
            editedBitmap = customAdjust.adjustSaturation(editedBitmap, adjustParameters);

            //balanceEditedBitmap = editedBitmap;
        }

        //removed to asyncTask
        //imageView.setImageBitmap(editedBitmap);
    }

    //endregion

    //region IUpdate

    @Override
    public void UpdateBalance(int color, int radius) {
        adjustParameters.update(adjustCurrentType, CustomAdjust.AdjustTypeParameter.COLOR, color);
        adjustParameters.update(adjustCurrentType, CustomAdjust.AdjustTypeParameter.RADIUS, radius);

        //update SeekBar color
        seekBarBalance.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBarBalance.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        seekBarBalance.setProgress(50);
        seekBarBalance.setContentDescription(String.valueOf(color));
    }

    //endregion
}
