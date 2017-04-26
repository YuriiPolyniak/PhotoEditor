package com.project.yura.photoeditor.Model;

import android.graphics.Bitmap;

public class PreviewData {
    private Bitmap bitmap;
    private boolean isLiked;
    private IFilter filter = null;

    public PreviewData(/*String name, */ Bitmap bitmap, IFilter filter, boolean isLiked) {
       // this.name = name;
        this.bitmap = bitmap;
        this.filter = filter;
        this.isLiked = isLiked;
    }

//    public String getName() {
//        return name;
//    }

    public IFilter getFilter() {
        return filter;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public Bitmap getBitmap() {
        return bitmap;
    }

//    public void setBitmap(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean liked) {
        isLiked = liked;
    }

}
