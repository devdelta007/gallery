package com.gallery.picture.foto.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gallery.picture.foto.Model.ImageDetail;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.Model.PhotoHeader;
import com.gallery.picture.foto.utils.PreferencesManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ImageDataService extends Service {

    public static boolean isComplete = false;

    public static List<Object> photoDataList = new ArrayList<>();

    public static LinkedHashMap<String, ArrayList<PhotoData>> bucketimagesDataPhotoHashMap;
    public static LinkedHashMap<String, ArrayList<PhotoData>> bucketimagesDataLocationHashMap;
    public static LinkedHashMap<String, ArrayList<PhotoData>> bucketimagesDataCollectionHashMap;

    public static ArrayList<PhotoData> folderListArray = new ArrayList<PhotoData>();

    public static int TotalPhotos;
    public static ArrayList<PhotoData> photoDataArrayList = new ArrayList<>();




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Notification notification = new NotificationCompat.Builder(this, NotificationUtils.ANDROID_CHANNEL_ID)
//                        .setContentTitle("")
//                        .setPriority(NotificationManager.IMPORTANCE_MIN)
//                        .setCategory(Notification.CATEGORY_SERVICE)
//                        .setContentText("").build();
//
//                startForeground(1, notification);
//                stopForeground(STOP_FOREGROUND_REMOVE);
//            }
//        } catch (RuntimeException e) {
//
//        }


        photoDataList = new ArrayList<>();
        bucketimagesDataPhotoHashMap = new LinkedHashMap<>();
        bucketimagesDataCollectionHashMap = new LinkedHashMap<>();
        bucketimagesDataLocationHashMap = new LinkedHashMap<>();
        isComplete = false;

        Observable.fromCallable(() -> {
            Log.e("ImageGet", "com.gallery.picture.foto.service photo getting start....");
            Log.d("servicetime", String.valueOf(Calendar.getInstance().getTime()));
            photoDataList.clear();
            bucketimagesDataPhotoHashMap.clear();
            bucketimagesDataCollectionHashMap.clear();;
            bucketimagesDataLocationHashMap.clear();
            photoDataList = getImagesList();
            //getImages();
            return true;
        }).subscribeOn(Schedulers.io())
                .doOnError(throwable -> {
                    isComplete = true;
                    Intent intent1 = new Intent("LoardDataComplete");
                    intent1.putExtra("completed", true);
                    sendBroadcast(intent1);
                })
                .subscribe((result) -> {
                    isComplete = true;
                    Intent intent1 = new Intent("LoardDataComplete");
                    intent1.putExtra("completed", true);
                    Log.e("ImageGet", "com.gallery.picture.foto.service photo set list....");
                    sendBroadcast(intent1);
                });

        return super.onStartCommand(intent, flags, startId);
    }

    /*public void getImages(){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null,  MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        ArrayList<String> ids = new ArrayList<String>();
        int count = 0;
        Log.e("array size", "" + ids.size() + "===" + cursor.getCount());
        while (cursor.moveToNext()) {

            String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            if (!ids.contains(bucketId)) {
                PhotoData album = new PhotoData();
                album.BucketId = bucketId;
                album.folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if (!TextUtils.isEmpty(album.folderName)) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String result = cursor.getString(column_index);
                    album.folderPath = GetParentPath(result);
                    album.Id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    album.PathList = getCameraCover("" + album.BucketId); //----get four image path arraylist
                    album.setType(0);
                    Log.e("TAG", "GetPhotoAlbumLis: " + album.folderName);
                    if (album.PathList.size() > 0) {
                        folderListArray.add(album);
                        ids.add(album.BucketId);
                    }
                    TotalPhotos += album.PathList.size();
                    Log.e("TotalPhotos", String.valueOf(TotalPhotos));
                    Log.d("album", String.valueOf(album));
                }
            }

        }
        cursor.close();

    }*/

    public List<Object> getImagesList() {
        List<Object> photoList = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<String>();


        Cursor mCursor = null;

        String BUCKET_DISPLAY_NAME;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            BUCKET_DISPLAY_NAME = MediaStore.MediaColumns.BUCKET_DISPLAY_NAME;
        } else
            BUCKET_DISPLAY_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;


        String[] projection = {MediaStore.Images.Media.DATA/*, MediaStore.Images.Media.TITLE*/
//                , BUCKET_DISPLAY_NAME
                , MediaStore.MediaColumns.DATE_MODIFIED
                , MediaStore.MediaColumns.DISPLAY_NAME
                , MediaStore.MediaColumns.SIZE,
                MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                //*MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                // , MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media._ID
        };

        Uri uri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        mCursor = getContentResolver().query(
                uri, // Uri
                projection, // Projection
                null,
                null,
                "LOWER(" + MediaStore.Images.Media.DATE_MODIFIED + ") DESC");

        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
