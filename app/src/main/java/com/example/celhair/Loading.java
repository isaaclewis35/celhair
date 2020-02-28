package com.example.celhair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.RequestBody;
//import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class Loading extends AppCompatActivity {
    private OkHttpClient mHTTPClient;

    private RequestQueue requestQueue;

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


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mHTTPClient = new OkHttpClient();




        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            //rip
        } else {
            currentPhotoPath = extras.getString("NEW_PIC");
            //Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //newImage.setPic(bitmap);
            String tempString = extras.getString("PIC_NAME");
            //newImage.setName(tempString);
            mFileNames = extras.getStringArray("FILE_NAMES");

        }

        try{
            Log.d("FACE",currentPhotoPath);
        }
        catch(Exception ex){
            Log.d("FACE", ex.toString());
        }


        try{
            LikeDislikeTask ldt = new LikeDislikeTask();
            ldt.execute();
        }
        catch(Exception ex){
            Log.d("FACE", ex.toString());
        }
        //Log.d("FACE", "hereHTTP");


        //
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("FACE", "hereHTTP");
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
        //


    }



    private class LikeDislikeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Log.d("CHIT", "hereHTTP");

                File file = new File(currentPhotoPath);

                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", currentPhotoPath,
                                RequestBody.create(MediaType.parse("image/jpg"),file))
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        //concatenates the like/dislike and message id into the url
                        //.url("http://10.0.2.2:5000/matches")
                        .url("http://ec2-3-18-225-17.us-east-2.compute.amazonaws.com:5000/matches")
                        .post(formBody)
                        .build();


                try (okhttp3.Response response = mHTTPClient.newCall(request).execute()) {
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("FACE", e.toString());
                    return "Error: Could not complete request.";
                }
            }
            catch(Exception ex){
                Log.d("FACE", ex.toString());
                return "Error: Could not complete request.";
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("FACE", result);


        }


    }

    //adapted from https://developer.android.com/training/volley/requestqueue
    private void sendImage(Bitmap imageToSend){

        String sendable = getStringFromBitmap(imageToSend);


        JSONObject hairObject = new JSONObject();

        try {
            hairObject.put("compressed_image", sendable);
        }
        catch(Exception ex){
            Log.d("HAIR", ex.toString());
        }
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();


        String url ="http://www.example.com";

// Formulate the request and handle the response.
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, hairObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray toLoad = response.getJSONArray("names");
                            mFileNames = new String[toLoad.length()];
                            for(int i = 0; i< toLoad.length();i++){
                                mFileNames[i] = toLoad.getString(i);
                            }
                        }
                        catch(Exception Ex){
                            Log.d("HAIR", Ex.toString());
                        }
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

// add it to the RequestQueue
        requestQueue.add(getRequest);


    }

    //from https://stackoverflow.com/questions/30818538/converting-json-object-with-bitmaps
    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    // from https://stackoverflow.com/questions/30818538/converting-json-object-with-bitmaps
    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
