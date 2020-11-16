package com.example.musicplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.musicplayer.R;

import java.io.File;
import java.io.FileNotFoundException;

class PicturePicker implements View.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    public static final int REQUEST_CODE_PERMISSION_STORAGE=8000;
    public static final int REQUEST_CODE_PERMISSION_CAMERA=8001;
    public static final int REQUEST_CODE_TAKE_PICTURE=8002;
    public static final int REQUEST_CODE_PICK_PICTURE=8003;
    private final Activity activity;
    private Dialog mCameraDialog;
    private final OnPicturePickedListener picturePickedListener;
    private String outputImageTempFilePath,tempOutputImageTempFilePath;
    public PicturePicker(Activity activity,OnPicturePickedListener listener){
        this.activity=activity;
        this.picturePickedListener=listener;
    }
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults){

        if (requestCode==REQUEST_CODE_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickPicture();
            }
            return;
        }
    }
    public void onActivityResultOk(int requestCode,  int resultCode,@Nullable Intent data) {
        if (requestCode==REQUEST_CODE_TAKE_PICTURE){
            if (resultCode==Activity.RESULT_OK) {
                if (outputImageTempFilePath != null) {
                    File outputImage = new File(outputImageTempFilePath);
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    outputImageTempFilePath=null;
                }
                if (tempOutputImageTempFilePath != null) {
                    this.outputImageTempFilePath = tempOutputImageTempFilePath;
                    if (picturePickedListener != null) {
                        picturePickedListener.onPicturePicked(outputImageTempFilePath);
                    }
                    tempOutputImageTempFilePath=null;
                }
            }else{
                if (tempOutputImageTempFilePath != null) {
                    File outputImage = new File(tempOutputImageTempFilePath);
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tempOutputImageTempFilePath=null;
                }
            }
            /*try {
                // 将拍摄的照片显示出来
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(null));
                //imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }else if (requestCode==REQUEST_CODE_PICK_PICTURE) {
            if (resultCode==Activity.RESULT_OK) {
                Uri uri = data.getData();
                handleImage(uri);
            }
        }
    }
    public void showPickDialog(){
        try {
            if (mCameraDialog != null) {
                mCameraDialog.dismiss();
                mCameraDialog = null;
            }
            mCameraDialog = new Dialog(activity, R.style.BottomDialog);
            View mainView = View.inflate(activity, R.layout.dialog_bottom, null);
            //初始化视图
            mainView.findViewById(R.id.take_button).setOnClickListener(this);
            mainView.findViewById(R.id.pick_button).setOnClickListener(this);
            mainView.findViewById(R.id.cancel_button).setOnClickListener(this);
            mCameraDialog.setContentView(mainView);
            mCameraDialog.setOnCancelListener(this);
            mCameraDialog.setOnDismissListener(this);
            Window dialogWindow = mCameraDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = (int) activity.getResources().getDisplayMetrics().widthPixels; // 宽度
            mainView.measure(0, 0);
            lp.height = mainView.getMeasuredHeight();

            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            mCameraDialog.show();
        }catch(Throwable tr){
            tr.printStackTrace();
        }
    }
    public void startTakePicture(){
        takePicture();
        /*if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_STORAGE);
        } else {
            takePicture();
        }*/
    }
    public void startPickPicture(){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_STORAGE);
        } else {
            pickPicture();
        }
    }
    private void takePicture(){
        File outputImage = new File(activity.getExternalCacheDir(), "tmp_image_" + System.currentTimeMillis() + ".jpg");
        tempOutputImageTempFilePath=outputImage.getAbsolutePath();
        Uri outputImageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outputImageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", outputImage);
        } else {
            //小于android 版本7.0（24）的场合
            outputImageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);

    }
    private void pickPicture(){
        //打开相册
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE); // 打开相册
    }
    private void handleImage(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        //displayImage(imagePath);
        if (picturePickedListener!=null){
            picturePickedListener.onPicturePicked(imagePath);
        }
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = activity.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //imageView.setImageBitmap(bitmap);
        } else {
           //Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id==R.id.cancel_button||id==R.id.pick_button||id==R.id.take_button){
            if (mCameraDialog!=null){
                mCameraDialog.dismiss();
                mCameraDialog=null;
            }
            if (id==R.id.pick_button){
                startPickPicture();
            }else if (id==R.id.take_button) {
                startTakePicture();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mCameraDialog=null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mCameraDialog=null;
    }
    public interface OnPicturePickedListener{
        void onPicturePicked(String imagePath);
    }
}
