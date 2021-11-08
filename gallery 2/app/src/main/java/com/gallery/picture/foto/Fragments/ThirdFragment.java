package com.gallery.picture.foto.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.gallery.picture.foto.Adapters.ExploreFavRVAdapter;
import com.gallery.picture.foto.Adapters.ExploreRecyclerViewAdapter;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ad.AdEventListener;
import com.gallery.picture.foto.ad.AdmobAdManager;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.room.AppDataBase;
import com.gallery.picture.foto.room.Location;
import com.gallery.picture.foto.room.LocationDAO;
import com.gallery.picture.foto.service.ImageDataService;
import com.gallery.picture.foto.ui.FavouriteActivity;
import com.gallery.picture.foto.ui.PlacesActivity;
import com.gallery.picture.foto.ui.SettingsActivity;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.gallery.picture.foto.service.ImageDataService.bucketimagesDataLocationHashMap;
import static com.gallery.picture.foto.service.ImageDataService.photoDataArrayList;

public class ThirdFragment extends Fragment {

    AdmobAdManager admobAdManager;
    boolean isNativeADsShow = true;

    RecyclerView photosRecyclerview, rv_1, rv_2;
    RecyclerView.LayoutManager photosLayoutManager;

    ShapeableImageView places;
    Toolbar photosToolbar;
    CollapsingToolbarLayout photosCollapsingToolbarLayout;
    ImageView more_vert1, iv_empty;
    RelativeLayout rl_empty, ll_banner;
    ProgressBar lout_progress_bar1;
    CardView card_view;
    FrameLayout frame_layout;
    View text_layout, view, inner_view;
    RecyclerView.LayoutManager galleryLayoutManager, FirstLayoutManager, SecondLayoutManager;
    TextView txt_lcollection,nodataid;

