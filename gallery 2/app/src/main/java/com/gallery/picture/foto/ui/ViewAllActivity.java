package com.gallery.picture.foto.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.picture.foto.Adapters.SimpleRecyclerViewAdapter;
import com.gallery.picture.foto.Fragments.SecondFragment;
import com.gallery.picture.foto.Model.ImageDetail;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.RecyclerItemClickListener;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class ViewAllActivity extends AppCompatActivity {

    RecyclerView ViewAllRecyclerView;
    TextView custom_title;
    Toolbar toolbar;
    AppBarLayout apptoolbar;
    TextView collection, collections;
    ImageView backButton;
    ImageView more_vert_viewall;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        ViewAllRecyclerView = findViewById(R.id.ViewAllRecyclerView);
        //custom_title = findViewById(R.id.custom_title);
        toolbar = findViewById(R.id.toolbar1);
        apptoolbar = findViewById(R.id.apptoolbar);
        collection = findViewById(R.id.collection);
        collections = findViewById(R.id.collections);
        backButton = findViewById(R.id.backButton);
        more_vert_viewall = findViewById(R.id.more_vert_viewall);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().hide();

        String text1 = (String) getIntent().getExtras().get("text");
        collections.setText(text1);
        collection.setText(SecondFragment.folderListArray.size()+ " Collections");


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2 );
        ViewAllRecyclerView.setLayoutManager(layoutManager);
        ArrayList<ImageDetail> images = new ArrayList<ImageDetail>();

        Object text = getIntent().getExtras().get("text");

        //custom_title.setText((CharSequence) text);
        /*getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#FFFFFF")));
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle((CharSequence) text);*/

        SimpleRecyclerViewAdapter collectionsNestedRecyclerViewAdapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            collectionsNestedRecyclerViewAdapter = new SimpleRecyclerViewAdapter(this, null);
        }
        ViewAllRecyclerView.setAdapter(collectionsNestedRecyclerViewAdapter);

        ViewAllRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, ViewAllRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*if (position < 24) {*/
                    Intent inc = new Intent(getApplicationContext(), HiddenCollection.class);
                    inc.putExtra("position", position);

                    startActivity(inc);
                /*} else{
                    Intent in = new Intent(getApplicationContext(), NewCollection.class);
                    startActivity(in);
                }*/
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        more_vert_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ViewAllActivity.this,more_vert_viewall);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.collections_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.two:
                                Intent inc = new Intent(ViewAllActivity.this, SettingsActivity.class);
                                startActivity(inc);
                                return true;
                            default:
                                return true;

                        }
                    }
                });

                popup.show();
            }
        });



    }




}