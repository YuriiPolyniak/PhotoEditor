package com.project.yura.photoeditor.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageUtils {
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String VK_PACKAGE = "com.vkontakte.android";
    public static final String VIBER_PACKAGE = "com.viber.voip";
    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String TWITTER_PACKAGE = "com.twitter.android";

    public static boolean isAppInstalled(Context context, String packageName) {
        boolean installed = false;

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

}
