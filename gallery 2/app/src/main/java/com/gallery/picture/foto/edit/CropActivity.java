package com.gallery.picture.foto.edit;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.edit.utils.AspectRatio;
import com.gallery.picture.foto.edit.utils.RatioText;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.ImageDeleteEvent;
import com.gallery.picture.foto.utils.RxBus;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class CropActivity extends AppCompatActivity {

    private static final String TAG = "CropActivity";
    private CropImageView img_crop_path;

    private LinearLayout layout_option;
    private HorizontalScrollView layout_crop_ratio;
    private LinearLayout ratio_list_group;

    private ImageView img_close;
    private ImageView img_save;
    private RelativeLayout layout_crop;
    private RelativeLayout layout_rotate;
    private RelativeLayout layout_vertical_rotate;
    private RelativeLayout layout_horizontal_rotate;
    private Bitmap originalBitmap;

    RelativeLayout lout_save;
    private String imagePath;
    private String outputPath;

    int i =0;
    private ImageView txt_save;
int index = 0;
    private CompositeDisposable disposables = new CompositeDisposable();

    private int[] non_selected = new int[]{R.drawable.ic_icon_custom, R.drawable.ratio_4_5,
            R.drawable.ratio_insstory, R.drawable.ratio_5_4, R.drawable.ratio_3_4, R.drawable.ratio_4_3, R.drawable.ratio_fbpost,
            R.drawable.ratio_fbcover, R.drawable.ratio_pinpost, R.drawable.ratio_3_2, R.drawable.ratio_9_16,
            R.drawable.ratio_16_9, R.drawable.ratio_1_2, R.drawable.ratio_youtubecover, R.drawable.ratio_twitterpost,
            R.drawable.ratio_twitterheader};

    private int[] selected = new int[]{R.drawable.ic_icon_custom_selected, R.drawable.ratio_4_5_click,
            R.drawable.ratio_insstory_click, R.drawable.ratio_5_4_click, R.drawable.ratio_3_4_click, R.drawable.ratio_4_3_click, R.drawable.ratio_fbpost_click,
            R.drawable.ratio_fbcover_click, R.drawable.ratio_pinpost_click, R.drawable.ratio_3_2_click, R.drawable.ratio_9_16_click,
            R.drawable.ratio_16_9_click, R.drawable.ratio_1_2_click, R.drawable.ratio_youtubecover_click, R.drawable.ratio_twitterpost_click,
            R.drawable.ratio_twitterheader_click};

    private Single<Bitmap> getCroppedBitmap() {
        return Single.fromCallable(() -> img_crop_path.getCroppedImage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        getSupportActionBar().hide();





        /*if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
*/




        init();
        click();


        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_crop_ratio.getVisibility() == View.VISIBLE) {
                disposables.add(getCroppedBitmap()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
//                            .doOnSubscribe(subscriber -> loadingDialogListener.showLoadingDialog())
//                            .doFinally(() -> loadingDialogListener.dismissLoadingDialog())
                        .subscribe(bitmap -> {
                            Log.d("onClick:subscribe", "onClick:subscribe ");
                            originalBitmap = bitmap;
                            img_crop_path.setImageBitmap(originalBitmap);
//                                changeMainBitmap(bitmap, true);
                            layout_crop_ratio.setVisibility(View.GONE);


                            img_crop_path.setShowCropOverlay(false);
                        }, e -> {
                            Log.d("onClick:erro ", "onClick:erro ");
                            e.printStackTrace();
                            layout_crop_ratio.setVisibility(View.GONE);
                            txt_save.setVisibility(View.GONE);

                            img_crop_path.setShowCropOverlay(false);
                            Toast.makeText(getApplicationContext(), "Error while saving image", Toast.LENGTH_LONG).show();
                        }));
            } else{ i=0;
                try {

                    OutputStream fOut = null;
                    File file = new File(outputPath);
                    if (file.exists()) {
                        file.delete();// the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    }
                    fOut = new FileOutputStream(file);

                    Bitmap pictureBitmap = originalBitmap; // obtaining the Bitmap
                    pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush(); // Not really required
                    fOut.close(); // do not forget to close the stream
                    Intent intent = new Intent();
                    intent.putExtra("cropPath", file.getAbsolutePath());
                    setResult(RESULT_OK, intent);

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }}


                    /*try {

                    OutputStream fOut = null;
                    File file = new File(outputPath);
                    if (file.exists()) {
                        file.delete();// the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    }
                    fOut = new FileOutputStream(file);

                    Bitmap pictureBitmap = originalBitmap; // obtaining the Bitmap
                    pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush(); // Not really required
                    fOut.close(); // do not forget to close the stream
                    Intent intent = new Intent();
                    intent.putExtra("cropPath", file.getAbsolutePath());

                    setResult(RESULT_OK, intent);
                    finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/


            }
        });


        /*lout_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_save.setVisibility(View.VISIBLE);
                txt_unsaved.setVisibility(View.GONE);
                img_crop_path.setShowCropOverlay(true);
            }
        });*/


    }
    public static void setLightStatusBar(View view, Activity activity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }
    public static void clearLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.setStatusBarColor(ContextCompat
                    .getColor(activity,R.color.colorPrimary));
        }
    }

    private void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        imagePath = getIntent().getStringExtra("imagePath");
        outputPath = getIntent().getStringExtra("outputPath");
        Log.d("imagepathincrop", "init: " + imagePath);
        img_crop_path = findViewById(R.id.img_crop_path);
        img_crop_path.setShowCropOverlay(false);
        img_crop_path.setRotatedDegrees(0);
        Glide.with(this).asBitmap().load(imagePath).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                img_crop_path.setImageBitmap(bitmap);
                originalBitmap = bitmap;
            }

            @Override
            public void onLoadCleared(Drawable placeholder) {
            }
        });


