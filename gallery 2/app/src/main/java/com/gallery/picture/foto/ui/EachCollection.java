package com.gallery.picture.foto.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.gallery.picture.foto.R;


public class EachCollection extends AppCompatActivity {

    RecyclerView RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_collection);

        RecyclerView = findViewById(R.id.RecyclerView);

        LayoutManager layoutManager = new GridLayoutManager(this, 4);
        RecyclerView.setLayoutManager(layoutManager);



        //NestedRecyclerViewAdapter nestedRecyclerViewAdapter = new NestedRecyclerViewAdapter(5, this, new ArrayList<ImageDetail>());
        //RecyclerView.setAdapter(nestedRecyclerViewAdapter);

    }
}