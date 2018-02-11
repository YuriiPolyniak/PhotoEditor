package com.project.yura.photoeditor.ui.activity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.utils.Helper;
import com.project.yura.photoeditor.utils.NetworkUtils;
import com.project.yura.photoeditor.utils.PackageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.OnClick;

import static com.project.yura.photoeditor.utils.PackageUtils.FACEBOOK_PACKAGE;
import static com.project.yura.photoeditor.utils.PackageUtils.INSTAGRAM_PACKAGE;
import static com.project.yura.photoeditor.utils.PackageUtils.TWITTER_PACKAGE;
import static com.project.yura.photoeditor.utils.PackageUtils.VIBER_PACKAGE;
import static com.project.yura.photoeditor.utils.PackageUtils.VK_PACKAGE;

public class SaveActivity extends BaseActivity {

    private CurrentSession currentSession;
    private File imageToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentSession = CurrentSession.GetInstance();

        saveResult(currentSession.currentBitmap);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_save;
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
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }

                Helper.refreshGallery(this, Uri.fromFile(imageToSave));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_to, R.anim.slide_left_from);
    }

    @OnClick(R.id.save_back_button)
    public void returnBack() {
        onBackPressed();
    }

    @OnClick(R.id.save_home_button)
    public void goHome() {
        Intent home = new Intent(this, MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(home);
        overridePendingTransition(R.anim.slide_down_to, R.anim.slide_down_from);
    }

    @OnClick(R.id.save_facebook_button)
    public void shareFacebook() {
        String appName = "Facebook";

        if (!PackageUtils.isAppInstalled(this, FACEBOOK_PACKAGE)) {
            showToast("Sharing images through " + appName
                    + " requires install " + appName + " app");
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG).show();
            return;
        }
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(currentSession.currentBitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

    @OnClick(R.id.save_other_button)
    public void shareSomewhere() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    @OnClick(R.id.save_vk_button)
    public void shareVK() {
        sharePhoto("VK", VK_PACKAGE);
    }

    @OnClick(R.id.save_viber_button)
    public void shareViber() {
        sharePhoto("Viber", VIBER_PACKAGE);
    }

    @OnClick(R.id.save_instagram_button)
    public void shareInstagram() {
        sharePhoto("Instagram", INSTAGRAM_PACKAGE);
    }

    @OnClick(R.id.save_twitter_button)
    public void shareTwitter() {
        sharePhoto("Twitter", TWITTER_PACKAGE);
    }

    private void sharePhoto(String appName, String packageName) {
        if (!PackageUtils.isAppInstalled(this, packageName)) {
            showToast("Sharing images through " + appName
                    + " requires install " + appName + " app");
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG).show();
            return;
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageToSave));
        i.setPackage(packageName);
        startActivity(i);
    }
}
