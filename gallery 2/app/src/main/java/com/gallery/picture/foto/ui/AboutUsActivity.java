package com.gallery.picture.foto.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.gallery.picture.foto.R;

public class AboutUsActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatTextView txtVersion;
    ImageView ic_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ic_back = findViewById(R.id.ic_back);


        toolbar = findViewById(R.id.toolbar);
        txtVersion = findViewById(R.id.txt_version);
        intView();

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void intView() {



        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersion.setText("(" + version + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


}
