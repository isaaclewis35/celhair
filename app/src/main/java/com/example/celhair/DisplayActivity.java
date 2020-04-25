package com.example.celhair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DisplayActivity extends AppCompatActivity {

    private ImageView mPicView;
    private Button mShareButton;
    private Button mRecycle;

    private String file;
    private String currentPhotoPath;
    private String[] mFileNames;
    private OkHttpClient mHTTPClient;


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

        mHTTPClient = new OkHttpClient();


        mPicView = (ImageView) findViewById(R.id.pictureView2);


        mRecycle = (Button) findViewById(R.id.nextButton);
        mRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "http://ec2-3-18-225-17.us-east-2.compute.amazonaws.com:5000/static/" + currentPhotoPath);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        mShareButton = (Button) findViewById(R.id.pictureButton);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LikeDislikeTask ldt = new LikeDislikeTask();
                    ldt.execute();
                }
                catch(Exception ex){
                    Log.d("FACE", ex.toString());
                }
            }
        });






        Bundle extras = getIntent().getExtras();
        if (extras == null) {
        } else {

            currentPhotoPath = extras.getString("NEW_PIC");
            Log.d("FACE",currentPhotoPath);
            String pic = extras.getString("PIC_NAME");
            mFileNames = extras.getStringArray("FILE_NAMES");

        }

    }

    public String saveBitmap(Bitmap bmp){
        try{
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(dir, currentPhotoPath);
            FileOutputStream fOut = new FileOutputStream(file);
            String filePath = file.getAbsolutePath();
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            return filePath;
        }
        catch(Exception ex){
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(),ex.toString(),duration);
            toast.show();
            Log.d("FACE", ex.toString());
        }
        return("uh oh");
    }

    private class LikeDislikeTask extends AsyncTask<String, Void, byte[]> {
        @Override
        protected byte[] doInBackground(String... params) {
            Request request = new Request.Builder()
                    //concatenates the like/dislike and message id into the url
                    .url("http://ec2-3-18-225-17.us-east-2.compute.amazonaws.com:5000/static/" + currentPhotoPath)
                    .build();


            try (Response response = mHTTPClient.newCall(request).execute()) {
                return response.body().bytes();
            } catch (Exception e) {
                e.printStackTrace();
                int duration = Toast.LENGTH_LONG;
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                if (bytes != null && bytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    String path = saveBitmap(bitmap);
                    Intent intent = Loading.newIntent(getApplicationContext(),path,"new_face", mFileNames);
                    try {
                        startActivity(intent);
                    }
                    catch(Exception ex){
                        Log.d("FACE", "yo");
                        Log.d("FACE", "error",ex);
                    }

                }
            } catch (Exception e) {
                Log.d("FACE", "oh shit");
            }
        }


    }


    @Override
    protected void onStart(){

        super.onStart();

        Picasso.get().load("http://ec2-3-18-225-17.us-east-2.compute.amazonaws.com:5000/static/" + currentPhotoPath).into(mPicView);

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
