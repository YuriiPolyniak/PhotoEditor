package com.project.yura.photoeditor.manager;

import android.graphics.Bitmap;

public class CurrentSession {
    private static CurrentSession _instance = null;

    public Bitmap currentBitmap = null;
    public String path = null;
    public String realPath = null;

    private CurrentSession(){ }

    public static CurrentSession GetInstance() {
        if (_instance == null) {
            _instance = new CurrentSession();
        }
        return _instance;
    }
}
