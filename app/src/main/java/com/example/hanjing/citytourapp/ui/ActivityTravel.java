package com.example.hanjing.citytourapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.example.hanjing.citytourapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by hanjing on 2017/3/18.
 */

public class ActivityTravel extends Activity implements View.OnClickListener {

    private ImageView weather_btn, food_btn,
            photo_btn, global_btn, chat_btn, analyze_btn, take_picture;

    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_layout);

        weather_btn = (ImageView)findViewById(R.id.weather_btn);
        food_btn = (ImageView)findViewById(R.id.food_btn);
        photo_btn = (ImageView)findViewById(R.id.photo_btn);
        global_btn = (ImageView)findViewById(R.id.global_btn);
        chat_btn = (ImageView)findViewById(R.id.chat_btn);
        analyze_btn = (ImageView)findViewById(R.id.analyze_btn);
        take_picture = (ImageView)findViewById(R.id.take_picture);

        weather_btn.setOnClickListener(this);
        food_btn.setOnClickListener(this);
        photo_btn.setOnClickListener(this);
        global_btn.setOnClickListener(this);
        chat_btn.setOnClickListener(this);
        analyze_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.weather_btn:
                intent = new Intent(ActivityTravel.this,ChooseAreaActivity.class);
                intent.putExtra("from_main_activity", true);
                startActivity(intent);
                break;


            case R.id.food_btn:
                intent = new Intent(ActivityTravel.this,ActivityFood.class);
                startActivity(intent);
                break;

            case R.id.photo_btn:
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(ActivityTravel.this,
                            "com.example.hanjing.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                imageUri = Uri.fromFile(outputImage);

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序

                break;

            case R.id.global_btn:
                break;

            case R.id.chat_btn:
                break;

            //拍照
            case R.id.analyze_btn:
                break;

            default:
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;

            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver().openInputStream(imageUri));
                        take_picture.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } }
                break;
            default:
                break;
        }
    }

}
