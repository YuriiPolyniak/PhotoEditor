package com.project.yura.photoeditor.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.utils.Helper;
import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.utils.ImageHelper;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class EditImageActivity extends BaseActivity {
    private static final String KEY_IMAGE_TO_EDIT_URI = "image_uri";

    @BindView(R.id.imageToEdit)
    ImageView imageView;
    @BindView(R.id.preview_button)
    ImageView previewButton;

    private Bitmap originalBitmap;
    private CurrentSession currentSession;
    private boolean displayOriginal = false;

    public static Intent getIntentByUri(Context context, Uri data) {
        Intent intent = new Intent(context, EditImageActivity.class);
        intent.putExtra(KEY_IMAGE_TO_EDIT_URI, data);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentSession = CurrentSession.GetInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Uri uri = getIntent().getExtras().getParcelable(KEY_IMAGE_TO_EDIT_URI);

            currentSession.imageUri = uri;
            originalBitmap = ImageHelper.getBitmapFromUri(this, uri);
            currentSession.currentBitmap = originalBitmap;
        }

        Glide.with(this)
                .load(currentSession.currentBitmap)
                .into(imageView);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_image;
    }

    @OnClick(R.id.adjust_select)
    public void adjustSelect(View view) {
        startActivity(new Intent(this, ApplyAdjustActivity.class));
        overridePendingTransition(R.anim.slide_up_to, R.anim.slide_up_from);
    }

    @OnClick(R.id.frame_select)
    public void frameSelect(View view) {
        startActivity(new Intent(this, ApplyFrameActivity.class));
        overridePendingTransition(R.anim.slide_up_to, R.anim.slide_up_from);
    }

    @OnClick(R.id.filter_select)
    public void filterSelect(View view) {
        startActivity(new Intent(this, ApplyFilterActivity.class));
        overridePendingTransition(R.anim.slide_up_to, R.anim.slide_up_from);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!displayOriginal) {
            Glide.with(this)
                    .load(currentSession.currentBitmap)
                    .into(imageView);
        } else {
            previewClick();
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Exit editing this picture?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Exit", (dialog1, which) -> {
                    EditImageActivity.super.onBackPressed();
                    overridePendingTransition(R.anim.slide_left_to, R.anim.slide_left_from);
                })
                .create();
        dialog.setOnShowListener(arg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.darkOrange));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.colorText));
        });
        dialog.show();
    }

    @OnClick(R.id.cancel_button)
    public void returnBack(View view) {
        onBackPressed();
    }

    @OnClick(R.id.ok_button)
    public void saveResult(View view) {
        Intent save = new Intent(this, SaveActivity.class);
        startActivity(save);
        overridePendingTransition(R.anim.slide_right_to, R.anim.slide_right_from);
    }

    @OnClick(R.id.preview_button)
    void previewClick() {
        if (currentSession.currentBitmap != null) {
            if (displayOriginal) {
                Glide.with(this)
                        .load(currentSession.currentBitmap)
                        .into(imageView);
                previewButton.setImageResource(R.drawable.preview_button_dark);
                displayOriginal = false;
            } else {
                imageView.setImageBitmap(originalBitmap);//currentSession.currentBitmap);
                previewButton.setImageResource(R.drawable.preview_button_light);
                displayOriginal = true;
            }
        }
    }
}