//        SimpleDateFormat format = new SimpleDateFormat("MMM yyyy", Locale.US);
        SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        if (mCursor != null) {
            ArrayList<String> favList = PreferencesManager.getFavouriteList(this);
            if (favList == null) {
                favList = new ArrayList<>();
            }

        mCursor.moveToFirst();


        Date date = null;
        List<Object> locationList = new ArrayList<>();
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
            if (size != 0) {

                String bucketId = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));


                String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String title = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));


//                    Log.e("imagesize", "size: " + size);

//                    String bucketName = mCursor.getString(mCursor.getColumnIndex(BUCKET_DISPLAY_NAME));
                long d = mCursor.getLong(mCursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
                d = d * 1000;
                String strDate = format.format(d);
//                    String strFileDate = format2.format(d);



                String folder = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if(folder==null || folder.equals(""))
                    folder = "Unknown";

                PhotoData imagesData = new PhotoData(folder, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)), bucketId, strDate, path, title, size, d, favList.contains(path));
/*
                imagesData.setFolderName(folder);
                imagesData.setId(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
                imagesData.setBucketId(bucketId);
                imagesData.setFolderPath(GetParentPath(path));
                imagesData.setDate(strDate);
                imagesData.setFilePath(path);
                imagesData.setFileName(title);
                imagesData.setLocationData(getLocation(path));
                imagesData.setSize(size);
                imagesData.setDateValue(d);*/

                /*String s = imagesData.getLocationData() == null ? "ab" : imagesData.getLocationData().get(0).toString();
                Log.d("sld", s);*/
//                    imagesData.setFolderName(bucketName);

                /*if (favList.contains(path)) {
                    imagesData.setFavorite(true);
                } else {
                    imagesData.setFavorite(false);
                }*/


