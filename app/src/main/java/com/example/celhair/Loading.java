package com.example.celhair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;

public class Loading extends AppCompatActivity {

    private RecyclerView mHairRecycler;
    private face_image[] mBaseFace;
    private face_image newImage;
    private String[] mFileNames;
    private String currentPhotoPath;

    public static Intent newIntent(Context packageContext, String newPic, String fileName, String[] fileNames) {

        Intent intent = new Intent(packageContext, Loading.class);
        intent.putExtra("NEW_PIC", newPic);
        intent.putExtra("PIC_NAME", fileName);
        intent.putExtra("FILE_NAMES", fileNames);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("FACE", "here");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            //rip
        } else {
            currentPhotoPath = getIntent().getParcelableExtra("NEW_PIC");
            //Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //newImage.setPic(bitmap);
            String tempString = extras.getString("PIC_NAME");
            //newImage.setName(tempString);
            mFileNames = extras.getStringArray("FILE_NAMES");

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = recycler_activity.newIntent(getApplicationContext(),currentPhotoPath,"new_face", mFileNames);
                try {
                    startActivity(intent);
                }
                catch(Exception ex){
                    Log.d("FACE", "yo");
                    Log.d("FACE", "error",ex);
                }
            }
        }, 3000);



    }

}
