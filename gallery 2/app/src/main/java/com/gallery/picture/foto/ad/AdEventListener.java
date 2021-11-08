package com.gallery.picture.foto.ad;

public interface AdEventListener {
    void onAdLoaded(Object object);

    void onAdClosed();

    void onLoadError(String errorCode);
}
