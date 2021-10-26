package com.example.zz.superresolution;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context hContext;

    private List<History_card> hCardList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardDate;
        ImageView oldView, newView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            cardDate = (TextView) view.findViewById(R.id.card_date);
            oldView = (ImageView) view.findViewById(R.id.old_imageview);
            newView = (ImageView) view.findViewById(R.id.new_imageview);
        }
    }

    public CardAdapter(List<History_card> cardList){
        hCardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(hContext == null){
            hContext = parent.getContext();
        }
        View view = LayoutInflater.from(hContext).inflate(R.layout.history_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History_card card = hCardList.get(position);
        holder.cardDate.setText(card.getDate());
        Glide.with(hContext).load(card.getOld_image_url()).into(holder.oldView);
        Glide.with(hContext).load(card.getNew_image_url()).into(holder.newView);
//        holder.oldView.setImageURI(card.getOld_image_url());
//        holder.newView.setImageURI(card.getNew_image_url());
    }

    @Override
    public int getItemCount() {
        return hCardList.size();
    }
}
