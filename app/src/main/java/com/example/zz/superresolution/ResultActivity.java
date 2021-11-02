package com.example.zz.superresolution;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.smssdk.SMSSDK;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView photo = (ImageView) findViewById(R.id.photo);
        Intent intent = getIntent();
        Uri imgUri = intent.getData();
        //byte[] byteArray = intent.getByteArrayExtra("processed_imagebytes");
        //Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        //photo.setImageBitmap(image);
        Glide.with(this).load(imgUri).into(photo);
        //Glide.with(hContext).load(card.getOld_image_url()).into(holder.oldView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}