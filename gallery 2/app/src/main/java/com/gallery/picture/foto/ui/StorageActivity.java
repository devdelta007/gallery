package com.gallery.picture.foto.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.gallery.picture.foto.Adapters.HeaderListAdapter;
import com.gallery.picture.foto.Adapters.StorageAdapter;
import com.gallery.picture.foto.Interface.BottomListner;
import com.gallery.picture.foto.Model.InternalStorageFilesModel;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.event.DisplayFavoriteEvent;
import com.gallery.picture.foto.event.RenameEvent;
import com.gallery.picture.foto.ui.DisplayImageActivity;
import com.gallery.picture.foto.ui.OpenZipFileActivity;
import com.gallery.picture.foto.ui.VideoPlayActivity;
import com.gallery.picture.foto.utils.BottomSheetFragment;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.RxBus;
import com.gallery.picture.foto.utils.StorageUtils;
import com.gallery.picture.foto.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gallery.picture.foto.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.gallery.picture.foto.utils.Utils.getMimeTypeFromFilePath;

public class StorageActivity extends AppCompatActivity implements BottomListner {

    String storage_type;
    String rootPath, compressPath, extractPath;
    boolean isGrid = false, isShowHidden = false, isCheckAll = false;

    int selected_Item = 0;
    int pos = 0;

    HeaderListAdapter pathAdapter;
    StorageAdapter adapter;

    ProgressDialog loadingDialog;
    TextView btn_internal_storage, btn_sd_card, txt_text_more, txt_text_compress, txt_text_extract, txt_text_send, txt_text_copy, txt_text_move, txt_text_delete, txt_text_past;
    LinearLayout lout_storage_option, lout_past, lout_compress, lout_extract, lout_send, lout_copy, lout_move, lout_delete, ll_bottom_option, ll_paste_option, lout_more, lout_past_cancel;
    ImageView  iv_close, img_more, img_compress, img_extract, iv_clear, img_send, img_copy, img_move, img_delete, iv_uncheck, iv_check_all, img_past, iv_fav_unfill, iv_fav_fill, iv_back_search;
    AppCompatImageView iv_more, iv_list_grid, iv_back, iv_search;
    AppCompatTextView txt_select;
    RelativeLayout lout_toolbar, lout_search_bar, ll_check_all, lout_selected, ll_favourite;
    RecyclerView rv_header, recycler_view;
    LinearLayout ll_empty;
    ProgressBar progress_bar;
    EditText edt_search;

    private ArrayList<String> arrayListFilePaths = new ArrayList<>();
    ArrayList<InternalStorageFilesModel> storageList = new ArrayList<>();
    ArrayList<InternalStorageFilesModel> backUpstorageList = new ArrayList<>();

    boolean isFileFromSdCard = false;
    String sdCardPath;

    int sdCardPermissionType = 0;
    boolean isSdCard = false;

    static int videoImage_code = 30, zip_open = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_internal_storage = findViewById(R.id.btn_internal_storage);
        btn_sd_card = findViewById(R.id.btn_sd_card);
        txt_text_more = findViewById(R.id.txt_text_more);
        txt_text_compress = findViewById(R.id.txt_text_compress);
        txt_text_extract = findViewById(R.id.txt_text_extract);
        txt_text_send = findViewById(R.id.txt_text_send);
        txt_text_copy = findViewById(R.id.txt_text_copy);
        txt_text_move = findViewById(R.id.txt_text_move);
        txt_text_delete = findViewById(R.id.txt_text_delete);
        txt_text_past = findViewById(R.id.txt_text_past);
        lout_storage_option = findViewById(R.id.lout_storage_option);
        lout_past = findViewById(R.id.lout_past);
        lout_compress = findViewById(R.id.lout_compress);
        lout_extract = findViewById(R.id.lout_extract);
        lout_send = findViewById(R.id.lout_send);
        lout_copy = findViewById(R.id.lout_copy);
        lout_move = findViewById(R.id.lout_move);
        lout_delete = findViewById(R.id.lout_delete);
        ll_bottom_option = findViewById(R.id.ll_bottom_option);
        ll_paste_option = findViewById(R.id.ll_paste_option);
        lout_more = findViewById(R.id.lout_more);
        img_more = findViewById(R.id.img_more);
        img_compress = findViewById(R.id.img_compress);
        img_extract = findViewById(R.id.img_extract);
        iv_clear = findViewById(R.id.iv_clear);
        img_send = findViewById(R.id.img_send);
        img_copy = findViewById(R.id.img_copy);
        img_move = findViewById(R.id.img_move);
        img_delete = findViewById(R.id.img_delete);
        iv_uncheck = findViewById(R.id.iv_uncheck);
        iv_check_all = findViewById(R.id.iv_check_all);
        img_past = findViewById(R.id.img_past);
        iv_fav_unfill = findViewById(R.id.iv_fav_unfill);
        iv_fav_fill = findViewById(R.id.iv_fav_fill);
        iv_more = findViewById(R.id.iv_more);
        iv_list_grid = findViewById(R.id.iv_list_grid);
        iv_close = findViewById(R.id.iv_close);
        txt_select = findViewById(R.id.txt_select);
        lout_toolbar = findViewById(R.id.lout_toolbar);
        lout_search_bar = findViewById(R.id.lout_search_bar);
        ll_check_all = findViewById(R.id.ll_check_all);
        lout_selected = findViewById(R.id.lout_selected);
        ll_favourite = findViewById(R.id.ll_favourite);
        rv_header = findViewById(R.id.rv_header);
        recycler_view = findViewById(R.id.recycler_view);
        ll_empty = findViewById(R.id.ll_empty);
        progress_bar = findViewById(R.id.progress_bar);
        edt_search = findViewById(R.id.edt_search);
        iv_back = findViewById(R.id.iv_back);
        iv_search = findViewById(R.id.iv_search);
        iv_list_grid = findViewById(R.id.iv_list_grid);
        iv_back_search = findViewById(R.id.iv_back_search);
        lout_past_cancel = findViewById(R.id.lout_past_cancel);

        intView();

