package com.example.zz.superresolution.ui.upload;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.Toast;

import com.example.zz.superresolution.R;
import com.example.zz.superresolution.ResultActivity;
import com.example.zz.superresolution.VideoResultActivity;
import com.example.zz.superresolution.databinding.FragmentUploadBinding;
import com.mob.commons.filesys.FileUploader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class UploadFragment extends Fragment {

    public static final int TAKE_PHOTO = 1;
    public static final int SELECT_PHOTO = 2;
    private UploadViewModel uploadViewModel;
    private FragmentUploadBinding binding;
    private Uri imageUri;
    private View root;
    String fileType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        uploadViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(UploadViewModel.class);
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageButton takePhoto = root.findViewById(R.id.take_photo);
        ImageButton selectPhoto = root.findViewById(R.id.select_photo);

        UploadFragment.this.requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1111);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                File outputImage = new File(getActivity().getExternalCacheDir(), "output_photo"+rand.nextInt(10000)+".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(getActivity(),
                            "com.example.zz.superresolution.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                Log.d("imageUri", imageUri.toString());
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clicked", "selectPhoto button clicked");
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    UploadFragment.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    //要在AndroidManifest.xml中添加该权限，否则不会弹出权限请求提示框
                } else {
                    //openImage();
                    new AlertDialog.Builder(getActivity()).setTitle("Select")
                            .setMessage("Image or Video?")
                            .setPositiveButton("Video",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            fileType = "video";
                                            openVideo();
                                        }
                                    })
                            .setNegativeButton("Image", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    fileType = "image";
                                    openImage();
                                }
                            })
                            .show();

                }
            }
        });

    }

    private void openImage(){
        //Intent intent = new Intent("android.intent.action.GET_CONTENT");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("image/* video/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    private void openVideo(){
        //Intent intent = new Intent("android.intent.action.GET_CONTENT");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("image/* video/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openImage();
                }else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == FragmentActivity.RESULT_OK){
                    //try{
                        //Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        //bitmap = translate(bitmap);
                        //photo.setImageBitmap(bitmap);
                    toResultPage(getPathFromUri(imageUri));
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
                }
                break;
            case SELECT_PHOTO:
                //String imagePath = imageUri.getPath();
                //Uri uri = data.getData();
                if(Build.VERSION.SDK_INT >= 19){
                    handleImageOnKitKat(data);
                }else {
                    handleImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private String getPathFromUri(Uri uri){
        String imagePath = null;
        //Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getActivity(), uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = null;
        //Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getActivity(), uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path;
        String authroity = uri.getAuthority();
        path = uri.getPath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if(!path.startsWith(sdPath)) {
            int sepIndex = path.indexOf(File.separator, 1);
            if(sepIndex == -1) path = null;
            else {
                path = sdPath + path.substring(sepIndex);
            }
        }

        if(path == null || !new File(path).exists()) {
            ContentResolver resolver = getActivity().getContentResolver();
            String[] projection = new String[]{ MediaStore.MediaColumns.DATA };
            Cursor cursor = resolver.query(uri, projection, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    try {
                        int index = cursor.getColumnIndexOrThrow(projection[0]);
                        if (index != -1) path = cursor.getString(index);
                        Log.i("imagePath", "getMediaPathFromUri query " + path);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        path = null;
                    } finally {
                        cursor.close();
                    }
                }
            }
        }
        return path;

//        String path = null;
//        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
//        if (cursor != null){
//            if (cursor.moveToFirst()){
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            bitmap = translate(bitmap);
//            photo.setImageBitmap(bitmap);
            toResultPage(imagePath);
        }else {
            Toast.makeText(getActivity(), "Failed to get the image", Toast.LENGTH_SHORT).show();
        }
    }

    private void toResultPage(String imagePath){
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //压缩图像，quality值越小压缩率越大，100表示不压缩
//        image.compress(Bitmap.CompressFormat.PNG,100,bytes);
//        byte[] byteArray = bytes.toByteArray();
        if(fileType.equals("video")){
            Intent intent = new Intent(getActivity(), VideoResultActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        }
        else if(fileType.equals("image")){
            Intent intent = new Intent(getActivity(), ResultActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        }
        //Intent intent = new Intent(getActivity(), ResultActivity.class);
        //intent.putExtra("processed_imagebytes", imageUri);
        //intent.setData(imageUri);
//        intent.putExtra("imagePath", imagePath);
//        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}