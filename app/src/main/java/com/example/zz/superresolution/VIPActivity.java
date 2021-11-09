package com.example.zz.superresolution;

import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VIPActivity extends AppCompatActivity {

    String base_url = "http://101.35.24.184:9008/vip/open";
    final String PREFS_NAME = "userinfo";
    final String[] months = new String[]{"1","3","6","12"};
    int index;
    Button vip_btn;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipactivity);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }

        vip_btn = (Button) findViewById(R.id.vip_btn);
        vip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] month_items = new String[]{"1个月  ¥10", "3个月  ¥28", "6个月  ¥58", "1年     ¥98"};//创建item
                AlertDialog alertDialog = new AlertDialog.Builder(VIPActivity.this)
                        .setTitle("开通会员")
//                        .setMessage("确认开通一年会员吗？")
                        .setIcon(R.drawable.vip)
                        .setSingleChoiceItems(month_items, 4, new DialogInterface.OnClickListener() {//添加单选框
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                index = i;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                pay();
                                submit_vip();
//                                    vip_btn.setText(R.string.renew_vip);
//                                Toast.makeText(getApplicationContext(), "这是确定按钮", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "取消开通，您将无法享受会员尊享特权。", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    private boolean pay(){
        return true;
    }

    private boolean submit_vip(){
        String url = base_url+"?month="+months[index];
        Log.d("vip",url);
        flag = false;

        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("month", months[index]);
        Call call;
//        if(vip_btn.getText().equals(R.string.renew_vip)) Log.d("vip","R.string.renew_vip");
        Request request1 = new Request.Builder() //开通会员
                .url(url)
                .header("Cookie", "token="+token)
                .post(formBody.build())
                .build();

        call = client.newCall(request1);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("vip", "onFailure: ");
                e.printStackTrace();
                showResult("开通会员失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("vip", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("vip", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    showResult("开通成功");
                    flag = true;
                }
                else{
                    Log.d("vip", "vip failed");
                    showResult("开通会员失败");
                }
            }
        });
        return flag;
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VIPActivity.this,msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }
}