package com.gallery.picture.foto.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ad.AdmobAdManager;
import com.gallery.picture.foto.service.ImageDataService;
import com.gallery.picture.foto.utils.Constant;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    AdmobAdManager admobAdManager;
    public static boolean isSplashOpen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSplashOpen = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        admobAdManager = AdmobAdManager.getInstance(SplashActivity.this);
        admobAdManager.loadInterstitialAd(SplashActivity.this, getResources().getString(R.string.interstitial_id));
        Constant.isShowFirstTimeADs = false;
        Constant.isShowFirstTimeOptionADs = false;

/** Hiding Title bar of this activity screen */

        setContentView(R.layout.activity_splash);

/** Making this activity, full screen */
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/


        init();
    }


    public void init() {

        boolean isPermission = isPermissionGranted();
        if (isPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(new Intent(SplashActivity.this, ImageDataService.class));
                startService(new Intent(SplashActivity.this, ImageDataService.class));
            } else {

                startService(new Intent(SplashActivity.this, ImageDataService.class));
            }
        }


        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isPermission) {
                    intent = new Intent(SplashActivity.this, MainActivity2.class);
                } else {
                    intent = new Intent(SplashActivity.this, PermissionActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }
}

