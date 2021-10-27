package com.example.zz.superresolution;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private History_card[] cards = {new History_card("February 14", Uri.parse("https://alifei03.cfp.cn/creative/vcg/800/new/VCG41N1210205351.jpg"),Uri.parse("https://alifei03.cfp.cn/creative/vcg/800/new/VCG41N1210205351.jpg")),
            new History_card("April 19", Uri.parse("https://t7.baidu.com/it/u=2291349828,4144427007&fm=193&f=GIF"),Uri.parse("https://t7.baidu.com/it/u=2291349828,4144427007&fm=193&f=GIF")),
            new History_card("November 6", Uri.parse("https://t7.baidu.com/it/u=839828294,1619278046&fm=193&f=GIF"),Uri.parse("https://t7.baidu.com/it/u=839828294,1619278046&fm=193&f=GIF")),
            new History_card("December 25", Uri.parse("https://t7.baidu.com/it/u=805456074,3405546217&fm=193&f=GIF"),Uri.parse("https://t7.baidu.com/it/u=805456074,3405546217&fm=193&f=GIF"))
    };

    private List<History_card> cardList = new ArrayList<>();

    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }

        initCards();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.history_rv);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardAdapter(cardList);
        recyclerView.setAdapter(adapter);
    }

    private void initCards(){
        cardList.clear();
        for(int i = 0; i < cards.length; i++){
            cardList.add(cards[i]);
        }
    }
}