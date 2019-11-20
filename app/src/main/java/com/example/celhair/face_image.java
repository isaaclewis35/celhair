package com.example.celhair;

import android.graphics.Bitmap;

public class face_image {

    private Bitmap mFace;
    private String mFileName;

    public String getName(){return mFileName;}

    public Bitmap getPic(){return mFace;}

    public void setPic(Bitmap newPic){mFace = newPic;}

    public void setName(String fileName){mFileName = fileName;}

}
