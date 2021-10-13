package com.example.zz.superresolution;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView photo = (ImageView) findViewById(R.id.photo);
        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("processed_imagebytes");
        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        photo.setImageBitmap(image);
    }
}