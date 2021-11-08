package com.gallery.picture.foto;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.gallery.picture.foto.ad.AppOpenManager;
import com.gallery.picture.foto.utils.NotificationUtils;
import com.onesignal.OneSignal;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class GalleryApp extends Application {

    String ONESIGNAL_APP_ID = "52477e20-42ee-4c81-8d6b-da8a5f2e3e08";
    AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appOpenManager = new AppOpenManager(this);

        new Thread(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationUtils notiUtils = new NotificationUtils(GalleryApp.this);
            }
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
            OneSignal.initWithContext(GalleryApp.this);
            OneSignal.setAppId(ONESIGNAL_APP_ID);

            YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(getResources().getString(R.string.app_metrica)).build();
            YandexMetrica.activate(getApplicationContext(), config);
            YandexMetrica.enableActivityAutoTracking(GalleryApp.this);
        });
    }

}