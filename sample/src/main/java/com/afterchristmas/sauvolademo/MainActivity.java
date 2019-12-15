package com.afterchristmas.sauvolademo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;


public class MainActivity extends AppCompatActivity {

    private ImageView iv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_show = findViewById(R.id.iv_show);
        Bitmap originImg = BitmapFactory.decodeResource(getResources(), R.drawable.test_take_photo);
        Pix pix = ReadFile.readBitmap(originImg);
        pix = Convert.convertTo8(pix);
        Pix resultPix = Binarize.sauvolaBinarizeTiled(pix, 30, 0.2F, 1, 1);
        Bitmap resultBitmap = WriteFile.writeBitmap(resultPix);
        iv_show.setImageBitmap(resultBitmap);
    }
}
