package com.project.yura.photoeditor.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.utils.Helper;
import com.project.yura.photoeditor.R;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditImageActivity extends BaseActivity {
    public static final String IMAGE_TO_EDIT_URI = "image_uri";

    @BindView(R.id.imageToEdit)
    ImageView imageView;
    @BindView(R.id.preview_button)
    ImageView previewButton;

    private Bitmap originalBitmap;
    //Bitmap currentBitmap = null;
    private CurrentSession currentSession;
    private boolean displayOriginal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentSession = CurrentSession.GetInstance();
        String pathString = null;
        if (getIntent().getExtras() != null) {
            pathString = getIntent().getExtras().getString(IMAGE_TO_EDIT_URI);
            currentSession.path = pathString;
        }
        // String realPath = null;
        if (pathString != null && !Objects.equals(pathString, "")) {
            Uri imageUri = Uri.parse(pathString);
            try {
                currentSession.currentBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //imageUri.get
            ////String path = null;
            //String path = Helper.getRealPathFromURI(imageUri, getContentResolver());

            String path = Helper.getPathFromUri(this, imageUri);
            currentSession.realPath = path;
            //String path = imageUri.getPath();
            if (path != null) {
                //if (pathString != null) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(path);
                    //exif = new ExifInterface(pathString);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                    Matrix m = new Matrix();
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            m.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            m.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            m.postRotate(270);
                            break;
                    }

                    currentSession.currentBitmap = Bitmap.createBitmap(currentSession.currentBitmap, 0, 0,
                            currentSession.currentBitmap.getWidth(),
                            currentSession.currentBitmap.getHeight(), m, true);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                    currentSession.currentBitmap = Helper.ResizeBitmap(
                            currentSession.currentBitmap,
                            displayMetrics.widthPixels,
                            displayMetrics.heightPixels, false);
                    originalBitmap = currentSession.currentBitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            currentSession.path = pathString;
            currentSession.currentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            currentSession.currentBitmap = Helper.ResizeBitmap(
                    currentSession.currentBitmap,
                    displayMetrics.widthPixels,
                    displayMetrics.heightPixels, false);
            originalBitmap = currentSession.currentBitmap;
        }

        imageView.setImageBitmap(currentSession.currentBitmap);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_image;
    }

    //region onTouchEvent
    private float mx;
    private float my;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curX, curY;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
//                Log.d("LOG TOUCH", "DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
//                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
//                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                mx = curX;
                my = curY;
//                Log.d("LOG TOUCH", "MOVE");
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
//                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
//                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
//                Log.d("LOG TOUCH", "UP");
                break;
        }

        return true;
    }
    //endregion

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
        //imageView = (ImageView) findViewById(R.id.imageToEdit);
        if (!displayOriginal) {
            imageView.setImageBitmap(currentSession.currentBitmap);
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
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditImageActivity.super.onBackPressed();
                        overridePendingTransition(R.anim.slide_left_to, R.anim.slide_left_from);
                    }
                })
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
                imageView.setImageBitmap(currentSession.currentBitmap);
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