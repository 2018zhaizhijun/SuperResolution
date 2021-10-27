package com.example.zz.superresolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VIPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipactivity);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }
    }
}