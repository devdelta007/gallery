package com.gallery.picture.foto.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import com.gallery.picture.foto.Adapters.ZipHeaderListAdapter;
import com.gallery.picture.foto.Adapters.ZipStorageAdapter;
import com.gallery.picture.foto.Model.InternalStorageFilesModel;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.gallery.picture.foto.utils.StorageUtils;
import com.gallery.picture.foto.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gallery.picture.foto.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import static com.gallery.picture.foto.utils.Utils.getMimeTypeFromFilePath;

public class OpenZipFileActivity extends AppCompatActivity {

    AppCompatImageView ivBack;
    RelativeLayout loutToolbar;
    RecyclerView rvHeader;
    RecyclerView recyclerView;
    LinearLayout llEmpty;
    ProgressBar progressBar;
    TextView txtHeaderTitle;
    AppCompatTextView btnCancel;
    AppCompatTextView btnExtract;
    RelativeLayout loutSelected;
    LinearLayout llBottomOption;
    ImageView ivClose;
    AppCompatTextView txtSelect;
    ImageView ivUncheck;
    ImageView ivCheckAll;
    RelativeLayout llCheckAll;
    RelativeLayout llFavourite, ll_check_all;


    String headerName, path, rootPath;
    ArrayList<InternalStorageFilesModel> storageList = new ArrayList<>();
    ArrayList<String> arrayListFilePaths = new ArrayList<>();

    ZipHeaderListAdapter pathAdapter;
    ZipStorageAdapter adapter;
    ProgressDialog loadingDialog;

    File file;
    int selected_Item = 0;
    boolean isShowHidden = false;

    boolean isCheckAll = true;

