package com.example.zz.superresolution;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.zz.superresolution.databinding.ActivityNavigationBinding;

import java.io.FileNotFoundException;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int SELECT_PHOTO = 2;
    private ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_upload, R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
//        switch (requestCode){
//            case TAKE_PHOTO:
//                Log.d("result", "result_activity");
//                if(resultCode == RESULT_OK){
//                    super.onActivityResult(requestCode,resultCode,data);
//                }
//                break;
//            case SELECT_PHOTO:
//                super.onActivityResult(requestCode,resultCode,data);
//                break;
//            default:
//                break;
//        }

//        switch(resultCode){
//            case 1://该结果码要与Fragment中的一致
//                //根据结果码获取数据
//                super.onActivityResult(requestCode,resultCode,data);
//                break;
//            case 2:
//                super.onActivityResult(requestCode,resultCode,data);
//                break;
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        List<Fragment> fragments = getSupportFragmentManager().getFragments();
//        if (fragments == null)
//        {
//            return;
//        }
//        // 查找在Fragment中onRequestPermissionsResult方法并调用
//        for (Fragment fragment : fragments)
//        {
//            if (fragment != null)
//            {
//                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
//                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            }
//        }
//    }

}