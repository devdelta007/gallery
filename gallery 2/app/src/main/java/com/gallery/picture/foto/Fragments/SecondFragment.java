package com.gallery.picture.foto.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.picture.foto.Adapters.CollectionsNestedRecyclerViewAdapter;
import com.gallery.picture.foto.BottomSheetDialog;
import com.gallery.picture.foto.Model.ImageDetail;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.Model.PhotoHeader;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ui.ViewAllActivity;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.service.ImageDataService;
import com.gallery.picture.foto.ui.SettingsActivity;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.gallery.picture.foto.service.ImageDataService.bucketimagesDataPhotoHashMap;


public class SecondFragment extends Fragment {


    RecyclerView.LayoutManager layoutManager2;

    RecyclerView CollectionsNestedRecyclerView2;
    TextView galleyCamerasize, AppViewAll;
    Toolbar galleyToolbar, toolbar2;
    CollapsingToolbarLayout galleryCollapsingToolbarLayout;
    ImageView more_vert1, create;
    View view = null;
    Context thiscontext;
    public static LinkedHashMap<String, ArrayList<PhotoData>> folderListArray = new LinkedHashMap<String, ArrayList<PhotoData>>();
    public List<Object> locationListArray = new ArrayList<>();
    CollectionsNestedRecyclerViewAdapter nestedRecyclerViewAdapter2;
    public static int TotalPhotos, TotalCollections;
    List<PhotoData> displayImageList = new ArrayList<>();
    private Activity myActivity;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myActivity = getActivity();
        thiscontext = container.getContext();
        if (view == null) {
            view = inflater.inflate(R.layout.second_fragment, container, false);
        }

        galleyCamerasize = view.findViewById(R.id.galley_camerasize);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //toolbar=  view.findViewById(R.id.toolbar);
        galleryCollapsingToolbarLayout = view.findViewById(R.id.galley_collapsing_toolbar);
        galleryCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        galleryCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        galleyToolbar = view.findViewById(R.id.galley_toolbar);
        toolbar2 = view.findViewById(R.id.toolbar2);
        more_vert1 = view.findViewById(R.id.more_vert1);
        create = view.findViewById(R.id.create);
        CollectionsNestedRecyclerView2 = view.findViewById(R.id.CollectionsNestedRecyclerView2);



        AppViewAll = view.findViewById(R.id.appViewAll);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:


                AppViewAll.setTextColor(getResources().getColor(R.color.white));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }

        new Thread(SecondFragment.this::getImageData).start();
        displayFavoriteEvent();
        displayDeleteEvent();
        copyMoveEvent();

        more_vert1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), more_vert1);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.collections_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.two:
                                Intent inc = new Intent(getActivity(), SettingsActivity.class);
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

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();

                bottomSheet.show(getActivity().getSupportFragmentManager(), "exampleBottomSheet");


            }
        });

        AppViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), ViewAllActivity.class);

                in.putExtra("text", "App Collections");
                startActivity(in);
            }
        });


        galleryCollapsingToolbarLayout.setTitle("Collections");
        //collapsingToolbarLayout.setSubtitle("Total view size : " + Integer.toString(list.length));









        /*CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black));*/

        galleyCamerasize.setText(TotalCollections + " Collections");




        /*CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Camera");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);*/


        return view;
    }


    private void copyMoveEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(CopyMoveEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<CopyMoveEvent>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void call(CopyMoveEvent event) {
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                if (event.getCopyMoveList() != null && event.getCopyMoveList().size() != 0 && event.getType() != 3) {

                    ArrayList<File> imageList = new ArrayList<>();
                    imageList = event.getCopyMoveList();

                    if (imageList == null) {
                        imageList = new ArrayList<>();
                    }

                    //SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
                    String strDate = "";
                    String strFileDate = "";

                    ArrayList<PhotoData> imagesData1 = new ArrayList<>();
                    for (int i = 0; i < imageList.size(); i++) {

                        File file1 = imageList.get(i);

                        if (!file1.isDirectory())
                            if (file1.exists()) {
                                PhotoData imagesData = new PhotoData();
                                strDate = format.format(file1.lastModified());
                                strFileDate = format2.format(file1.lastModified());
                                imagesData.setFilePath(file1.getPath());
                                imagesData.setFileName(file1.getName());
                                imagesData.setFolderName(file1.getParentFile().getName());
                                imagesData.setSize(file1.length());

                                /*try {*/
                                imagesData.setDate(strFileDate);
                               /* } catch (ParseException e) {
                                    e.printStackTrace();
                                }
*/
                                imagesData1.add(imagesData);
                            }


                    }
                    if (imagesData1 != null && imagesData1.size() != 0) {

                        int y = 0;
                        for (int d = 0; d < imagesData1.size(); d++) {
                            String folderName = imagesData1.get(d).getFolderName();
                            if(folderName.equals("0"))
                                folderName = "Unknown";

                            if (folderListArray.containsKey(folderName)) {
                                ArrayList<PhotoData> imagesData2 = folderListArray.get(imagesData1.get(d).getFolderName());
                                if (imagesData2 == null)
                                    imagesData2 = new ArrayList<>();

                                imagesData2.add(imagesData1.get(d));
                                folderListArray.put(imagesData1.get(d).getFolderName(), imagesData2);

                            } else {
                                ArrayList<PhotoData> imagesData2 = new ArrayList<>();
                                imagesData2.add(imagesData1.get(d));
                                folderListArray.put(imagesData1.get(d).getFolderName(), imagesData2);
                                galleyCamerasize.setText(folderListArray.size() + " Collections");
                            }
                            nestedRecyclerViewAdapter2.notifyDataSetChanged();
                            setAdapter();

                            /*for (int iw = 0; iw < folderListArray.size(); iw++) {
                                for(int id = 0; id<folderListArray.get(listkeys.get(iw)).size(); id++){

                                if (folderListArray.get(listkeys.get(iw)).get(id) instanceof PhotoData) {
                                    PhotoData model = (PhotoData) folderListArray.get(listkeys.get(iw)).get(id);

                                    if (model.getFolderName().equalsIgnoreCase(imagesData1.get(d).getFolderName())) {

                                        //ImageDetail imageDetail = new ImageDetail(imagesData1.get(d).getFilePath(), imagesData1.get(d).getDate(), imagesData1.get(d).getFileName(), false);
                                        folderListArray.get(iw).PathList.add(imageDetail);
                                        folderListArray.get(listkeys.get(iw)).get(id)


                                        y++;
                                        Log.d("y", Integer.toString(y));
                                        Log.d("pathlist", folderListArray.get(iw).getPathList().toString());
                                        nestedRecyclerViewAdapter2.notifyDataSetChanged();
                                        setAdapter();
                                        break;


                                    }

                                }

                                if(iw == folderListArray.size()-1){
                                    PhotoData p = new PhotoData();

                                    p = imagesData1.get(d);

                                    ImageDetail imageDetail2 = new ImageDetail(imagesData1.get(d).getFilePath(), imagesData1.get(d).getDate(), imagesData1.get(d).getFileName(), false);
                                    ArrayList<ImageDetail> temp = new ArrayList<>();
                                    temp.add(imageDetail2);
                                    p.PathList = temp;

                                    folderListArray.add(p);

                                    nestedRecyclerViewAdapter2.notifyDataSetChanged();
                                    setAdapter();
                                    break;
                                }}}*/
                        }
                    }











                          /*  for (int i = 0; i < listkeys.size(); i++) {
                                ArrayList<PhotoData> imagesData = ImageDataService.bucketimagesDataPhotoHashMap.get(listkeys.get(i));

                                if (imagesData != null && imagesData.size() != 0) {
                                    PhotoHeader bucketData = new PhotoHeader();

                                    bucketData.setTitle(listkeys.get(i));
                                    bucketData.setPhotoList(imagesData);

                                    photoList.add(bucketData);
                                    photoList.addAll(imagesData);

                                }
                            }
*/
/*

                            setAdapter();
*/


                }
            }


        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private ArrayList<PhotoData> setDateWiseSortAs(boolean isNewest, ArrayList<PhotoData> imagesSortData) {

        Collections.sort(imagesSortData, new Comparator<PhotoData>() {
            @Override
            public int compare(PhotoData t1, PhotoData t2) {

                int compareResult = 0;

                try {
                    if (isNewest) {
//                        return t2.getDate().compareTo(t1.getDate());

                        return Long.valueOf(t2.getDateValue()).compareTo(t1.getDateValue());
                    } else {

//                        return t1.getDate().compareTo(t2.getDate());
                        return Long.valueOf(t1.getDateValue()).compareTo(t2.getDateValue());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return compareResult;
            }
        });

        return imagesSortData;
    }

    private void displayFavoriteEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(DisplayFavoriteEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<DisplayFavoriteEvent>() {
            @Override
            public void call(DisplayFavoriteEvent event) {

                if (event.getFilePath() != null && !event.getFilePath().equalsIgnoreCase("")) {

                    setUpdateFavourite(event.isFavorite(), event.getFilePath());

                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void setUpdateFavourite(boolean favorite, String filePath) {
        if (folderListArray != null && folderListArray.size() != 0) {
            for (int i = 0; i < folderListArray.size(); i++) {
                /*for (int j = 0; j < folderListArray.get(i).getPathList().size(); j++) {
                    if (folderListArray.get(i) instanceof PhotoData) {
                        ImageDetail model = (ImageDetail) folderListArray.get(i).getPathList().get(j);

                        if (model.getName() != null) {
                            if (model.getName().equalsIgnoreCase(filePath)) {
                                model.setFavorite(favorite);
                                if (nestedRecyclerViewAdapter2 != null) {
                                    nestedRecyclerViewAdapter2.notifyItemChanged(i);
                                }
                                break;
                            }
                        }
                    }
                }*/

                Set<String> keys = folderListArray.keySet();
                ArrayList<String> listkeys = new ArrayList<String>();
                listkeys.addAll(keys);
                //listkeys = sortNameAscending(listkeys);

                for (int aj = 0; aj < folderListArray.get(listkeys.get(i)).size(); aj++) {
                    PhotoData model = folderListArray.get(listkeys.get(i)).get(aj);

                    if (model.getFileName() != null) {
                        if (model.getFileName().equalsIgnoreCase(filePath)) {
                            model.setFavorite(favorite);
                            if (nestedRecyclerViewAdapter2 != null) {
                                nestedRecyclerViewAdapter2.notifyItemChanged(i);
                            }
                            break;
                        }
                    }

                }

            }}
    }

    private void displayDeleteEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(DisplayDeleteEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<DisplayDeleteEvent>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void call(DisplayDeleteEvent event) {

                if (event.getDeleteList() != null && event.getDeleteList().size() != 0) {
                    ArrayList<String> deleteList = new ArrayList<>();
                    deleteList = event.getDeleteList();

                    updateDeleteImageData(deleteList);


                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
        RxBus.getInstance().addSubscription(this, subscription);
    }


    private void updateDeleteImageData(ArrayList<String> deleteList) {
        for (int l = 0; l < deleteList.size(); l++) {

            String strDate = deleteList.get(l);
            File file = new File(strDate);
            String name1 = file.getParentFile().getName();
            if(name1.equals("0"))
                name1 = "Unknown";

            if (strDate != null && !strDate.equalsIgnoreCase(""))
                if (folderListArray.containsKey(name1)) {
                    ArrayList<PhotoData> imagesData1 = folderListArray.get(name1);
                    if (imagesData1 == null)
                        imagesData1 = new ArrayList<>();


                    for (int i = 0; i < imagesData1.size(); i++) {
                        try {
                            if (imagesData1.get(i).getFilePath().equalsIgnoreCase(strDate)) {
                                imagesData1.remove(i);
                                break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    File f = new File(strDate);
                    String name = f.getParentFile().getName();
                    if (imagesData1 != null && imagesData1.size() != 0) {
                        folderListArray.put(name, imagesData1);
                    } else {
                        folderListArray.remove(name);
                    }

                }

            if (nestedRecyclerViewAdapter2 != null) {

                nestedRecyclerViewAdapter2.notifyDataSetChanged();
                setAdapter();
            }
        }
    }


    public void setAdapter() {


        layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        CollectionsNestedRecyclerView2.setLayoutManager(layoutManager2);
        nestedRecyclerViewAdapter2 = new CollectionsNestedRecyclerViewAdapter(getActivity(), folderListArray);
        CollectionsNestedRecyclerView2.setAdapter(nestedRecyclerViewAdapter2);
        CollectionsNestedRecyclerView2.setHasFixedSize(false);
    }

    private ArrayList<String> sortNameAscending(ArrayList<String> imagesSortData) {

        Collections.sort(imagesSortData, new Comparator<String>() {
            @Override
            public int compare(String t1, String t2) {
//                File file1 = new File(t1.getFilePath());
//                File file2 = new File(t2.getFilePath());

                return t1.compareToIgnoreCase(t2);
            }
        });

        return imagesSortData;
    }

    private void getImageData() {


        while (true) {
            if (ImageDataService.isComplete) {
                Log.e("threadComplete2", "thread");

                break;
            }
        }

        if(getActivity() == null)
            return;
        Log.e("getActivity2", getActivity().toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TotalCollections = 0;



                new Thread(SecondFragment.this::setSortData).start();



                TotalCollections = folderListArray.size();
                galleyCamerasize.post(new Runnable() {
                    @Override
                    public void run() {
                        galleyCamerasize.setText(TotalCollections + " Collections");
                    }
                });


            }
        });
    }

    public ArrayList<ImageDetail> getCameraCover(String id) {

        String data = null;
        String eachDate = null;
        String title = null;
        String imageId = null;

        ArrayList<ImageDetail> result = new ArrayList<>();
        final String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Images.Media._ID};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";
        final Cursor cursor = thiscontext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                orderBy);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(getActivity());
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
    }

    private void setSortData() {
        /*List<PhotoData> dataList1 = new ArrayList<>();
        for (int i = 0; i < photoList.size(); i++) {
            if (photoList.get(i) instanceof PhotoData) {
                //((PhotoData) photoList.get(i)).setLocationData(getLocation(((PhotoData) photoList.get(i)).getFilePath()));
                dataList1.add((PhotoData) photoList.get(i));
            }
        }*/
        displayImageList = new ArrayList<>();
        //Constant.displayImageList.addAll(dataList1);

        Set<String> keys = bucketimagesDataPhotoHashMap.keySet();
        ArrayList<String> listkeys = new ArrayList<>();
        listkeys.addAll(keys);
        //listkeys = sortNameAscending(listkeys);

        for (int i = 0; i < listkeys.size(); i++) {
            ArrayList<PhotoData> imagesData = bucketimagesDataPhotoHashMap.get(listkeys.get(i));

            if (imagesData != null && imagesData.size() != 0) {
                PhotoHeader bucketData = new PhotoHeader();

                bucketData.setTitle(listkeys.get(i));


                if (imagesData.size() != 0) {

                    displayImageList.addAll(imagesData);
                }

            }
        }

        /*ArrayList<String> ids = new ArrayList<String>();
        // Log.e("array size", "" + ids.size() + "===" + cursor.getCount());
        for (int k = 0; k < displayImageList.size(); k++) {

            String bucketId = displayImageList.get(k).getBucketId();
            if (!ids.contains(bucketId)) {
                PhotoData album = new PhotoData();
                album.BucketId = bucketId;
                album.folderName = displayImageList.get(k).getFolderName();
                if (!TextUtils.isEmpty(album.folderName)) {
                    File file = new File(displayImageList.get(k).getFilePath());
                    album.folderPath = file.getAbsoluteFile().getParent();


                    album.Id = displayImageList.get(k).getId();
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

        }*/

        folderListArray = ImageDataService.bucketimagesDataCollectionHashMap;
        /*

         */
        if (nestedRecyclerViewAdapter2 != null) {
            nestedRecyclerViewAdapter2.notifyDataSetChanged();

        }

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAdapter();
                TotalCollections = folderListArray.size();

                galleyCamerasize.setText(TotalCollections + " Collections");
            }
        });
    }

}
