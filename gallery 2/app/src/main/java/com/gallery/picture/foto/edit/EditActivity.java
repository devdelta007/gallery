package com.gallery.picture.foto.edit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.edit.CropActivity;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.ImageDeleteEvent;
import com.gallery.picture.foto.utils.RxBus;

import java.io.File;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    LinearLayout lout_crop, lout_back, lout_save;
    private String imagePath;
    private String outputPath;
    private ImageView img_edit_path;
    boolean isEditImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().hide();


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }


        init();
        click();



    }

    private void init() {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }*/


        imagePath = getIntent().getStringExtra("imagePath");
        outputPath = getIntent().getStringExtra("outputPath");

        img_edit_path = findViewById(R.id.img_edit_path);
        Glide.with(this).load(imagePath).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(img_edit_path);

        lout_crop = findViewById(R.id.lout_crop);
        lout_back = findViewById(R.id.lout_back);
        lout_save = findViewById(R.id.lout_save);


    }

    private void click() {
        lout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }

        });

        lout_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                RxBus.getInstance().post(new EditImageEvent(outputPath, getString(R.string.app_name)));
                intent.putExtra("outputPath", outputPath);

                ArrayList<File> list = new ArrayList<>();
                File file = new File(outputPath);
                if (file.exists()) {
                    list.add(file);
                    RxBus.getInstance().post(new CopyMoveEvent(list, 2, new ArrayList<>()));
                }

                setResult(RESULT_OK, intent);
                finish();

            }
        });
        lout_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, CropActivity.class);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("outputPath", outputPath);
                startActivityForResult(intent, 100);

            }
        });
        /*layout_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, FilterActivity.class);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("outputPath", outputPath);
                startActivityForResult(intent, 200);
            }
        });
        layout_sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, StickerActivity.class);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("outputPath", outputPath);
                startActivityForResult(intent, 300);
            }
        });
        layout_paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, PaintActivity.class);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("outputPath", outputPath);
                startActivityForResult(intent, 400);
            }
        });
        layout_beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, BeautyActivity.class);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("outputPath", outputPath);
                startActivityForResult(intent, 500);
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String cropPath = data.getStringExtra("cropPath");
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            File file = new File(cropPath);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            isEditImage = true;

            MediaScannerConnection.scanFile(EditActivity.this, new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                }
            });
            imagePath = cropPath;
            Glide.with(this).load(cropPath).apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(img_edit_path);
        }
    }

    @Override
    public void onBackPressed() {
        RxBus.getInstance().post(new ImageDeleteEvent(outputPath));

        setResult(RESULT_CANCELED);
        finish();
    }
}