        displayDeleteEvent();
        displayFavoriteEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSdCard = Utils.externalMemoryAvailable(StorageActivity.this);
    }


    public void intView() {
        Intent intent = getIntent();
        isSdCard = Utils.externalMemoryAvailable(StorageActivity.this);
        storage_type = intent.getStringExtra("type");


        if (storage_type.equalsIgnoreCase("Internal")) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if (storage_type.equalsIgnoreCase("Download")) {
            rootPath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS;
        } else if (storage_type.equalsIgnoreCase("CopyMove")) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            rootPath = Utils.getExternalStoragePath(this, true);
        }

        sdCardPath = Utils.getExternalStoragePath(this, true);

        if (storage_type.equalsIgnoreCase("CopyMove")) {
            isFileFromSdCard = Constant.isFileFromSdCard;
            if (Constant.pastList != null && Constant.pastList.size() != 0) {
                ll_paste_option.setVisibility(View.VISIBLE);

                pastList.addAll(Constant.pastList);

            } else {
                ll_paste_option.setVisibility(View.GONE);
            }
        } else {
            ll_paste_option.setVisibility(View.GONE);
        }

        lout_toolbar.setVisibility(View.VISIBLE);
        lout_search_bar.setVisibility(View.GONE);

        isGrid = PreferencesManager.getDirList_Grid(this);
        isShowHidden = PreferencesManager.getShowHidden(this);

        iv_check_all.setColorFilter(getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

        if (isGrid) {
            iv_list_grid.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        } else {
            iv_list_grid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
        }

        setSearchBarData();
        getDataList();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage("Delete file...");
        loadingDialog.setCanceledOnTouchOutside(false);

        lout_storage_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStorageOptionClose();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lout_toolbar.setVisibility(View.GONE);
                lout_search_bar.setVisibility(View.VISIBLE);
                showKeyboard();
                backUpstorageList = new ArrayList<>();
                for (int f = 0; f < storageList.size(); f++) {
                    backUpstorageList.add(storageList.get(f));
                }
            }
        });
        iv_list_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGrid) {
                    isGrid = false;
                    PreferencesManager.saveToDirList_Grid(getApplicationContext(), isGrid);
                    setrecycler_viewData();
                    iv_list_grid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                    // iv_list_grid.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                } else {
                    isGrid = true;
                    PreferencesManager.saveToDirList_Grid(getApplicationContext(), isGrid);
                    setrecycler_viewData();
                    iv_list_grid.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                    // iv_list_grid.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));

                }
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
                    lout_toolbar.setVisibility(View.VISIBLE);
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
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectionClose();
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.getText().clear();
                iv_clear.setVisibility(View.GONE);
                setClear();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm.isAcceptingText()) {
                    //writeToLog("Software Keyboard was shown");

                } else {
                    showKeyboard();
                    // writeToLog("Software Keyboard was not shown");
                }
            }
        });
        btn_internal_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                arrayListFilePaths.clear();
                arrayListFilePaths.add(rootPath);
                refreshData();
                setStorageOptionClose();
            }
        });
        lout_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile();
            }
        });
        lout_compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCompressDialog();
            }
        });
        iv_back_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setsearchBack();
            }
        });
        lout_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.isCopyData = true;
                setCopyMoveOptinOn();
            }
        });
        lout_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.isCopyData = false;
                setCopyMoveOptinOn();
            }
        });
        lout_extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExtractDialog();
            }
        });
        lout_past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pastList != null && pastList.size() != 0) {

                    if (isFileFromSdCard) {
                        sdCardPermissionType = 2;
                        File file = new File(sdCardPath);
                        int mode = StorageUtils.checkFSDCardPermission(file, StorageActivity.this);
                        if (mode == 2) {
                            Toast.makeText(StorageActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                        } else {
                            setCopyMoveAction();
                        }
                    } else {
                        setCopyMoveAction();
                    }


                }
            }
        });
        lout_past_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.pastList = new ArrayList<>();
                pastList = new ArrayList<>();
                ll_paste_option.setVisibility(View.GONE);
                if (storage_type.equalsIgnoreCase("CopyMove")) {
                    finish();
                }
            }
        });
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMoreMenu();
            }
        });
        lout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
        lout_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptionBottom();
            }
        });
    }



    private void setStorageOptionClose() {
        if (lout_storage_option.getVisibility() == View.VISIBLE) {
            lout_storage_option.setVisibility(View.GONE);
        }
    }

    public void setSearchBarData() {
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strSearch = charSequence.toString();
                Log.e("searchText addChange: ", strSearch);
                // hideSoftKeyboard(binding.edt_search);
                setSearch(strSearch);

                if (i2 == 0) {
                    iv_clear.setVisibility(View.GONE);
                } else {
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.e("TAG", "edt_search: " + edt_search.getText().toString());

                    if (!edt_search.getText().toString().isEmpty() && edt_search.getText().toString().trim().length() != 0) {

                        String strSearch = edt_search.getText().toString().trim();

                        hideSoftKeyboard(edt_search);
                        setSearch(strSearch);


                    } else {
                        Toast.makeText(StorageActivity.this, "Enter file name", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });

    }

    public void getDataList() {
        progress_bar.setVisibility(View.VISIBLE);
        new Thread(this::getList).start();
    }

    @Override
    public void onBackPressed() {
        if (lout_storage_option.getVisibility() == View.VISIBLE) {
            setStorageOptionClose();
        } else if (lout_search_bar.getVisibility() == View.VISIBLE) {
            setsearchBack();
        } else if (lout_selected.getVisibility() == View.VISIBLE) {
            setSelectionClose();
        } else if (arrayListFilePaths.size() == 1) {
            super.onBackPressed();

        } else if (arrayListFilePaths.size() != 0 && arrayListFilePaths.size() != 1) {
            arrayListFilePaths.remove(arrayListFilePaths.size() - 1);
            storageList.clear();
            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
            // adapter.notifyDataSetChanged();

        } else {
            super.onBackPressed();
        }
    }

    public void setSearch(String searchText) {
        Log.e("searchText: ", searchText);
        storageList.clear();
        for (int i = 0; i < backUpstorageList.size(); i++) {
            String s = backUpstorageList.get(i).getFileName();
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(searchText.toLowerCase())) {
                //adding the element to filtered list
                InternalStorageFilesModel pack = backUpstorageList.get(i);
                storageList.add(pack);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            setrecycler_viewData();
        }

        if (storageList != null && storageList.size() != 0) {
            recycler_view.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            recycler_view.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }





    private void setUnFavourite() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(this);
        if (favList == null) {
            favList = new ArrayList<>();
        }

        int counter = 0;

        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {
                if (storageList.get(i).isFavorite()) {

                    storageList.get(i).setFavorite(false);
                    counter++;
                    if (favList.contains(storageList.get(i).getFilePath())) {
                        favList.remove(storageList.get(i).getFilePath());
                    }
                }
            }
            storageList.get(i).setSelected(false);
            storageList.get(i).setCheckboxVisible(false);
        }
        adapter.notifyDataSetChanged();
        ll_bottom_option.setVisibility(View.GONE);
        lout_selected.setVisibility(View.GONE);
        lout_toolbar.setVisibility(View.VISIBLE);

        String strMsg = "";
        if (counter == 1) {
            strMsg = " item removed from Favourites.";
        } else {
            strMsg = " items removed from Favourites.";
        }
        Toast.makeText(this, counter + strMsg, Toast.LENGTH_SHORT).show();
        PreferencesManager.setFavouriteList(StorageActivity.this, favList);
    }

    private void setFavourite() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        ArrayList<String> favList = PreferencesManager.getFavouriteList(this);
        if (favList == null) {
            favList = new ArrayList<>();
        }

        int counter = 0;

        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {
                if (!storageList.get(i).isFavorite()) {
                    favList.add(0, storageList.get(i).getFilePath());
                    counter++;
                }
                storageList.get(i).setFavorite(true);

            }
            storageList.get(i).setSelected(false);
            storageList.get(i).setCheckboxVisible(false);
        }
        adapter.notifyDataSetChanged();
        ll_bottom_option.setVisibility(View.GONE);
        lout_selected.setVisibility(View.GONE);
        lout_toolbar.setVisibility(View.VISIBLE);

        String strMsg = "";
        if (counter == 1) {
            strMsg = " item added to Favourites.";
        } else {
            strMsg = " items added to Favourites.";
        }
        Toast.makeText(this, counter + strMsg, Toast.LENGTH_SHORT).show();

        PreferencesManager.setFavouriteList(StorageActivity.this, favList);
    }

    private void showMoreOptionBottom() {
        PopupMenu popup = new PopupMenu(this, lout_more);
        popup.getMenuInflater().inflate(R.menu.storage_more_menu, popup.getMenu());

        if (selected_Item == 1) {
            popup.getMenu().findItem(R.id.menu_rename).setVisible(true);
            popup.getMenu().findItem(R.id.menu_details).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.menu_rename).setVisible(false);
            popup.getMenu().findItem(R.id.menu_details).setVisible(false);
        }

        if (isDir) {
            popup.getMenu().findItem(R.id.menu_share).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.menu_share).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_rename:
                        showRenameDialog();
                        break;

                    case R.id.menu_details:
                        showDetailDialog();
                        break;
