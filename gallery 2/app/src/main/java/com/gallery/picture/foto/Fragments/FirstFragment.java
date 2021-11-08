package com.gallery.picture.foto.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.picture.foto.Adapters.PhotoAdapter;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.Model.PhotoHeader;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ad.AdEventListener;
import com.gallery.picture.foto.ad.AdmobAdManager;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.event.RenameEvent;
import com.gallery.picture.foto.service.ImageDataService;
import com.gallery.picture.foto.ui.DisplayImageActivity;
import com.gallery.picture.foto.ui.SettingsActivity;
import com.gallery.picture.foto.ui.StorageActivity;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.gallery.picture.foto.utils.StorageUtils;
import com.gallery.picture.foto.utils.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class FirstFragment extends Fragment {


Context thiscontext;
    TextView camerasize, txt_select, txt_text_send, txt_text_copy, txt_text_move, txt_text_delete, txt_text_more, txt_text_compress;

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView more_vert1, iv_check_all,img_send, img_copy, img_move, img_delete, img_more, img_compress, iv_fav_unfill, iv_fav_fill, iv_uncheck;
    GridLayoutManager gridLayoutManager;
    RelativeLayout lout_selected, ll_favourite,ll_check_all, ll_banner;
    LinearLayout ll_bottom_option, lout_send, lout_copy, lout_move, lout_delete, lout_more, lout_compress;
    ImageView iv_close;
    public static List<Object> photoList = new ArrayList<>();

    LinkedHashMap<String, ArrayList<PhotoData>> bucketimagesDataPhotoHashMap;
    public static ArrayList<PhotoData> folderListArray = new ArrayList<PhotoData>();

    PhotoAdapter adapter;
    View view;
    RecyclerView recyclerview;
    int selected_Item = 0;
    boolean isCheckAll = false;
    boolean isFileFromSdCard = false;
    int pos = 0;
    int sdCardPermissionType = 0;
    String sdCardPath;
    String rootPath, compressPath, extractPath;

    int firstTime = 0;

    public static int totalView = Constant.displayImageList.size();
    AdmobAdManager admobAdManager;


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.first_fragment, container, false);



        thiscontext = container.getContext();
        recyclerview = view.findViewById(R.id.recyclerview);
        lout_selected = view.findViewById(R.id.lout_selected);


        ll_banner = view.findViewById(R.id.ll_banner);
        ll_bottom_option = view.findViewById(R.id.ll_bottom_option);
        txt_select = view.findViewById(R.id.txt_select);
        iv_check_all = view.findViewById(R.id.iv_check_all);
        txt_text_send= view.findViewById(R.id.txt_text_send);
        txt_text_copy  = view.findViewById(R.id.txt_text_copy);
        txt_text_move  = view.findViewById(R.id.txt_text_move);
        txt_text_delete  = view.findViewById(R.id.txt_text_delete);
        txt_text_more   = view.findViewById(R.id.txt_text_more);
        img_send    = view.findViewById(R.id.img_send);
        img_copy   = view.findViewById(R.id.img_copy);
        img_move   = view.findViewById(R.id.img_move);
        img_delete        = view.findViewById(R.id.img_delete);
        img_more   = view.findViewById(R.id.img_more);
        lout_send   = view.findViewById(R.id.lout_send);
        lout_copy       = view.findViewById(R.id.lout_copy);
        lout_move     = view.findViewById(R.id.lout_move);
        lout_delete     = view.findViewById(R.id.lout_delete);
        lout_more = view.findViewById(R.id.lout_more);
        lout_compress = view.findViewById(R.id.lout_compress);
        img_compress = view.findViewById(R.id.img_compress);
        txt_text_compress = view.findViewById(R.id.txt_text_compress);
        iv_close = view.findViewById(R.id.iv_close);
        ll_favourite = view.findViewById(R.id.ll_favourite);
        iv_fav_unfill      = view.findViewById(R.id.iv_fav_unfill);
        iv_fav_fill   = view.findViewById(R.id.iv_fav_fill);
        ll_check_all = view.findViewById(R.id.ll_check_all);
        iv_uncheck = view.findViewById(R.id.iv_uncheck);

        ll_favourite = view.findViewById(R.id.ll_favourite);

        admobAdManager = AdmobAdManager.getInstance(getActivity());

            admobAdManager.loadInterstitialAd(getActivity(), getResources().getString(R.string.interstitial_id), 1, new AdmobAdManager.OnAdClosedListener() {
                @Override
                public void onAdClosed(Boolean isShowADs) {

                }
            });


        new Thread(FirstFragment.this::getImageData).start();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }

        rootPath = file.getPath();

        File file2 = new File(file.getPath() + "/" + getString(R.string.compress_file));
        if (!file2.exists()) {
            file2.mkdirs();
        }


        File file3 = new File(file.getPath() + "/" + getString(R.string.extract_file));
        if (!file2.exists()) {
            file2.mkdirs();
        }

        compressPath = file2.getPath();
        extractPath = file3.getPath();


        sdCardPath = Utils.getExternalStoragePath(getActivity(), true);

        displayDeleteEvent();
        displayFavoriteEvent();
        copyMoveEvent();
        camerasize = view.findViewById(R.id.camerasize);

        //toolbar=  view.findViewById(R.id.toolbar);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        more_vert1 = view.findViewById(R.id.more_vert1);
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


        collapsingToolbarLayout.setTitle("Gallery");
     iv_close.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             setSelectionClose();
         }
     });
     ll_check_all.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if (isCheckAll) {
                 isCheckAll = false;
                 selectEvent(false);
                 iv_check_all.setVisibility(View.GONE);

                 lout_selected.setVisibility(View.GONE);
                // loutToolbar.setVisibility(View.VISIBLE);
             } else {
                 isCheckAll = true;
                 selectEvent(true);
                iv_check_all.setVisibility(View.VISIBLE);
             }
         }
     });

     ll_favourite.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if (selected_Item != 0) {
                 if (iv_fav_fill.getVisibility() == View.VISIBLE) {
                     setUnFavourite();
                 } else {
                     setFavourite();
                 }
             }
         }
     });
     lout_delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             showDeleteDialog();
         }
     });
     lout_move.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Constant.isCopyData = false;
             setCopyMoveOptinOn();
         }
     });
     lout_more.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             showMoreOptionBottom();
         }
     });
     lout_copy.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Constant.isCopyData = true;
             setCopyMoveOptinOn();
         }
     });

        Log.d("resourceid", getResources().getResourceName(Integer.parseInt("7f0800a3", 16)));


        return view;
    }

    public void loadBanner() {

        if (photoList != null && photoList.size() > 10)
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

    private void showMoreOptionBottom() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(getActivity(), lout_more);
        popup.getMenuInflater().inflate(R.menu.storage_more_menu, popup.getMenu());

        if (selected_Item == 1) {
            popup.getMenu().findItem(R.id.menu_rename).setVisible(true);
            popup.getMenu().findItem(R.id.menu_details).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.menu_rename).setVisible(false);
            popup.getMenu().findItem(R.id.menu_details).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_rename:
                        showRenameDialog();
                        break;

                    case R.id.menu_details:
                        showDetailDialog();
                        break;
                   /* case R.id.menu_compress:
                        showCompressDialog();
                        break;*/
                    case R.id.menu_share:
                        shareFile();
                        break;
                }
                return false;
            }
        });
        popup.show();

    }

    private void setCopyMoveOptinOn() {
        if (photoList != null) {
            Constant.isFileFromSdCard = isFileFromSdCard;
            Constant.pastList = new ArrayList<>();

            boolean isSdcardPath = false;

            for (int i = 0; i < photoList.size(); i++) {

                if (photoList.get(i) != null)
                    if (photoList.get(i) instanceof PhotoData) {

                        PhotoData model = (PhotoData) photoList.get(i);
                        if (model.isSelected()) {
                            File file = new File(model.getFilePath());
                            if (file.exists()) {
                                Constant.pastList.add(file);
                                if (!isSdcardPath) {
                                    if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
                                        if (file.getPath().contains(sdCardPath)) {
                                            isSdcardPath = true;
                                        }
                                    }
                                }

                            }

                        }

                    }
            }


            setSelectionClose();

            Intent intent = new Intent(getActivity(), StorageActivity.class);
            intent.putExtra("type", "CopyMove");
            startActivity(intent);
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure do you want to delete it?");
        builder.setCancelable(true);
        builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (isFileFromSdCard) {
                    sdCardPermissionType = 1;
                    File file = new File(sdCardPath);
                    int mode = StorageUtils.checkFSDCardPermission(file, getActivity());
                    if (mode == 2) {
                        Toast.makeText(getActivity(), "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                    } else {
                        setDeleteFile();
                    }
                } else {
                    setDeleteFile();
                }

            }
        });

        builder.setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void setDeleteFile() {

        new Thread(this::deleteFile).start();
    }


    public void deleteFile() {
        if (photoList != null) {
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> dateList = new ArrayList<>();
            for (int i = 0; i < photoList.size(); i++) {

                if (photoList.get(i) != null)
                    if (photoList.get(i) instanceof PhotoData) {

                        PhotoData model = (PhotoData) photoList.get(i);
                        if (model.isSelected()) {

                            File file = new File(model.getFilePath());

                            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                            String strDate = "";
                            strDate = format.format(file.lastModified());

                            boolean isDelete = StorageUtils.deleteFile(file, getActivity());

                            if (isDelete) {
                                list.add(model.getFilePath());
                                dateList.add(strDate);
                                MediaScannerConnection.scanFile(getActivity(), new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                                    }
                                });
                            }

                        } else {

                            model.setCheckboxVisible(false);
                        }

                    }
            }


            for (int i = 0; i < photoList.size(); i++) {
                if (photoList.get(i) != null)

                    if (photoList.get(i) instanceof PhotoData) {
                        PhotoData model = (PhotoData) photoList.get(i);
                        if (model.isSelected()) {

                            boolean isPre = false, isNext = false;

                            if (i != 0) {
                                if (photoList.get(i - 1) instanceof PhotoHeader) {
                                    isPre = true;
                                } else {
                                    isPre = false;
                                }
                            }

                            if (i < (photoList.size() - 2)) {
                                if (photoList.get(i + 1) instanceof PhotoHeader) {
                                    isNext = true;
                                } else {
                                    isNext = false;
                                }
                            }

                            if (isPre && isNext) {
                                //  objectList.remove(i + 1);
                                photoList.remove(i);
                                photoList.remove(i - 1);

                            } else if (i == (photoList.size() - 1)) {
                                if (isPre) {
                                    photoList.remove(i);
                                    photoList.remove(i - 1);
                                } else {
                                    photoList.remove(i);
                                }
                            } else {
                                photoList.remove(i);
                            }

                            if (i != 0) {
                                i--;
                            }
                        }
                    }
            }

            updateMainList(list, dateList);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    selected_Item = 0;
                    OnSelected(true, false, 0);
                    adapter.notifyDataSetChanged();
                    RxBus.getInstance().post(new DisplayDeleteEvent(list));
                    Toast.makeText(getActivity(), "Delete image successfully", Toast.LENGTH_SHORT).show();
                }
            });

        }
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
        if (photoList != null && photoList.size() != 0) {
            for (int i = 0; i < photoList.size(); i++) {

                if (photoList.get(i) instanceof PhotoData) {
                    PhotoData model = (PhotoData) photoList.get(i);

                    if (model.getFilePath() != null) {
                        if (model.getFilePath().equalsIgnoreCase(filePath)) {
                            model.setFavorite(favorite);
                            if (adapter != null) {
                                adapter.notifyItemChanged(i);
                            }
                            break;
                        }
                    }
                }
            }

        }



    }

    public void getImageData() {
        photoList = new ArrayList<>();
        bucketimagesDataPhotoHashMap = new LinkedHashMap<>();


        while (true) {
            if (ImageDataService.isComplete) {
                Log.e("threadComplete1", "thread");
                Log.d("servicetime", String.valueOf(Calendar.getInstance().getTime()));
                break;
            }

        }

        if(getActivity() == null)
            return;
        Log.e("getActivity", getActivity().toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photoList = new ArrayList<>();
                bucketimagesDataPhotoHashMap.clear();
                bucketimagesDataPhotoHashMap = ImageDataService.bucketimagesDataPhotoHashMap;


                setSortData();


                setAdapter();
                loadBanner();

                camerasize.post(new Runnable() {

                    @Override
                    public void run() {
                        totalView = Constant.displayImageList.size();
                        camerasize.setText("Total view size : " + totalView + "");
                    }
                });

            }
        });

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
        if (photoList != null && photoList.size() != 0) {


            for (int d = 0; d < deleteList.size(); d++) {

                for (int i = 0; i < photoList.size(); i++) {

                    if (photoList.get(i) instanceof PhotoData) {
                        PhotoData model = (PhotoData) photoList.get(i);

                        if (model.getFilePath().equalsIgnoreCase(deleteList.get(d))) {

                            boolean isPre = false, isNext = false;

                            if (i != 0) {
                                if (photoList.get(i - 1) instanceof PhotoHeader) {
                                    isPre = true;
                                } else {
                                    isPre = false;
                                }
                            }

                            if (i < (photoList.size() - 2)) {
                                if (photoList.get(i + 1) instanceof PhotoHeader) {
                                    isNext = true;
                                } else {
                                    isNext = false;
                                }
                            }

                            if (isPre && isNext) {
                                //  objectList.remove(i + 1);
                                photoList.remove(i);
                                photoList.remove(i - 1);

                            } else if (i == (photoList.size() - 1)) {
                                if (isPre) {
                                    photoList.remove(i);
                                    photoList.remove(i - 1);
                                } else {
                                    photoList.remove(i);
                                }
                            } else {
                                photoList.remove(i);
                            }

                            if (i != 0) {
                                i--;
                            }
                            if(!model.getFilePath().equalsIgnoreCase(deleteList.get(d)))

                                totalView--;
                            camerasize.setText("Total view size : " + Integer.toString(totalView) + "");

                            if (d == deleteList.size() - 1) {
                                break;
                            }

                        }

                    }

                }
            }


            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }


            if (photoList != null && photoList.size() != 0) {
                recyclerview.setVisibility(View.VISIBLE);

            } else {
                recyclerview.setVisibility(View.GONE);

            }

            updateMainList(deleteList, new ArrayList<>());
        }
    }

    public void updateMainList(ArrayList<String> list, ArrayList<String> dateList) {

        if (list != null && list.size() != 0 && dateList != null && dateList.size() != 0) {


            for (int l = 0; l < dateList.size(); l++) {

                ArrayList<String> favList = PreferencesManager.getFavouriteList(getActivity());

                if (favList == null) {
                    favList = new ArrayList<>();
                }

                for (int f = 0; f < favList.size(); f++) {

                    if (favList.get(f) != null && !favList.get(f).equalsIgnoreCase("")) {

                        if (favList.get(f).equalsIgnoreCase(dateList.get(l))) {
                            favList.remove(f);
                            break;
                        }

                    }
                }

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



            }
        } else {

            Set<String> keys = bucketimagesDataPhotoHashMap.keySet();
            ArrayList<String> listkeys = new ArrayList<>();
            listkeys.addAll(keys);

            for (int l = 0; l < list.size(); l++) {

                ArrayList<String> favList = PreferencesManager.getFavouriteList(getActivity());

                if (favList == null) {
                    favList = new ArrayList<>();
                }

                for (int f = 0; f < favList.size(); f++) {

                    if (favList.get(f) != null && !favList.get(f).equalsIgnoreCase("")) {

                        if (favList.get(f).equalsIgnoreCase(list.get(l))) {
                            favList.remove(f);
                            break;
                        }

                    }
                }
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

            }


        }

    }

        private void copyMoveEvent() {
        Subscription subscription = RxBus.getInstance().toObservable(CopyMoveEvent.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(new Action1<CopyMoveEvent>() {
            @Override
            public void call(CopyMoveEvent event) {
                if (event.getCopyMoveList() != null && event.getCopyMoveList().size() != 0 && event.getType() != 3) {

                    ArrayList<File> imageList = new ArrayList<>();
                    imageList = event.getCopyMoveList();

                    if (imageList == null) {
                        imageList = new ArrayList<>();
                    }

                    SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
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

                                imagesData.setSize(file1.length());

                                /*try {
*/                                    imagesData.setDate(strFileDate);
                               /* } catch (ParseException e) {
                                    e.printStackTrace();
                                }*/

                                imagesData1.add(imagesData);
                            }
                    }

                    if (imagesData1 != null && imagesData1.size() != 0) {

                        if (bucketimagesDataPhotoHashMap.containsKey(strDate)) {
                            ArrayList<PhotoData> imagesData2 = bucketimagesDataPhotoHashMap.get(strDate);
                            if (imagesData2 == null)
                                imagesData2 = new ArrayList<>();

                            imagesData2.addAll(imagesData1);
                            totalView++;
                            camerasize.setText("Total view size : " + Integer.toString( totalView + 1)+ "");
                            ArrayList<PhotoData> imagesSortData = setDateWiseSortAs(false, imagesData2);


                            bucketimagesDataPhotoHashMap.put(strDate, imagesSortData);

                        } else {
                            ArrayList<PhotoData> imagesData2 = new ArrayList<>();
                            imagesData2.addAll(imagesData1);
                            totalView++;
                            camerasize.setText("Total view size : " + Integer.toString(totalView + 1)+ "");
                            ArrayList<PhotoData> imagesSortData = new ArrayList<>();
                            if (imagesData2.size() != 0 && imagesData2.size() != 1) {
                                imagesSortData = setDateWiseSortAs(false, imagesData2);
                            } else {
                                imagesSortData.addAll(imagesData2);
                            }
                            try {

                                LinkedHashMap<String, ArrayList<PhotoData>> map = new LinkedHashMap<>();
                                map.put(strDate, imagesSortData);
                                map.putAll(bucketimagesDataPhotoHashMap);
                                bucketimagesDataPhotoHashMap = new LinkedHashMap<>();

                                bucketimagesDataPhotoHashMap.putAll(map);
//                                bucketimagesDataPhotoHashMap.put(strDate, imagesSortData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            bucketimagesDataPhotoHashMap.put(strDate, imagesSortData);
                        }

                        // sort
                        // data set
///////////
                      /*  for (int i = 0; i < photoList.size(); i++) {

                            if (photoList.get(i) != null)
                                if (photoList.get(i) instanceof PhotoData) {

                                    PhotoData model = (PhotoData) photoList.get(i);
                                    model.setCheckboxVisible(false);
                                    model.setSelected(false);

                                }

                        }*/


                        photoList.clear();
                        Set<String> keys = bucketimagesDataPhotoHashMap.keySet();
                        ArrayList<String> listkeys = new ArrayList<>();
                        listkeys.addAll(keys);


                        for (int i = 0; i < listkeys.size(); i++) {
                            ArrayList<PhotoData> imagesData = bucketimagesDataPhotoHashMap.get(listkeys.get(i));

                            if (imagesData != null && imagesData.size() != 0) {
                                PhotoHeader bucketData = new PhotoHeader();

                                bucketData.setTitle(listkeys.get(i));
                                bucketData.setPhotoList(imagesData);

                                photoList.add(bucketData);
                                photoList.addAll(imagesData);

                            }
                        }

//                        if (adapter != null) {
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            setAdapter();
//                        }

                        setAdapter();



                    }

                }

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
                    dataList.add(getAddress(Latitude, Longitude, getActivity()));
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

    private Object getAddress(Float Latitude, Float Longitude, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()*//*
        String city = addresses.get(0).getLocality();
        //String state = addresses.get(0).getAdminArea();
        //String country = addresses.get(0).getCountryName();
        //String postalCode = addresses.get(0).getPostalCode();
        //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*//*
        return city;//*addresses*//*;

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

    private void setSortData() {
        /*List<PhotoData> dataList1 = new ArrayList<>();
        for (int i = 0; i < photoList.size(); i++) {
            if (photoList.get(i) instanceof PhotoData) {
                //((PhotoData) photoList.get(i)).setLocationData(getLocation(((PhotoData) photoList.get(i)).getFilePath()));
                dataList1.add((PhotoData) photoList.get(i));
            }
        }*/
        Constant.displayImageList = new ArrayList<>();
        //Constant.displayImageList.addAll(dataList1);

        Set<String> keys = bucketimagesDataPhotoHashMap.keySet();
        ArrayList<String> listkeys = new ArrayList<>();
        listkeys.addAll(keys);

        photoList.clear();

        for (int i = 0; i < listkeys.size(); i++) {
            ArrayList<PhotoData> imagesData = bucketimagesDataPhotoHashMap.get(listkeys.get(i));

            if (imagesData != null && imagesData.size() != 0) {
                PhotoHeader bucketData = new PhotoHeader();

                bucketData.setTitle(listkeys.get(i));


                if (imagesData.size() != 0) {
                    bucketData.setPhotoList(imagesData);

                    photoList.add(bucketData);
                    photoList.addAll(imagesData);

                    Constant.displayImageList.addAll(imagesData);
                }

            }
        }


        if (adapter != null) {
            adapter.notifyDataSetChanged();

        }
    }


    private void setAdapter() {


        if (photoList != null && photoList.size() != 0) {
            recyclerview.setVisibility(View.VISIBLE);


            gridLayoutManager = new GridLayoutManager(getActivity(), 4, LinearLayoutManager.VERTICAL, false);
            recyclerview.setLayoutManager(gridLayoutManager);
            adapter = new PhotoAdapter(thiscontext, photoList);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(final int position) {
                    if (adapter.getItemViewType(position) == PhotoAdapter.ITEM_HEADER_TYPE) {
                        return 4;
                    }
                    return 1;
                }
            });
//            recyclerView.setItemViewCacheSize(15);
            recyclerview.setAdapter(adapter);
            recyclerview.hasFixedSize();



//            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(new PhotoAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {

                    try {

                        if (photoList.get(position) instanceof PhotoData) {

                            PhotoData imageList = (PhotoData) photoList.get(position);
                            if (imageList.isCheckboxVisible()) {

                                if (imageList.isSelected()) {
                                    imageList.setSelected(false);
                                } else
                                    imageList.setSelected(true);
                                adapter.notifyItemChanged(position);
                                setSelectedFile();

                            } else {

                                int pos = -1;
                                List<PhotoData> dataList = new ArrayList<>();

                                for (int i = 0; i < photoList.size(); i++) {
                                    if (photoList.get(i) instanceof PhotoData) {
                                        dataList.add((PhotoData) photoList.get(i));
                                        if (position == i) {
                                            pos = dataList.size() - 1;
                                        }
                                    }
                                }

                                Constant.displayImageList = new ArrayList<>();
                                Constant.displayImageList.addAll(dataList);
                                Intent intent = new Intent(getActivity(), DisplayImageActivity.class);
                                intent.putExtra("index", -2);
                                intent.putExtra("position", pos);
                                intent.putExtra("photoListPosition", position);
                                startActivity(intent);

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });



            adapter.setOnLongClickListener(new PhotoAdapter.LongClickListener() {
                @Override
                public void onItemLongClick(int position, View v) {
                    try {

                        if (photoList.get(position) instanceof PhotoData) {
                            PhotoData imageList = (PhotoData) photoList.get(position);

                            for (int i = 0; i < photoList.size(); i++) {
                                if (photoList.get(i) != null)

                                    if (photoList.get(i) instanceof PhotoData) {

                                        PhotoData model = (PhotoData) photoList.get(i);
                                        model.setCheckboxVisible(true);

                                    }

                            }
                            imageList.setCheckboxVisible(true);
                            imageList.setSelected(true);

                            adapter.notifyDataSetChanged();
                            setSelectedFile();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            recyclerview.setVisibility(View.GONE);


        }
    }

    private void setSelectedFile() {
        int selected = 0;

        boolean isFavourite = false, isShowFavOption = false;

        ArrayList<Integer> favSelectList = new ArrayList<>();
        ArrayList<Integer> favUnSelectList = new ArrayList<>();
        boolean isSdcardPath = false;
        for (int i = 0; i < photoList.size(); i++) {

            if (photoList.get(i) != null)
                if (photoList.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(i);
                    if (model.isSelected()) {
                        selected++;
                        pos = i;

                        if (model.isFavorite()) {
                            favSelectList.add(1);
                        } else {
                            favUnSelectList.add(0);
                        }

                        /*if (!isSdcardPath) {
                            if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
                                if (model.getFilePath().contains(sdCardPath)) {
                                    isSdcardPath = true;
                                }
                            }
                        }*/
                    }

                }
        }

//        if (favUnSelectList.size() == 0 && favSelectList.size() != 0) {
//            isFavourite = true;
//        } else if (favUnSelectList.size() != 0) {
//            isFavourite = false;
//        }
        if (favUnSelectList.size() == 0 && favSelectList.size() != 0) {
            isFavourite = true;
            isShowFavOption = true;
        } else if (favUnSelectList.size() != 0 && favSelectList.size() == 0) {
            isFavourite = false;
            isShowFavOption = true;
        } else {
            isShowFavOption = false;
        }


        isFileFromSdCard = isSdcardPath;
        if (selected == 0) {

            setInvisibleButton(lout_send, img_send, txt_text_send);
            setInvisibleButton(lout_move, img_move, txt_text_move);
            setInvisibleButton(lout_delete, img_delete, txt_text_delete);
            setInvisibleButton(lout_copy, img_copy, txt_text_copy);
            setInvisibleButton(lout_more, img_more, txt_text_more);
            setInvisibleButton(lout_compress, img_compress, txt_text_compress);
            if (favUnSelectList.size() == 0 && favSelectList.size() != 0) {
                isFavourite = true;
            } else if (favUnSelectList.size() != 0) {
                isFavourite = false;
            }
            ll_favourite.setVisibility(View.GONE);

        } else {
            setVisibleButton(lout_send, img_send, txt_text_send);
            setVisibleButton(lout_move, img_move, txt_text_move);
            setVisibleButton(lout_delete, img_delete, txt_text_delete);
            setVisibleButton(lout_copy, img_copy, txt_text_copy);
            setVisibleButton(lout_more, img_more, txt_text_more);
            setVisibleButton(lout_compress, img_compress, txt_text_compress);


            if (isShowFavOption) {
                ll_favourite.setVisibility(View.VISIBLE);
                if (isFavourite) {
                    iv_fav_fill.setVisibility(View.VISIBLE);
                    iv_fav_unfill.setVisibility(View.GONE);
                } else {
                    iv_fav_fill.setVisibility(View.GONE);
                    iv_fav_unfill.setVisibility(View.VISIBLE);
                }
            } else {
                ll_favourite.setVisibility(View.GONE);
            }

        }

       /* if (selected == 0) {

            OnSelected(true, false, selected);
            setClose();
        } else {
            OnSelected(false, true, selected);

        }*/

        if (selected == 0) {
            OnSelected(true, false, selected);
            setSelectionClose();
        } else {
            OnSelected(false, true, selected);
        }
//        OnSelected(false, true, selected);
        selected_Item = selected;
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



    @Override
    public void onResume() {

        super.onResume();
        if (lout_selected.getVisibility() == View.VISIBLE) {
            setSelectionClose();
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    if (lout_selected.getVisibility() == View.VISIBLE) {
                        setSelectionClose();
                    } else
                        getActivity().onBackPressed();// handle back button

                    return true;

                }

                return false;
            }
        });
    }


    private void setSelectionClose() {
        setClose();
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        OnSelected(true, false, 0);
    }

    private void setClose() {
        for (int i = 0; i < photoList.size(); i++) {

            if (photoList.get(i) != null)
                if (photoList.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(i);
                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                }

        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
        selected_Item = 0;
    }

    private void OnSelected(boolean isShowToolbar, boolean isShowSelected, int selected) {
        if (isShowToolbar) {

        } else {

        }


        if (isShowSelected) {
            lout_selected.setVisibility(View.VISIBLE);
            ll_bottom_option.setVisibility(View.VISIBLE);
           // recyclerview.setPadding(0, 72, 0, 0);

        } else {
            lout_selected.setVisibility(View.GONE);
            ll_bottom_option.setVisibility(View.GONE);
           // recyclerview.setPadding(0,0,0,0);
        }
        txt_select.setText(selected + " selected"+ "");
    }
    private void setVisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(true);
        button.setEnabled(true);
        /*imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.black));*/
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:

                imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);
                textView.setTextColor(getResources().getColor(R.color.white));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }
    }

    private void setInvisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(false);
        button.setEnabled(false);
       /* imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.invisible_color), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.invisible_color));*/

    }

    private void selectEvent(boolean isSelect) {
        if (isSelect) {

            for (int i = 0; i < photoList.size(); i++) {

                if (photoList.get(i) != null)
                    if (photoList.get(i) instanceof PhotoData) {

                        PhotoData model = (PhotoData) photoList.get(i);
                        model.setSelected(true);

                    }
            }

            adapter.notifyDataSetChanged();
            setSelectedFile();
        } else {

            for (int i = 0; i < photoList.size(); i++) {
                if (photoList.get(i) != null)
                    if (photoList.get(i) instanceof PhotoData) {

                        PhotoData model = (PhotoData) photoList.get(i);
                        model.setSelected(false);
                        model.setCheckboxVisible(false);

                    }
            }

            adapter.notifyDataSetChanged();
            ll_bottom_option.setVisibility(View.GONE);
            selected_Item = 0;
        }
    }

    private void setFavourite() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(getActivity());
        if (favList == null) {
            favList = new ArrayList<>();
        }

        int counter = 0;
        for (int i = 0; i < photoList.size(); i++) {

            if (photoList.get(i) != null)
                if (photoList.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(i);
                    if (model.isSelected()) {
                        if (!model.isFavorite()) {

                            favList.add(0, model.getFilePath());

                            counter++;

                        }
                        model.setFavorite(true);

                    }

                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                }
        }

        if (adapter != null)
            adapter.notifyDataSetChanged();
        selected_Item = 0;
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        OnSelected(true, false, 0);

        String strMsg = "";
        if (counter == 1) {
            strMsg = " item added to Favourites.";
        } else {
            strMsg = " items added to Favourites.";
        }
        Toast.makeText(getActivity(), counter + strMsg, Toast.LENGTH_SHORT).show();

        PreferencesManager.setFavouriteList(getActivity(), favList);
        RxBus.getInstance().post(new DisplayFavoriteEvent(favList, true));
    }

    private void setUnFavourite() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(getActivity());
        if (favList == null) {
            favList = new ArrayList<>();
        }

        int counter = 0;

        for (int i = 0; i < photoList.size(); i++) {

            if (photoList.get(i) != null)
                if (photoList.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(i);
                    if (model.isSelected()) {
                        if (model.isFavorite()) {

                            model.setFavorite(false);
                            counter++;
                            if (favList.contains(model.getFilePath())) {
                                favList.remove(model.getFilePath());
                            }
                        }
                    }
                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
        selected_Item = 0;
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        OnSelected(true, false, 0);

        String strMsg = "";
        if (counter == 1) {
            strMsg = " item removed from Favourites.";
        } else {
            strMsg = " items removed from Favourites.";
        }
        Toast.makeText(getActivity(), counter + strMsg, Toast.LENGTH_SHORT).show();
        PreferencesManager.setFavouriteList(getActivity(), favList);
        RxBus.getInstance().post(new DisplayFavoriteEvent(favList, false));
    }

    private void showRenameDialog() {
        setSelectionClose();
        if (photoList.get(pos) instanceof PhotoData) {

            PhotoData model = (PhotoData) photoList.get(pos);
            File file = new File(model.getFilePath());

            Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_rename);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);

            AppCompatEditText edtFileName;
            LinearLayout btn_cancel, btn_ok;
            edtFileName = dialog.findViewById(R.id.edt_file_name);
            btn_cancel = dialog.findViewById(R.id.btn_cancel);
            btn_ok = dialog.findViewById(R.id.btn_ok);

            edtFileName.setText(file.getName());

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mimeType = Utils.getFilenameExtension(file.getName());

                    if (!edtFileName.getText().toString().isEmpty()) {
                        if (edtFileName.getText().toString().equalsIgnoreCase(file.getName())) {
                            dialog.show();
                        } else if (!file.isDirectory()) {
                            String currentString = edtFileName.getText().toString();
                            String[] separated = currentString.split("\\.");
                            String type = separated[separated.length - 1];

                            if (!type.equalsIgnoreCase(mimeType)) {
                                // show dialog
//                            Log.e("", "show mimeType dialog");
                                Dialog validationDialog = new Dialog(getActivity(), R.style.WideDialog);
                                validationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                validationDialog.setCancelable(true);
                                validationDialog.setContentView(R.layout.dialog_rename_validation);
                                validationDialog.setCanceledOnTouchOutside(true);
                                validationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                validationDialog.getWindow().setGravity(Gravity.CENTER);

                                validationDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        validationDialog.dismiss();
                                        edtFileName.setText(file.getName());

                                    }
                                });

                                validationDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        validationDialog.dismiss();
                                        dialog.dismiss();
                                        // set rename
                                        reNameFile(file, edtFileName.getText().toString());
                                    }
                                });

                                validationDialog.show();

                            } else {
                                // set rename
                                Log.e("", "rename");
                                dialog.dismiss();
                                reNameFile(file, edtFileName.getText().toString());


                            }
                        } else {

                            // set rename
                            dialog.dismiss();
                            reNameFile(file, edtFileName.getText().toString());
                        }

                    } else {
                        Toast.makeText(getActivity(), "New name can't be empty.", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            dialog.show();
        }
    }

    private void reNameFile(File file, String newName) {
        File file2 = new File(file.getParent() + "/" + newName);
        Log.e("1", "file name: " + file.getPath());
        Log.e("2", "file2 name: " + file2.getPath());
        if (file2.exists()) {
            Log.e("rename", "File already exists!");
            showRenameValidationDialog();
        } else {
            boolean renamed = false;

            if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("") && file.getPath().contains(sdCardPath)) {
                renamed = StorageUtils.renameFile(file, newName, getActivity());
            } else {
                renamed = file.renameTo(file2);


            }

            if (renamed) {
                Log.e("LOG", "File renamed...");
                MediaScannerConnection.scanFile(getActivity(), new String[]{file2.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });

                if (photoList.get(pos) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(pos);
                    model.setFilePath(file2.getPath());
                    model.setFileName(file2.getName());
                }

                adapter.notifyItemChanged(pos);
                Toast.makeText(getActivity(), "Rename file successfully", Toast.LENGTH_SHORT).show();
                RxBus.getInstance().post(new RenameEvent(file, file2));
            } else {
                Log.e("LOG", "File not renamed...");
            }
//            storageList.clear();
//            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));

        }
    }

    private void showRenameValidationDialog() {
        Dialog validationDialog = new Dialog(getActivity(), R.style.WideDialog);
        validationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        validationDialog.setCancelable(true);
        validationDialog.setContentView(R.layout.dialog_rename_same_name_validation);
        validationDialog.setCanceledOnTouchOutside(true);
        validationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        validationDialog.getWindow().setGravity(Gravity.CENTER);

        LinearLayout btn_ok;
        btn_ok = validationDialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationDialog.dismiss();
            }
        });

        validationDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDetailDialog() {
        if (photoList.get(pos) instanceof PhotoData) {

            PhotoData model = (PhotoData) photoList.get(pos);

            final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.details_dialog);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);


            File file = new File(model.getFilePath());

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

                txt_file_size.setText(Formatter.formatShortFileSize(getActivity(), file.length()));

                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
                String strDate = format.format(file.lastModified());

                txt_modified.setText(strDate);


                if (file.isDirectory()) {
                    lout_resolution.setVisibility(View.GONE);
                    lout_duration.setVisibility(View.GONE);

                } else {

                    if (type != null && type.contains("image")) {
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
                            lout_resolution.setVisibility(View.VISIBLE);


                        } catch (Exception e) {
                            e.printStackTrace();
                            lout_resolution.setVisibility(View.GONE);
                        }
                    } else if (type != null && type.contains("video")) {

                        try {
                            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                            metaRetriever.setDataSource(file.getPath());
                            int height = Integer.parseInt(Objects.requireNonNull(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
                            int width = Integer.parseInt(Objects.requireNonNull(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));

                            String time = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            long timeInMillisec = Long.parseLong(time);

                            String strDuration = getDurationString((int) timeInMillisec);

                            txt_duration.setText(strDuration);

                            txt_resolution.setText(width + "X" + height);
                            lout_resolution.setVisibility(View.VISIBLE);
                            lout_duration.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            lout_resolution.setVisibility(View.GONE);
                            lout_duration.setVisibility(View.GONE);
                        }
                    }

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
    }

    private String getDurationString(int duration) {

       /* long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        if (hours == 0) {
            return minutes + ":" + seconds;
        } else
            return hours + ":" + minutes + ":" + seconds;*/

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        String strMin = "";
        if (minutes < 10) {
            strMin = "0" + minutes;
        } else {
            strMin = String.valueOf(minutes);
        }

        String strSec = "";
        if (seconds < 10) {
            strSec = "0" + seconds;
        } else {
            strSec = String.valueOf(seconds);
        }

        String strHour = "";
        if (hours < 10) {
            strHour = "0" + hours;
        } else {
            strHour = String.valueOf(hours);
        }

        if (hours == 0) {
            return strMin + ":" + strSec;
        } else
            return strHour + ":" + strMin + ":" + strSec;
    }

    private void shareFile() {
        ArrayList<Uri> uris = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        for (int i = 0; i < photoList.size(); i++) {

            if (photoList.get(i) != null)
                if (photoList.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(i);
                    if (model.isSelected()) {
                        Uri uri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName() + ".provider", new File(model.getFilePath()));
                        uris.add(uri);
                    }

                }
        }

        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "Share with..."));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StorageUtils.REQUEST_SDCARD_WRITE_PERMISSION) {
            String p = PreferencesManager.getSDCardTreeUri(getActivity());
            Uri oldUri = null;
            if (p != null) oldUri = Uri.parse(p);
            Uri treeUri = null;
            if (resultCode == Activity.RESULT_OK) {
                treeUri = data.getData();
                if (treeUri != null) {
                    PreferencesManager.setSDCardTreeUri(getActivity(), treeUri.toString());
                    if (sdCardPermissionType == 1) {
                        setDeleteFile();
                    } else if (sdCardPermissionType == 3) {
                        //setcompress();
                    }
                }
            }
            if (resultCode != Activity.RESULT_OK) {
                if (treeUri != null) {
                    PreferencesManager.setSDCardTreeUri(getActivity(), oldUri.toString());
//                    deleteFile();
                    if (sdCardPermissionType == 1) {
                        setDeleteFile();
                    } else if (sdCardPermissionType == 3) {
                        //setcompress();
                    }
                }
                return;
            }
            try {
                final int takeFlags = data.getFlags()
                        & ( Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getActivity().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
