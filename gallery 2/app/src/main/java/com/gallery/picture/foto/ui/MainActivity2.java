package com.gallery.picture.foto.ui;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gallery.picture.foto.Adapters.MyFragmentAdapter;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ad.AdmobAdManager;
import com.gallery.picture.foto.utils.Constant;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.tabs.TabLayout;

import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity2 extends AppCompatActivity {


    ViewPager viewPager;
    String ONESIGNAL_APP_ID = "52477e20-42ee-4c81-8d6b-da8a5f2e3e08";

    TabLayout tabLayout;
    static Map<FileTime, String> FileDate;
    static LinkedHashMap<FileTime, String> sortedMap;
    static ArrayList<String> uniqueDates;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    static ArrayList<ArrayList<String>> q;
    static ArrayList<String> values;
    View view = null;
    AdmobAdManager admobAdManager;
    private InterstitialAd mInterstitialAd;



    /*protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    startService(new Intent(MainActivity2.this, ImageDataService.class));

                } else {
                    // exit the app if one permission is not granted
                    Toast.makeText(this, "Storage Required permission!", Toast.LENGTH_LONG).show();
                }
                

                // all permissions were granted

                break;
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SplashActivity.isSplashOpen = false;
        Constant.isOpenImage = true;
        admobAdManager = AdmobAdManager.getInstance(MainActivity2.this);

        if (!Constant.isShowFirstTimeADs) {
            admobAdManager.loadInterstitialAd(MainActivity2.this, getResources().getString(R.string.interstitial_id), 1, new AdmobAdManager.OnAdClosedListener() {
                @Override
                public void onAdClosed(Boolean isShowADs) {
                    if (!Constant.isShowFirstTimeADs) {
                        Constant.isShowFirstTimeADs = isShowADs;
                    }
                }
            });
        } else {
            if (!Constant.isShowFirstTimeOptionADs)
                admobAdManager.loadInterstitialAd(MainActivity2.this, getResources().getString(R.string.interstitial_id), 1, new AdmobAdManager.OnAdClosedListener() {
                    @Override
                    public void onAdClosed(Boolean isShowADs) {
                        if (!Constant.isShowFirstTimeOptionADs) {
                            Constant.isShowFirstTimeOptionADs = isShowADs;
                        }
                    }
                });
        }


                viewPager = findViewById(R.id.viewpager);
        FileDate = new HashMap<FileTime, String>();
        sortedMap = new LinkedHashMap<FileTime, String>();
        uniqueDates = new ArrayList<String>();
        q = new ArrayList<ArrayList<String>>();
        values = new ArrayList<String>();





        /*checkPermissions();*/






        /*String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA

        };

// content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

// Make the query.
        Cursor cur = managedQuery(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.i("ListingImages", " query count=" + cur.getCount());
        int i = 0;
        if (cur.moveToFirst()) {

            String bucket;
            String date, data, DateAdded, DateModified, DisplayName;
            String g = new String("Camera");

            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            int dateColumn2 = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_ADDED);

            int dateColumn3 = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_MODIFIED);

            int displayname = cur.getColumnIndex(
                    MediaStore.Images.Media.DISPLAY_NAME);

            int DATA = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);

            do {

                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                data = cur.getString(DATA);
                DateAdded = cur.getString(dateColumn2);
                DateModified = cur.getString(dateColumn3);
                DisplayName = cur.getString(displayname);
                data = cur.getString(DATA);
                SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
                GalleryImages galleryImages = new GalleryImages(DisplayName, data, date, DateModified, bucket);
                galleryImagesArray.add(galleryImages);

                if (bucket != null && bucket.equals("Camera")) {
                    i++;

                }

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket
                        + "  date_taken=" + date + "  date_added=" + DateAdded + "  date_modified=" + DateModified + "  displayname=" + DisplayName + "  data=" + data);
            } while (cur.moveToNext());
            Log.i("Camera1", galleryImagesArray.toString());
            Log.i("Camera1", String.valueOf(galleryImagesArray.size()));
        }

        galleryImagesArray.sort(new dateSorter());
        Log.i("Camera1", galleryImagesArray.toString());
        Log.i("Camera1", String.valueOf(galleryImagesArray.size()));*/


        /*File file = new File("/storage/emulated/0/DCIM/Camera");
        File[] files = file.listFiles();


        list = file.list();

        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            } });
        list = file.list();
        int j=1;*/
        /*for(File f: files) {

            Log.d("file" + j, f.getPath());
            j++;
        }
        j=1;
        for(String s: list){

            Log.d("list"+j, s);
            j++;
        }*/


       /* for(j =0; j<files.length; j++) {

            ArrayList<String> a = new ArrayList<>();



            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(Paths.get(file.getPath() + "/" + list[j]), BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileTime date = attr.creationTime();

            String dateCreated = df.format(date.toMillis());
            Log.d("milli", String.valueOf(date.toMillis()));
            FileDate.put(date, list[j]);

        }*/




        /*Log.d("dateCreated",FileDate.toString())*/
        ;

        /*FileDate.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x ->{ sortedMap.put(x.getKey(), x.getValue());
                    String format = df.format(x.getKey().toMillis());
                    uniqueDates.add(format); values.add(x.getValue()); });*/



        /*Log.d("FileDate", String.valueOf(FileDate.size()));
        Log.d("sortedmap",sortedMap.toString());
        Log.d("sortedmap", String.valueOf(sortedMap.size()));
        Log.d("uniquedates", String.valueOf(uniqueDates.size()));*/









        /*for(j=0;j<list.length;j++){
            Log.d("list1",list[j].toString());
}
         Log.d("list1", Integer.toString(list.length));*/


        getSupportActionBar().hide();
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), this, 3);
        viewPager.setAdapter(myFragmentAdapter);

        /*ViewPagerIndicator indicator = (ViewPagerIndicator) findViewById(R.id.pager_indicator);
        indicator.setPager(viewPager);*/

        tabLayout = findViewById(R.id.pager_indicator);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setOffscreenPageLimit(2);

        //adds margin between tab layout

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(5, 0, 5, 0);
            tab.requestLayout();
        }




    }




}