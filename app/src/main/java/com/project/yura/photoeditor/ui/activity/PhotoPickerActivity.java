package com.project.yura.photoeditor.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.utils.ImageHelper;

import java.io.File;
import java.io.IOException;

public abstract class PhotoPickerActivity extends BaseActivity {
    public static final int RESULT_GET_PHOTO = 10001;
    private static final int TAKE_PHOTO = 0;
    private static final int CHOOSE_PHOTO = 1;

    public static final int RESULT_TAKE_PHOTO = 100;
    public static final int RESULT_CHOOSE_PHOTO = 101;

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final String STATE_PHOTO_PATH = "photoPath";

    protected String currentPhotoPath;
    private boolean isTakePhoto;

    protected abstract void openImage(Uri data);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(STATE_PHOTO_PATH);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PHOTO_PATH, currentPhotoPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (isTakePhoto) {
                        takePhoto();
                    } else {
                        choosePhoto();
                    }
                } else {
                    Toast.makeText(this, "You need to allow access to external storage", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_TAKE_PHOTO:
                    openImage(ImageHelper.uriFromFile(this, new File(currentPhotoPath)));
                    break;
                case RESULT_CHOOSE_PHOTO:
                    openImage(data.getData());
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getPhoto(boolean isTakePhoto) {
        this.isTakePhoto = isTakePhoto;
        int hasStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission == PackageManager.PERMISSION_GRANTED) {
            if (isTakePhoto) {
                takePhoto();
            } else {
                choosePhoto();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public void takePhoto() {
        if (Build.VERSION.SDK_INT >= 24) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = ImageHelper.createImageFile(getApplicationContext());
                    currentPhotoPath = photoFile.getAbsolutePath();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Toast.makeText(PhotoPickerActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            getPackageName() + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
                }
            }
        } else {
            File photoFile = null;
            try {
                photoFile = ImageHelper.createImageFile(this);
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(PhotoPickerActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Intent intent = ImageHelper.getTakePictureIntent(photoFile);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, RESULT_TAKE_PHOTO);
                }
            }
        }
    }

    private void choosePhoto() {
        Intent intent = ImageHelper.getChoosePictureIntent();
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RESULT_CHOOSE_PHOTO);
        }
    }

    public void showPickerDialog() {
        CharSequence items[] = new CharSequence[]{getString(R.string.take_photo), getString(R.string.choose_photo)};
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setItems(items, (dialog, which) -> {
            if (which == TAKE_PHOTO) {
                getPhoto(true);
            } else if (which == CHOOSE_PHOTO) {
                getPhoto(false);
            }
        });
        adb.setNegativeButton(android.R.string.cancel, null);
        adb.show();
    }
}