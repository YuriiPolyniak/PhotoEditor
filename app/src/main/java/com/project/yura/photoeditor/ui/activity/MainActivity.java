package com.project.yura.photoeditor.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.project.yura.photoeditor.utils.Helper;
import com.project.yura.photoeditor.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends PhotoPickerActivity {
//    private final int PICK_IMAGE_REQUEST = 1;
//    private final int CAMERA_IMAGE_REQUEST = 2;

//    private static Uri mCapturedImageURI;

    @OnClick(R.id.camera_button)
    public void cameraClick(View view) {
        getPhoto(true);
//        //camera stuff
//        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        //folder stuff
//        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "PhotoEditorImages");
//        imagesFolder.mkdirs();
//
//        File image = new File(imagesFolder, "QR_" + timeStamp + ".jpg");
//        //Uri uriSavedImage = Uri.fromFile(image);
//        mCapturedImageURI = Uri.fromFile(image);
//
//        //image.
//        //imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
//        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//        startActivityForResult(imageIntent, CAMERA_IMAGE_REQUEST);
    }

    @OnClick(R.id.library_button)
    public void libraryClick(View view) {
        getPhoto(false);

//        Intent intent = new Intent();
//
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//            overridePendingTransition(R.anim.slide_right_to, R.anim.slide_right_from);
    }

    @Override
    protected void openImage(Uri data) {
        startActivity(EditImageActivity.getIntentByUri(this, data));
        overridePendingTransition(R.anim.slide_right_to, R.anim.slide_right_from);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            Intent intent = new Intent(this, EditImageActivity.class);
//            intent.putExtra(EditImageActivity.IMAGE_TO_EDIT_URI, uri.toString());
//            startActivity(intent);
//        }
//        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            Helper.refreshGallery(this, mCapturedImageURI);
//
//            Intent intent = new Intent(this, EditImageActivity.class);
//            intent.putExtra(EditImageActivity.IMAGE_TO_EDIT_URI, mCapturedImageURI.toString());
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_right_to, R.anim.slide_right_from);
//        }
//    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }
}
