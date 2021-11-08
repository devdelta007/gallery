package com.gallery.picture.foto.ui;

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
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.gallery.picture.foto.Model.ImageDetail;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.Adapters.NestedRecyclerViewAdapter;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ad.AdmobAdManager;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.event.RenameEvent;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.gallery.picture.foto.utils.StorageUtils;
import com.gallery.picture.foto.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.gallery.picture.foto.Fragments.SecondFragment.folderListArray;
import static com.gallery.picture.foto.service.ImageDataService.bucketimagesDataCollectionHashMap;


public class HiddenCollection extends AppCompatActivity {

    AdmobAdManager admobAdManager;

    RecyclerView RecyclerView;
    RelativeLayout toolbar1;
    ImageView more_vert;
    ImageView backButton;
    TextView collection, collections;
    NestedRecyclerViewAdapter nestedRecyclerViewAdapter;
    int position;

    TextView txt_select, txt_text_send, txt_text_copy, txt_text_move, txt_text_delete, txt_text_more, txt_text_compress;
    ImageView iv_check_all,img_send, img_copy, img_move, img_delete, img_more, img_compress, iv_fav_unfill, iv_fav_fill, iv_uncheck;
    LinearLayout ll_bottom_option, lout_send, lout_copy, lout_move, lout_delete, lout_more, lout_compress;
    RelativeLayout lout_selected, ll_favourite,ll_check_all;
    ImageView iv_close;
    int selected_Item = 0;
    boolean isCheckAll = false;
    boolean isFileFromSdCard = false;
    int pos = 0;

    int sdCardPermissionType = 0;
    String sdCardPath;

    Set<String> keys = new HashSet<>();
    ArrayList<String> listkeys = new ArrayList<>();
    ArrayList<PhotoData> photoData = new ArrayList<>();

