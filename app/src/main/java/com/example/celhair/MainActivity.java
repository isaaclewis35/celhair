package com.example.celhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
                    test = 1;
                    Bitmap kopec = getBitmapFromAsset(getApplicationContext(),"tests/DavidKopec.jpg");
                    mPicView.setImageBitmap(kopec);
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

        /*
        mSetButton = (Button) findViewById(R.id.setButton);
        mSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePicture();
                //dispatchTakePictureIntent();
                setPic(mPicView);
            }
        });
        */
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

    /*
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    */
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

        //bitmap = getBitmapFromAsset(getApplicationContext(),"000001.jpg");
        imageView.setImageBitmap(bitmap);
    }




}
