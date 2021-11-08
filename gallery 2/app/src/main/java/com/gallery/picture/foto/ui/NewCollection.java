package com.gallery.picture.foto.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gallery.picture.foto.R;

import static androidx.recyclerview.widget.RecyclerView.LayoutManager;
import static androidx.recyclerview.widget.RecyclerView.OnClickListener;

public class NewCollection extends AppCompatActivity {
    androidx.recyclerview.widget.RecyclerView RecyclerView;
    Toolbar toolbar1;
    ImageView more_vert;
    ImageView backButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_collection);
        RecyclerView = findViewById(R.id.RecyclerView);
        toolbar1 = findViewById(R.id.toolbar1);
        more_vert = findViewById(R.id.more_vert);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LayoutManager layoutManager = new GridLayoutManager(this, 4);
        RecyclerView.setLayoutManager(layoutManager);

        //NestedRecyclerViewAdapter nestedRecyclerViewAdapter = new NestedRecyclerViewAdapter(5, this, new ArrayList<ImageDetail>());
        //RecyclerView.setAdapter(nestedRecyclerViewAdapter);

        getSupportActionBar().hide();

        /*more_vert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(NewCollection.this,more_vert);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.collection_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(NewCollection.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();
            }
        });*/

    }
}