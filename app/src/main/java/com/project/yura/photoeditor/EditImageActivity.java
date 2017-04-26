package com.project.yura.photoeditor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.project.yura.photoeditor.Model.CurrentSession;
import com.project.yura.photoeditor.Model.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditImageActivity extends AppCompatActivity {
    public static String IMAGE_TO_EDIT_URI = "image_uri";
    @BindView(R.id.imageToEdit) ImageView imageView;
    @BindView(R.id.preview_button) ImageView previewButton;

    Bitmap originalBitmap;
    //Bitmap currentBitmap = null;
    CurrentSession currentSession;
    boolean displayOriginal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        ButterKnife.bind(this);

        currentSession = CurrentSession.GetInstance();

        String pathString = getIntent().getExtras().getString(IMAGE_TO_EDIT_URI);
        currentSession.path = pathString;
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
                        case ExifInterface.ORIENTATION_ROTATE_90 :
                            m.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180 :
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
        }

        //previewButton = (ImageView) findViewById(R.id.preview_button);
        //imageView = (ImageView) findViewById(R.id.imageToEdit);
        imageView.setImageBitmap(currentSession.currentBitmap);


//        vScroll = (ScrollView) findViewById(R.id.vScroll);
//        hScroll = (HorizontalScrollView) findViewById(R.id.hScroll);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        ImageView imageView = (ImageView) findViewById(R.id.imageToEdit);
//
//        imageView.setScaleType(ImageView.ScaleType.MATRIX);
//        Matrix m = new Matrix();
//        int oldWidth = currentSession.currentBitmap.getWidth();
//        int oldHeight = currentSession.currentBitmap.getHeight();
//        /*LinearLayout shit = (LinearLayout)findViewById(R.id.shit);
//        shit.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        shit.layout(0, 0,
//                shit.getMeasuredWidth(),
//                shit.getMeasuredHeight());*/
////        int newWidth = imageView.getWidth();
////        int newHeight = imageView.getHeight();
//
//        int newWidth = findViewById(R.id.vScroll).getWidth();
//        int newHeight = findViewById(R.id.vScroll).getHeight();
////        int newWidth3 = findViewById(R.id.hScroll).getWidth();
////        int newHeight3 = findViewById(R.id.hScroll).getHeight();
//        float bitmapRatio = (float) oldWidth / (float) oldHeight;
//        float newBitmapRatio = (float) newWidth / (float) newHeight;
//        float scale;
//
//        if (bitmapRatio > newBitmapRatio) {
//            scale = (float) newWidth / (float) oldWidth;
//        } else {
//            scale = (float) newHeight / (float) oldHeight;
//        }
//        m.postScale(scale, scale, oldWidth / 2.0f, oldHeight / 2.0f);
//
////        imageView.setMaxWidth(newWidth);
////        imageView.setMaxHeight(newHeight);
//        imageView.setImageMatrix(m);
//    }

    float mx, my;
   // ScrollView vScroll;
   // HorizontalScrollView hScroll;

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

    @OnClick(R.id.adjust_select)
    public void adjustSelect(View view) {
        startActivity(new Intent(this, ApplyAdjustActivity.class));
    }

    @OnClick(R.id.frame_select)
    public void frameSelect(View view) {
        startActivity(new Intent(this, ApplyFrameActivity.class));
    }

    @OnClick(R.id.filter_select)
    public void filterSelect(View view) {
        startActivity(new Intent(this, ApplyFilterActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //imageView = (ImageView) findViewById(R.id.imageToEdit);
        if (!displayOriginal){
            imageView.setImageBitmap(currentSession.currentBitmap);
        } else {
            previewClick(null);
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
    }

    @OnClick(R.id.preview_button)
    public void previewClick(View view) {
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
