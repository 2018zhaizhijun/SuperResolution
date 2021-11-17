package com.example.zz.superresolution;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoResultActivity extends AppCompatActivity {

    private String base_url = "http://101.35.24.184";
    private String upload_url = base_url+":9008/upload/";
    private String fileType=null;
    //private byte[] SR_img=null;
    private String SR_img=null;
    private Uri SR_Uri;
    final String PREFS_NAME = "userinfo";
    private VideoView video;
    private ProgressBar mProBar;
    private FrameLayout layout;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_video);

        video = (VideoView) findViewById(R.id.video);
        //mediaController = new MediaController(this);
        //video.setMediaController(mediaController);
        //mediaController.setMediaPlayer(video);

        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("imagePath");
        Log.d("videoPath", videoPath);
        File file = new File(videoPath);

        upload_url = upload_url+"video/single/";
        Log.d("imagePath", "is video type");

//        Uri uri = Uri.parse(videoPath);
//        video.setVideoURI(uri);
//        video.start();

        createProgressBar();
        UploadAndShowVideo(this, file, videoPath);

//        UploadAndShowImage(this, imgBytes, new MyCallback(){
//            @Override
//            public void onSuccess(byte[] resposeBody){
//                SR_img = resposeBody;
//                Bitmap imageBitmap = BitmapFactory.decodeByteArray(SR_img, 0, SR_img.length);
//                Uri imageUri = saveImageToGallery(ResultActivity.this, imageBitmap);
//                //Glide.with(ResultActivity.this).load(imageUri).into(photo);
//                SR_Uri = imageUri;
//            }
//        });

//        video.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(VideoResultActivity.this);
//                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //photo.setDrawingCacheEnabled(true);
//                        //Bitmap imageBitmap = photo.getDrawingCache();
//                        //try {
//                        //Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
////                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(SR_img, 0, SR_img.length);
////                        if (imageBitmap != null) {
////                            saveImageToGallery(ResultActivity.this, imageBitmap);
////                        }
//                        //url2bitmap(base_url+SR_img);
//                        saveFile(base_url+SR_img);
//                        //photo.setDrawingCacheEnabled(false);
//                        //imageBitmap.recycle();
//                        System.gc();
//                        //}catch (FileNotFoundException e){
//                        //    e.printStackTrace();
//                        //}
//                    }
//                });
//                builder.show();
//                return true;
//            }
//        });
    }

    public void saveFile(String url){
        int i = url.lastIndexOf("/");
        final String fileName = url.substring(i + 1, url.length());
        final String subForder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";

        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }

        final File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            try {
                myCaptureFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DownloadUtil.get().download(url, subForder, fileName, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                //下载完成进行相关逻辑操作
                Log.d("upload", "download successed");
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(myCaptureFile);
                intent.setData(uri);
                sendBroadcast(intent);//发送一个广播
                Log.d("upload", uri.toString());
//                MediaPlayer mp = new MediaPlayer();
//                mp.setDataSource(VideoResultActivity.this, uri);
                VideoResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(VideoResultActivity.this, "视频已保存", Toast.LENGTH_SHORT).show();
                            Thread.sleep(2000);
                            finish();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
//                        layout.removeView(mProBar);
//                        video.setVideoURI(uri);
////                video.requestFocus();
//                        video.start();
                    }
                });

            }

            @Override
            public void onDownloading(int progress) {
                //下载进行中展示进度
            }

            @Override
            public void onDownloadFailed(Exception e) {
                //下载异常进行相关提示操作

            }
        });

    }

    private void UploadAndShowVideo(Context context, File file, String videoPath){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS) //连接超时
                .readTimeout(300, TimeUnit.SECONDS) //读取超时
                .writeTimeout(300, TimeUnit.SECONDS) //写超时
                .build();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody fileBody = RequestBody.create(mediaType,file);
        String[] str = videoPath.split("/");
        String fileName = str[str.length - 1];
//        String fileName = "SR_"+System.currentTimeMillis();
//        Log.d("upload", fileType);
//        String ext;
//        if(fileType.equals("video"))
//            ext = ".avi";
//        else
//            ext = ".png";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video", fileName, fileBody)
                .addFormDataPart("tag", fileName)
                .build();

        String token = getToken();
        Log.d("upload", upload_url);
        Request request = new Request.Builder()
                .url(upload_url)
                .header("Cookie", "token="+token)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("upload", "onFailure: ");
                e.printStackTrace();
                finish();
                //Toast.makeText(ResultActivity.this,"超分辨率转换失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("upload", "onResponse: ");
                int code = response.code();
                String resposeBody = response.body().string();
                Log.d("upload", resposeBody);
                if(code == HttpURLConnection.HTTP_OK){
                    //byte[] resposeBody = response.body().bytes();
                    //String resposeBody = response.body().string();
                    VideoResultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //SR_img = resposeBody;
//                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(SR_img, 0, SR_img.length);
//                            Uri imageUri = saveImageToGallery(ResultActivity.this, imageBitmap);
                            try{
                                JSONObject jsonObject=new JSONObject(resposeBody);
                                //SR_img=jsonObject.getJSONObject("data").getString("token");
                                SR_img = jsonObject.getJSONArray("data").getString(1);
                                Log.d("upload", base_url+SR_img);
                                Thread.sleep(1000*120);

                                saveFile(base_url+SR_img);
//                                Uri uri = Uri.parse(base_url+SR_img);
//                                video.setVideoURI(uri);
//                                layout.removeView(mProBar);
//                                video.start();

//                                video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                    @Override
//                                    public void onPrepared(MediaPlayer mp) {
//                                        //close the progress dialog when buffering is done
//                                        pd.dismiss();
//                                    }
//                                });

//                                layout.removeView(mProBar);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    //Toast.makeText(ResultActivity.this,"超分辨率转换失败", Toast.LENGTH_SHORT).show();
                    Log.d("upload", "transform failed");
                }
            }
        });
    }

    private void createProgressBar() {
        layout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);
    }

    private String getToken(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}