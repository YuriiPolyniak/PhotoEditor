package com.project.yura.photoeditor;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.yura.photoeditor.Model.Helper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    //old version
    /*
    public void cameraClick(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
    }*/



    private static Uri mCapturedImageURI;

    @OnClick(R.id.camera_button)
    public void cameraClick(View view) {
        String fileName = "temp.jpg";

       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
       // String imageFileName = "JPEG_" + timeStamp + "_.jpg";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

        // mCurrentPhotoPath = image.getAbsolutePath();
        ///////

        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "PhotoEditorImages");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "QR_" + timeStamp + ".jpg");
        //Uri uriSavedImage = Uri.fromFile(image);
        mCapturedImageURI = Uri.fromFile(image);

        //image.
        //imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(imageIntent, CAMERA_IMAGE_REQUEST);

        /////////////
//        //working camera intent
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, fileName);
//        mCapturedImageURI = getContentResolver()
//                .insert(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        values);
//        Intent intent = new Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                mCapturedImageURI);
//        startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
    }


    @OnClick(R.id.library_button)
    public void libraryClick(View view) {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(EditImageActivity.IMAGE_TO_EDIT_URI, uri.toString());
            startActivity(intent);
        }
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            //Bitmap picture = (Bitmap) data.getExtras().get("data");
            //Object picture = data.getExtras().get("data");
            //Bundle extras = data.getExtras();
            //View back = findViewById(R.id.activity_main);
            //back.setBackground(new BitmapDrawable(getResources(), picture));
            //File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);


/*
            //mode = MODE_VIEWER;
            String[] projection = { MediaStore.Images.Media.DATA };
            //Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
            Cursor cursor = getContentResolver().query(mCapturedImageURI, projection, null, null, null);

            int column_index_data = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            //THIS IS WHAT YOU WANT!
            String capturedImageFilePath = cursor.getString(column_index_data);

            Bitmap bitmap = BitmapFactory.decodeFile(capturedImageFilePath);
            View back = findViewById(R.id.activity_main);
            back.setBackground(new BitmapDrawable(getResources(), bitmap));
            */
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

//            //good code
//            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            final Uri contentUri = mCapturedImageURI;
//            scanIntent.setData(contentUri);
//            sendBroadcast(scanIntent);
            Helper.refreshGallery(this, mCapturedImageURI);

            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(EditImageActivity.IMAGE_TO_EDIT_URI, mCapturedImageURI.toString());
            startActivity(intent);
            
            /* old
            Uri uri = data.getData();
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra(EditImageActivity.IMAGE_TO_EDIT_URI, uri.toString());
            startActivity(intent);
            */

//            Intent intent = new Intent(this, EditImageActivity.class);
//            intent.putExtras(data.getExtras());
//            startActivity(intent);
        }
    }
}
