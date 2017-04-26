package com.project.yura.photoeditor;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.project.yura.photoeditor.Model.CurrentSession;
import com.project.yura.photoeditor.Model.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveActivity extends AppCompatActivity {
    CurrentSession currentSession;
    File imageToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        currentSession = CurrentSession.GetInstance();

        saveResult(currentSession.currentBitmap);
    }

    private void saveResult(Bitmap currentBitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        //File mypath = new File(directory,"profile.jpg");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "PhotoEditorImages");
        imagesFolder.mkdirs();
        File subFolder = new File(imagesFolder, "Edited");
        subFolder.mkdirs();
        imageToSave = new File(subFolder, "EDITED_" + timeStamp + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageToSave);
            // Use the compress method on the BitMap object to write image to the OutputStream
            currentSession.currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();

                Helper.refreshGallery(this, Uri.fromFile(imageToSave));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void returnBack(View view) {
        onBackPressed();
    }

    public void goHome(View view) {
        Intent home = new Intent(this, MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(home);
    }

    public void shareFacebook(View view) {

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(currentSession.currentBitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

       /* ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Title")
                .setContentDescription(
                        "\"Body Of Test Post\"")
                .setContentUrl(Uri.parse("http://someurl.com/here"))
                .build();

        shareDialog.show(linkContent);*/
    }

    public void shareSomewhere(View view) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        //final File photoFile = new File(getFilesDir(), "foo.jpg");

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }

    public void shareVK(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));
        i.setPackage("com.vkontakte.android");
        startActivity(i);
    }

    public void shareViber(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));
        i.setPackage("com.viber.voip");
        startActivity(i);
    }

    public void shareInstagram(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));
        i.setPackage("com.instagram.android");
        startActivity(i);
    }

    public void shareTwitter(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));
        i.setPackage("com.twitter.android");
        startActivity(i);
    }
}
