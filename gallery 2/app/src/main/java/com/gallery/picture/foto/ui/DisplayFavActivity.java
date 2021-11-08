package com.gallery.picture.foto.ui;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.gallery.picture.foto.Adapters.DisplayImageAdapter;
import com.gallery.picture.foto.Interface.ToggleInterface2;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;

import com.gallery.picture.foto.edit.CropActivity;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.gallery.picture.foto.utils.StorageUtils;
import com.gallery.picture.foto.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DisplayFavActivity extends AppCompatActivity implements ToggleInterface2{

    int position = -1;
    public static List<PhotoData> displayImageList = new ArrayList<>();
    DisplayImageAdapter adapter;
    ViewPager vp;

    boolean isEditImage = false;


    AppBarLayout apptoolbar;
    RelativeLayout parentRelative;
    LinearLayout lout_back, lout_fav, lout_moreVert, lout_edit, lout_delete, lout_share, ll_bottom;
    ImageView fav, more_vert;
    Toolbar toolbar1;
    TextView image_title;
    Intent intent;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
    /*    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

        /*if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/
        setContentView(R.layout.activity_display_fav);
        vp = (findViewById(R.id.vp));
        lout_back = findViewById(R.id.lout_back);

        lout_fav = findViewById(R.id.lout_fav);
        lout_moreVert = findViewById(R.id.lout_moreVert);
        lout_share = findViewById(R.id.lout_share);
        lout_delete = findViewById(R.id.lout_delete);
        lout_edit = findViewById(R.id.lout_edit);
        fav = findViewById(R.id.fav);
        more_vert = findViewById(R.id.more_vert);
        apptoolbar = findViewById(R.id.apptoolbar);
        parentRelative = findViewById(R.id.parentRelative);
        toolbar1 = findViewById(R.id.toolbar1);
        image_title = findViewById(R.id.image_title);

        ll_bottom = findViewById(R.id.ll_bottom);
        intent = getIntent();


        intView();

        copyMoveEvent();

        lout_moreVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreMenu();
            }
        });
        lout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String extra_text = "https://play.google.com/store/apps/details?id=" + getPackageName();
                File file = new File(displayImageList.get(vp.getCurrentItem()).getFilePath());
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file);
                Intent shareIntent = new Intent();
                shareIntent.setAction("android.intent.action.SEND");
                shareIntent.setType(Utils.getMimeTypeFromFilePath(file.getPath()));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, extra_text);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                startActivity(Intent.createChooser(shareIntent, "Share with..."));
            }
        });
        lout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }

        });

        lout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i = 0;
                i = vp.getCurrentItem();

                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayFavActivity.this);
                builder.setMessage("Are you sure do you want to delete it?");
                builder.setCancelable(true);

                int finalI = i;
                builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        setDeleteFile();


                        /*photoList.remove(getIntent().getExtras().get("photoListPosition"));

                        PhotoAdapter p_adapter = new PhotoAdapter(DisplayFavActivity.this, photoList);
                        p_adapter.notifyDataSetChanged();*/


                    }
                });

                builder.setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                /*if (isSlideshow) {
                    if (isSlideshowStart) {
                        startSlideshow();
                    }
                }*/
                    }
                });

                builder.show();


            }
        });
        lout_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickFav();
            }
        });

        lout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file1 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.app_name));
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File output = new File(file1.getPath() + "/Edit" );

                if (!output.exists()) {
                    output.mkdirs();
                }

                String timeStamp = new SimpleDateFormat("HHmmss_dMyy").format(new Date());
                String fileName = "IMG_" + timeStamp + ".jpeg";

                String strOutput = output.getPath() + "/" + fileName;

                Intent intent = new Intent(DisplayFavActivity.this, CropActivity.class);
                intent.putExtra("imagePath", displayImageList.get(vp.getCurrentItem()).getFilePath());
                intent.putExtra("outputPath", strOutput);

                startActivityForResult(intent, 100);
            }
        });



    }



    private void intView() {

        if(intent.getExtras().getInt("folderPosition") == (-2)){

            ArrayList<String> favlist = PreferencesManager.getFavouriteList(this);
            for(int j =0; j<favlist.size();j++) {
                ArrayList<PhotoData> allFav = new ArrayList<>();
                for (int i = 0; i < Constant.displayImageList.size(); i++) {
                    if (Constant.displayImageList.get(i).isFavorite())
                        allFav.add(Constant.displayImageList.get(i));
                    displayImageList = allFav;
                }
            }
        }
        if (intent != null) {
            position = intent.getIntExtra("position", 0);
        }
        try {

            adapter = new DisplayImageAdapter( this, this, displayImageList);
            vp.setAdapter(adapter);
            vp.setCurrentItem(position);
            updateFavData();

           /* int pos = position;
            pos++;
            txtTitle.setText(pos + "/" + displayImageList.size());*/
            image_title.setText(displayImageList.get(vp.getCurrentItem()).getFileName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                position = i;

//                adCount++;
//                updateFavData();
              /*  int pos = position;
                pos++;
                txtTitle.setText(pos + "/" + displayImageList.size());*/
                image_title.setText(displayImageList.get(position).getFileName());
                updateFavData();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showMoreMenu() {

        PopupMenu popup = new PopupMenu(this, more_vert);
        popup.getMenuInflater().inflate(R.menu.display_image_menu, popup.getMenu());


        popup.getMenu().findItem(R.id.set_as_wallpaper).setVisible(true);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_details:
                        showDetailDialog();
                        break;

                    case R.id.set_as_wallpaper:

                        WallpaperManager myWallpaperManager
                                = WallpaperManager.getInstance(DisplayFavActivity.this);
                        try {

                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            bmOptions.inMutable = true;
                            Bitmap bitmap = BitmapFactory.decodeFile(displayImageList.get(position).getFilePath(), bmOptions);


                            if (bitmap != null) {
                                myWallpaperManager.setBitmap(bitmap);
                                Toast.makeText(DisplayFavActivity.this, "Set Wallpaper Successfully", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(DisplayFavActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(DisplayFavActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
                return false;
            }
        });
        popup.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDetailDialog() {
        final Dialog dialog = new Dialog(DisplayFavActivity.this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.details_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        File file = new File(displayImageList.get(position).getFilePath());

        TextView txt_title = dialog.findViewById(R.id.txt_title);
        TextView txt_format = dialog.findViewById(R.id.txt_format);
        TextView txt_modified = dialog.findViewById(R.id.txt_modified);
        TextView txt_resolution = dialog.findViewById(R.id.txt_resolution);
        TextView txt_file_size = dialog.findViewById(R.id.txt_file_size);
        TextView txt_duration = dialog.findViewById(R.id.txt_duration);
        TextView txt_path = dialog.findViewById(R.id.txt_path);
        TextView txt_takenTime = dialog.findViewById(R.id.txt_takenTime);
        TextView txt_location = dialog.findViewById(R.id.txt_location);
        LinearLayout lout_takenTime = dialog.findViewById(R.id.lout_takenTime);
        LinearLayout lout_location = dialog.findViewById(R.id.lout_location);
        LinearLayout lout_duration = dialog.findViewById(R.id.lout_duration);
        LinearLayout lout_resolution = dialog.findViewById(R.id.lout_resolution);
        lout_duration.setVisibility(View.GONE);

        TextView btn_ok = dialog.findViewById(R.id.btn_ok);


        if (file.exists()) {
            txt_title.setText(file.getName());
            txt_path.setText(file.getPath());

            String type = Utils.getMimeTypeFromFilePath(file.getPath());

            txt_format.setText(type);

            txt_file_size.setText(Formatter.formatShortFileSize(this, file.length()));

            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
            String strDate = format.format(file.lastModified());
            txt_modified.setText(strDate);


            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class);
                long createdAt = attr.creationTime().toMillis();
                txt_takenTime.setText(format.format(createdAt));
                lout_takenTime.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                lout_takenTime.setVisibility(View.GONE);
            }
            try {
                if (getLocation(file.getPath()) != null) {
                    if (getLocation(file.getPath()).get(0).toString() != "ab") {
                        txt_location.setText(getLocation(file.getPath()).get(0).toString());
                        lout_location.setVisibility(View.VISIBLE);
                    }
                }else {
                    lout_location.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                lout_location.setVisibility(View.GONE);
            }


            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                txt_resolution.setText(imageWidth + " x " + imageHeight);
//                txt_resolution.setVisibility(View.VISIBLE);
                lout_resolution.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                e.printStackTrace();
//                txt_resolution.setVisibility(View.GONE);
                lout_resolution.setVisibility(View.GONE);
            }


        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void setClickFav() {

        if (displayImageList.get(vp.getCurrentItem()).isFavorite()) {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.heart));

            displayImageList.get(vp.getCurrentItem()).setFavorite(false);

            setUnFavorite(displayImageList.get(vp.getCurrentItem()).getFilePath());
            RxBus.getInstance().post(new DisplayFavoriteEvent(displayImageList.get(vp.getCurrentItem()).getFilePath(), false));


        } else {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));

            displayImageList.get(vp.getCurrentItem()).setFavorite(true);

            setFavorite(displayImageList.get(vp.getCurrentItem()).getFilePath());
            RxBus.getInstance().post(new DisplayFavoriteEvent(displayImageList.get(vp.getCurrentItem()).getFilePath(), true));

        }

    }



    private void setUnFavorite(String filePath) {
        ArrayList<String> favList = PreferencesManager.getFavouriteList(DisplayFavActivity.this);

        if (favList == null) {
            favList = new ArrayList<>();
        }

        for (int f = 0; f < favList.size(); f++) {

            if (favList.get(f) != null && !favList.get(f).equalsIgnoreCase("")) {

                if (favList.get(f).equalsIgnoreCase(filePath)) {
                    favList.remove(f);
                    break;
                }

            }
        }

        PreferencesManager.setFavouriteList(DisplayFavActivity.this, favList);
    }

    private void setFavorite(String filePath) {
        ArrayList<String> favList = PreferencesManager.getFavouriteList(DisplayFavActivity.this);

        if (favList == null) {
            favList = new ArrayList<>();
        }

        favList.add(filePath);
        PreferencesManager.setFavouriteList(DisplayFavActivity.this, favList);
    }
    public interface UpdateInterface {

        public void onUpdate();
    }

    @Override
    public void onBackPressed() {

        /*if(intent.getExtras().getInt("folderPosition") == (-2)) {
            ArrayList<PhotoData> allFav = new ArrayList<>();
            for (int i = 0; i < displayImageList.size(); i++) {
                if (displayImageList.get(i).isFavorite())
                    allFav.add(displayImageList.get(i));
                displayImageList = allFav;
            }
            adapter.notifyDataSetChanged();
            adapter = new DisplayImageAdapter(DisplayImageActivity.this, DisplayImageActivity.this, displayImageList);
            vp.setAdapter(adapter);
            *//*FavouriteActivity f = new FavouriteActivity();
            f.onUpdate();*//*
        }*/



        super.onBackPressed();
    }

    public void setDeleteFile() {

        int finalI = vp.getCurrentItem();

        ArrayList<String> deleteList = new ArrayList<>();

        File file1 = new File(displayImageList.get(vp.getCurrentItem()).getFilePath());
        int mode = StorageUtils.checkFSDCardPermission(file1, DisplayFavActivity.this);
        if (mode == 0) {


            boolean isDelete = StorageUtils.deleteFile(file1, DisplayFavActivity.this);

            if (isDelete) {
                MediaScannerConnection.scanFile(DisplayFavActivity.this, new String[]{file1.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });

                deleteList.add(file1.getPath());


                displayImageList.remove(vp.getCurrentItem());

                adapter.notifyDataSetChanged();

                adapter = new DisplayImageAdapter(DisplayFavActivity.this, DisplayFavActivity.this, displayImageList);
                vp.setAdapter(adapter);

                RxBus.getInstance().post(new DisplayDeleteEvent(deleteList));

                if (finalI < displayImageList.size() - 1) {
                    position = finalI;
                    vp.setCurrentItem(finalI);

                } else {
                    if (displayImageList.size() == 0) {
                        onBackPressed();
                    } else {
                        try {
                            vp.setCurrentItem(finalI - 1);
                           /* int pos = viewPager.getCurrentItem();
                            pos++;
                            txtTitle.setText(pos + "/" + displayImageList.size());*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            } else {
                Toast.makeText(DisplayFavActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void copyMoveEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(CopyMoveEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<CopyMoveEvent>() {
            @Override
            public void call(CopyMoveEvent event) {


                if (event.getDeleteList() != null && event.getDeleteList().size() != 0) {
                    ArrayList<String> deleteList = new ArrayList<>();
                    deleteList = event.getDeleteList();

                    if (deleteList != null && deleteList.size() != 0)
                        for (int i = 0; i < deleteList.size(); i++) {

                            try {
                                if (deleteList.get(i).equalsIgnoreCase(displayImageList.get(vp.getCurrentItem()).getFilePath())) {
                                    int finalI = vp.getCurrentItem();

                                    displayImageList.remove(vp.getCurrentItem());
                                    adapter.notifyDataSetChanged();

                                    adapter = new DisplayImageAdapter(DisplayFavActivity.this, DisplayFavActivity.this, displayImageList);
                                    vp.setAdapter(adapter);

                                    if (finalI < displayImageList.size() - 1) {
                                        position = finalI;
                                        vp.setCurrentItem(finalI);

                                    } else {
                                        if (displayImageList.size() == 0) {
                                            onBackPressed();
                                        } else {
                                            try {
                                                vp.setCurrentItem(finalI - 1);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void updateFavData() {
        if (displayImageList.get(vp.getCurrentItem()).isFavorite()) {

            fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));


        } else {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.heart));


        }
    }


    @Override
    public void onToggleclick() {
        if (apptoolbar.getVisibility() == View.VISIBLE) {
            apptoolbar.setVisibility(View.INVISIBLE);
            ll_bottom.setVisibility(View.INVISIBLE);
        } else {
            apptoolbar.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.VISIBLE);
        }
    }

    private List<Object> getLocation(String filePath){
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(filePath);
        } catch (
                IOException e) {
            Log.e("ECSGDHSFDYSG",e.getMessage()+"::");
            e.printStackTrace();
        }

        try {
            List<Object> dataList = new ArrayList<>();
            String LATITUDE = ei.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String LATITUDE_REF = ei.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String LONGITUDE = ei.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String LONGITUDE_REF = ei.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            Float Latitude = null, Longitude = null;
            if ((LATITUDE != null) && (LATITUDE_REF != null) && (LONGITUDE != null) && (LONGITUDE_REF != null)) {
                if (LATITUDE_REF.equals("N")) {
                    Latitude = convertToDegree(LATITUDE);
                } else {
                    Latitude = 0 - convertToDegree(LATITUDE);
                }
                if (LONGITUDE_REF.equals("E")) {
                    Longitude = convertToDegree(LONGITUDE);
                } else {
                    Longitude = 0 - convertToDegree(LONGITUDE);
                }

                try {

                    dataList.add(getAddress(Latitude, Longitude, this));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                dataList.add(Latitude);
                dataList.add(Longitude);
                Log.d("datalist", dataList.toString());
                return dataList;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String getAddress(Float Latitude, Float Longitude, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

//        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()*//*
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*//*
//        String premises = addresses.get(0).getPremises();
//        Locale locale = addresses.get(0).getLocale();
//        String featureName = addresses.get(0).getFeatureName();
//        String subAdminArea = addresses.get(0).getSubAdminArea();
//        String subLocality = addresses.get(0).getSubLocality();
//        String thoroughfare = addresses.get(0).getThoroughfare();
        String city = addresses.get(0).getLocality();
        String subLocality = addresses.get(0).getSubLocality();


        /*Log.d("address", address);
        Log.d("state", state);
        Log.d("country", country);
        Log.d("postalCode", postalCode);
        Log.d("knownName", knownName);
        Log.d("premises", premises);
        Log.d("featureName", featureName);
        Log.d("locale", locale.toString());
        Log.d("subAdminArea", subAdminArea);
        Log.d("thoroughfare", thoroughfare);*/

        Log.d("city", city);
        Log.d("subLocality", subLocality);

        String concat = subLocality.concat(", " + city);

        return city==null ? "ab" : concat;//*addresses*//*;

    }

    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


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

            MediaScannerConnection.scanFile(DisplayFavActivity.this, new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                }
            });
            ArrayList<File> list = new ArrayList<>();

            File file3 = new File(cropPath);
            if (file3.exists()) {
                list.add(file3);
                RxBus.getInstance().post(new CopyMoveEvent(list, 2, new ArrayList<>()));
            }


        }
    }


}