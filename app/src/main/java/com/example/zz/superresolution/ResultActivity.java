package com.example.zz.superresolution;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    private String url = "http://101.35.24.184:9008/upload/image/single";
    private byte[] SR_img=null;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        photo = (ImageView) findViewById(R.id.photo);
        Intent intent = getIntent();
        Uri imgUri = intent.getData();
        //byte[] byteArray = intent.getByteArrayExtra("processed_imagebytes");
        //Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        //photo.setImageBitmap(image);
        //Glide.with(this).load(imgUri).into(photo);
        //Glide.with(hContext).load(card.getOld_image_url()).into(holder.oldView);

        Bitmap bitmap=null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
        byte[] imgBytes = bytes.toByteArray();

        //Glide.with(this).load(imgUri).into(photo);

        UploadAndShowImage(this, imgBytes);

        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //photo.setDrawingCacheEnabled(true);
                        //Bitmap imageBitmap = photo.getDrawingCache();
                        //try {
                        //Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(SR_img, 0, SR_img.length);
                        if (imageBitmap != null) {
                            saveImageToGallery(ResultActivity.this, imageBitmap);
                        }
                        //photo.setDrawingCacheEnabled(false);
                        imageBitmap.recycle();
                        System.gc();
                        //}catch (FileNotFoundException e){
                        //    e.printStackTrace();
                        //}
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    public Uri saveImageToGallery(Context context, Bitmap bmp) {
        // 创建文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "SR");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //图片文件名称
        String fileName = "SR_"+System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        //Log.d("222", "fileName:" + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("111",e.getMessage());
            e.printStackTrace();
        }

        // 把文件插入到系统图库
        String path = file.getAbsolutePath();
        //Log.d("222", "path:" + path);
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
            Toast.makeText(context,"successfully saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e("save",e.getMessage());
            e.printStackTrace();
            Toast.makeText(context,"failed to save", Toast.LENGTH_SHORT).show();
        }
        // 最后通知图库更新
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        context.sendBroadcast(intent);
        return uri;
    }

    private void UploadAndShowImage(Context context, byte[] imgBytes){
        OkHttpClient client = new OkHttpClient.Builder()
//                .retryOnConnectionFailure(true)
//                .connectTimeout(60L, TimeUnit.SECONDS) //连接超时
//                .readTimeout(60L, TimeUnit.SECONDS) //读取超时
//                .writeTimeout(60L, TimeUnit.SECONDS) //写超时
                .build();
        MediaType mediaType = MediaType.parse("image/jpeg");
        RequestBody requestBody = RequestBody.create(mediaType,imgBytes);

        Request request = new Request.Builder()
                //.addHeader("Connection", "close")
                //.addHeader("Content-Type", "multipart/form-data;boundary=,")
                .url(url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("upload", "onFailure: ");
                e.printStackTrace();
                //Toast.makeText(ResultActivity.this,"超分辨率转换失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("upload", "onResponse: ");
                int code = response.code();
                if(code == HttpURLConnection.HTTP_OK){
                    byte[] resposeBody = response.body().bytes();
                    SR_img = resposeBody;
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(SR_img, 0, SR_img.length);
                    Uri imageUri = saveImageToGallery(ResultActivity.this, imageBitmap);

                    //photo.setDrawingCacheEnabled(false);
                    //imageBitmap.recycle();
                    //System.gc();
                    Glide.with(context).load(imageUri).into(photo);
                }
                else{
                    Toast.makeText(ResultActivity.this,"超分辨率转换失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}