//                    case R.id.menu_compress:
//                        showCompressDialog();
//                        break;
                    case R.id.menu_share:
                        sendFile();
                        break;

                }
                return false;
            }
        });
        popup.show();

       /* for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {
                pos = i;
                break;
            }
        }*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == videoImage_code && resultCode == RESULT_OK) {
            if (Constant.arrayListFilePaths != null) {
                arrayListFilePaths = Constant.arrayListFilePaths;
                storageList.clear();
                getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
                setHeaderData();
                setrecycler_viewData();
            }

        } else if (requestCode == zip_open && resultCode == RESULT_OK) {
            refreshData();

        } else if (requestCode == StorageUtils.REQUEST_SDCARD_WRITE_PERMISSION) {
            String p = PreferencesManager.getSDCardTreeUri(StorageActivity.this);
            Uri oldUri = null;
            if (p != null) oldUri = Uri.parse(p);
            Uri treeUri = null;
            if (resultCode == Activity.RESULT_OK) {
                treeUri = data.getData();
                if (treeUri != null) {
                    PreferencesManager.setSDCardTreeUri(StorageActivity.this, treeUri.toString());
                    if (sdCardPermissionType == 1) {
                        setDeleteFile();
                    } else if (sdCardPermissionType == 2) {
                        setCopyMoveAction();
                    } else if (sdCardPermissionType == 3) {
                        setcompress();
                    } else if (sdCardPermissionType == 4) {
                        setExtract();
                    }
//                    deleteFile();
                }
            }
            if (resultCode != Activity.RESULT_OK) {
                if (treeUri != null) {
                    PreferencesManager.setSDCardTreeUri(StorageActivity.this, oldUri.toString());
//                    deleteFile();
                    if (sdCardPermissionType == 1) {
                        setDeleteFile();
                    } else if (sdCardPermissionType == 2) {
                        setCopyMoveAction();
                    } else if (sdCardPermissionType == 3) {
                        setcompress();
                    } else if (sdCardPermissionType == 4) {
                        setExtract();
                    }
                }
                return;
            }
            try {
                final int takeFlags = data.getFlags()
                        & ( Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDetailDialog() {
        final Dialog dialog = new Dialog(StorageActivity.this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_details);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        File file = new File(storageList.get(pos).getFilePath());

        TextView txt_title = dialog.findViewById(R.id.txt_title);
        TextView txt_format = dialog.findViewById(R.id.txt_format);
        TextView txt_time = dialog.findViewById(R.id.txt_time);
        TextView txt_resolution = dialog.findViewById(R.id.txt_resolution);
        TextView txt_file_size = dialog.findViewById(R.id.txt_file_size);
        TextView txt_duration = dialog.findViewById(R.id.txt_duration);
        TextView txt_path = dialog.findViewById(R.id.txt_path);

        LinearLayout lout_duration = dialog.findViewById(R.id.lout_duration);
        LinearLayout lout_resolution = dialog.findViewById(R.id.lout_resolution);

        lout_resolution.setVisibility(View.GONE);
        lout_duration.setVisibility(View.GONE);

        TextView btn_ok = dialog.findViewById(R.id.btn_ok);


        if (file.exists()) {
            txt_title.setText(file.getName());
            txt_path.setText(file.getPath());

            String type = getMimeTypeFromFilePath(file.getPath());

            txt_format.setText(type);

            txt_file_size.setText(Formatter.formatShortFileSize(this, file.length()));

            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
            String strDate = format.format(file.lastModified());

            txt_time.setText(strDate);


            if (file.isDirectory()) {
                lout_resolution.setVisibility(View.GONE);
                lout_duration.setVisibility(View.GONE);

            } else {

                if (type != null && type.contains("image")) {
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

    private void showRenameDialog() {
        setSelectionClose();
        File file = new File(storageList.get(pos).getFilePath());

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
                            Dialog validationDialog = new Dialog(StorageActivity.this, R.style.WideDialog);
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
                    Toast.makeText(StorageActivity.this, "New name can't be empty.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();
    }


    public void setCopyMoveAction() {

        if (Constant.isCopyData) {
            // copy file
            // rootPath

            if (loadingDialog != null) {
                loadingDialog.setMessage("Copy file");
                loadingDialog.show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    copyFile();
                }
            }).start();

        } else {
            if (loadingDialog != null) {
                loadingDialog.setMessage("Move file");
                loadingDialog.show();
            }
            // move file
            new Thread(new Runnable() {
                @Override
                public void run() {
                    moveFile();
                }
            }).start();
        }
    }

    private void moveFile() {
//        File file = new File(rootPath);
        ArrayList<String> existFiles = new ArrayList<>();
        ArrayList<File> moveFileList = new ArrayList<>();
        ArrayList<String> deleteFileList = new ArrayList<>();

        for (int i = 0; i < pastList.size(); i++) {
            File from = new File(rootPath);
            File to = pastList.get(i);
            String newPath = from.getPath() + "/" + to.getName();
            File fileee = new File(newPath);
            Log.d("rootPath", rootPath);
            Log.d("rootPath", to.getPath());
            Log.d("rootPath", newPath);

            if (fileee.exists()) {
                existFiles.add(fileee.getName());


            }
        }
        if(existFiles.size()!=0){
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null) {
                                    if (loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }
                                }

                                Toast.makeText(StorageActivity.this, "Move failed,"+"\n"+" File: " + existFiles.toString()+ " already exists!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        },30);
                    }
                });
                return;
        }


        for (int i = 0; i < pastList.size(); i++) {
            File file = pastList.get(i);

            String dir;
            if (file.isDirectory()) {
                dir = file.getPath();
            } else {
                dir = file.getParent();
            }

            File from = new File(rootPath);
            File to = pastList.get(i);

            try {


                if (file.isDirectory()) {

                    String newPath = from.getPath() + "/" + to.getName();
                    File file1 = new File(newPath);

                    boolean isMove = StorageUtils.moveFile(to, file1, StorageActivity.this);
                    if (isMove) {
                        moveFileList.add(file1);
                        deleteFileList.add(file1.getPath());
                    }
//                    Utils.moveDirectory(to, file1, StorageActivity.this);

                    Log.e("move", "file is move");


                } else {
//                    File file1 = Utils.moveFile(to, from);
//                    moveFileList.add(file1);

                    String newPath = from.getPath() + "/" + to.getName();
                    File fileee = new File(newPath);
                    if (fileee.exists()) {
                        String[] separated = to.getName().split("\\.");
                        String name = separated[0];
                        String type = separated[1];

                        String newPath2 = from.getPath() + "/" + name + "_" + System.currentTimeMillis() + "." + type;

                        File file2 = new File(newPath2);

                        boolean isMove = StorageUtils.moveFile(to, file2, StorageActivity.this);

                        if (isMove) {
                            moveFileList.add(file2);
                            deleteFileList.add(file.getPath());
                        }

                    } else {
                        boolean isMove = StorageUtils.moveFile(to, fileee, StorageActivity.this);
                        if (isMove) {
                            moveFileList.add(fileee);
                            deleteFileList.add(file.getPath());
                        }
                    }


                   /* if (isMove) {
                        moveFileList.add(from);
                        deleteFileList.add(file.getPath());
                    }*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null) {
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }

                        Constant.pastList = new ArrayList<>();
                        pastList = new ArrayList<>();
                        ll_paste_option.setVisibility(View.GONE);

                        Toast.makeText(StorageActivity.this, "Move file successfully", Toast.LENGTH_SHORT).show();
                        if (storage_type.equalsIgnoreCase("CopyMove")) {

                            RxBus.getInstance().post(new CopyMoveEvent(moveFileList, 2, deleteFileList));
                            RxBus.getInstance().post(new DisplayDeleteEvent(deleteFileList));
                            // moveFileList ,deleteFileList
                            finish();
                        } else {
                            refreshData();
                            storageList.clear();
                            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
                            adapter.notifyDataSetChanged();

                            if (storageList != null && storageList.size() != 0) {
                                recycler_view.setVisibility(View.VISIBLE);
                                ll_empty.setVisibility(View.GONE);
                            } else {
                                recycler_view.setVisibility(View.GONE);
                                ll_empty.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }, 30);

            }
        });
    }

    private void copyFile() {
        File file = new File(rootPath);

        ArrayList<File> copyFileList = new ArrayList<>();
        for (int i = 0; i < pastList.size(); i++) {
            File file1 = pastList.get(i);

            try {
                if (file1.isDirectory()) {

                    String newPath = file.getPath() + "/" + file1.getName();
                    File fileee = new File(newPath);

                    if (fileee.exists()) {
                        folder_counter = 1;
                        File newFolder = folderFile(file1.getName(), file.getPath());
                        StorageUtils.copyFile(file1, newFolder, StorageActivity.this);
//                        Utils.copyDirectoryOneLocationToAnotherLocation(file1, newFolder, StorageActivity.this);
                        copyFileList.add(newFolder);
                    } else {
                        StorageUtils.copyFile(file1, fileee, StorageActivity.this);
//                        Utils.copyDirectoryOneLocationToAnotherLocation(file1, fileee, StorageActivity.this);
                        copyFileList.add(fileee);
                    }
                } else {
                    String newPath = file.getPath() + "/" + file1.getName();
                    File fileee = new File(newPath);
                    if (fileee.exists()) {
                        // String currentString = "Fruit: they taste good.very nice actually";
                        String[] separated = file1.getName().split("\\.");
                        String name = separated[0];
                        String type = separated[1];

                        String newPath2 = file.getPath() + "/" + name + "_" + System.currentTimeMillis() + "." + type;
                        File file2 = new File(newPath2);
                        StorageUtils.copyFile(file1, file2, StorageActivity.this);
//                        Utils.copyDirectoryOneLocationToAnotherLocation(file1, file2, StorageActivity.this);
                        copyFileList.add(file2);

                    } else {
                        StorageUtils.copyFile(file1, fileee, StorageActivity.this);
//                        Utils.copyDirectoryOneLocationToAnotherLocation(file1, fileee, StorageActivity.this);
                        copyFileList.add(fileee);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Constant.pastList = new ArrayList<>();
                        pastList = new ArrayList<>();


                        RxBus.getInstance().post(new CopyMoveEvent(copyFileList, 1, new ArrayList<>()));
                        if (storage_type.equalsIgnoreCase("CopyMove")) {
                            if (loadingDialog != null) {
                                if (loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }
                            ll_paste_option.setVisibility(View.GONE);
                            Toast.makeText(StorageActivity.this, "Copy file successfully", Toast.LENGTH_SHORT).show();
                            // copyFileList
                            finish();
                        } else {

                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (loadingDialog != null) {
                                        if (loadingDialog.isShowing()) {
                                            loadingDialog.dismiss();
                                        }
                                    }
                                    ll_paste_option.setVisibility(View.GONE);
                                    Toast.makeText(StorageActivity.this, "Copy file successfully", Toast.LENGTH_SHORT).show();
                                    refreshData();

                                    storageList.clear();
                                    getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
                                    adapter.notifyDataSetChanged();

                                    if (storageList != null && storageList.size() != 0) {
                                        recycler_view.setVisibility(View.VISIBLE);
                                        ll_empty.setVisibility(View.GONE);
                                    } else {
                                        recycler_view.setVisibility(View.GONE);
                                        ll_empty.setVisibility(View.VISIBLE);
                                    }
                                }
                            }, 50);

                        }
                    }
                }, 20);

            }
        });
    }

    private void setVisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(true);
        button.setEnabled(true);
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.black));
    }

    private void setInvisibleButton(LinearLayout button, ImageView imageView, TextView textView) {
        button.setClickable(false);
        button.setEnabled(false);
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.invisible_color), PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(R.color.invisible_color));
    }


    private void refreshData() {
        if (storageList != null && storageList.size() != 0 && arrayListFilePaths != null && arrayListFilePaths.size() != 0) {
            storageList.clear();
            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
            adapter.notifyDataSetChanged();

            if (storageList != null && storageList.size() != 0) {
                recycler_view.setVisibility(View.VISIBLE);
                ll_empty.setVisibility(View.GONE);
            } else {
                recycler_view.setVisibility(View.GONE);
                ll_empty.setVisibility(View.VISIBLE);
            }
        }
    }

    ArrayList<File> pastList = new ArrayList<>();

    private void setCopyMoveOptinOn() {
        Constant.isFileFromSdCard = isFileFromSdCard;
        Constant.pastList = new ArrayList<>();
        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {

                File file = new File(storageList.get(i).getFilePath());
                if (file.exists()) {
                    pastList.add(file);
                    Constant.pastList.add(file);
                }
            }
        }

        setSelectionClose();

        ll_paste_option.setVisibility(View.VISIBLE);
    }

    private void sendFile() {
        ArrayList<Uri> uris = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {

                if (!storageList.get(i).isDir()) {
                    Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(storageList.get(i).getFilePath()));
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

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure do you want to delete it?");
        builder.setCancelable(false);
        builder.setPositiveButton(Html.fromHtml("<font color='#ffba00'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (isFileFromSdCard) {
                    sdCardPermissionType = 1;
                    File file = new File(sdCardPath);
                    int mode = StorageUtils.checkFSDCardPermission(file, StorageActivity.this);
                    if (mode == 2) {
                        Toast.makeText(StorageActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                    } else {
                        setDeleteFile();
                    }
                } else {
                    setDeleteFile();
                }

            }
        });

        builder.setNegativeButton(Html.fromHtml("<font color='#ffba00'>No</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void setDeleteFile() {
        if (loadingDialog != null)
            if (!loadingDialog.isShowing()) {
                loadingDialog.setMessage("Delete file...");
                loadingDialog.show();
            }

        new Thread(StorageActivity.this::deleteFile).start();
    }


    private void setSelectionClose() {
        isCheckAll = false;
        iv_check_all.setVisibility(View.GONE);
        for (int i = 0; i < storageList.size(); i++) {
            storageList.get(i).setSelected(false);
            storageList.get(i).setCheckboxVisible(false);
        }
        adapter.notifyDataSetChanged();
        ll_bottom_option.setVisibility(View.GONE);
        lout_selected.setVisibility(View.GONE);
        lout_toolbar.setVisibility(View.VISIBLE);
    }

    private void setsearchBack() {
        edt_search.getText().clear();
        iv_clear.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            //writeToLog("Software Keyboard was shown");
            //  hideSoftKeyboard(binding.edt_search);

            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }

        } else {
            // writeToLog("Software Keyboard was not shown");
        }
        setClear();

        lout_search_bar.setVisibility(View.GONE);
        lout_toolbar.setVisibility(View.VISIBLE);
    }

    private void setClear() {

        storageList.clear();

        if (backUpstorageList != null)
            for (int f = 0; f < backUpstorageList.size(); f++) {

                storageList.add(backUpstorageList.get(f));
            }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            setrecycler_viewData();
        }

        if (storageList != null && storageList.size() != 0) {
            recycler_view.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            recycler_view.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }


    public void setMoreMenu() {
        PopupMenu popup = new PopupMenu(this, iv_more);
        popup.getMenuInflater().inflate(R.menu.storage_menu, popup.getMenu());

        if (isShowHidden) {
            popup.getMenu().findItem(R.id.menu_hidden).setTitle("Don't show hidden files");
        } else {
            popup.getMenu().findItem(R.id.menu_hidden).setTitle("Show hidden files");
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_sort:
//                          setSortMenu();
                        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(StorageActivity.this);
                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        break;

                    case R.id.menu_create_folder:
                        showCreateFolder();
                        break;

                    case R.id.menu_hidden:
                        if (isShowHidden) {
                            isShowHidden = false;
                        } else {
                            isShowHidden = true;
                        }
                        PreferencesManager.saveToShowHidden(StorageActivity.this, isShowHidden);
                        storageList.clear();
                        getFilesList(rootPath);
                        break;
                }
                return false;
            }
        });
        popup.show();

    }

    private void showCreateFolder() {
        Dialog dialog = new Dialog(StorageActivity.this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_create_folder);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        AppCompatEditText edtFileName;
        LinearLayout btn_cancel, btn_ok;

        edtFileName = dialog.findViewById(R.id.edt_file_name);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok = dialog.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtFileName.getText().toString().isEmpty()) {

                    String strFileName = edtFileName.getText().toString();
                    File file = new File(rootPath + "/" + strFileName);
                    if (file.exists()) {
                        // show validation
                        dialog.dismiss();
                        Toast.makeText(StorageActivity.this, "This file already exists", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        storageList.clear();
                        getFilesList(rootPath);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(StorageActivity.this, "Enter folder name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private void setSortMenu() {

        PopupMenu popup = new PopupMenu(this, iv_more);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_name_ascending:
                        if (storageList != null && storageList.size() != 0) {
                            sortNameAscending();
                            adapter.notifyDataSetChanged();
                            PreferencesManager.saveToSortType(StorageActivity.this, 1);
                        }

                        break;
                    case R.id.menu_name_descending:
                        if (storageList != null && storageList.size() != 0) {
                            sortNameDescending();
                            adapter.notifyDataSetChanged();
                            PreferencesManager.saveToSortType(StorageActivity.this, 2);
                        }

                        break;

                    case R.id.menu_big_to_small:
                        if (storageList != null && storageList.size() != 0) {
                            sortSizeDescending();
                            adapter.notifyDataSetChanged();
                            PreferencesManager.saveToSortType(StorageActivity.this, 3);
                        }
                        break;

                    case R.id.menu_small_to_big:
                        if (storageList != null && storageList.size() != 0) {
                            sortSizeAscending();
                            adapter.notifyDataSetChanged();
                            PreferencesManager.saveToSortType(StorageActivity.this, 4);
                        }
                        break;

                    case R.id.menu_time_newest:
                        if (storageList != null && storageList.size() != 0) {
                            setDateWiseSortAs(true);
                            adapter.notifyDataSetChanged();

                            PreferencesManager.saveToSortType(StorageActivity.this, 5);
                        }
                        break;

                    case R.id.menu_time_oldest:
                        ;
                        if (storageList != null && storageList.size() != 0) {
                            setDateWiseSortAs(false);
                            adapter.notifyDataSetChanged();
                            PreferencesManager.saveToSortType(StorageActivity.this, 6);
                        }


                        break;

                }
                return false;
            }
        });

        popup.show();
    }

    private void sortNameAscending() {

        Collections.sort(storageList, new Comparator<InternalStorageFilesModel>() {
            @Override
            public int compare(InternalStorageFilesModel t1, InternalStorageFilesModel t2) {

                File file1 = new File(t1.getFilePath());
                File file2 = new File(t2.getFilePath());

                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        });
    }

    private void sortNameDescending() {

        Collections.sort(storageList, new Comparator<InternalStorageFilesModel>() {
            @Override
            public int compare(InternalStorageFilesModel t1, InternalStorageFilesModel t2) {

                File file1 = new File(t1.getFilePath());
                File file2 = new File(t2.getFilePath());

                return file2.getName().compareToIgnoreCase(file1.getName());
            }
        });
    }

    private void sortSizeAscending() {

        Collections.sort(storageList, new Comparator<InternalStorageFilesModel>() {
            @Override
            public int compare(InternalStorageFilesModel t1, InternalStorageFilesModel t2) {

                File file1 = new File(t1.getFilePath());
                File file2 = new File(t2.getFilePath());

                return Long.valueOf(file1.length()).compareTo(file2.length());

            }
        });
    }

    private void sortSizeDescending() {

        Collections.sort(storageList, new Comparator<InternalStorageFilesModel>() {
            @Override
            public int compare(InternalStorageFilesModel t1, InternalStorageFilesModel t2) {

                File file1 = new File(t1.getFilePath());
                File file2 = new File(t2.getFilePath());

                return Long.valueOf(file2.length()).compareTo(file1.length());

            }
        });
    }

    private void setDateWiseSortAs(boolean isNewest) {

        Collections.sort(storageList, new Comparator<InternalStorageFilesModel>() {
            @Override
            public int compare(InternalStorageFilesModel i1, InternalStorageFilesModel i2) {

                File file1 = new File(i1.getFilePath());
                File file2 = new File(i2.getFilePath());
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                String strDate1 = format.format(file1.lastModified());
                String strDate2 = format.format(file2.lastModified());

                int compareResult = 0;

                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(strDate1);
                    date2 = format.parse(strDate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (isNewest) {
                    compareResult = date2.compareTo(date1);
                } else {
                    compareResult = date1.compareTo(date2);
                }


                return compareResult;
            }
        });

    }


    private void selectEvent(boolean isSelect) {
        if (isSelect) {

            for (int i = 0; i < storageList.size(); i++) {
                storageList.get(i).setSelected(true);
            }
            adapter.notifyDataSetChanged();
            setSelectedFile();
        } else {

            for (int i = 0; i < storageList.size(); i++) {
                storageList.get(i).setSelected(false);
                storageList.get(i).setCheckboxVisible(false);
            }
            adapter.notifyDataSetChanged();
            ll_bottom_option.setVisibility(View.GONE);
            selected_Item = 0;
        }
    }

    private void getList() {

        ArrayList<String> favList = new ArrayList<>();

        favList = PreferencesManager.getFavouriteList(StorageActivity.this);
        if (favList == null)
            favList = new ArrayList<>();

        String filePath = rootPath;
        arrayListFilePaths.add(rootPath);
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isShowHidden) {
                    InternalStorageFilesModel model = new InternalStorageFilesModel();
                    model.setFileName(file.getName());
                    model.setFilePath(file.getPath());

                    if (favList.contains(file.getPath())) {
                        model.setFavorite(true);
                    } else {
                        model.setFavorite(false);
                    }

                    if (file.isDirectory()) {
                        model.setDir(true);
                    } else {
                        model.setDir(false);
                    }
                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                    model.setMineType(getMimeTypeFromFilePath(file.getPath()));

                    storageList.add(model);
                } else {
                    if (!file.getName().startsWith(".")) {
                        InternalStorageFilesModel model = new InternalStorageFilesModel();
                        model.setFileName(file.getName());
                        model.setFilePath(file.getPath());

                        if (file.isDirectory()) {
                            model.setDir(true);
                        } else {
                            model.setDir(false);
                        }

                        if (favList.contains(file.getPath())) {
                            model.setFavorite(true);
                        } else {
                            model.setFavorite(false);
                        }

                        model.setCheckboxVisible(false);
                        model.setSelected(false);

                        model.setMineType(getMimeTypeFromFilePath(file.getPath()));

                        storageList.add(model);
                    }
                }

            }
        }

        if (storageList != null && storageList.size() != 0) {
            int sortType = PreferencesManager.getSortType(StorageActivity.this);
            if (sortType == 1) {
                sortNameAscending();
            } else if (sortType == 2) {
                sortNameDescending();
            } else if (sortType == 3) {
                sortSizeDescending();
            } else if (sortType == 4) {
                sortSizeAscending();
            } else if (sortType == 5) {
                setDateWiseSortAs(true);
            } else if (sortType == 6) {
                setDateWiseSortAs(false);
            } else {
                sortNameAscending();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress_bar.setVisibility(View.GONE);

                setHeaderData();
                setrecycler_viewData();

            }
        });

    }

    private void getFilesList(String filePath) {

        ArrayList<String> favList = new ArrayList<>();

        favList = PreferencesManager.getFavouriteList(StorageActivity.this);
        if (favList == null)
            favList = new ArrayList<>();

        rootPath = filePath;
        //
        //
        // ();

        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isShowHidden) {
                    InternalStorageFilesModel model = new InternalStorageFilesModel();
                    model.setFileName(file.getName());
                    model.setFilePath(file.getPath());

                    if (file.isDirectory()) {
                        model.setDir(true);
                    } else {
                        model.setDir(false);
                    }

                    if (favList.contains(file.getPath())) {
                        model.setFavorite(true);
                    } else {
                        model.setFavorite(false);
                    }
                    model.setCheckboxVisible(false);
                    model.setSelected(false);

                    model.setMineType(getMimeTypeFromFilePath(file.getPath()));

                    storageList.add(model);
                } else {
                    if (!file.getName().startsWith(".")) {
                        InternalStorageFilesModel model = new InternalStorageFilesModel();
                        model.setFileName(file.getName());
                        model.setFilePath(file.getPath());

                        if (file.isDirectory()) {
                            model.setDir(true);
                        } else {
                            model.setDir(false);
                        }

                        if (favList.contains(file.getPath())) {
                            model.setFavorite(true);
                        } else {
                            model.setFavorite(false);
                        }
                        model.setCheckboxVisible(false);
                        model.setSelected(false);

                        model.setMineType(getMimeTypeFromFilePath(file.getPath()));

                        storageList.add(model);
                    }
                }

            }
        }


        if (storageList != null && storageList.size() != 0) {
            int sortType = PreferencesManager.getSortType(StorageActivity.this);
            if (sortType == 1) {
                sortNameAscending();
            } else if (sortType == 2) {
                sortNameDescending();
            } else if (sortType == 3) {
                sortSizeDescending();
            } else if (sortType == 4) {
                sortSizeAscending();
            } else if (sortType == 5) {
                setDateWiseSortAs(true);
            } else if (sortType == 6) {
                setDateWiseSortAs(false);
            } else {
                sortNameAscending();
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (pathAdapter != null) {
            pathAdapter.notifyDataSetChanged();
            setToPathPosition();

        }
        if (storageList != null && storageList.size() != 0) {
            recycler_view.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            recycler_view.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }

        if (lout_search_bar.getVisibility() == View.VISIBLE) {
            backUpstorageList = new ArrayList<>();
            for (int s = 0; s < storageList.size(); s++) {
                backUpstorageList.add(storageList.get(s));
            }
        }
    }

    private void setToPathPosition() {
//        rv_header.scrollToPosition(arrayListFilePaths.size()-1);
        int bottom = rv_header.getAdapter().getItemCount() - 1;
        rv_header.smoothScrollToPosition(bottom);
    }

    private void openFile(File file, InternalStorageFilesModel filesModel) {

        String strMime_type = filesModel.getMineType();
        if (file.isDirectory()) {
            if (file.canRead()) {
                for (int i = 0; i < storageList.size(); i++) {
                    if (storageList.get(i) != null)
                        storageList.get(i).setSelected(false);
                }
                adapter.notifyDataSetChanged();
                storageList.clear();
                arrayListFilePaths.add(filesModel.getFilePath());
                getFilesList(filesModel.getFilePath());
//                adapter.notifyDataSetChanged();

            } else {//Toast to your not openable type
                Toast.makeText(StorageActivity.this, "Folder can't be read!", Toast.LENGTH_SHORT).show();
            }

        } else if (strMime_type != null && ( strMime_type.equalsIgnoreCase("image/jpeg") || strMime_type.equalsIgnoreCase("image/png") || strMime_type.equalsIgnoreCase("image/webp") )) {
           /* Intent intent = new Intent(StorageActivity.this, DisplayImageActivity.class);
            intent.putExtra("path", file.getAbsolutePath());
            startActivity(intent);*/

            PhotoData data = new PhotoData();
            data.setFileName(filesModel.getFileName());
            data.setFilePath(filesModel.getFilePath());

            data.setFavorite(filesModel.isFavorite());

            Constant.displayImageList = new ArrayList<>();
            Constant.displayImageList.add(data);
            Intent intent = new Intent(StorageActivity.this, DisplayImageActivity.class);
            intent.putExtra("pos", 0);
            Constant.arrayListFilePaths = arrayListFilePaths;
            startActivityForResult(intent, videoImage_code);

            /*Uri uri = FileProvider.getUriForFile(StorageActivity.this, getPackageName() + ".provider", file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeTypeFromFilePath(file.getPath()));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open with"));*/

        } else if (strMime_type != null && ( strMime_type.equalsIgnoreCase("video/mp4") || strMime_type.equalsIgnoreCase("video/x-matroska") )) {
         /*   Intent intent = new Intent(StorageActivity.this, VideoViewActivity.class);
            intent.putExtra("path", filesModel.getFilePath());
            startActivity(intent);*/
            /*Uri uri = FileProvider.getUriForFile(StorageActivity.this, getPackageName() + ".provider", file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeTypeFromFilePath(file.getPath()));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open with"));*/

            PhotoData data = new PhotoData();
            data.setFileName(filesModel.getFileName());
            data.setFilePath(filesModel.getFilePath());

            Constant.displayVideoList = new ArrayList<>();
            Constant.displayVideoList.add(data);

            Intent intent = new Intent(StorageActivity.this, VideoPlayActivity.class);
            intent.putExtra("pos", 0);

            Constant.arrayListFilePaths = arrayListFilePaths;
//            intent.putExtra("FilePath", file.getPath());
//            intent.putExtra("FileName", file.getName());
//            intent.putExtra("Duration", imageList.getDuration());
            startActivityForResult(intent, videoImage_code);
        } else if (strMime_type != null && ( strMime_type.equalsIgnoreCase("application/vnd.android.package-archive") )) {


            try {
                Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                String mimeType = getMimeTypeFromFilePath(filesModel.getFilePath());
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(StorageActivity.this, getApplicationContext().getPackageName() + ".provider", new File(filesModel.getFilePath()));
                } else {
                    uri = Uri.fromFile(new File(filesModel.getFilePath()));
                }
                installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.setDataAndType(uri, mimeType);
                startActivity(installIntent);
            } catch (Exception e) {
            }


        } else if (strMime_type != null && ( strMime_type.equalsIgnoreCase("application/zip") )) {
//            Uri uri = FileProvider.getUriForFile(StorageActivity.this, getPackageName() + ".provider", file);
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, getMimeTypeFromFilePath(file.getPath()));
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            startActivity(Intent.createChooser(intent, "Open with"));

            File makeDirectory = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));

            if (!makeDirectory.exists()) {
                makeDirectory.mkdir();
            }

            file = new File(makeDirectory.getPath() + "/.zipExtract");
            if (file.exists()) {
                boolean isDelete1 = StorageUtils.deleteFile(file, StorageActivity.this);

                if (isDelete1) {
                    MediaScannerConnection.scanFile(StorageActivity.this, new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                        }
                    });
                }
            }

            Intent intent = new Intent(StorageActivity.this, OpenZipFileActivity.class);
            intent.putExtra("ZipName", filesModel.getFileName());
            intent.putExtra("ZipPath", filesModel.getFilePath());
            startActivityForResult(intent, zip_open);

