package com.gallery.picture.foto.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gallery.picture.foto.Adapters.favRecyclerViewAdapter;
import com.gallery.picture.foto.Model.ImageDetail;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class FavouriteActivity extends AppCompatActivity {

    RecyclerView RecyclerView;
    RelativeLayout toolbar1;
    ImageView more_vert;
    ImageView backButton;
    TextView collection, collections;
    public List<PhotoData> displayImageList = new ArrayList<>();

    TextView  txt_select, txt_text_send, txt_text_copy, txt_text_move, txt_text_delete, txt_text_more, txt_text_compress;
    ImageView iv_check_all,img_send, img_copy, img_move, img_delete, img_more, img_compress, iv_fav_unfill, iv_fav_fill, iv_uncheck;
    LinearLayout ll_bottom_option, lout_send, lout_copy, lout_move, lout_delete, lout_more, lout_compress;
    RelativeLayout lout_selected, ll_favourite,ll_check_all;
    ImageView iv_close;
    int selected_Item = 0;
    boolean isCheckAll = false;
    boolean isFileFromSdCard = false;
    int pos = 0;

    ArrayList<PhotoData> allFav = new ArrayList<>();
    favRecyclerViewAdapter RecyclerViewAdapter;
    DisplayFavActivity displayFavActivity;

    @Override
    public void onBackPressed() {
        if (lout_selected.getVisibility() == View.VISIBLE) {
            setSelectionClose();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {

        allFav.clear();

        displayFavoriteEvent();
        displayDeleteEvent();

        for (int i = displayImageList.size()-1; i > -1 ; i--){
            if (displayImageList.get(i).isFavorite())
                allFav.add(displayImageList.get(i));
        }
        collection.setText(allFav.size() + " items");
        RecyclerViewAdapter = new favRecyclerViewAdapter(this,allFav);
        RecyclerView.setAdapter(RecyclerViewAdapter);


        /*displayImageList = Constant.displayImageList;
        allFav.clear();
        for (int i = 0; i < displayImageList.size(); i++) {
            if (displayImageList.get(i).isFavorite())
                allFav.add(displayImageList.get(i));
        }

        LayoutManager layoutManager = new GridLayoutManager(this, 4);
        RecyclerView.setLayoutManager(layoutManager);
        ArrayList<ImageDetail> folderFiles = new ArrayList<ImageDetail>();
        //folderFiles = (ArrayList<ImageDetail>) getIntent().getExtras().get("folderFiles");
        collection.setText(allFav.size() + " items");
        RecyclerViewAdapter = new favRecyclerViewAdapter(this, allFav);
        RecyclerView.setAdapter(RecyclerViewAdapter);*/
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);


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
        lout_selected = findViewById(R.id.lout_selected);
        ll_bottom_option = findViewById(R.id.ll_bottom_option);
        txt_select = findViewById(R.id.txt_select);

/*DisplayFavActivity.updateClickListner(new UpdateInterface() {
    @Override
    public void onUpdate() {

    }
});*/

        displayImageList = Constant.displayImageList;
        for (int i = displayImageList.size()-1; i > -1 ; i--) {
            if (displayImageList.get(i).isFavorite())
                allFav.add(displayImageList.get(i));
        }

        LayoutManager layoutManager = new GridLayoutManager(this, 4);
        RecyclerView.setLayoutManager(layoutManager);
        ArrayList<ImageDetail> folderFiles = new ArrayList<ImageDetail>();
        //folderFiles = (ArrayList<ImageDetail>) getIntent().getExtras().get("folderFiles");
        collections.setText("Favourite Collection");
        setAdapter();

        displayFavoriteEvent();
        displayDeleteEvent();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        getSupportActionBar().hide();

        more_vert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(FavouriteActivity.this, more_vert);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.collections_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.two:
                                Intent inc = new Intent(FavouriteActivity.this, SettingsActivity.class);
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

    }

    private void setAdapter(){
        collection.setText(allFav.size() + " items");
        RecyclerViewAdapter = new favRecyclerViewAdapter(this, allFav);
        RecyclerView.setAdapter(RecyclerViewAdapter);

        RecyclerViewAdapter.setOnItemClickListener(new favRecyclerViewAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                try {

                    if (allFav.get(position) instanceof PhotoData) {

                        PhotoData imageList = (PhotoData) allFav.get(position);

                        if (imageList.isCheckboxVisible()) {

                            if (imageList.isSelected()) {
                                imageList.setSelected(false);
                            } else
                                imageList.setSelected(true);
                            RecyclerViewAdapter.notifyItemChanged(position);
                            setSelectedFile();

                        } else {

                            Intent i = new Intent(FavouriteActivity.this, DisplayFavActivity.class);
                            i.putExtra("folderPosition", -2);
                            i.putExtra("position", position);

                            startActivity(i);


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });








        RecyclerViewAdapter.setOnLongClickListener(new favRecyclerViewAdapter.LongClickListener() {
            @Override
            public void onItemLongClick(int position, View v) {
                try {

                    if (allFav.get(position) instanceof PhotoData) {
                        PhotoData imageList = (PhotoData) allFav.get(position);

                        for (int i = 0; i < allFav.size(); i++) {
                            if (allFav.get(i) != null)

                                if (allFav.get(i) instanceof PhotoData) {

                                    PhotoData model = (PhotoData) allFav.get(i);
                                    model.setCheckboxVisible(true);

                                }

                        }
                        imageList.setCheckboxVisible(true);
                        imageList.setSelected(true);

                        RecyclerViewAdapter.notifyDataSetChanged();
                        setSelectedFile();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        if (displayImageList != null && displayImageList.size() != 0) {
            for (int i = 0; i < allFav.size(); i++) {

                if (displayImageList.get(i) instanceof PhotoData) {
                    PhotoData model = (PhotoData) allFav.get(i);

                    if (model.getFilePath() != null) {
                        if (model.getFilePath().equalsIgnoreCase(filePath)) {
                            model.setFavorite(favorite);


                            break;
                        }
                    }
                }
            }

        }

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

    /*private void updateDeleteImageData(ArrayList<String> deleteList) {
        if (displayImageList != null && displayImageList.size() != 0) {


            for (int d = 0; d < deleteList.size(); d++) {

                for (int i = 0; i < displayImageList.size(); i++) {

                    if (displayImageList.get(i) instanceof PhotoData) {
                        PhotoData model = (PhotoData) displayImageList.get(i);

                        if (RecyclerViewAdapter != null) {
                            RecyclerViewAdapter.notifyItemChanged(i);

                        }

                    }
                }


            }


        }
    }
*/
    private void updateDeleteImageData(ArrayList<String> deleteList) {
        if (displayImageList != null && displayImageList.size() != 0) {


            for (int d = 0; d < deleteList.size(); d++) {

                for (int i = 0; i < displayImageList.size(); i++) {

                    if (displayImageList.get(i) instanceof PhotoData) {
                        PhotoData model = (PhotoData) displayImageList.get(i);

                        if (model.getFilePath().equalsIgnoreCase(deleteList.get(d))) {

                            displayImageList.remove(i);

                        }

                    }

                }
            }


           /* if (RecyclerViewAdapter != null) {
                RecyclerViewAdapter.notifyDataSetChanged();
                collection.setText(allFav.size() + " items");
                RecyclerViewAdapter = new favRecyclerViewAdapter(this, allFav);
                RecyclerView.setAdapter(RecyclerViewAdapter);
            }

*/

            //updateMainList(deleteList, new ArrayList<>());
        }
    }

    private void setSelectionClose() {
        setClose();
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        OnSelected(true, false, 0);
    }

    private void setClose() {
        for (int i = 0; i < allFav.size(); i++) {

            if (allFav.get(i) != null)
                if (allFav.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) allFav.get(i);
                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                }

        }
        if (RecyclerViewAdapter != null)
            RecyclerViewAdapter.notifyDataSetChanged();
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
          //  ll_bottom_option.setVisibility(View.VISIBLE);
        } else {
            lout_selected.setVisibility(View.GONE);
          //  ll_bottom_option.setVisibility(View.GONE);
        }
        txt_select.setText(selected + " selected");
    }
    private void setVisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(true);
        button.setEnabled(true);
       /* imageView.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.black));*/
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:

                imageView.setColorFilter(ContextCompat.getColor(FavouriteActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
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

            for (int i = 0; i < allFav.size(); i++) {

                if (allFav.get(i) != null)
                    if (allFav.get(i) instanceof PhotoData) {

                        PhotoData model = (PhotoData) allFav.get(i);
                        model.setSelected(true);

                    }
            }

            RecyclerViewAdapter.notifyDataSetChanged();
            setSelectedFile();
        } else {

            for (int i = 0; i < allFav.size(); i++) {
                if (allFav.get(i) != null)
                    if (allFav.get(i) instanceof PhotoData) {

                        PhotoData model = (PhotoData) allFav.get(i);
                        model.setSelected(false);
                        model.setCheckboxVisible(false);

                    }
            }

            RecyclerViewAdapter.notifyDataSetChanged();
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
        for (int i = 0; i < allFav.size(); i++) {

            if (allFav.get(i) != null)
                if (allFav.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) allFav.get(i);
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

        if (RecyclerViewAdapter != null)
            RecyclerViewAdapter.notifyDataSetChanged();
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

        for (int i = 0; i < allFav.size(); i++) {

            if (allFav.get(i) != null)
                if (allFav.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) allFav.get(i);
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
        if (RecyclerViewAdapter != null)
            RecyclerViewAdapter.notifyDataSetChanged();
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
        for (int i = 0; i < allFav.size(); i++) {

            if (allFav.get(i) != null)
                if (allFav.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) allFav.get(i);
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

}




    /* public void onUpdate() {


            *//*ArrayList<PhotoData> allFav = new ArrayList<>();
            for(int i = 0; i<DisplayImageActivity.displayImageList.size(); i++){
                if(DisplayImageActivity.displayImageList.get(i).isFavorite())
                    allFav.add(DisplayImageActivity.displayImageList.get(i));
                DisplayImageActivity.displayImageList = allFav;
            }*//*


        RecyclerViewAdapter = new favRecyclerViewAdapter(this, (ArrayList<PhotoData>) DisplayImageActivity.displayImageList);
            RecyclerView.setAdapter(RecyclerViewAdapter);
        RecyclerViewAdapter.notifyDataSetChanged();
    }*/
