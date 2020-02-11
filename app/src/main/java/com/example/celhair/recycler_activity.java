package com.example.celhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.bytedeco.opencv.opencv_features2d.*;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class recycler_activity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext, String newPic,String fileName, String[] fileNames) {

        Intent intent = new Intent(packageContext, recycler_activity.class);
        intent.putExtra("NEW_PIC", newPic);
        intent.putExtra("PIC_NAME", fileName);
        intent.putExtra("FILE_NAMES", fileNames);
        return intent;
    }

    private RecyclerView mHairRecycler;
    private face_image[] mBaseFace;
    private HairAdapter mAdapter;
    private face_image newImage;
    private String[] mFileNames;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hair_recycler);

        mHairRecycler = (RecyclerView) findViewById(R.id.hair_recycler_view);
        mHairRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        newImage = new face_image();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            //rip
        } else {
            photoPath = extras.getString("NEW_PIC");
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            newImage.setPic(bitmap);
            String tempString = extras.getString("PIC_NAME");
            newImage.setName(tempString);
            mFileNames = extras.getStringArray("FILE_NAMES");

        }

        loadFaces(mFileNames);
        //https://stackoverflow.com/questions/21651852/android-load-all-files-from-a-folder


    }


    private void loadFaces(String[] fileNames){


        Bitmap[] faces_list = new Bitmap[fileNames.length];
        int i = 0;
        for(String filename : fileNames){
            faces_list[i] = getBitmapFromAsset(getApplicationContext(),"faces/"+filename);
            i++;
        }

        mBaseFace = new face_image[faces_list.length];
        for(int x = 0; x < faces_list.length; x++){
            mBaseFace[x] = new face_image();
            mBaseFace[x].setName(fileNames[x]);
            mBaseFace[x].setPic(faces_list[x]);
        }
        //Log.d("FACE","here");
        updateFaces();
    }
    // adapted from https://stackoverflow.com/questions/8501309/android-get-bitmap-or-sound-from-assets
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

    private void updateFaces() {

        if (mAdapter == null) {
            mAdapter = new HairAdapter(mBaseFace);
            mHairRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }



    private class HairHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Bitmap image;
        private ImageView mHairImage;
        private face_image mFaceImage;


        public HairHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.hair_element, parent, false));
            itemView.setOnClickListener(this);

            mHairImage = (ImageView) itemView.findViewById(R.id.hair_view);
            mHairImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //takePicture();
                    //dispatchTakePictureIntent();
                    Intent intent = DisplayActivity.newIntent(getApplicationContext(),mFaceImage.getName(),"new_face", mFileNames);
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

        public void bind(face_image newFace) {
            mFaceImage = newFace;

            mHairImage.setImageBitmap(mFaceImage.getPic());
            //Log.d("FACE",mFaceImage.getName());
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class HairAdapter extends RecyclerView.Adapter<HairHolder> {

        private face_image[] mFaces;

        public HairAdapter(face_image[] faces) {
            mFaces = faces;
        }

        @Override
        public HairHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new HairHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(HairHolder holder, int position) {
            face_image face = mFaces[position];
            holder.bind(face);
        }

        @Override
        public int getItemCount() {
            return mFaces.length;
        }
    }
}
