package com.example.zz.superresolution;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Timer timer = new Timer();
        timer.schedule(new Task(),3000);
    }

    private class Task extends TimerTask {

        @Override
        public void run() {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
    }
}