    @Override
    public void onBackPressed() {
        if (lout_selected.getVisibility() == View.VISIBLE) {
            setSelectionClose();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_collection);

        Constant.isOpenImage = true;
        admobAdManager = AdmobAdManager.getInstance(HiddenCollection.this);

        if (!Constant.isShowFirstTimeADsOnAlbums) {
            admobAdManager.loadInterstitialAd(HiddenCollection.this, getResources().getString(R.string.interstitial_id), 1, new AdmobAdManager.OnAdClosedListener() {
                @Override
                public void onAdClosed(Boolean isShowADs) {
                    if (!Constant.isShowFirstTimeADsOnAlbums) {
                        Constant.isShowFirstTimeADsOnAlbums = isShowADs;
                    }
                }
            });
        }

        RecyclerView = findViewById(R.id.RecyclerView);
        toolbar1 = findViewById(R.id.toolbar1);
        more_vert = findViewById(R.id.more_vert);
        backButton = findViewById(R.id.backButton);
        collection = findViewById(R.id.collection);
        collections = findViewById(R.id.collections);

        iv_check_all = findViewById(R.id.iv_check_all);
        txt_text_send= findViewById(R.id.txt_text_send);
        txt_text_copy  = findViewById(R.id.txt_text_copy);
        txt_text_move  = findViewById(R.id.txt_text_move);
        txt_text_delete  = findViewById(R.id.txt_text_delete);
        txt_text_more   = findViewById(R.id.txt_text_more);
        img_send    = findViewById(R.id.img_send);
        img_copy   = findViewById(R.id.img_copy);
        img_move   = findViewById(R.id.img_move);
        img_delete        = findViewById(R.id.img_delete);
        img_more   = findViewById(R.id.img_more);
        lout_send   = findViewById(R.id.lout_send);
        lout_copy       = findViewById(R.id.lout_copy);
        lout_move     = findViewById(R.id.lout_move);
        lout_delete     = findViewById(R.id.lout_delete);
        lout_more = findViewById(R.id.lout_more);
        lout_compress = findViewById(R.id.lout_compress);
        img_compress = findViewById(R.id.img_compress);
        txt_text_compress = findViewById(R.id.txt_text_compress);
        iv_close = findViewById(R.id.iv_close);
        ll_favourite = findViewById(R.id.ll_favourite);
        iv_fav_unfill      = findViewById(R.id.iv_fav_unfill);
        iv_fav_fill   = findViewById(R.id.iv_fav_fill);
        ll_check_all = findViewById(R.id.ll_check_all);
        iv_uncheck = findViewById(R.id.iv_uncheck);
        ll_favourite = findViewById(R.id.ll_favourite);
        lout_selected = findViewById(R.id.lout_selected);
        ll_bottom_option = findViewById(R.id.ll_bottom_option);
        txt_select = findViewById(R.id.txt_select);


        keys = folderListArray.keySet();
        listkeys = new ArrayList<String>();
        listkeys.addAll(keys);


        pos = 0;
        for (int i = 0; i < folderListArray.size(); i++) {
            for(int j = 0; j< folderListArray.get(listkeys.get(i)).size(); j++){
                if (folderListArray.get(listkeys.get(i)).get(j) != null)
                    if (folderListArray.get(listkeys.get(i)).get(j) instanceof PhotoData) {

                        PhotoData model = (PhotoData) folderListArray.get(listkeys.get(i)).get(j);
                        model.setCheckboxVisible(false);
                        model.setSelected(false);

                    }
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        LayoutManager layoutManager = new GridLayoutManager(this, 4);
        RecyclerView.setLayoutManager(layoutManager);
        ArrayList<ImageDetail> folderFiles = new ArrayList<ImageDetail>();
        //folderFiles = (ArrayList<ImageDetail>) getIntent().getExtras().get("folderFiles");
        if(nestedRecyclerViewAdapter != null)
        nestedRecyclerViewAdapter.notifyDataSetChanged();

        position =  getIntent().getExtras().getInt("position");

        photoData = folderListArray.get(listkeys.get(position));

        collections.setText(photoData.get(0).getFolderName());
        collection.setText(photoData.size()+ " items");

        nestedRecyclerViewAdapter = new NestedRecyclerViewAdapter(position, this, photoData);
        RecyclerView.setAdapter(nestedRecyclerViewAdapter);


        displayDeleteEvent();
        copyMoveEvent();

        getSupportActionBar().hide();



        nestedRecyclerViewAdapter.setOnItemClickListener(new NestedRecyclerViewAdapter.ClickListener() {
            @Override
            public void onItemClick(int p, View v) {

                 try {

                        if (folderListArray.get(listkeys.get(position)).get(p) instanceof PhotoData) {

                            PhotoData imageList = (PhotoData) folderListArray.get(listkeys.get(position)).get(p);
                            if (imageList.isCheckboxVisible()) {

                                if (imageList.isSelected()) {
                                    imageList.setSelected(false);
                                } else
                                    imageList.setSelected(true);
                                nestedRecyclerViewAdapter.notifyItemChanged(p);
                                setSelectedFile();

                            } else {

                Intent i = new Intent(HiddenCollection.this, Slide.class);
                i.putExtra("folderPosition", position);
                i.putExtra("position", p);

                startActivity(i);

                 }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });



        nestedRecyclerViewAdapter.setOnLongClickListener(new NestedRecyclerViewAdapter.LongClickListener() {
            @Override
            public void onItemLongClick(int p, View v) {
                try {


                    if (folderListArray.get(listkeys.get(position)).get(p) instanceof PhotoData) {
                        PhotoData imageList = (PhotoData)folderListArray.get(listkeys.get(position)).get(p);

                            for (int j = 0; j < folderListArray.get(listkeys.get(position)).size(); j++) {
                                if (folderListArray.get(listkeys.get(position)).get(j) != null)
                                    if (folderListArray.get(listkeys.get(position)).get(j) instanceof PhotoData) {

                                        PhotoData model = (PhotoData) folderListArray.get(listkeys.get(position)).get(j);
                                        model.setCheckboxVisible(true);

                                    }
                            }

                        imageList.setCheckboxVisible(true);
                        imageList.setSelected(true);

                        nestedRecyclerViewAdapter.notifyDataSetChanged();
                        setSelectedFile();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        more_vert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HiddenCollection.this,more_vert);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.collections_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.two:
                                Intent inc = new Intent(HiddenCollection.this, SettingsActivity.class);
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
                    toolbar1.setVisibility(View.VISIBLE);
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

        ArrayList<PhotoData> photoDatas = folderListArray.get(listkeys.get(position));
        if(photoDatas==null) {
            photoDatas = new ArrayList<PhotoData>();
            finish();
        }

        nestedRecyclerViewAdapter.notifyDataSetChanged();
        nestedRecyclerViewAdapter = new NestedRecyclerViewAdapter(position, HiddenCollection.this, photoDatas);
        RecyclerView.setAdapter(nestedRecyclerViewAdapter);
        collection.setText((photoDatas.size())+ " items");
        Log.d("size", photoDatas.size()+ "updateDeleteImageData: "+folderListArray.get(listkeys.get(position)).size());
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



                    if (imageList != null && imageList.size() != 0) {

                        nestedRecyclerViewAdapter.notifyDataSetChanged();
                        nestedRecyclerViewAdapter = new NestedRecyclerViewAdapter(position, HiddenCollection.this, folderListArray.get(listkeys.get(position)));
                        RecyclerView.setAdapter(nestedRecyclerViewAdapter);
                        collection.setText((folderListArray.get(listkeys.get(position)).size()) + " items");
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

    private void setSelectionClose() {
        setClose();
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        OnSelected(true, false, 0);
    }

    private void setClose() {
        for (int i = 0; i < folderListArray.size(); i++) {
            for(int j = 0; j< folderListArray.get(listkeys.get(i)).size(); j++){
            if (folderListArray.get(listkeys.get(i)).get(j) != null)
                if (folderListArray.get(listkeys.get(i)).get(j) instanceof PhotoData) {

                    PhotoData model = (PhotoData) folderListArray.get(listkeys.get(i)).get(j);
                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                }
        }
        }
        if (nestedRecyclerViewAdapter != null)
            nestedRecyclerViewAdapter.notifyDataSetChanged();
        selected_Item = 0;
    }

    private void OnSelected(boolean isShowToolbar, boolean isShowSelected, int selected) {
        if (isShowToolbar) {
            toolbar1.setVisibility(View.VISIBLE);
        } else {
            toolbar1.setVisibility(View.GONE);
        }


        if (isShowSelected) {
            lout_selected.setVisibility(View.VISIBLE);
            ll_bottom_option.setVisibility(View.VISIBLE);
        } else {
            lout_selected.setVisibility(View.GONE);
            ll_bottom_option.setVisibility(View.GONE);
        }
        txt_select.setText(selected + " selected");
    }
    private void setVisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(true);
        button.setEnabled(true);
        /*imageView.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.black));*/
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:

                imageView.setColorFilter(ContextCompat.getColor(HiddenCollection.this, R.color.white), PorterDuff.Mode.SRC_IN);
                textView.setTextColor(getResources().getColor(R.color.white));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }
    }

    private void setInvisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(false);
        button.setEnabled(false);
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.invisible_color), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.invisible_color));
    }

    private void selectEvent(boolean isSelect) {
        if (isSelect) {


                for(int j = 0; j< folderListArray.get(listkeys.get(position)).size(); j++){
                    if (folderListArray.get(listkeys.get(position)).get(j) != null)
                        if (folderListArray.get(listkeys.get(position)).get(j) instanceof PhotoData) {

                            PhotoData model = (PhotoData) folderListArray.get(listkeys.get(position)).get(j);
                            model.setSelected(true);

                        }
                }




            nestedRecyclerViewAdapter.notifyDataSetChanged();
            setSelectedFile();
        } else {


            for (int i = 0; i < folderListArray.size(); i++) {
                for(int j = 0; j< folderListArray.get(listkeys.get(i)).size(); j++){
                    if (folderListArray.get(listkeys.get(i)).get(j) != null)
                        if (folderListArray.get(listkeys.get(i)).get(j) instanceof PhotoData) {

                            PhotoData model = (PhotoData) folderListArray.get(listkeys.get(i)).get(j);
                            model.setSelected(false);
                            model.setCheckboxVisible(false);

                        }
                }
            }

            nestedRecyclerViewAdapter.notifyDataSetChanged();
            ll_bottom_option.setVisibility(View.GONE);
            selected_Item = 0;
        }
    }

    private void setFavourite() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(this);
        if (favList == null) {
            favList = new ArrayList<>();
        }

        int counter = 0;

        for (int i = 0; i < folderListArray.size(); i++) {
            for(int j = 0; j< folderListArray.get(listkeys.get(i)).size(); j++){
                if (folderListArray.get(listkeys.get(i)).get(j) != null)
                    if (folderListArray.get(listkeys.get(i)).get(j) instanceof PhotoData) {

                        PhotoData model = (PhotoData) folderListArray.get(listkeys.get(i)).get(j);
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
        }

        if (nestedRecyclerViewAdapter != null)
            nestedRecyclerViewAdapter.notifyDataSetChanged();
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
        Toast.makeText(this, counter + strMsg, Toast.LENGTH_SHORT).show();

        PreferencesManager.setFavouriteList(this, favList);
        RxBus.getInstance().post(new DisplayFavoriteEvent(favList, true));
    }

    private void setUnFavourite() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(this);
        if (favList == null) {
            favList = new ArrayList<>();
        }

        int counter = 0;

        for (int i = 0; i < folderListArray.size(); i++) {
            for(int j = 0; j< folderListArray.get(listkeys.get(i)).size(); j++){
                if (folderListArray.get(listkeys.get(i)).get(j) != null)
                    if (folderListArray.get(listkeys.get(i)).get(j) instanceof PhotoData) {

                        PhotoData model = (PhotoData) folderListArray.get(listkeys.get(i)).get(j);
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
        }

        if (nestedRecyclerViewAdapter != null)
            nestedRecyclerViewAdapter.notifyDataSetChanged();
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
        Toast.makeText(this, counter + strMsg, Toast.LENGTH_SHORT).show();
        PreferencesManager.setFavouriteList(this, favList);
        RxBus.getInstance().post(new DisplayFavoriteEvent(favList, false));
    }
    private void setSelectedFile() {
        int selected = 0;

        boolean isFavourite = false, isShowFavOption = false;

        ArrayList<Integer> favSelectList = new ArrayList<>();
        ArrayList<Integer> favUnSelectList = new ArrayList<>();
        boolean isSdcardPath = false;


      //  for (int i = 0; i < folderListArray.size(); i++) {
            for(int j = 0; j< folderListArray.get(listkeys.get(position)).size(); j++){
                if (folderListArray.get(listkeys.get(position)).get(j) != null)
                    if (folderListArray.get(listkeys.get(position)).get(j) instanceof PhotoData) {

                        PhotoData model = (PhotoData) folderListArray.get(listkeys.get(position)).get(j);
                        if (model.isSelected()) {
                            selected++;
                            pos = j;

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
      //  }


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

    private void showMoreOptionBottom() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, lout_more);
        popup.getMenuInflater().inflate(R.menu.storage_more_menu, popup.getMenu());

        if (selected_Item == 1) {
            popup.getMenu().findItem(R.id.menu_rename).setVisible(true);
            popup.getMenu().findItem(R.id.menu_details).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.menu_rename).setVisible(false);
            popup.getMenu().findItem(R.id.menu_details).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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

        if (photoData != null) {
            Constant.isFileFromSdCard = isFileFromSdCard;
            Constant.pastList = new ArrayList<>();

            boolean isSdcardPath = false;


           for(int i = 0; i<photoData.size(); i++){
               PhotoData model = (PhotoData) photoData.get(i);
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


            setSelectionClose();

            Intent intent = new Intent(this, StorageActivity.class);
            intent.putExtra("type", "CopyMove");
            startActivity(intent);
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure do you want to delete it?");
        builder.setCancelable(true);

        builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (isFileFromSdCard) {
                    sdCardPermissionType = 1;
                    File file = new File(sdCardPath);
                    int mode = StorageUtils.checkFSDCardPermission(file, HiddenCollection.this);
                    if (mode == 2) {
                        Toast.makeText(HiddenCollection.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
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


        new Thread(HiddenCollection.this::deleteFile).start();
}


    public void deleteFile() {

        if (photoData != null) {
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> dateList = new ArrayList<>();

            for(int i = 0; i<photoData.size(); i++){
                PhotoData model = (PhotoData) photoData.get(i);

                if (model.isSelected()) {

                    File file = new File(model.getFilePath());

                    String strLocation = "";
                    strLocation = model.getFolderName();

                    boolean isDelete = StorageUtils.deleteFile(file, this);

                    if (isDelete) {
                        list.add(model.getFilePath());
                        dateList.add(strLocation);
                        MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                            }
                        });
                    }

                } else {

                    model.setCheckboxVisible(false);
                }
            }




            for (int i = 0; i < photoData.size(); i++) {
                if (photoData.get(i) != null)

                    if (photoData.get(i) instanceof PhotoData) {
                        PhotoData model = (PhotoData) photoData.get(i);
                        if (model.isSelected()) {

                            photoData.remove(i);
                        }
                    }
            }

            updateMainList(list, dateList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    selected_Item = 0;
                    OnSelected(true, false, 0);
                    nestedRecyclerViewAdapter.notifyDataSetChanged();
                    RxBus.getInstance().post(new DisplayDeleteEvent(list));
                    Toast.makeText(HiddenCollection.this, "Delete image successfully", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void updateMainList(ArrayList<String> list, ArrayList<String> dateList) {

        if (list != null && list.size() != 0 && dateList != null && dateList.size() != 0) {


            for (int l = 0; l < dateList.size(); l++) {



                String strDate = dateList.get(l);

                if (strDate != null && !strDate.equalsIgnoreCase(""))
                    if (bucketimagesDataCollectionHashMap.containsKey(strDate)) {
                        ArrayList<PhotoData> imagesData1 = bucketimagesDataCollectionHashMap.get(strDate);
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
                            bucketimagesDataCollectionHashMap.put(strDate, imagesData1);
                        } else {
                            bucketimagesDataCollectionHashMap.remove(strDate);
                        }

                    }



            }
        } else {

            Set<String> keys = bucketimagesDataCollectionHashMap.keySet();
            ArrayList<String> listkeys = new ArrayList<>();
            listkeys.addAll(keys);

            for (int l = 0; l < list.size(); l++) {


                for (int b = 0; b < listkeys.size(); b++) {
                    ArrayList<PhotoData> imagesData = bucketimagesDataCollectionHashMap.get(listkeys.get(b));

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
                            bucketimagesDataCollectionHashMap.put(strDate, imagesData);
                        } else {
                            bucketimagesDataCollectionHashMap.remove(strDate);
                        }

                    }
                }

            }


        }

    }

    private void showRenameDialog() {
        setSelectionClose();
        if (photoData.get(pos) instanceof PhotoData) {

            PhotoData model = (PhotoData) photoData.get(pos);
            File file = new File(model.getFilePath());

            Dialog dialog = new Dialog(this, R.style.WideDialog);
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
                                Dialog validationDialog = new Dialog(HiddenCollection.this, R.style.WideDialog);
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
                        Toast.makeText(HiddenCollection.this, "New name can't be empty.", Toast.LENGTH_SHORT).show();
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
                renamed = StorageUtils.renameFile(file, newName, this);
            } else {
                renamed = file.renameTo(file2);


            }

            if (renamed) {
                Log.e("LOG", "File renamed...");
                MediaScannerConnection.scanFile(this, new String[]{file2.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });

                if (photoData.get(pos) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoData.get(pos);
                    model.setFilePath(file2.getPath());
                    model.setFileName(file2.getName());
                }

                nestedRecyclerViewAdapter.notifyItemChanged(pos);
                Toast.makeText(this, "Rename file successfully", Toast.LENGTH_SHORT).show();
                RxBus.getInstance().post(new RenameEvent(file, file2));
            } else {
                Log.e("LOG", "File not renamed...");
            }
//            storageList.clear();
//            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));

        }
    }

    private void showRenameValidationDialog() {
        Dialog validationDialog = new Dialog(this, R.style.WideDialog);
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
        if (photoData.get(pos) instanceof PhotoData) {

            PhotoData model = (PhotoData) photoData.get(pos);

            final Dialog dialog = new Dialog(this, R.style.WideDialog);
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

                txt_file_size.setText(Formatter.formatShortFileSize(this, file.length()));

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

        for (int i = 0; i < photoData.size(); i++) {

            if (photoData.get(i) != null)
                if (photoData.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoData.get(i);
                    if (model.isSelected()) {
                        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(model.getFilePath()));
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


    private Object getAddress(Float Latitude, Float Longitude, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()*//*
        String city = addresses.get(0).getLocality();
        String subLocality = addresses.get(0).getSubLocality();
        //String state = addresses.get(0).getAdminArea();
        //String country = addresses.get(0).getCountryName();
        //String postalCode = addresses.get(0).getPostalCode();
        //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*//*
        String concat = subLocality.concat(", " + city);

        return city==null ? "ab" : concat;//*addresses*//*;
    }


}