//            showZipDialog(filesModel);

        } else {

            Uri uri = FileProvider.getUriForFile(StorageActivity.this, getPackageName() + ".provider", file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeTypeFromFilePath(file.getPath()));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open with"));
        }
    }


    private void setrecycler_viewData() {
        if (storageList != null && storageList.size() != 0) {
            recycler_view.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);

            if (isGrid) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.setMargins(getResources().getDimensionPixelSize(R.dimen._6sdp), 0, getResources().getDimensionPixelSize(R.dimen._6sdp), 0);
                recycler_view.setLayoutParams(lp);

                GridLayoutManager layoutManager = new GridLayoutManager(StorageActivity.this, 4);
                recycler_view.setLayoutManager(layoutManager);
                adapter = new StorageAdapter(StorageActivity.this, storageList, isGrid);
                recycler_view.setAdapter(adapter);
            } else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, 0, 0, 0);
                recycler_view.setLayoutParams(lp);

                LinearLayoutManager layoutManager = new LinearLayoutManager(StorageActivity.this);
                recycler_view.setLayoutManager(layoutManager);
                adapter = new StorageAdapter(StorageActivity.this, storageList, isGrid);
                recycler_view.setAdapter(adapter);
            }

            //  recycler_view.setItemViewCacheSize(storageList.size());
            adapter.setOnItemClickListener(new StorageAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (storageList.get(position).isCheckboxVisible()) {

                        if (storageList.get(position).isSelected()) {
                            storageList.get(position).setSelected(false);
                        } else
                            storageList.get(position).setSelected(true);

                        adapter.notifyDataSetChanged();
                        setSelectedFile();

                    } else {
                        InternalStorageFilesModel filesModel = storageList.get(position);
                        File file = new File(filesModel.getFilePath());//get the selected item path
                        if (lout_search_bar.getVisibility() == View.VISIBLE) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                            if (imm.isAcceptingText()) {
//writeToLog("Software Keyboard was shown");

                                hideSoftKeyboard(edt_search);
                            } else {

// writeToLog("Software Keyboard was not shown");
                            }
                        }
                        openFile(file, filesModel);
                    }
                }
            });

            adapter.setOnLongClickListener(new StorageAdapter.LongClickListener() {
                @Override
                public void onItemLongClick(int position, View v) {
                    if (ll_paste_option.getVisibility() == View.GONE) {
                        storageList.get(position).setSelected(true);
                        for (int i = 0; i < storageList.size(); i++) {
                            if (storageList.get(i) != null)
                                storageList.get(i).setCheckboxVisible(true);
                        }
                        adapter.notifyDataSetChanged();
                        setSelectedFile();
                    }
                }
            });
          /*  adapter.setOnLongClickListener(new StorageAdapter.LongClickListener() {
                @Override
                public void onItemLongClick(int position, View v) {
                   *//* storageList.get(position).setSelected(true);
                    for (int i = 0; i < storageList.size(); i++) {
                        if (storageList.get(i) != null)
                            storageList.get(i).setCheckboxVisible(true);
                    }
                    adapter.notifyDataSetChanged();
                    setSelectedFile();*//*
                }
            });*/


        } else {
            recycler_view.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    boolean isDir = false;

    private void setSelectedFile() {
        int selected = 0;
        isDir = false;
        boolean isSdcardPath = false;

        boolean isSelectZipFile = false;

        boolean isFavourite = false, isShowFavOption = false;

        ArrayList<Integer> favSelectList = new ArrayList<>();
        ArrayList<Integer> favUnSelectList = new ArrayList<>();

        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {
                pos = i;
                selected++;
                if (storageList.get(i).isDir()) {
                    isDir = true;
                }

                if (storageList.get(i).isFavorite()) {
                    favSelectList.add(1);
                } else {
                    favUnSelectList.add(0);
                }

                if (!isSelectZipFile) {
                    String type = storageList.get(i).getMineType();
                    if (type != null)
                        if (type.equalsIgnoreCase("application/zip")) {
                            isSelectZipFile = true;
                        }
                }

                if (!isSdcardPath)
                    if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
                        if (storageList.get(i).getFilePath().contains(sdCardPath)) {
                            isSdcardPath = true;
                        }
                    }
            }
        }


        /*if (favUnSelectList.size() == 0 && favSelectList.size() != 0) {
            isFavourite = true;
        } else if (favUnSelectList.size() != 0) {
            isFavourite = false;
        }*/

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
            ll_bottom_option.setVisibility(View.GONE);
            OnSelected(true, false, selected);
            setSelectionClose();
        } else {
            ll_bottom_option.setVisibility(View.VISIBLE);
            OnSelected(false, true, selected);
        }

        selected_Item = selected;


        if (selected == 1) {

            if (isSelectZipFile) {
                lout_compress.setVisibility(View.GONE);
                lout_extract.setVisibility(View.VISIBLE);
            } else {
                lout_extract.setVisibility(View.GONE);
                lout_compress.setVisibility(View.VISIBLE);
            }

        } else {
            lout_extract.setVisibility(View.GONE);
            lout_compress.setVisibility(View.VISIBLE);
        }

        if (selected == 0) {

            setInvisibleButton(lout_send, img_send, txt_text_send);
            setInvisibleButton(lout_move, img_move, txt_text_move);
            setInvisibleButton(lout_delete, img_delete, txt_text_delete);
            setInvisibleButton(lout_copy, img_copy, txt_text_copy);
            setInvisibleButton(lout_more, img_more, txt_text_more);
            setInvisibleButton(lout_compress, img_compress, txt_text_compress);
            setInvisibleButton(lout_extract, img_extract, txt_text_extract);

            iv_fav_unfill.setVisibility(View.GONE);
            iv_fav_fill.setVisibility(View.GONE);
            ll_favourite.setVisibility(View.GONE);

        } else {
            setVisibleButton(lout_send, img_send, txt_text_send);
            setVisibleButton(lout_move, img_move, txt_text_move);
            setVisibleButton(lout_delete, img_delete, txt_text_delete);
            setVisibleButton(lout_copy, img_copy, txt_text_copy);
            setVisibleButton(lout_more, img_more, txt_text_more);
            setVisibleButton(lout_compress, img_compress, txt_text_compress);
            setVisibleButton(lout_extract, img_extract, txt_text_extract);

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


            if (selected == 1) {
                setVisibleButton(lout_more, img_more, txt_text_more);
            } else {
                if (isDir) {
                    setInvisibleButton(lout_more, img_more, txt_text_more);
                } else {
                    setVisibleButton(lout_more, img_more, txt_text_more);
                }
            }


//            if (isDir) {
//                imgSend.setColorFilter(ContextCompat.getColor(StorageActivity.this, R.color.invisible_color), PorterDuff.Mode.SRC_IN);
//                txt_text_send.setTextColor(getResources().getColor(R.color.invisible_color));
//                lout_send.setClickable(false);
//                lout_send.setEnabled(false);
//            } else {
//                imgSend.setColorFilter(ContextCompat.getColor(StorageActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
//                txt_text_send.setTextColor(getResources().getColor(R.color.black));
//                lout_send.setClickable(true);
//                lout_send.setEnabled(true);
//            }
        }


    }

    public void OnSelected(boolean isShowToolbar, boolean isShowSelected, int selected) {
        if (isShowToolbar) {
            lout_toolbar.setVisibility(View.VISIBLE);
        } else {
            lout_toolbar.setVisibility(View.GONE);
        }

        if (isShowSelected) {
            lout_selected.setVisibility(View.VISIBLE);
        } else {
            lout_selected.setVisibility(View.GONE);
        }

        txt_select.setText(selected + " selected");
        /*if (selected == 0 || selected == 1) {
            txt_select.setText("Selected " + selected + " item");
        } else
            txt_select.setText("Selected " + selected + " items");*/
    }


    public void setHeaderData() {
        if (arrayListFilePaths != null && arrayListFilePaths.size() != 0) {

            LinearLayoutManager manager = new LinearLayoutManager(StorageActivity.this, recycler_view.HORIZONTAL, false);
            rv_header.setLayoutManager(manager);
            pathAdapter = new HeaderListAdapter(StorageActivity.this, arrayListFilePaths);
            rv_header.setAdapter(pathAdapter);

            pathAdapter.setOnItemClickListener(new HeaderListAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.e("path seleted:", arrayListFilePaths.get(position));

                    Log.e("onItemClick", "position: " + position);
                    for (int i = arrayListFilePaths.size() - 1; i > position; i--) {
                        Log.e("onItemClick", "remove index: " + i);
                        arrayListFilePaths.remove(i);
                    }

                    storageList.clear();
                    getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
                    /*adapter.notifyDataSetChanged();
                    pathAdapter.notifyDataSetChanged();*/
                }

                @Override
                public void onItemHeaderClick(int position, View v) {
                    if (isSdCard) {

                        if (lout_storage_option.getVisibility() == View.VISIBLE) {
                            setStorageOptionClose();
                        } else {
                            lout_storage_option.setVisibility(View.VISIBLE);
                        }

                    } else {
                        for (int i = arrayListFilePaths.size() - 1; i > position; i--) {
                            Log.e("onItemClick", "remove index: " + i);
                            arrayListFilePaths.remove(i);
                        }

                        storageList.clear();
                        getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));
                    }
                }

                @Override
                public void onItemHomeClick(int position, View v) {
                    finish();
                }
            });
        }
    }

    protected void hideSoftKeyboard(EditText input) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(edt_search.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        edt_search.requestFocus();
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
                renamed = StorageUtils.renameFile(file, newName, StorageActivity.this);
            } else {
                renamed = file.renameTo(file2);
            }

            if (renamed) {
                Log.e("LOG", "File renamed...");
                MediaScannerConnection.scanFile(StorageActivity.this, new String[]{file2.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                    }
                });
                storageList.get(pos).setFilePath(file2.getPath());
                storageList.get(pos).setFileName(file2.getName());
                adapter.notifyItemChanged(pos);

                RxBus.getInstance().post(new RenameEvent(file, file2));
                Toast.makeText(this, "Rename file successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("LOG", "File not renamed...");
            }