    ExploreRecyclerViewAdapter FirstRecyclerViewAdapter;
    ExploreFavRVAdapter SecondRecyclerViewAdapter;
    TextView txt_places, txt_favs, favViewAll, lViewAll, txt_favcollection;
    ArrayList<String> favList = new ArrayList<>();
    public static AppDataBase db;
    public static LocationDAO locationDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.third_fragment, container, false);

        admobAdManager = AdmobAdManager.getInstance(getActivity());
        loadNativeAd();
        loadBanner();




        //toolbar=  view.findViewById(R.id.toolbar);
        photosCollapsingToolbarLayout = view.findViewById(R.id.photos_collapsing_toolbar);
        more_vert1 = view.findViewById(R.id.more_vert1);
        lout_progress_bar1 = view.findViewById(R.id.lout_progress_bar1);
        iv_empty = view.findViewById(R.id.iv_empty);
        places = view.findViewById(R.id.places);
        text_layout = view.findViewById(R.id.text_layout);
        inner_view = view.findViewById(R.id.inner_view);
        favViewAll = view.findViewById(R.id.favViewAll);
        lViewAll = view.findViewById(R.id.lViewAll);
        lViewAll.setVisibility(View.GONE);
        txt_places = text_layout.findViewById(R.id.txt_places);
        txt_favs = text_layout.findViewById(R.id.txt_favs);
        rv_1 = inner_view.findViewById(R.id.rv_1);
        rv_2 = inner_view.findViewById(R.id.rv_2);
        txt_favcollection = inner_view.findViewById(R.id.txt_favcollection);
        txt_lcollection = view.findViewById(R.id.txt_lcollection);
        rl_empty = view.findViewById(R.id.rl_empty);
        nodataid = view.findViewById(R.id.nodataid);
        frame_layout = view.findViewById(R.id.frame_layout);
        card_view = view.findViewById(R.id.card_view);
        ll_banner = view.findViewById(R.id.ll_banner);

        

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:

                lViewAll.setTextColor(getResources().getColor(R.color.white));
                favViewAll.setTextColor(getResources().getColor(R.color.white));

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }

        db = Room.databaseBuilder(getActivity(),
                AppDataBase.class, "location").build();
        locationDao = db.locationDao();


        favList = PreferencesManager.getFavouriteList(getActivity());
        if (favList == null) {
            favList = new ArrayList<>();
        }
        if (favList.size() == 0) {

            txt_favs.setVisibility(View.GONE);


            txt_favcollection.setVisibility(View.GONE);
            favViewAll.setVisibility(View.GONE);
            rv_2.setVisibility(View.GONE);
        } else {
            txt_favs.setText(favList.size() + " Favourites");

            txt_favcollection.setVisibility(View.VISIBLE);
            favViewAll.setVisibility(View.VISIBLE);
            rv_2.setVisibility(View.VISIBLE);
        }

        Log.d("favlist", favList.toString());
        SecondLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_2.setLayoutManager(SecondLayoutManager);
        Log.d("favlistba", favList.toString());
        SecondRecyclerViewAdapter = new ExploreFavRVAdapter(getActivity(), favList);

        rv_2.setAdapter(SecondRecyclerViewAdapter);
        rv_2.setHasFixedSize(false);


        lout_progress_bar1.setVisibility(View.VISIBLE);
        rv_1.setVisibility(View.GONE);
        txt_places.setVisibility(View.GONE);

        new Thread(ThirdFragment.this::getImageData).start();
        displayFavoriteEvent();
        displayDeleteEvent();


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



        favViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inc = new Intent(getActivity(), FavouriteActivity.class);


                startActivity(inc);
            }
        });
        lViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), PlacesActivity.class);
                startActivity(in);
            }
        });

        photosCollapsingToolbarLayout.setTitle("Explore");
        //collapsingToolbarLayout.setSubtitle("Total view size : " + Integer.toString(list.length));
        photosCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        photosCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        photosCollapsingToolbarLayout.setContentDescription("desc");







        /*CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black));*/


        /*photosRecyclerview = view.findViewById(R.id.photos_recyclerview);
        photosLayoutManager = new LinearLayoutManager(getActivity());
        photosRecyclerview.setLayoutManager(photosLayoutManager);
        ClickListener clickListener = null;
        photosRecyclerViewAdapter = new RecyclerViewAdapter(  null, getActivity(), images3);
        photosRecyclerview.setAdapter(photosRecyclerViewAdapter);*/

        /*CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Camera");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);*/

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
               // iv_empty.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),  android.graphics.PorterDuff.Mode.MULTIPLY);
                nodataid.setTextColor(getResources().getColor(R.color.white));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
               // iv_empty.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent),  android.graphics.PorterDuff.Mode.MULTIPLY);
                nodataid.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }

        return view;
    }

    private void loadNativeAd() {
        admobAdManager.LoadNativeAd(getActivity(), getResources().getString(R.string.native_app_id), new AdEventListener() {
            @Override
            public void onAdLoaded(Object object) {
                if (object != null) {
                    frame_layout.setVisibility(View.VISIBLE);
                    card_view.setVisibility(View.VISIBLE);
                    isNativeADsShow = true;
                    admobAdManager.populateUnifiedNativeAdView(getActivity(), frame_layout, (NativeAd) object, true, false);

                } else {
                    isNativeADsShow = false;
                    card_view.setVisibility(View.GONE);
                    frame_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdClosed() {
                card_view.setVisibility(View.GONE);
                frame_layout.setVisibility(View.GONE);
            }

            @Override
            public void onLoadError(String errorCode) {
                isNativeADsShow = false;
                card_view.setVisibility(View.GONE);
                frame_layout.setVisibility(View.GONE);
            }
        });
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

    private void getImageData() {
        while (true) {
            if (ImageDataService.isComplete) {
                Log.e("threadComplete3", "thread");
                break;
            }
        }

        ArrayList<Location> all = (ArrayList<Location>) locationDao.getAll();
        int lsize = all.size();
        List<String> locationList = new ArrayList<>();

        Log.d("lsize", String.valueOf(locationDao.getAll().size()));
        bucketimagesDataLocationHashMap.clear();
        photoDataArrayList = setDateWiseSortAs(false, photoDataArrayList);

        for (int i = 0; i < photoDataArrayList.size(); i++) {

            String path = photoDataArrayList.get(i).getFilePath();

            if(lsize>i) {
                Location lt = all.get(i); //locationDao.findByPath(path);
                if(lt == null)
                    lt = new Location();
                locationList = lt.getLocation();  // lt.getLocation();
                photoDataArrayList.get(i).setLocationData(locationList);
                Log.d("aryroom", i + ""+path );
            }
            else {
                locationList = getLocation(path);

                Location l = new Location(path, (locationList));
                locationDao.insert(l);

                photoDataArrayList.get(i).setLocationData(locationList);
                Log.d("ary", i + ""+path );
            }
            if (locationList != null && !locationList.isEmpty()) {
                if (locationList.get(0) != null && !locationList.get(0).equals(""))
                    if (!locationList.get(0).equals("ab")) {



                        String string = locationList.get(0).toString();
                        if (bucketimagesDataLocationHashMap.containsKey(string)) {
                            ArrayList<PhotoData> imagesData2 = bucketimagesDataLocationHashMap.get(string);
                            if (imagesData2 == null)
                                imagesData2 = new ArrayList<>();

                            imagesData2.add(photoDataArrayList.get(i));
                            bucketimagesDataLocationHashMap.put(string, imagesData2);

                        } else {
                            ArrayList<PhotoData> imagesData1 = new ArrayList<>();
                            imagesData1.add(photoDataArrayList.get(i));
                            bucketimagesDataLocationHashMap.put(string, imagesData1);
                        }
                    }
            }


        }
        Log.d("lsizeaa", String.valueOf(locationDao.getAll().size()));
        Log.d("bisize", String.valueOf(bucketimagesDataLocationHashMap.size()));

        if (getActivity() == null)
            return;
        Log.e("getActivity3", getActivity().toString());
        Log.e("getActivity3", getActivity().toString());


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lout_progress_bar1.setVisibility(View.GONE);
                rv_1.setVisibility(View.VISIBLE);



                Log.d("bucketsize", bucketimagesDataLocationHashMap.size() + " " + bucketimagesDataLocationHashMap.keySet().toString());

                setAdapter();

            }
        });


    }

    public void loadBanner() {


            admobAdManager.LoadAdaptiveBanner(getActivity(), ll_banner, getResources().getString(R.string.banner_id), new AdEventListener() {
                @Override
                public void onAdLoaded(Object object) {

                    ll_banner.setVisibility(View.VISIBLE);
                    Log.d("banner", "onAdLoaded: ");
                }

                @Override
                public void onAdClosed() {

                }

                @Override
                public void onLoadError(String errorCode) {
                    ll_banner.setVisibility(View.GONE);
                    Log.d("banner", "onLoadError: ");
                }
            });


    }

    @Override
    public void onDetach() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                locationDao.deleteAll();
            }
        }).start();

        super.onDetach();
    }

    public void setAdapter() {


        FirstLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_1.setLayoutManager(FirstLayoutManager);

        FirstRecyclerViewAdapter = new ExploreRecyclerViewAdapter(getActivity());

        rv_1.setAdapter(FirstRecyclerViewAdapter);
        rv_1.setHasFixedSize(false);

        if(favList == null)
            favList = new ArrayList<String>();
        if (favList.size() == 0) {

            //txt_favs.setText("0 Favourite");

            txt_favs.setVisibility(View.GONE);


            txt_favcollection.setVisibility(View.GONE);
            favViewAll.setVisibility(View.GONE);
            rv_2.setVisibility(View.GONE);
        } else {
            txt_favs.setVisibility(View.VISIBLE);
            txt_favs.setText(favList.size() + " Favourites");

            txt_favcollection.setVisibility(View.VISIBLE);
            favViewAll.setVisibility(View.VISIBLE);
            rv_2.setVisibility(View.VISIBLE);
        }
        if (bucketimagesDataLocationHashMap == null || bucketimagesDataLocationHashMap.isEmpty()) {
            txt_places.setText("0 Place");
            txt_places.setVisibility(View.GONE);
            txt_lcollection.setVisibility(View.GONE);
            lViewAll.setVisibility(View.GONE);
            rv_1.setVisibility(View.GONE);

            if (favList.size() == 0) {
                rl_empty.setVisibility(View.VISIBLE);
                card_view.setVisibility(View.GONE);
                
            } else {
                rl_empty.setVisibility(View.GONE);
                card_view.setVisibility(View.VISIBLE);

            }

        } else {
            txt_lcollection.setVisibility(View.VISIBLE);
            lViewAll.setVisibility(View.VISIBLE);
            rv_1.setVisibility(View.VISIBLE);
            txt_places.setVisibility(View.VISIBLE);
            txt_places.setText(bucketimagesDataLocationHashMap.size() + " Places");
            card_view.setVisibility(View.VISIBLE);

        }
        Log.d("favlist", favList.toString());
        SecondLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_2.setLayoutManager(SecondLayoutManager);
        Log.d("favlistba", favList.toString());
        SecondRecyclerViewAdapter = new ExploreFavRVAdapter(getActivity(), favList);

        rv_2.setAdapter(SecondRecyclerViewAdapter);
        rv_2.setHasFixedSize(false);

    }


    /*@Override
    public void onResume() {
        text_layout = view.findViewById(R.id.text_layout);

        TextView txt_places = text_layout.findViewById(R.id.txt_places);
        TextView txt_favs = text_layout.findViewById(R.id.txt_favs);
        if (bucketimagesDataLocationHashMap.keySet().size() == 1)
            txt_places.setText(bucketimagesDataLocationHashMap.keySet().size() + " Place");
        else
            txt_places.setText(bucketimagesDataLocationHashMap.keySet().size() + " Places");


        ArrayList<PhotoData> favList = new ArrayList<PhotoData>();
        favList.clear();
        for (int i = 0; i < Constant.displayImageList.size(); i++) {

            if (Constant.displayImageList.get(i).isFavorite())
                favList.add(Constant.displayImageList.get(i));
        }
        if (favList.size() == 0)
            txt_favs.setText("0 Favourite");
        else
            txt_favs.setText(favList.size() + " Favourites");
        super.onResume();
    }*/
    /*private void displayDeleteEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(DisplayDeleteEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<DisplayDeleteEvent>() {
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
    }*/