//                    try {
//                        date = format2.parse(strFileDate);
//                        imagesData.setDate(date);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }



                /*if (!ids.contains(bucketId)) {

                    if (!TextUtils.isEmpty(imagesData.folderName)) {


                        imagesData.PathList = getCameraCover("" + imagesData.BucketId); //----get four image path arraylist
                        imagesData.setType(0);

                        if (imagesData.PathList.size() > 0) {
                            folderListArray.add(imagesData);
                            ids.add(imagesData.BucketId);
                        }
                        TotalPhotos += imagesData.PathList.size();

                    }
                }*/

                photoDataArrayList.add(imagesData);

                if (bucketimagesDataPhotoHashMap.containsKey(strDate)) {
                    ArrayList<PhotoData> imagesData1 = bucketimagesDataPhotoHashMap.get(strDate);
                    if (imagesData1 == null)
                        imagesData1 = new ArrayList<>();

                    imagesData1.add(imagesData);
                    bucketimagesDataPhotoHashMap.put(strDate, imagesData1);

                } else {
                    ArrayList<PhotoData> imagesData1 = new ArrayList<>();
                    imagesData1.add(imagesData);
                    bucketimagesDataPhotoHashMap.put(strDate, imagesData1);
                }


                if (bucketimagesDataCollectionHashMap.containsKey(folder)) {
                    ArrayList<PhotoData> imagesData1 = bucketimagesDataCollectionHashMap.get(folder);
                    if (imagesData1 == null)
                        imagesData1 = new ArrayList<>();

                    imagesData1.add(imagesData);
                    bucketimagesDataCollectionHashMap.put(folder, imagesData1);

                } else {

                    ArrayList<PhotoData> imagesData1 = new ArrayList<>();
                    imagesData1.add(imagesData);
                    bucketimagesDataCollectionHashMap.put(folder, imagesData1);
                }
               /* if (locationList != null && !locationList.isEmpty()) {
                    if(locationList.get(0) != null && !locationList.get(0).equals(""))
                    if (!locationList.get(0).equals("ab")) {
                        String string = locationList.get(0).toString();
                        if (bucketimagesDataLocationHashMap.containsKey(string)) {
                            ArrayList<PhotoData> imagesData2 = bucketimagesDataLocationHashMap.get(string);
                            if (imagesData2 == null)
                                imagesData2 = new ArrayList<>();

                            imagesData2.add(imagesData);
                            bucketimagesDataLocationHashMap.put(string, imagesData2);

                        } else {
                            ArrayList<PhotoData> imagesData1 = new ArrayList<>();
                            imagesData1.add(imagesData);
                            bucketimagesDataLocationHashMap.put(string, imagesData1);
                        }
                    }
                }
                Log.d("bisize", String.valueOf(bucketimagesDataLocationHashMap.size()));*/
            }
        }

        mCursor.close();
    }


        Set<String> keys = bucketimagesDataPhotoHashMap.keySet();
        ArrayList<String> listkeys = new ArrayList<String>();
        listkeys.addAll(keys);

        photoList.clear();

        for (int i = 0; i < listkeys.size(); i++) {

            {
                ArrayList<PhotoData> imagesData = bucketimagesDataPhotoHashMap.get(listkeys.get(i));

                if (imagesData != null && imagesData.size() != 0) {
                    PhotoHeader bucketData = new PhotoHeader();

                    bucketData.setTitle(listkeys.get(i));

                    bucketData.setPhotoList(imagesData);

                    photoList.add(bucketData);
                    photoList.addAll(imagesData);

                }
            }
        }


        return photoList;
    }

    public String GetParentPath(String path) {
        File file = new File(path);
        return file.getAbsoluteFile().getParent();
    }

  /*  public ArrayList<ImageDetail> getCameraCover(String id) {

        String data = null;
        String eachDate = null;
        String title = null;
        String imageId = null;

        ArrayList<ImageDetail> result = new ArrayList<>();
        final String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Images.Media._ID};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";
        final Cursor cursor =getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                orderBy);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(this);
        if (favList == null) {
            favList = new ArrayList<>();
        }
        if (cursor.moveToFirst()) {
            do {
                data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                eachDate = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                title = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));

                File filePath = new File(data);
                double length = filePath.length();
                if (length > 0)
                    result.add(new ImageDetail(data, eachDate, title, favList.contains(data)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }*/

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
                /*dataList.add(Latitude);
                dataList.add(Longitude);
                Log.d("datalist", dataList.toString());*/
                Log.d("exiffp", filePath.toString()+ ";" +LATITUDE+ ";" +LATITUDE_REF + ";" +LONGITUDE + ";"+ LONGITUDE_REF);
                return dataList;
            } else {
                Log.d("exifnull", filePath.toString()+ ";" +LATITUDE+ ";" +LATITUDE_REF + ";" +LONGITUDE + ";"+ LONGITUDE_REF);
                return null;
            }
        } catch (Exception e) {

            return null;
        }
    }

    private String getAddress(Float Latitude, Float Longitude, Context context) throws IOException {

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

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

       // Log.d("city", city);
        //Log.d("subLocality", subLocality);

        String concat = subLocality.concat(", " + city);
        Log.d("lstring", concat + ";"+ city);
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
}