//            storageList.clear();
//            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));

        }
    }


    public void deleteFile() {
        if (storageList != null)
            for (int i = 0; i < storageList.size(); i++) {
                if (storageList.get(i).isSelected()) {

                    File file = new File(storageList.get(i).getFilePath());
                    boolean isDelete = StorageUtils.deleteFile(file, StorageActivity.this);

                 /*   Uri deleteUrl = FileProvider.getUriForFile(StorageActivity.this, getPackageName() + ".provider", file);
                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.delete(deleteUrl, null, null);

                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    if (isDelete) {
                        MediaScannerConnection.scanFile(StorageActivity.this, new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                            }
                        });
                    }

                }
            }


        if (storageList != null) {

            for (int i = 0; i < storageList.size(); i++) {
                storageList.get(i).setCheckboxVisible(false);
                if (storageList.get(i).isSelected()) {
                    storageList.remove(i);

                    if (i != 0) {
                        i--;
                    }
                }
            }


            try {
                if (storageList.size() != 1 && 1 < storageList.size()) {
                    if (storageList.get(1).isSelected()) {
                        storageList.remove(1);
                    }
                }

                if (storageList.size() != 0) {
                    if (storageList.get(0).isSelected()) {
                        storageList.remove(0);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OnSelected(true, false, 0);
                ll_bottom_option.setVisibility(View.GONE);

                if (adapter != null)
                    adapter.notifyDataSetChanged();

                if (loadingDialog != null)
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                if (storageList != null && storageList.size() != 0) {
                    recycler_view.setVisibility(View.VISIBLE);
                    ll_empty.setVisibility(View.GONE);
                } else {
                    recycler_view.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.VISIBLE);
                }

                Toast.makeText(StorageActivity.this, "Delete file successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    int folder_counter = 1;

    private File folderFile(String folderName, String filepath) {
        File newFolder1 = new File(filepath + "/" + folderName + "(" + folder_counter + ")");
        if (!newFolder1.exists()) {
            // newFolder1.mkdir();
            return newFolder1;
        } else {
            folder_counter++;
            File file = folderFile(folderName, filepath);
            return file;
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

        if (backUpstorageList != null && backUpstorageList.size() != 0) {
            for (int i = 0; i < backUpstorageList.size(); i++) {


                InternalStorageFilesModel model = backUpstorageList.get(i);

                if (model.getFilePath() != null) {
                    if (model.getFilePath().equalsIgnoreCase(filePath)) {
                        model.setFavorite(favorite);
                        break;
                    }
                }

            }

        }

        if (storageList != null && storageList.size() != 0) {
            for (int i = 0; i < storageList.size(); i++) {


                InternalStorageFilesModel model = storageList.get(i);

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


    private void updateDeleteImageData(ArrayList<String> deleteList) {
        if (storageList != null && storageList.size() != 0) {
            for (int d = 0; d < deleteList.size(); d++) {

                for (int i = 0; i < storageList.size(); i++) {

                    if (storageList.get(i).getFilePath().equalsIgnoreCase(deleteList.get(d))) {
                        storageList.remove(i);

                        if (i != 0) {
                            i--;
                        }

                        break;
                    }

                }

                try {
                    if (storageList.size() != 1 && 1 < storageList.size()) {
                        if (storageList.get(1).getFilePath().equalsIgnoreCase(deleteList.get(d))) {
                            storageList.remove(1);
                        }
                    }

                    if (storageList.size() != 0) {
                        if (storageList.get(0).getFilePath().equalsIgnoreCase(deleteList.get(d))) {
                            storageList.remove(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if (storageList != null && storageList.size() != 0) {
                recycler_view.setVisibility(View.VISIBLE);
                ll_empty.setVisibility(View.GONE);

            } else {
                recycler_view.setVisibility(View.GONE);
                ll_empty.setVisibility(View.VISIBLE);
            }
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

    @Override
    public void onBottomClick(int sortType) {
        switch (sortType) {

            case 1:
                if (storageList != null && storageList.size() != 0) {
                    sortNameAscending();
                    adapter.notifyDataSetChanged();
                    PreferencesManager.saveToSortType(StorageActivity.this, 1);
                }

                break;
            case 2:
                if (storageList != null && storageList.size() != 0) {
                    sortNameDescending();
                    adapter.notifyDataSetChanged();
                    PreferencesManager.saveToSortType(StorageActivity.this, 2);
                }

                break;

            case 3:
                if (storageList != null && storageList.size() != 0) {
                    sortSizeDescending();
                    adapter.notifyDataSetChanged();
                    PreferencesManager.saveToSortType(StorageActivity.this, 3);
                }
                break;

            case 4:
                if (storageList != null && storageList.size() != 0) {
                    sortSizeAscending();
                    adapter.notifyDataSetChanged();
                    PreferencesManager.saveToSortType(StorageActivity.this, 4);
                }
                break;

            case 5:
                if (storageList != null && storageList.size() != 0) {
                    setDateWiseSortAs(true);
                    adapter.notifyDataSetChanged();

                    PreferencesManager.saveToSortType(StorageActivity.this, 5);
                }
                break;

            case 6:
                if (storageList != null && storageList.size() != 0) {
                    setDateWiseSortAs(false);
                    adapter.notifyDataSetChanged();
                    PreferencesManager.saveToSortType(StorageActivity.this, 6);
                }


                break;

        }
    }

    String extract_file_name;

    private void showExtractDialog() {
        Dialog dialog = new Dialog(this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_compress);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        AppCompatEditText edtFileName;
        LinearLayout btn_cancel, btn_ok;
        AppCompatTextView txt_title;

        edtFileName = dialog.findViewById(R.id.edt_file_name);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok = dialog.findViewById(R.id.btn_ok);
        txt_title = dialog.findViewById(R.id.txt_title);

        txt_title.setText("Extract file");

        edtFileName.setHint("Enter extract file name");

        extractPath = rootPath;
        if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
            if (rootPath.contains(sdCardPath)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
                if (!file.exists()) {
                    file.mkdirs();
                }

                File file2 = new File(file.getPath() + "/" + getString(R.string.extract_file));
                if (!file2.exists()) {
                    file2.mkdirs();
                }

                extractPath = file2.getPath();
            }
        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtFileName.getText().toString().isEmpty()) {

//                    String[] separated = edtFileName.getText().toString().split("\\.");
//                    String strFileName = separated[0];
                    String strFileName = edtFileName.getText().toString();

                    File file = new File(extractPath + "/" + strFileName);
                    if (file.exists()) {
                        // show validation
                        Toast.makeText(StorageActivity.this, "File name already use", Toast.LENGTH_SHORT).show();
                        /*dialog.dismiss();
                        setClose();
                        showCompressValidationDialog();*/
                    } else {
                        extract_file_name = strFileName;
                        dialog.dismiss();

                        if (isFileFromSdCard) {
                            sdCardPermissionType = 4;
                            File file2 = new File(sdCardPath);
                            int mode = StorageUtils.checkFSDCardPermission(file2, StorageActivity.this);
                            if (mode == 2) {
                                Toast.makeText(StorageActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                            } else {
                                setExtract();
                            }
                        } else {
                            setExtract();
                        }


                    }
                } else {
                    Toast.makeText(StorageActivity.this, getResources().getString(R.string.extract_validation), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private void showCompressDialog() {
        Dialog dialog = new Dialog(this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_compress);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        AppCompatEditText edtFileName;
        LinearLayout btn_cancel, btn_ok;

        edtFileName = dialog.findViewById(R.id.edt_file_name);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok = dialog.findViewById(R.id.btn_ok);

        compressPath = rootPath;
        if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
            if (rootPath.contains(sdCardPath)) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
                if (!file.exists()) {
                    file.mkdirs();
                }

                File file2 = new File(file.getPath() + "/" + getString(R.string.compress_file));
                if (!file2.exists()) {
                    file2.mkdirs();
                }

                compressPath = file2.getPath();
            }
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtFileName.getText().toString().isEmpty()) {

                    String[] separated = edtFileName.getText().toString().split("\\.");
                    String strFileName = separated[0];
                    File file = new File(compressPath + "/" + strFileName + ".zip");
                    if (file.exists()) {
                        // show validation
                        Toast.makeText(StorageActivity.this, "File name already use", Toast.LENGTH_SHORT).show();
                        /*dialog.dismiss();
                        setClose();
                        showCompressValidationDialog();*/
                    } else {
                        zip_file_name = strFileName;
                        dialog.dismiss();

                        if (isFileFromSdCard) {
                            sdCardPermissionType = 3;
                            File file2 = new File(sdCardPath);
                            int mode = StorageUtils.checkFSDCardPermission(file2, StorageActivity.this);
                            if (mode == 2) {
                                Toast.makeText(StorageActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                            } else {
                                setcompress();
                            }
                        } else {
                            setcompress();
                        }


                    }
                } else {
                    Toast.makeText(StorageActivity.this, getResources().getString(R.string.zip_validation), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    String zip_file_name;

    private void setExtract() {
        if (loadingDialog != null)
            if (!loadingDialog.isShowing()) {
                loadingDialog.setMessage("Extract file...");
                loadingDialog.show();
            }


        new Thread(this::extractfile).start();

    }

    private void extractfile() {

//        File file = new File(rootPath);
        File file = new File(extractPath + "/" + extract_file_name);

        if (!file.exists()) {
            file.mkdirs();
        }

        File fileZip = new File(storageList.get(pos).getFilePath());

        //ZipArchive zipArchive = new ZipArchive();
        //zipArchive.unzip(fileZip.getPath(), file.getPath(), "");

        String finalZipFilePath = file.getPath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSelectionClose();
                if (finalZipFilePath != null) {
                    storageList.clear();
                    getFilesList(rootPath);

                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    Toast.makeText(StorageActivity.this, "Extract file successfully", Toast.LENGTH_SHORT).show();

                    MediaScannerConnection.scanFile(StorageActivity.this, new String[]{finalZipFilePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                        }
                    });

//                    RxBus.getInstance().post(new CopyMoveEvent(finalZipFilePath));

                }
            }
        });
    }


    private void setcompress() {
        if (loadingDialog != null)
            if (!loadingDialog.isShowing()) {
                loadingDialog.setMessage("Compress file...");
                loadingDialog.show();
            }
        new Thread(this::compressfile).start();
    }


    private void compressfile() {

        String zipName = zip_file_name;
        File from, file3 = null;

        if (selected_Item == 1) {

            from = new File(storageList.get(pos).getFilePath());
            if (!from.exists()) {
                from.mkdir();
            }

        } else {

            File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
            if (!file2.exists()) {
                file2.mkdirs();
            }

            file3 = new File(file2.getPath() + "/" + ".ZIP");
            if (!file3.exists()) {
                file3.mkdirs();
            }

            from = new File(file3.getPath() + "/" + zipName);
            if (!from.exists()) {
                from.mkdir();
            }

            for (int i = 0; i < storageList.size(); i++) {
                if (storageList.get(i) != null) {

                    InternalStorageFilesModel model = storageList.get(i);
                    if (model.isSelected()) {

                        File to = new File(model.getFilePath());
                        String newPath = from.getPath() + "/" + to.getName();
                        File fileee = new File(newPath);
                        StorageUtils.copyFile(to, fileee, StorageActivity.this);

                    }
                }
            }
        }

        String zipFilePath = "";
        if (from != null) {
            if (selected_Item == 1) {
                zipFilePath = compressPath + "/" + zipName + ".zip";

            } else {
                zipFilePath = compressPath + "/" + from.getName() + ".zip";
               /* if (rootPath.contains(sdCardPath)) {
                    String sdrootPath = PreferencesManager.getSDCardTreeUri(StorageActivity.this);
                    zipFilePath = sdrootPath + "/" + from.getName() + ".zip";
                }*/
            }

            //ZipArchive.zip(from.getPath(), zipFilePath, "");
        }


        String finalZipFilePath = zipFilePath;
        File finalFile = file3;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSelectionClose();
                if (finalZipFilePath != null) {

                    MediaScannerConnection.scanFile(StorageActivity.this, new String[]{finalZipFilePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                        }
                    });

                    RxBus.getInstance().post(new CopyMoveEvent(finalZipFilePath));

                    if (selected_Item != 1) {
                        boolean isDelete = StorageUtils.deleteFile(from, StorageActivity.this);
                        if (finalFile != null) {
                            boolean isDelete1 = StorageUtils.deleteFile(finalFile, StorageActivity.this);

                            if (isDelete1) {
                                MediaScannerConnection.scanFile(StorageActivity.this, new String[]{finalFile.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                                    }
                                });
                            }
                        }


                        if (isDelete) {
                            MediaScannerConnection.scanFile(StorageActivity.this, new String[]{from.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                                }
                            });
                        }
                    }

                    storageList.clear();
                    getFilesList(rootPath);
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    Toast.makeText(StorageActivity.this, "Compress file successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
