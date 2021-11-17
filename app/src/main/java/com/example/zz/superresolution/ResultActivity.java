package com.example.zz.superresolution;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.concurrent.Callable;
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
import com.example.zz.superresolution.DownloadUtil;

public class ResultActivity extends AppCompatActivity {

    private String base_url = "http://101.35.24.184";
    private String upload_url = base_url+":9008/upload/";
    private String fileType=null;
    //private byte[] SR_img=null;
    private String SR_img=null;
    private Uri SR_Uri;
    final String PREFS_NAME = "userinfo";
    ImageView photo;
    ProgressBar mProBar;
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        photo = (ImageView) findViewById(R.id.photo);
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        Log.d("imagePath", imagePath);
        File file = new File(imagePath);

        //if(isImageType(imagePath)){
        upload_url = upload_url+"image/single/";
        fileType = "image";
        Log.d("imagePath", "is image type");
//        }
//        else if(isVideoType(imagePath)){
//            upload_url = upload_url+"video/single/";
//            fileType = "video";
//            Log.d("imagePath", "is video type");
//        }
//        else{
//            Toast.makeText(this,"请选择图片或视频", Toast.LENGTH_SHORT).show();
//            finish();
//        }

        //Uri imgUri = intent.getData();
        //byte[] byteArray = intent.getByteArrayExtra("processed_imagebytes");
        //Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        //photo.setImageBitmap(image);
        //Glide.with(this).load(imgUri).into(photo);
        //Glide.with(hContext).load(card.getOld_image_url()).into(holder.oldView);

//        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(imgUri.toString());
//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
//        if (mimeType!=null&&mimeType.contains("video")){
//
//        }
//        else if (mimeType!=null&&mimeType.contains("image")){
//
//        }

//        Bitmap bitmap=null;
//        try {
//            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
//        byte[] imgBytes = bytes.toByteArray();

        //Glide.with(this).load(imgUri).into(photo);

        createProgressBar();
        UploadAndShowImage(this, file, imagePath);

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
//                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(SR_img, 0, SR_img.length);
//                        if (imageBitmap != null) {
//                            saveImageToGallery(ResultActivity.this, imageBitmap);
//                        }
                        url2bitmap(base_url+SR_img);
                        //saveFile(base_url+SR_img);
                        //photo.setDrawingCacheEnabled(false);
                        //imageBitmap.recycle();
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
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(myCaptureFile));
                sendBroadcast(intent);//发送一个广播
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

//        final DownloadUtil downloadUtil = new DownloadUtil(4, subForder, fileName, url, this);
//        downloadUtil.setOnDownloadListener(new DownloadUtil.OnDownloadListener() {
//            @Override
//            public void Start(int fileSize) {
//                Log.i("TAG---fileSize", fileSize + "");
//                max = fileSize;//文件总长度
//                progressBar.setMax(fileSize);
//            }
//
//            @Override
//            public void Progress(int downloadedSize) {
//                Log.i("TAG---downloadedSize", downloadedSize + "");
//                progressBar.setProgress(downloadedSize);
//                tv_progress.setText((int) downloadedSize * 100 / max + "%");
//            }
//
//            @Override
//            public void End() {
//                Log.i("TAG---end", "End");
//                // 下载后将图片or视频保存到相册中
//                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                intent.setData(Uri.fromFile(myCaptureFile ));
//                sendBroadcast(intent);//发送一个广播
//            }
//        });
    }

    public void url2bitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            if (bm != null) {
                saveImageToGallery(ResultActivity.this, bm,url);
            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ResultActivity.this,"保存失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    public void saveImageToGallery(Context context, Bitmap bmp, String url) {
        // 创建文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "SR");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //图片文件名称
        //String fileName = "SR_"+System.currentTimeMillis() + ".jpg";
        String[] str = url.split("/");
        String fileName = str[str.length - 1];
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
            Toast.makeText(context,"图片已保存", Toast.LENGTH_SHORT).show();
            Log.d("save", "SR_image inserted into gallery");
        } catch (FileNotFoundException e) {
            Log.e("save",e.getMessage());
            e.printStackTrace();
            //Toast.makeText(context,"failed to save", Toast.LENGTH_SHORT).show();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        //return uri;
    }

    private void UploadAndShowImage(Context context, File file, String imagePath){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS) //连接超时
                .readTimeout(300, TimeUnit.SECONDS) //读取超时
                .writeTimeout(300, TimeUnit.SECONDS) //写超时
                .build();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody fileBody = RequestBody.create(mediaType,file);
        String[] str = imagePath.split("/");
        String fileName = str[str.length - 1];
//        String fileName = "SR_"+System.currentTimeMillis();
        Log.d("upload", fileType);
//        String ext;
//        if(fileType.equals("video"))
//            ext = ".avi";
//        else
//            ext = ".png";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileType, fileName, fileBody)
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
                    ResultActivity.this.runOnUiThread(new Runnable() {
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
                                Thread.sleep(20000);
                                Glide.with(context).load(base_url+SR_img).into(photo);
//                                if(fileType.equals("image"))
//                                    Glide.with(context).load(base_url+SR_img).into(photo);
//                                else{
//                                    Bitmap thumbnail = createVideoThumbnail(base_url+SR_img, MediaStore.Images.Thumbnails.MINI_KIND);
//                                    Glide.with(context).load(thumbnail).into(photo);
//                                }
                                layout.removeView(mProBar);
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

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath.startsWith("http://")
                    || filePath.startsWith("https://")
                    || filePath.startsWith("widevine://")) {
                retriever.setDataSource(filePath,new Hashtable<String, String>());
            }else {
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
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

    private Boolean isImageType(String path){
        int lastDot = path.lastIndexOf('.');
        if(lastDot < 0)
            return null;
        String ext = path.substring(lastDot+1).toUpperCase(Locale.ROOT);
        //return sFileTypeMap.get(path.substring(lastDot+1).toUpperCase(Locale.ROOT));
        return ext.equals("JPG") || ext.equals("JPEG") || ext.equals("PNG");
    }

    private Boolean isVideoType(String path){
        int lastDot = path.lastIndexOf('.');
        if(lastDot < 0)
            return null;
        String ext = path.substring(lastDot+1).toUpperCase(Locale.ROOT);
        //return sFileTypeMap.get(path.substring(lastDot+1).toUpperCase(Locale.ROOT));
        return ext.equals("MP4") || ext.equals("MPG") || ext.equals("MPEG") || ext.equals("AVI");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}