    String sdCardPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_zip_file);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivBack = findViewById(R.id.iv_back);
        loutToolbar = findViewById(R.id.lout_toolbar);
        rvHeader = findViewById(R.id.rv_header);
        recyclerView = findViewById(R.id.recycler_view);
        llEmpty = findViewById(R.id.ll_empty);
        progressBar = findViewById(R.id.progress_bar);
        txtHeaderTitle = findViewById(R.id.txt_header_title);
        btnCancel = findViewById(R.id.btn_cancel);
        btnExtract = findViewById(R.id.btn_extract);
        loutSelected = findViewById(R.id.lout_selected);
        llBottomOption = findViewById(R.id.ll_bottom_option);
        ivClose = findViewById(R.id.iv_close);
        txtSelect = findViewById(R.id.txt_select);
        ivUncheck = findViewById(R.id.iv_uncheck);
        ivCheckAll = findViewById(R.id.iv_check_all);
        llFavourite = findViewById(R.id.ll_favourite);
        ll_check_all = findViewById(R.id.ll_check_all);


        initView();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_Item != 0)
                    setExtractZip();
                else
                    Toast.makeText(OpenZipFileActivity.this, "please select file", Toast.LENGTH_SHORT).show();
            }
        });
        ll_check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckAll) {
                    isCheckAll = false;
                    selectEvent(false);
                    ivCheckAll.setVisibility(View.GONE);

                } else {
                    isCheckAll = true;
                    selectEvent(true);
                    ivCheckAll.setVisibility(View.VISIBLE);
                }
            }
        });





    }

    private void initView() {
        Intent intent = getIntent();
        headerName = intent.getStringExtra("ZipName");
        path = intent.getStringExtra("ZipPath");

        txtHeaderTitle.setText(headerName);
        isShowHidden = PreferencesManager.getShowHidden(this);
        llFavourite.setVisibility(View.GONE);

        ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
        progressBar.setVisibility(View.VISIBLE);


        sdCardPath = Utils.getExternalStoragePath(this, true);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage("Delete file...");
        loadingDialog.setCanceledOnTouchOutside(false);

        llBottomOption.setVisibility(View.GONE);
        if (isCheckAll) {
            ivCheckAll.setVisibility(View.VISIBLE);
        } else {
            ivCheckAll.setVisibility(View.GONE);
        }

        new Thread(this::getZipOpen).start();
    }

    private HashMap<String, List<String>> openZipFile(String path) {

        HashMap<String, List<String>> contents = new HashMap<>();

        ZipFile zip = null;
        try {
            zip = new ZipFile(path);

            int i = 0;

            for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
//            System.out.println(entry);
//            System.out.println(i);

                Log.e("ZipFileOpenTop", "file name: " + entry.getName());
                if (entry.isDirectory()) {

                    String directory = entry.getName();

                    String[] separated = directory.split("/");
                    int size = separated.length;
                    String dirName = separated[0];

                    String file = entry.getName();
                    int pos = file.lastIndexOf("/");
                    String fileName = separated[separated.length - 1];
//                    String fileName = file.substring(pos + 1);


                    Log.e("ZipFileOpenDir", "length: " + separated.length);
                    if (size == 2 /*|| size == 1*/) {

                        String lastname = separated[separated.length - 1];

                        Log.e("ZipFileOpenDir", "directory name: " + dirName + "      fileName: " + fileName + "      lastname: " + lastname);
//                        if (fileName.equalsIgnoreCase(lastname)) {

                        if (!contents.containsKey(dirName)) {
                            List<String> fileNames = contents.get(dirName);
                            if (fileNames == null)
                                fileNames = new ArrayList<>();

                            if (fileName != null && !fileName.equalsIgnoreCase(""))
                                if (!fileNames.contains(fileName)) {
                                    fileNames.add(fileName);
                                }

//                            contents.put(dirName, fileNames);

                            contents.put(dirName, fileNames);
                            Log.e("ZipFile", "directory name is: " + dirName);
                        } else {
                            List<String> fileNames = contents.get(dirName);
                            if (fileNames == null)
                                fileNames = new ArrayList<>();
//                            Log.e("ZipFileOpenDir", "directory name: " + dirName + "      fileName: " + fileName + "      lastname: " + lastname);

                            if (fileName != null && !fileName.equalsIgnoreCase(""))
                                if (!fileNames.contains(fileName)) {
                                    fileNames.add(fileName);
                                }

                            contents.put(dirName, fileNames);
                        }

//                        }

                    }


                } else {
                    String file = entry.getName();
                    int pos = file.lastIndexOf("/");
                    String[] separated = file.split("/");

                    int size = separated.length;

                    Log.e("ZipFileOpen", "length: " + separated.length);

                    if (size == 2 || size == 1) {
                        String dirName = separated[0];
//                        String fileName = file.substring(pos + 1);

                        String fileName = separated[separated.length - 1];
                        String lastname = separated[separated.length - 1];

                        Log.e("ZipFileOpen", "directory name: " + dirName + "    fileName: " + fileName + "    lastname: " + lastname);

//                        if (fileName.equalsIgnoreCase(lastname)) {


                        if (!contents.containsKey(dirName)) {

                            List<String> fileNames = contents.get(dirName);
                            if (fileNames == null)
                                fileNames = new ArrayList<>();


                            if (fileName != null && !fileName.equalsIgnoreCase(""))
                                if (!fileNames.contains(fileName)) {
                                    fileNames.add(fileName);
                                }

                            contents.put(dirName, fileNames);

                        } else {
                            List<String> fileNames = contents.get(dirName);
                            if (fileNames == null)
                                fileNames = new ArrayList<>();

                            if (fileName != null && !fileName.equalsIgnoreCase(""))
                                if (!fileNames.contains(fileName)) {
                                    fileNames.add(fileName);
                                }
                            contents.put(dirName, fileNames);
                        }
//                        }
                    }
                  /*  if (pos != -1) {
                        String directory = file.substring(0, pos + 1);
                        String fileName = file.substring(pos + 1);
                        if (!contents.containsKey(directory)) {
                            contents.put(directory, new ArrayList<String>());
                            List<String> fileNames = contents.get(directory);
                            fileNames.add(fileName);
                        } else {
                            List<String> fileNames = contents.get(directory);
                            fileNames.add(fileName);
                        }
                    } else {
                        if (!contents.containsKey("root")) {
                            contents.put("root", new ArrayList<String>());
                        }
                        List<String> fileNames = contents.get("root");
                        fileNames.add(file);
                    }*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }


    @Override
    public void onBackPressed() {
        if (arrayListFilePaths.size() == 1) {
//            if (file.exists()) {
//                boolean isDelete1 = StorageUtils.deleteFile(file, OpenZipFileActivity.this);
//
//                if (isDelete1) {
//                    MediaScannerConnection.scanFile(OpenZipFileActivity.this, new String[]{file.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
//                        }
//                    });
//                }
//            }
            super.onBackPressed();

        } else if (arrayListFilePaths.size() != 0 && arrayListFilePaths.size() != 1) {
            arrayListFilePaths.remove(arrayListFilePaths.size() - 1);
            storageList.clear();
            getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 1));

        } else {
            super.onBackPressed();
        }
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
//                storageList.get(i).setCheckboxVisible(false);
            }
            adapter.notifyDataSetChanged();
            llBottomOption.setVisibility(View.GONE);
            selected_Item = 0;
            OnSelected(false, true, selected_Item);
        }
    }

    String extract_file_name;
    String extract_path;

    public void setExtractZip() {

        File file = new File(path);
        extract_path = file.getParent();

        if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
            if (path.contains(sdCardPath)) {
                File file222 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
                if (!file222.exists()) {
                    file222.mkdirs();
                }

                File file2 = new File(file222.getPath() + "/" + getString(R.string.extract_file));
                if (!file2.exists()) {
                    file2.mkdirs();
                }

                extract_path = file2.getPath();
            }
        }

        String[] separated = headerName.split("\\.");
        String strFileName = separated[0];

        File file1 = new File(extract_path + "/" + strFileName);
        if (file1.exists()) {
//            File newFolder = createUnZipFile(strFileName, extract_path);
//            extract_file_name = newFolder.getName();
//            setExtract();
            showExtractDialog(strFileName);
        } else {
            extract_file_name = strFileName;
            setExtract();
        }
    }

    public void showExtractDialog(String strFileName) {
        Dialog dialog = new Dialog(this, R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_rename_zip);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        AppCompatTextView txt_msg;
        LinearLayout btn_skip, btn_replace, btn_rename;


        txt_msg = dialog.findViewById(R.id.txt_msg);
        btn_skip = dialog.findViewById(R.id.btn_skip);
        btn_replace = dialog.findViewById(R.id.btn_replace);
        btn_rename = dialog.findViewById(R.id.btn_rename);

        txt_msg.setText(getResources().getString(R.string.str_extract_validation_1) + " " + strFileName + " " + getResources().getString(R.string.str_extract_validation_2));

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(OpenZipFileActivity.this, "Extraction cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        btn_replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extract_file_name = strFileName;
                File file1 = new File(extract_path + "/" + strFileName);
                dialog.dismiss();
                if (loadingDialog != null)
                    if (!loadingDialog.isShowing()) {
                        loadingDialog.setMessage("Extract file...");
                        loadingDialog.show();
                    }

                boolean isDelete1 = StorageUtils.deleteFile(file1, OpenZipFileActivity.this);

                if (isDelete1) {
                    MediaScannerConnection.scanFile(OpenZipFileActivity.this, new String[]{file1.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                            setExtract();
                        }
                    });
                }

            }
        });
        btn_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                File newFolder = createUnZipFile(strFileName, extract_path);
                extract_file_name = newFolder.getName();
                setExtract();
            }
        });

        dialog.show();
    }

    int zip_counter = 0;

    private File createUnZipFile(String strFile, String filepath) {
        zip_counter = 1;
        File newFolder = new File(filepath + "/" + strFile);
        if (!newFolder.exists()) {
            newFolder.mkdir();
            return newFolder;
        } else {
            return UnZipFile(strFile, filepath);
        }
    }

    private File UnZipFile(String folderName, String filepath) {
        File newFolder1 = new File(filepath + "/" + folderName + "(" + zip_counter + ")");
        if (!newFolder1.exists()) {
            newFolder1.mkdir();
            return newFolder1;
        } else {
            zip_counter++;
            File file = UnZipFile(folderName, filepath);
            return file;
        }
    }

    public void getZipOpen() {
        File makeDirectory = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));

        if (!makeDirectory.exists()) {
            makeDirectory.mkdir();
        }

        file = new File(makeDirectory.getPath() + "/.zipExtract");
        if (!file.exists()) {
            file.mkdir();
        }
        rootPath = file.getPath();

      /*  File file1 = new File(path);

        String type = getMimeTypeFromFilePath(file1.getPath());


        if (type != null && type.equalsIgnoreCase("application/zip")) {
            ZipArchive zipArchive = new ZipArchive();
            zipArchive.unzip(path, file.getPath(), "");

        }

        if (type != null && type.equalsIgnoreCase("application/rar")) {

            RarArchive rarArchive = new RarArchive();
            rarArchive.extractArchive(path, file.getPath());

        }*/

        HashMap<String, List<String>> hashMap = openZipFile(path);
        if (hashMap != null) {

        }

        Set<String> keys = hashMap.keySet();
        ArrayList<String> listkeys = new ArrayList<>();
        listkeys.addAll(keys);

        if (listkeys != null)

            for (int i = 0; i < listkeys.size(); i++) {

                if (listkeys.size() == 1) {

                    String[] separated = listkeys.get(i).split("\\.");
                    String mimeType = separated[separated.length - 1];

                    String type = getMimeTypeFromFilePath(listkeys.get(i));

                    InternalStorageFilesModel model = new InternalStorageFilesModel();
                    model.setFileName(listkeys.get(i));
                    model.setFilePath(listkeys.get(i));
                    model.setFavorite(false);

                    model.setCheckboxVisible(true);
                    model.setSelected(true);

                    if (type == null) {

                        List<String> innerFileList = hashMap.get(listkeys.get(i));
                        if (innerFileList == null)
                            innerFileList = new ArrayList<>();


                        for (int f = 0; f < innerFileList.size(); f++) {

                            InternalStorageFilesModel filesModel = new InternalStorageFilesModel();

                            filesModel.setFileName(innerFileList.get(f));
                            filesModel.setFilePath(innerFileList.get(f));
                            filesModel.setFavorite(false);

                            filesModel.setCheckboxVisible(true);
                            filesModel.setSelected(true);
                            selected_Item++;

                            String[] separated1 = innerFileList.get(f).split("\\.");
                            String mimeType1 = separated1[separated1.length - 1];


                            String type1 = getMimeTypeFromFilePath(innerFileList.get(f));

                            if (type1 == null) {
                                if (mimeType != null && !mimeType.equalsIgnoreCase(listkeys.get(i))) {
                                    filesModel.setDir(false);
                                } else {
                                    filesModel.setDir(true);
                                }
                            } else {
                                filesModel.setDir(false);
                            }

                            Log.e("listkeys", "name" + listkeys.get(i) + " type " + type);
                            filesModel.setMineType(type1);

                            storageList.add(filesModel);
                        }


                    } else {

                        model.setDir(false);
                        model.setMineType(type);
                        selected_Item++;
                        storageList.add(model);
                    }

//                    if (type == null) {
//                        if (mimeType != null && !mimeType.equalsIgnoreCase(listkeys.get(i))) {
//                            model.setDir(false);
//                        } else {
//                            model.setDir(true);
//                        }
//                    } else {
//                        model.setDir(false);
//                    }

                } else {


//                Boolean isDirectory = hashMap.get(listkeys.get(i));
                    InternalStorageFilesModel model = new InternalStorageFilesModel();
                    model.setFileName(listkeys.get(i));
                    model.setFilePath(listkeys.get(i));
                    model.setFavorite(false);

                    model.setCheckboxVisible(true);
                    model.setSelected(true);
                    selected_Item++;

                    String[] separated = listkeys.get(i).split("\\.");
                    String mimeType = separated[separated.length - 1];

                    String type = getMimeTypeFromFilePath(listkeys.get(i));

                    if (type == null) {
                        if (mimeType != null && !mimeType.equalsIgnoreCase(listkeys.get(i))) {
                            model.setDir(false);
                        } else {
                            model.setDir(true);
                        }
                    } else {
                        model.setDir(false);
                    }

                    Log.e("listkeys", "name" + listkeys.get(i) + " type " + type);
                    model.setMineType(type);

                    storageList.add(model);
                }

            }

        arrayListFilePaths.add(rootPath);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                setHeaderData();
                setRecyclerViewData();
            }
        });

