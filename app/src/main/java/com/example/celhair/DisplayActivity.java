package com.example.celhair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;


public class DisplayActivity extends AppCompatActivity {

    private ImageView mPicView;
    private Button mShareButton;
    private Button mRecycle;

    private String file;
    private String currentPhotoPath;
    private String[] mFileNames;



    public static Intent newIntent(Context packageContext, String newPic, String fileName, String[] fileNames) {

        Intent intent = new Intent(packageContext, DisplayActivity.class);
        intent.putExtra("NEW_PIC", newPic);
        intent.putExtra("PIC_NAME", fileName);
        intent.putExtra("FILE_NAMES", fileNames);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);



        mPicView = (ImageView) findViewById(R.id.pictureView2);


        mShareButton = (Button) findViewById(R.id.shareButton);
        /*
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //placeholder
            }
        });
*/
        mRecycle = (Button) findViewById(R.id.nextButton);
        mRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Loading.newIntent(getApplicationContext(),currentPhotoPath,"new_face", mFileNames);
                try {
                    startActivity(intent);
                }
                catch(Exception ex){
                    Log.d("FACE", "yo");
                    Log.d("FACE", "error",ex);
                }

                //placeholder
            }
        });



        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            //rip
        } else {

            currentPhotoPath = extras.getString("NEW_PIC");
            Log.d("FACE",currentPhotoPath);
            //Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //newImage.setPic(bitmap);
            String pic = extras.getString("PIC_NAME");
            //newImage.setName(tempString);
            mFileNames = extras.getStringArray("FILE_NAMES");






        }




    }

    @Override
    protected void onStart(){

        super.onStart();

        Bitmap bitmap = getBitmapFromAsset(getApplicationContext(),"faces/"+currentPhotoPath);
        mPicView.setImageBitmap(bitmap);


        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try{
                    Log.d("FACE","CURRENT PATH 1: " + currentPhotoPath);
                    setPic(mPicView);
                    Log.d("FACE","CURRENT PATH 2: " + currentPhotoPath);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, 3000);
        */

    }

    private void setPic(ImageView imageView) {
        try{
            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();
            Log.d("FACE", Integer.toString(targetW));
            Log.d("FACE", Integer.toString(targetH));

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Log.d("FACE", "CURRENT PATH: " + currentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile("faces/"+currentPhotoPath, bmOptions);
            //mNewPicture = bitmap;

            //bitmap = getBitmapFromAsset(getApplicationContext(),"000001.jpg");
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception ex){
            Log.d("FACE",ex.toString());
            ex.printStackTrace();
        }

    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            //Log.d("FACE", "oh god");
        }

        return bitmap;
    }


}