/*
    private void updateDeleteImageData(ArrayList<String> deleteList) {
        if (photoList != null && photoList.size() != 0) {

            updateMainList(deleteList, new ArrayList<>());
        }
    }

    public void updateMainList(ArrayList<String> list, ArrayList<String> dateList) {

        if (list != null && list.size() != 0 && dateList != null && dateList.size() != 0) {
            for (int l = 0; l < dateList.size(); l++) {

                String strDate = dateList.get(l);

                if (strDate != null && !strDate.equalsIgnoreCase(""))
                    if (bucketimagesDataPhotoHashMap.containsKey(strDate)) {
                        ArrayList<PhotoData> imagesData1 = bucketimagesDataPhotoHashMap.get(strDate);
                        if (imagesData1 == null)
                            imagesData1 = new ArrayList<>();


                        for (int i = 0; i < imagesData1.size(); i++) {
                            try {
                                if (imagesData1.get(i).getFilePath().equalsIgnoreCase(list.get(l))) {
                                    imagesData1.remove(i);
                                    break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (imagesData1 != null && imagesData1.size() != 0) {
                            bucketimagesDataPhotoHashMap.put(strDate, imagesData1);
                        } else {
                            bucketimagesDataPhotoHashMap.remove(strDate);
                        }

                    }

                if (bucketimagesDataLocationHashMap.containsKey(strDate)) {
                    ArrayList<PhotoData> imagesData1 = bucketimagesDataLocationHashMap.get(strDate);
                    if (imagesData1 == null)
                        imagesData1 = new ArrayList<>();


                    for (int i = 0; i < imagesData1.size(); i++) {
                        try {
                            if (imagesData1.get(i).getFilePath().equalsIgnoreCase(list.get(l))) {
                                imagesData1.remove(i);
                                break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    if (imagesData1 != null && imagesData1.size() != 0) {
                        bucketimagesDataLocationHashMap.put(strDate, imagesData1);
                    } else {
                        bucketimagesDataLocationHashMap.remove(strDate);
                    }

                }

            }
        } else {

            Set<String> keys = bucketimagesDataPhotoHashMap.keySet();
            ArrayList<String> listkeys = new ArrayList<>();
            listkeys.addAll(keys);

            for (int l = 0; l < list.size(); l++) {


                for (int b = 0; b < listkeys.size(); b++) {
                    ArrayList<PhotoData> imagesData = bucketimagesDataPhotoHashMap.get(listkeys.get(b));

                    String strDate = listkeys.get(b);
                    if (imagesData != null && imagesData.size() != 0) {

                        for (int i = 0; i < imagesData.size(); i++) {
                            try {
                                if (imagesData.get(i).getFilePath().equalsIgnoreCase(list.get(l))) {
                                    imagesData.remove(i);
                                    break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (imagesData != null && imagesData.size() != 0) {
                            bucketimagesDataPhotoHashMap.put(strDate, imagesData);
                        } else {
                            bucketimagesDataPhotoHashMap.remove(strDate);
                        }

                    }
                }
                Set<String> keys1 = bucketimagesDataLocationHashMap.keySet();
                ArrayList<String> listkeys1 = new ArrayList<>();
                listkeys1.addAll(keys1);
                for (int b = 0; b < listkeys1.size(); b++) {
                    ArrayList<PhotoData> imagesData1 = bucketimagesDataLocationHashMap.get(listkeys1.get(b));

                    String strDate = listkeys1.get(b);
                    if (imagesData1 != null && imagesData1.size() != 0) {

                        for (int i = 0; i < imagesData1.size(); i++) {
                            try {
                                if (imagesData1.get(i).getFilePath().equalsIgnoreCase(list.get(l))) {
                                    imagesData1.remove(i);
                                    break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (imagesData1 != null && imagesData1.size() != 0) {
                            bucketimagesDataLocationHashMap.put(strDate, imagesData1);
                        } else {
                            bucketimagesDataLocationHashMap.remove(strDate);
                        }

                    }
                }
            }


        }

    }*/

    private void displayFavoriteEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(DisplayFavoriteEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<DisplayFavoriteEvent>() {
            @Override
            public void call(DisplayFavoriteEvent event) {

                if (event.getFilePath() != null && !event.getFilePath().equalsIgnoreCase("")) {

                    setUpdateFavourite(event.isFavorite(), event.getFilePath());

                } else {
                    favList = PreferencesManager.getFavouriteList(getActivity());
                    SecondRecyclerViewAdapter.notifyDataSetChanged();
                    setAdapter();
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

        favList = PreferencesManager.getFavouriteList(getActivity());
        SecondRecyclerViewAdapter.notifyDataSetChanged();
        setAdapter();

    }

    private void displayDeleteEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(DisplayDeleteEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<DisplayDeleteEvent>() {
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
        if (bucketimagesDataLocationHashMap != null && bucketimagesDataLocationHashMap.size() != 0) {


            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updateMainList(deleteList, new ArrayList<>());
                }
            };
            new Thread(runnable).start();
        }
    }

    public void updateMainList(ArrayList<String> list, ArrayList<String> dateList) {

        if (list != null && list.size() != 0 && dateList != null && dateList.size() != 0) {
            for (int l = 0; l < dateList.size(); l++) {

                favList = PreferencesManager.getFavouriteList(getActivity());

                if (favList == null) {
                    favList = new ArrayList<>();
                }

                for (int f = 0; f < favList.size(); f++) {

                    if (favList.get(f) != null && !favList.get(f).equalsIgnoreCase("")) {

                        if (favList.get(f).equalsIgnoreCase(dateList.get(l))) {
                            favList.remove(f);
                            Log.d("deletefromfav", favList.toString());
                            break;
                        }

                    }
                }

                String strDate = getLocation(dateList.get(l)).get(0).toString();

                if (strDate != null && !strDate.equalsIgnoreCase(""))
                    if (bucketimagesDataLocationHashMap.containsKey(strDate)) {
                        ArrayList<PhotoData> imagesData1 = bucketimagesDataLocationHashMap.get(strDate);
                        if (imagesData1 == null)
                            imagesData1 = new ArrayList<>();


                        for (int i = 0; i < imagesData1.size(); i++) {
                            try {
                                if (imagesData1.get(i).getFilePath().equalsIgnoreCase(list.get(l))) {
                                    imagesData1.remove(i);
                                    try {
                                        locationDao.delete(locationDao.findByPath(list.get(l)));
                                    } catch (Exception e){
                                        Log.e("ECSGDHSFDYSG", e.getMessage() + "::");
                                        e.printStackTrace();
                                    }

                                    for(int ih =0; ih<photoDataArrayList.size(); ih++){
                                        if(photoDataArrayList.get(ih).getFilePath().equals(list.get(l)))
                                            photoDataArrayList.remove(ih);
                                    }

                                    break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (imagesData1 != null && imagesData1.size() != 0) {
                            bucketimagesDataLocationHashMap.put(strDate, imagesData1);
                        } else {
                            bucketimagesDataLocationHashMap.remove(strDate);
                        }

                    }

            }
        } else {


            for (int l = 0; l < list.size(); l++) {
                for(int ih =0; ih<photoDataArrayList.size(); ih++){
                    if(photoDataArrayList.get(ih).getFilePath().equals(list.get(l)))
                        photoDataArrayList.remove(ih);
                }
                try {
                    locationDao.delete(locationDao.findByPath(list.get(l)));
                } catch (Exception e){
                    Log.e("ECSGDHSFDYSG", e.getMessage() + "::");
                    e.printStackTrace();
                }


                favList = PreferencesManager.getFavouriteList(getActivity());

                if (favList == null) {
                    favList = new ArrayList<>();
                }

                for (int f = 0; f < favList.size(); f++) {

                    if (favList.get(f) != null && !favList.get(f).equalsIgnoreCase("")) {

                        if (favList.get(f).equalsIgnoreCase(list.get(l))) {
                            favList.remove(f);
                            Log.d("deletefromfav", favList.toString());
                            break;
                        }

                    }

                }
                Set<String> keys = bucketimagesDataLocationHashMap.keySet();
                ArrayList<String> listkeys = new ArrayList<>();
                listkeys.addAll(keys);

                for (int b = 0; b < listkeys.size(); b++) {
                    ArrayList<PhotoData> imagesData1 = bucketimagesDataLocationHashMap.get(listkeys.get(b));

                    String strDate = listkeys.get(b);
                    if (imagesData1 != null && imagesData1.size() != 0) {

                        for (int i = 0; i < imagesData1.size(); i++) {
                            try {
                                if (imagesData1.get(i).getFilePath().equalsIgnoreCase(list.get(l))) {
                                    imagesData1.remove(i);

                                    break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (imagesData1 != null && imagesData1.size() != 0) {
                            bucketimagesDataLocationHashMap.put(strDate, imagesData1);
                        } else {
                            bucketimagesDataLocationHashMap.remove(strDate);
                        }

                    }
                }
            }


        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                favList = PreferencesManager.getFavouriteList(getActivity());
                FirstRecyclerViewAdapter.notifyDataSetChanged();
                SecondRecyclerViewAdapter.notifyDataSetChanged();
                setAdapter();

            }
        });


    }


    private List<String> getLocation(String filePath) {
        ExifInterface ei = null;
        if(filePath==null)
            return null;
        try {
            ei = new ExifInterface(filePath);
        } catch (IOException e) {
            Log.e("ECSGDHSFDYSG", e.getMessage() + "::");
            e.printStackTrace();
        }

        try {
            List<String> dataList = new ArrayList<>();
            String LATITUDE = ei.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String LATITUDE_REF = ei.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String LONGITUDE = ei.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String LONGITUDE_REF = ei.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            Float Latitude = null, Longitude = null;
            if (( LATITUDE != null ) && ( LATITUDE_REF != null ) && ( LONGITUDE != null ) && ( LONGITUDE_REF != null )) {
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

                    dataList.add(getAddress(Latitude, Longitude, getActivity()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                /*dataList.add(Latitude);
                dataList.add(Longitude);
                Log.d("datalist", dataList.toString());*/
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
        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

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

        return city == null ? "ab" : concat;//*addresses*//*;

    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + ( FloatM / 60 ) + ( FloatS / 3600 ));

        return result;


    }
}