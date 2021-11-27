package com.example.zz.superresolution;

import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

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

public class HistoryActivity extends AppCompatActivity {

    String base_url = "http://101.35.24.184:9008/history/modify/desc";
    final String PREFS_NAME = "userinfo";

//    private ArrayList<History_card> cards = new ArrayList<>();
//    private History_card[] cards;
//    protected  ArrayList<History_card> cards = new ArrayList<>();
//    private History_card[] def_cards = {new History_card("February 14", Uri.parse("https://alifei03.cfp.cn/creative/vcg/800/new/VCG41N1210205351.jpg"),Uri.parse("https://alifei03.cfp.cn/creative/vcg/800/new/VCG41N1210205351.jpg")),
//            new History_card("April 19", Uri.parse("https://t7.baidu.com/it/u=2291349828,4144427007&fm=193&f=GIF"),Uri.parse("https://t7.baidu.com/it/u=2291349828,4144427007&fm=193&f=GIF")),
//            new History_card("November 6", Uri.parse("https://t7.baidu.com/it/u=839828294,1619278046&fm=193&f=GIF"),Uri.parse("https://t7.baidu.com/it/u=839828294,1619278046&fm=193&f=GIF")),
//            new History_card("December 25", Uri.parse("https://t7.baidu.com/it/u=805456074,3405546217&fm=193&f=GIF"),Uri.parse("https://t7.baidu.com/it/u=805456074,3405546217&fm=193&f=GIF"))
//    };

    private List<History_card> cardList = new ArrayList<>();

    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }
        Glide.get(this).clearMemory();//clear memory
        cardList.clear();
        try {
            getHist();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        initCards();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.history_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardAdapter(cardList);
        recyclerView.setAdapter(adapter);

    }

//    private void initCards(){
//        cardList.clear();
//        for(int i = 0; i < cards.length; i++){
//            cardList.add(cards[i]);
//        }
//    }

    private void getHist() throws InterruptedException {
        long page = 1;
        int pageSize = 10;
        String url = base_url+"?page="+page+"&pageSize="+pageSize;

        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("page", String.valueOf(page));
        formBody.add("pageSize", String.valueOf(pageSize));
//        formBody.add("tag", tag);

        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", "token="+token)
                .get()
//                .post(formBody.build())
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("history", "onFailure: ");
                e.printStackTrace();
                showResult("获取历史记录失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("history", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("history", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
//buffer reader bug
//                    StringBuilder builder = new StringBuilder();
//                    BufferedReader bufferedReader = new BufferedReader(
//                            new InputStreamReader(response.body().byteStream()));
//                    String str2 = "";
//                    for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
//                            .readLine()) {
//                        builder.append(s);
//                    }
//                    Log.i("history", ">>>>>>" + builder.toString());

//                    String resposeBody = response.body().string();
                    try{
//                        JSONObject jsonObject=new JSONObject(builder.toString()).getJSONObject("data");
//                        JSONObject jsonObject=new JSONObject(responseStr).getJSONObject("data");
                        JSONArray jsonArray=new JSONObject(responseStr).getJSONArray("data");
//                        JSONArray jsonArray = jsonObject.names();//.getJSONArray("data");
                        Log.d("history", jsonArray.toString());
                        int maxhis = jsonArray.length();
                        if(maxhis>pageSize) maxhis=pageSize;
                        for(int i=0;i<maxhis;i++){
                            JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                            String date = jsonObject.getString("gmtModify").substring(0,11);
                            Log.d("history", date);
                            Uri old_uri = Uri.parse("http://101.35.24.184"+jsonObject.getString("result"));
                            Log.d("history", old_uri.toString());
                            Uri new_uri = Uri.parse("http://101.35.24.184"+jsonObject.getString("rawMaterial"));
                            Log.d("history", new_uri.toString());
                            History_card card = new History_card(date,old_uri,new_uri);
                            cardList.add(card);
//                            cards[i]=card;
//                            Log.d("history",card.toString());
                        }
//                        Thread.sleep(10000);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("history", "error");
                    }

//                    showResult("获取历史记录成功");
                }
                else{
                    Log.d("history", "history failed");
                    showResult("获取历史记录失败");
                }
            }
        });

        Thread.sleep(1000);
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HistoryActivity.this,msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        cardList.clear();
//        finish();
//    }
}