//        Glide.with(this).load(imagePath).into(img_crop_path);
        Log.d("imageaspectratio", "onClick111: " + img_crop_path.getAspectRatio());

        ratio_list_group = findViewById(R.id.ratio_list_group);
        img_save = findViewById(R.id.img_save);
        layout_option = findViewById(R.id.layout_option);
        layout_crop_ratio = findViewById(R.id.layout_crop_ratio);
        layout_crop_ratio.setVisibility(View.GONE);
//        Glide.with(this).load(imagePath).into(img_crop_path);
        Log.d(TAG, "onClick111: " + img_crop_path.getAspectRatio());
        img_close = findViewById(R.id.img_close);

        layout_crop = findViewById(R.id.layout_crop);
        layout_rotate = findViewById(R.id.layout_rotate);
        layout_vertical_rotate = findViewById(R.id.layout_vertical_rotate);
        layout_horizontal_rotate = findViewById(R.id.layout_horizontal_rotate);
        setUpRatioList();

    }
    private void click() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_crop_ratio.getVisibility() == View.VISIBLE) {
                    layout_crop_ratio.setVisibility(View.GONE);
                    layout_option.setVisibility(View.VISIBLE);
                    img_crop_path.setShowCropOverlay(false);
                } else {
                    onBackPressed();
                }
            }
        });
        /*img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_crop_ratio.getVisibility() == View.VISIBLE) {

                    disposables.add(getCroppedBitmap()
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
//                            .doOnSubscribe(subscriber -> loadingDialogListener.showLoadingDialog())
//                            .doFinally(() -> loadingDialogListener.dismissLoadingDialog())
                            .subscribe(bitmap -> {
                                Log.d(TAG, "onClick:subscribe ");
                                originalBitmap = bitmap;
                                img_crop_path.setImageBitmap(originalBitmap);
//                                changeMainBitmap(bitmap, true);
                                layout_crop_ratio.setVisibility(View.GONE);
                                layout_option.setVisibility(View.VISIBLE);
                                img_crop_path.setShowCropOverlay(false);
                            }, e -> {
                                Log.d(TAG, "onClick:erro ");
                                e.printStackTrace();
                                layout_crop_ratio.setVisibility(View.GONE);
                                layout_option.setVisibility(View.VISIBLE);
                                img_crop_path.setShowCropOverlay(false);
                                Toast.makeText(getApplicationContext(), "Error while saving image", Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    try {
                        OutputStream fOut = null;
                        File file = new File(outputPath);
                        if (file.exists()) {
                            file.delete();// the File to save , append increasing numeric counter to prevent files from getting overwritten.
                        }
                        fOut = new FileOutputStream(file);

                        Bitmap pictureBitmap = originalBitmap; // obtaining the Bitmap
                        pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                        fOut.flush(); // Not really required
                        fOut.close(); // do not forget to close the stream
                        Intent intent = new Intent();
                        intent.putExtra("cropPath", file.getAbsolutePath());
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });*/
        layout_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_crop_ratio.setVisibility(View.VISIBLE);
                layout_option.setVisibility(View.GONE);
                img_crop_path.setShowCropOverlay(true);
            }
        });
        layout_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + img_crop_path.getRotatedDegrees());
                Matrix matrix = new Matrix();
                if (img_crop_path.getRotatedDegrees() == 90) {
                    matrix.postRotate(180);
                    try {
                        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                        img_crop_path.setRotatedDegrees(180);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (img_crop_path.getRotatedDegrees() == 180) {
                    try {
                        matrix.postRotate(270);
                        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                        img_crop_path.setRotatedDegrees(270);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (img_crop_path.getRotatedDegrees() == 270) {
                    try {
                        matrix.postRotate(0);
                        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                        img_crop_path.setRotatedDegrees(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (img_crop_path.getRotatedDegrees() == 0) {
                    try {
                        matrix.postRotate(90);
                        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                        img_crop_path.setRotatedDegrees(90);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        layout_vertical_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.preScale(1.0f, -1.0f);
                    Bitmap bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                    originalBitmap = bitmap;
                    img_crop_path.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        layout_horizontal_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.preScale(-1.0f, 1.0f);
                    Bitmap bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                    img_crop_path.setImageBitmap(bitmap);
                    originalBitmap = bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static int SELECTED_COLOR = R.color.colorPrimary;
    private static int UNSELECTED_COLOR = R.color.white;
    private TextView selectedTextView;
    private ImageView selectedImageView;
    private int position = 0;

    private void setUpRatioList() {
        ratio_list_group.removeAllViews();
        RatioText[] ratioTextList = RatioText.values();
        Log.d(TAG, "setUpRatioList: " + selected.length + "   " + non_selected.length + "   " + ratioTextList.length);
        for (int i = 0; i < ratioTextList.length; i++) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

            View inflatedLayout = inflater.inflate(R.layout.item_crop, null);
            ImageView img_ratio = inflatedLayout.findViewById(R.id.img_ratio);
            TextView txt_ratio = inflatedLayout.findViewById(R.id.txt_ratio);
            img_ratio.setImageResource(non_selected[i]);
            txt_ratio.setText(getResources().getText(ratioTextList[i].getRatioTextId()));
            ratio_list_group.addView(inflatedLayout);

            if (i == 0) {
                selectedTextView = txt_ratio;
                selectedImageView = img_ratio;
                position = 0;
            }

            img_ratio.setTag(i);
            txt_ratio.setTag(ratioTextList[i]);
            inflatedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleButtonStatus(selectedImageView, selectedTextView, (Integer) selectedImageView.getTag(), false);

                    LinearLayout linearLayout = (LinearLayout) v;
                    TextView currentTextView = (TextView) linearLayout.getChildAt(1);
                    ImageView currentImageView = (ImageView) linearLayout.getChildAt(0);
                    toggleButtonStatus(currentImageView, currentTextView, (Integer) currentImageView.getTag(), true);

                    selectedImageView = currentImageView;
                    position = (int) selectedImageView.getTag();
                    selectedTextView = currentTextView;

                    RatioText ratioText = (RatioText) currentTextView.getTag();
                    if (ratioText == RatioText.FREE) {
                        img_crop_path.setFixedAspectRatio(false);
                    } else {
                        AspectRatio aspectRatio = ratioText.getAspectRatio();
                        img_crop_path.setAspectRatio((int) aspectRatio.getAspectX(), (int) aspectRatio.getAspectY());
                    }
                }
            });

        }
        toggleButtonStatus(selectedImageView, selectedTextView, (Integer) selectedImageView.getTag(), true);
    }

    private void toggleButtonStatus(ImageView imageView, TextView view, int position,
                                    boolean isActive) {
        if (isActive) {
            imageView.setImageResource(selected[position]);
            view.setTextColor(getColorFromRes(SELECTED_COLOR));
        } else {
            imageView.setImageResource(non_selected[position]);
            view.setTextColor(getColorFromRes(UNSELECTED_COLOR));
        }
    }

    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(getApplicationContext(), resId);
    }

    @Override
    public void onBackPressed() {
        RxBus.getInstance().post(new ImageDeleteEvent(outputPath));

        setResult(RESULT_CANCELED);

        finish();
    }

    @Override
    public void onStop() {
        disposables.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }

}