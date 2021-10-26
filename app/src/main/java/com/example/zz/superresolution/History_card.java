package com.example.zz.superresolution;

import android.net.Uri;

public class History_card {
    private String date;

    private Uri old_image_url;

    private Uri new_image_url;

    public History_card(String date, Uri old_url, Uri new_url){
        this.date = date;
        this.old_image_url = old_url;
        this.new_image_url = new_url;
    }

    public String getDate(){
        return date;
    }

    public Uri getOld_image_url(){
        return old_image_url;
    }

    public Uri getNew_image_url(){
        return new_image_url;
    }
}
