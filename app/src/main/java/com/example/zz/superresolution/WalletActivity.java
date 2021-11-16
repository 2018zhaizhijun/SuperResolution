package com.example.zz.superresolution;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.zz.superresolution.alipay.AlipayOfSandbox;
import com.example.zz.superresolution.alipay.OrderInfoUtil2_0;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalletActivity extends AppCompatActivity {

    String base_url = "http://101.35.24.184:9008/wallet/recharge";
    final String PREFS_NAME = "userinfo";
    private String money = "¥ 0.00 ";
    private String balance;
    private String price = "10";
    TextView money_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        money_tv = (TextView) findViewById(R.id.monet_tv);
        Button money_btn = (Button) findViewById(R.id.money_btn);

        try {
            get_wallet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("wallet",money);
        money_tv.setText(money);

        money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                submit_vip();
                pay();
            }
        });
    }

    private void pay(){
        Intent intent = new Intent(getApplicationContext(), AlipayOfSandbox.class);
        Bundle bundle = new Bundle();
        bundle.putString("totalprice",price);
        intent.putExtras(bundle);
        startActivityForResult(intent,117);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 117){
            if(resultCode == 1){
                Log.d("wallet","pay success yeah");
                submit_wallet();
                Log.d("wallet","in if, ok.");
            }
            else{
                Log.d("wallet","pay not success");
            }
        }

    }

    private void get_wallet() throws InterruptedException {
        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        Request request = new Request.Builder()
                .url("http://101.35.24.184:9008/wallet/info")
                .header("Cookie", "token="+token)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("wallet", "onFailure: ");
                e.printStackTrace();
                showResult("查询钱包余额失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("wallet", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("wallet", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    try{
                        JSONObject jsonObject = new JSONObject(responseStr).getJSONObject("data");
                        Log.d("wallet", jsonObject.toString());
                        balance = jsonObject.getString("balance");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("wallet", "error");
                    }
                    Log.d("wallet","查询钱包余额成功");
                    money = "¥"+ balance +".00 ";
                }
                else if(code == 519){
                    Log.d("wallet","no money");
                    money = "¥ 0.00 ";
                }
                else{
                    Log.d("wallet", "wallet failed");
                    showResult("查询钱包余额失败");
                }
//                if(vip_type.equals("1")) {
//                    vip_flag=true;
//                    Log.d("vip","yes"+String.valueOf(vip_flag));
//                }
            }
        });
        Thread.sleep(500);
//        Log.d("vip","return "+String.valueOf(vip_flag));
    }

    private void submit_wallet(){
        String url = base_url+"?money="+price;
        Log.d("wallet",url);

        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("money", price);
        Call call;
        Request request1 = new Request.Builder()
                .url(url)
                .header("Cookie", "token="+token)
                .patch(formBody.build())
                .build();

        call = client.newCall(request1);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("wallet", "onFailure: ");
                e.printStackTrace();
                showResult("充值失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("wallet", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("wallet", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
//                    Toast.makeText(VIPActivity.this,"开通成功", Toast.LENGTH_LONG).show();
//                    showResult("开通成功");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog alertDialog1 = new AlertDialog.Builder(WalletActivity.this)
                                    .setTitle("充值钱包")
                                    .setMessage("充值成功!")
                                    .setIcon(R.drawable.wallet)
                                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        get_wallet();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    money_tv.setText(money);
                                                }
                                            });
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                        }
                    });
                }
                else{
                    Log.d("wallet", "wallet failed");
                    showResult("充值失败");
                }
            }
        });
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WalletActivity.this,msg, Toast.LENGTH_SHORT).show();
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