package com.gallery.picture.foto.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gallery.picture.foto.R;


public class CSingleViewActivity extends AppCompatActivity {

    TextView appViewAll, viewAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_single_view);

        appViewAll = findViewById(R.id.appViewAll);
        //viewAll = findViewById(R.id.viewAll);



    }


}
