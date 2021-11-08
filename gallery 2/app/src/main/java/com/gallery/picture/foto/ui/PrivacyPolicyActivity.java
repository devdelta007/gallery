package com.gallery.picture.foto.ui;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.gallery.picture.foto.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView webView;
    ImageView ic_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.webview);
        ic_back = findViewById(R.id.ic_back);

        init();
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {

            super.onBackPressed();
    }

    private void init() {

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getResources().getString(R.string.privacy_url));
        webView.requestFocus();
    }
}