//        getList();
    }


    private void setRecyclerViewData() {
        if (storageList != null && storageList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);

            if (selected_Item == 0) {
                llBottomOption.setVisibility(View.GONE);

            } else {
                llBottomOption.setVisibility(View.VISIBLE);
            }

            llBottomOption.setVisibility(View.VISIBLE);
            OnSelected(false, true, selected_Item);

//            if (isGrid) {
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                lp.setMargins(getResources().getDimensionPixelSize(R.dimen._6sdp), 0, getResources().getDimensionPixelSize(R.dimen._6sdp), 0);
//                recyclerView.setLayoutParams(lp);
//
//                GridLayoutManager layoutManager = new GridLayoutManager(StorageActivity.this, 4);
//                recyclerView.setLayoutManager(layoutManager);
//                adapter = new ZipStorageAdapter(StorageActivity.this, storageList, isGrid);
//                recyclerView.setAdapter(adapter);
//            } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, 0);
            recyclerView.setLayoutParams(lp);

            LinearLayoutManager layoutManager = new LinearLayoutManager(OpenZipFileActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ZipStorageAdapter(OpenZipFileActivity.this, storageList, false);
            recyclerView.setAdapter(adapter);
//            }

            //  recyclerView.setItemViewCacheSize(storageList.size());
            adapter.setOnItemClickListener(new ZipStorageAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {

//                    InternalStorageFilesModel filesModel = storageList.get(position);
//                    File file = new File(filesModel.getFilePath());//get the selected item path
//                    openFile(file, filesModel);
                    if (storageList.get(position).isCheckboxVisible()) {

                        if (storageList.get(position).isSelected()) {
                            storageList.get(position).setSelected(false);
                        } else
                            storageList.get(position).setSelected(true);

                        adapter.notifyDataSetChanged();
                        setSelectedFile();

                    }

                }
            });

            adapter.setOnLongClickListener(new ZipStorageAdapter.LongClickListener() {
                @Override
                public void onItemLongClick(int position, View v) {

                }
            });


        } else {
            recyclerView.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
            llBottomOption.setVisibility(View.GONE);

            if (selected_Item == 0) {
                llBottomOption.setVisibility(View.GONE);

            } else {
                llBottomOption.setVisibility(View.VISIBLE);
            }

            OnSelected(true, false, selected_Item);
        }
    }

    private void setSelectedFile() {
        int selected = 0;
        int allItemm = storageList.size();

        ArrayList<Integer> favSelectList = new ArrayList<>();
        ArrayList<Integer> favUnSelectList = new ArrayList<>();

        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).isSelected()) {
                selected++;
            }
        }


        llBottomOption.setVisibility(View.VISIBLE);
        OnSelected(false, true, selected);

        selected_Item = selected;
        if (selected == 0) {
            llBottomOption.setVisibility(View.GONE);

        } else {
            llBottomOption.setVisibility(View.VISIBLE);
        }

        if (allItemm == selected_Item) {
            isCheckAll = true;
        } else {
            isCheckAll = false;
        }

        if (isCheckAll) {
            ivCheckAll.setVisibility(View.VISIBLE);
        } else {
            ivCheckAll.setVisibility(View.GONE);
        }

    }

    public void OnSelected(boolean isShowToolbar, boolean isShowSelected, int selected) {
        if (isShowToolbar) {
            loutToolbar.setVisibility(View.VISIBLE);
        } else {
            loutToolbar.setVisibility(View.GONE);
        }

        if (isShowSelected) {
            loutSelected.setVisibility(View.VISIBLE);
        } else {
            loutSelected.setVisibility(View.GONE);
        }

        txtSelect.setText(selected + " selected");
        /*if (selected == 0 || selected == 1) {
            txtSelect.setText("Selected " + selected + " item");
        } else
            txtSelect.setText("Selected " + selected + " items");*/
    }

    public void setHeaderData() {
        if (arrayListFilePaths != null && arrayListFilePaths.size() != 0) {

            LinearLayoutManager manager = new LinearLayoutManager(OpenZipFileActivity.this, RecyclerView.HORIZONTAL, false);
            rvHeader.setLayoutManager(manager);
            pathAdapter = new ZipHeaderListAdapter(OpenZipFileActivity.this, arrayListFilePaths);
            rvHeader.setAdapter(pathAdapter);

            pathAdapter.setOnItemClickListener(new ZipHeaderListAdapter.ClickListener() {
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
                }
            });
        }
    }

    private void getList() {

        ArrayList<String> favList = new ArrayList<>();

        favList = PreferencesManager.getFavouriteList(OpenZipFileActivity.this);
        if (favList == null)
            favList = new ArrayList<>();

        String filePath = rootPath;
//        arrayListFilePaths.add(rootPath);
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

//        if (storageList != null && storageList.size() != 0) {
//            int sortType = PreferencesManager.getSortType(OpenZipFileActivity.this);
//            if (sortType == 1) {
//                sortNameAscending();
//            } else if (sortType == 2) {
//                sortNameDescending();
//            } else if (sortType == 3) {
//                sortSizeDescending();
//            } else if (sortType == 4) {
//                sortSizeAscending();
//            } else if (sortType == 5) {
//                setDateWiseSortAs(true);
//            } else if (sortType == 6) {
//                setDateWiseSortAs(false);
//            } else {
//                sortNameAscending();
//            }
//        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                setHeaderData();
                setRecyclerViewData();
            }
        });

    }


    private void getFilesList(String filePath) {

        ArrayList<String> favList = new ArrayList<>();

        favList = PreferencesManager.getFavouriteList(OpenZipFileActivity.this);
        if (favList == null)
            favList = new ArrayList<>();

        rootPath = filePath;


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


//        if (storageList != null && storageList.size() != 0) {
//            int sortType = PreferencesManager.getSortType(StorageActivity.this);
//            if (sortType == 1) {
//                sortNameAscending();
//            } else if (sortType == 2) {
//                sortNameDescending();
//            } else if (sortType == 3) {
//                sortSizeDescending();
//            } else if (sortType == 4) {
//                sortSizeAscending();
//            } else if (sortType == 5) {
//                setDateWiseSortAs(true);
//            } else if (sortType == 6) {
//                setDateWiseSortAs(false);
//            } else {
//                sortNameAscending();
//            }
//        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (pathAdapter != null) {
            pathAdapter.notifyDataSetChanged();
            setToPathPosition();

        }
        if (storageList != null && storageList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }

    }

    private void setToPathPosition() {
//        rvHeader.scrollToPosition(arrayListFilePaths.size()-1);
        int bottom = rvHeader.getAdapter().getItemCount() - 1;
        rvHeader.smoothScrollToPosition(bottom);
    }


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
        File file = new File(extract_path + "/" + extract_file_name);

        if (!file.exists()) {
            file.mkdirs();
        }

        File fileZip = new File(path);

        //ZipArchive zipArchive = new ZipArchive();
        //zipArchive.unzip(fileZip.getPath(), file.getPath(), "");

        String finalZipFilePath = file.getPath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalZipFilePath != null) {

                    MediaScannerConnection.scanFile(OpenZipFileActivity.this, new String[]{finalZipFilePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                        }
                    });

                    if (isCheckAll) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Toast.makeText(OpenZipFileActivity.this, "Extract file successfully", Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    } else {
//                        ArrayList<InternalStorageFilesModel> zipFileList = new ArrayList<>();
                        getZipDataList(finalZipFilePath);

                        if (zipFileList != null && zipFileList.size() != 0) {

                            for (int i = 0; i < storageList.size(); i++) {

                                if (!storageList.get(i).isSelected()) {

                                    for (int z = 0; z < zipFileList.size(); z++) {

                                        if (zipFileList.get(z).getFileName().equalsIgnoreCase(storageList.get(i).getFileName())) {

                                            File file1 = new File(zipFileList.get(z).getFilePath());
                                            boolean isDelete = StorageUtils.deleteFile(file1, OpenZipFileActivity.this);

                                            if (isDelete) {
                                                MediaScannerConnection.scanFile(OpenZipFileActivity.this, new String[]{file1.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                                    public void onScanCompleted(String path, Uri uri) {
                                                        // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                                                    }
                                                });
                                            }

                                            break;

                                        }
                                    }

                                }


                            }

                        }

                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Toast.makeText(OpenZipFileActivity.this, "Extract file successfully", Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();

                    }

//                    RxBus.getInstance().post(new CopyMoveEvent(finalZipFilePath));

                }
            }
        });
    }

    ArrayList<InternalStorageFilesModel> zipFileList = new ArrayList<>();

    private void getZipDataList(String filePath) {

        zipFileList = new ArrayList<>();
        getZipList(filePath);

//        return zipFileList;

    }

    private void getZipList(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {

                InternalStorageFilesModel model = new InternalStorageFilesModel();
                model.setFileName(file.getName());
                model.setFilePath(file.getPath());

                if (file.isDirectory()) {
                    model.setDir(true);
                } else {
                    model.setDir(false);
                }
                model.setCheckboxVisible(false);
                model.setSelected(false);

                model.setMineType(getMimeTypeFromFilePath(file.getPath()));

                zipFileList.add(model);

                if (file.isDirectory()) {
                    getZipList(file.getPath());
                }
            }
        }
    }

}