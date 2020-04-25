package com.example.celhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.celhair.recycler_activity.getBitmapFromAsset;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 23;
    private final int REQUEST_TAKE_PHOTO = 1;
    private Button mPictureButton;
    //private Button mSetButton;
    private Button mNext;
    private Bitmap mNewPicture;

    private int test;

    private String[] fileNames;



    private ImageView mPicView;
    String currentPhotoPath;

    public static Intent newIntent(Context packageContext) {

        Intent intent = new Intent(packageContext, MainActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = 0;

        //queue = Volley.newRequestQueue(this);

        mPicView = (ImageView) findViewById(R.id.pictureView2);
        mPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){
                if(test == 0){
                    //test = 1;
                    //Bitmap kopec = getBitmapFromAsset(getApplicationContext(),"tests/DavidKopec.jpg");
                    //mPicView.setImageBitmap(kopec);
                    try{
                        Picasso.get().load("http://10.0.2.2:5000/static/000001.jpg").into(mPicView);

                    }
                    catch(Exception ex){
                        Log.d("FACE",ex.toString());
                    }


                }
                else{
                        loadTest();
                        Intent intent = recycler_activity.newIntent(getApplicationContext(), currentPhotoPath, "new_face", fileNames);
                        try {
                            startActivity(intent);
                        }
                        catch(Exception ex){
                            Log.d("FACE", "yo");
                            Log.d("FACE", "error",ex);
                        }
                }


            }
        });

        mPictureButton = (Button) findViewById(R.id.pictureButton);
        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePicture();
                dispatchTakePictureIntent();
                //setPic(mPicView);
            }
        });

        mNext = (Button) findViewById(R.id.nextButton);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePicture();
                //dispatchTakePictureIntent();
                loadTest();
                Intent intent = Loading.newIntent(getApplicationContext(),currentPhotoPath,"new_face", fileNames);
                try {
                    startActivity(intent);
                }
                catch(Exception ex){
                    Log.d("FACE", "yo");
                    Log.d("FACE", "error",ex);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mPicView.setImageBitmap(imageBitmap);
            //setPic(mPicView);
            //https://developer.android.com/training/camera/photobasics#java
        }
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mPicView.setBackgroundResource(0);
            setPic(mPicView);

        }
    }

    private void loadTest(){
        try {
            StringBuilder stringB = new StringBuilder();
            InputStream is = getAssets().open("tests/results.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                stringB.append(str);
            }
            br.close();
            Log.d("HAIR", stringB.toString());
            fileNames = stringB.toString().split(",");
            for(int j = 0; j < fileNames.length; j++){
                fileNames[j] = fileNames[j].trim();
                Log.d("HAIR", fileNames[j]);
            }
        }
        catch(Exception Ex){
            Ex.printStackTrace();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("MACHINE", ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic(ImageView imageView) {
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

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        mNewPicture = bitmap;
        int degree = 90;
        try{
            //https://stackoverflow.com/questions/7286714/android-get-orientation-of-a-camera-bitmap-and-rotate-back-90-degrees
            ExifInterface exif = new ExifInterface(currentPhotoPath);
            degree = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            degree = exifToDegrees(degree);
        }
        catch(Exception ex){
            Log.d("FACE",ex.toString());
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedBitmap = Bitmap.createBitmap(mNewPicture, 0, 0, mNewPicture.getWidth(), mNewPicture.getHeight(), matrix, true);

        //bitmap = getBitmapFromAsset(getApplicationContext(),"000001.jpg");
        imageView.setImageBitmap(rotatedBitmap);
    }
    //https://stackoverflow.com/questions/7286714/android-get-orientation-of-a-camera-bitmap-and-rotate-back-90-degrees
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }



}
