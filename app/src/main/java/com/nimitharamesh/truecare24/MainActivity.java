package com.nimitharamesh.truecare24;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public String path = "";
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mImage = (ImageView) findViewById(R.id.image_view);

        Button getImage = (Button) findViewById(R.id.get_image);
        Button saveImage = (Button) findViewById(R.id.save_image);

        SharedPreferences myPref = getSharedPreferences("sharedPref", 0);
        String myPath = myPref.getString("Image Path", "");

        if (myPath.length() > 0) {

            loadImageFromMemory(myPath);
        }

        getImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Picasso.with(getApplicationContext())
                        .load("https://s3.amazonaws.com/poly-screenshots.angel.co/enhanced_screenshots/116382-original.jpg")
                        .resize(250,250)
                        .into(mImage);
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mImage.buildDrawingCache();
                Bitmap bitmap = mImage.getDrawingCache();
                path = saveToInternalMemory(bitmap);


                SharedPreferences sharedPref= getSharedPreferences("sharedPref", 0);
                SharedPreferences.Editor editor= sharedPref.edit();
                editor.putString("Image Path", path).commit();

            }
        });

    }

    private String saveToInternalMemory(Bitmap bitmap){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        // Path to /data/data/yourapp/app_data/imageDir
        File directory = contextWrapper.getDir("imageDirectory", Context.MODE_PRIVATE);

        // Create imageDirectory
        File mypath = new File(directory,"care.jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String temp = directory.getAbsolutePath();
        return directory.getAbsolutePath();
    }

    private void loadImageFromMemory(String path) {

        try {
            File file = new File(path, "care.jpg");
            Bitmap bmap = BitmapFactory.decodeStream(new FileInputStream(file));
            mImage.setImageBitmap(bmap);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
