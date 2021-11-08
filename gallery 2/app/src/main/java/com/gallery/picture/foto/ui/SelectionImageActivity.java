package com.gallery.picture.foto.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gallery.picture.foto.Adapters.PlacesAdapter;
import com.gallery.picture.foto.Fragments.FirstFragment;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.event.CopyMoveEvent;
import com.gallery.picture.foto.event.DisplayDeleteEvent;
import com.gallery.picture.foto.utils.Constant;
import com.gallery.picture.foto.utils.RxBus;
import com.gallery.picture.foto.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.gallery.picture.foto.utils.Constant.pastList;

public class SelectionImageActivity extends AppCompatActivity {
    public List<Object> photoList = new ArrayList<>();
    PlacesAdapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerview;
    TextView collections, create;
    String sdCardPath;
    boolean isFileFromSdCard = false;
    String rootPath, compressPath, extractPath;
    int sdCardPermissionType = 0;
    int folder_counter = 1;
    String newCollection;
    int selected_Item = 0;
    boolean isCheckAll = false;
    String storage_type;
    ImageView backButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_image);
        photoList = FirstFragment.photoList;
        recyclerview = findViewById(R.id.RecyclerView);
        collections = findViewById(R.id.collections);
        //collection = findViewById(R.id.collection);
        create = findViewById(R.id.create);
        backButton = findViewById(R.id.backButton);

        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Constant.isCopyData = true;
        newCollection = (String) getIntent().getExtras().get("newCollection");
        storage_type = getIntent().getStringExtra("type");

        collections.setText("0 selected");
        getSupportActionBar().hide();

        for (int i = 0; i < photoList.size(); i++) {
            if (photoList.get(i) != null)

                if (photoList.get(i) instanceof PhotoData) {

                    PhotoData model = (PhotoData) photoList.get(i);
                    model.setCheckboxVisible(true);
                    model.setSelected(false);

                }

        }

        setAdapter();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSdcardPath = false;
                pastList.clear();

               for (int i = 0; i < photoList.size(); i++) {
                   if (photoList.get(i) instanceof PhotoData) {
                        PhotoData model = (PhotoData) photoList.get(i);

                        if(model.isSelected()){
                File file = new File(model.getFilePath());
                if (file.exists()) {
                    pastList.add(file);
                    if (!isSdcardPath) {
                        if (sdCardPath != null && !sdCardPath.equalsIgnoreCase("")) {
                            if (file.getPath().contains(sdCardPath)) {
                                isSdcardPath = true;
                            }
                        }
                    }}}

                }}

                if (pastList != null && pastList.size() != 0) {

                    if (isFileFromSdCard) {
                        sdCardPermissionType = 2;
                        File file = new File(sdCardPath);
                        int mode = StorageUtils.checkFSDCardPermission(file, SelectionImageActivity.this);
                        if (mode == 2) {
                            Toast.makeText(SelectionImageActivity.this, "Please give a permission for manager operation", Toast.LENGTH_SHORT).show();
                        } else {
                            setCopyMoveAction();
                        }
                    } else {
                        setCopyMoveAction();
                    }


                }
                setSelectionClose();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setAdapter() {

        if (photoList != null && photoList.size() != 0) {
            recyclerview.setVisibility(View.VISIBLE);


            gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
            recyclerview.setLayoutManager(gridLayoutManager);
            adapter = new PlacesAdapter(this, photoList);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(final int position) {
                    if (adapter.getItemViewType(position) == PlacesAdapter.ITEM_HEADER_TYPE) {
                        return 4;
                    }
                    return 1;
                }
            });
//            recyclerView.setItemViewCacheSize(15);
            recyclerview.setAdapter(adapter);
            recyclerview.hasFixedSize();

            adapter.setOnItemClickListener(new PlacesAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {




                    try {

                        if (photoList.get(position) instanceof PhotoData) {

                            PhotoData imageList = (PhotoData) photoList.get(position);

                            if (imageList.isSelected()) {
                                imageList.setSelected(false);
                            } else{
                                imageList.setSelected(true);

                                }


                            adapter.notifyItemChanged(position);
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
                        //pos = i;

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

            /*setInvisibleButton(lout_send, img_send, txt_text_send);
            setInvisibleButton(lout_move, img_move, txt_text_move);
            setInvisibleButton(lout_delete, img_delete, txt_text_delete);
            setInvisibleButton(lout_copy, img_copy, txt_text_copy);
            setInvisibleButton(lout_more, img_more, txt_text_more);
            setInvisibleButton(lout_compress, img_compress, txt_text_compress);*/
            if (favUnSelectList.size() == 0 && favSelectList.size() != 0) {
                isFavourite = true;
            } else if (favUnSelectList.size() != 0) {
                isFavourite = false;
            }
            //ll_favourite.setVisibility(View.GONE);

        } else {
           /* setVisibleButton(lout_send, img_send, txt_text_send);
            setVisibleButton(lout_move, img_move, txt_text_move);
            setVisibleButton(lout_delete, img_delete, txt_text_delete);
            setVisibleButton(lout_copy, img_copy, txt_text_copy);
            setVisibleButton(lout_more, img_more, txt_text_more);
            setVisibleButton(lout_compress, img_compress, txt_text_compress);*/


            /*if (isShowFavOption) {
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
            }*/

        }

       /* if (selected == 0) {

            OnSelected(true, false, selected);
            setClose();
        } else {
            OnSelected(false, true, selected);

        }*/

        if (selected == 0) {
            OnSelected(true, false, selected);
            //setSelectionClose();
        } else {
            OnSelected(false, true, selected);
        }
//        OnSelected(false, true, selected);
        selected_Item = selected;
    }

    @Override
    public void onBackPressed() {

            setSelectionClose();

            super.onBackPressed();
    }

    private void setSelectionClose() {
        setClose();
        isCheckAll = false;
        //iv_check_all.setVisibility(View.GONE);
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
        /*if (isShowToolbar) {

        } else {

        }


        if (isShowSelected) {
            lout_selected.setVisibility(View.VISIBLE);
            ll_bottom_option.setVisibility(View.VISIBLE);
        } else {
            lout_selected.setVisibility(View.GONE);
            ll_bottom_option.setVisibility(View.GONE);
        }*/
        collections.setText(selected + " selected"+ "");
        //collection.setText(selected+ " selected out of 9");
    }

    public void setCopyMoveAction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                moveFile();
            }
        }).start();

    }

    private void moveFile() {
//        File file = new File(rootPath);

        ArrayList<File> moveFileList = new ArrayList<>();
        ArrayList<String> deleteFileList = new ArrayList<>();

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

                    boolean isMove = StorageUtils.moveFile(to, file1, SelectionImageActivity.this);
                    if (isMove) {
                        moveFileList.add(file1);
                        deleteFileList.add(file1.getPath());
                    }
//                    Utils.moveDirectory(to, file1, StorageActivity.this);

                    Log.e("move", "file is move");


                } else {
//                    File file1 = Utils.moveFile(to, from);
//                    moveFileList.add(file1);

                    /*String newPath = from.getPath() + "/Collections" +"/"+ to.getName();

                    File fileee = new File(newPath);*/


                    File file1 = new File(from.getPath() + "/" + getString(R.string.app_name));
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    File output = new File(file1.getPath() + "/Collections");

                    if (!output.exists()) {
                        output.mkdirs();
                    }

                    File output1 = new File(output.getPath()+"/"+newCollection);
                    if(!output1.exists()){
                        output1.mkdirs();
                    }

                    File fileee = new File(output1.getPath()+ "/"+to.getName());

                    Log.d("fileee", fileee.getPath().toString());

                    if (fileee.exists()) {
                        String[] separated = to.getName().split("\\.");
                        String name = separated[0];
                        String type = separated[1];

                        String newPath2 = from.getPath() + "/" + name + "_" + System.currentTimeMillis() + "." + type;
                        File file2 = new File(newPath2);
                        boolean isMove = StorageUtils.moveFile(to, file2, SelectionImageActivity.this);

                        if (isMove) {
                            moveFileList.add(file2);
                            deleteFileList.add(file.getPath());
                        }

                    } else {
                        boolean isMove = StorageUtils.moveFile(to, fileee, SelectionImageActivity.this);
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

                        Constant.pastList = new ArrayList<>();
                        pastList = new ArrayList<>();
                        //ll_paste_option.setVisibility(View.GONE);

                        Toast.makeText(SelectionImageActivity.this, "Move file successfully", Toast.LENGTH_SHORT).show();
                        if (storage_type.equalsIgnoreCase("CopyMove")) {

                            RxBus.getInstance().post(new CopyMoveEvent(moveFileList, 2, deleteFileList));
                            RxBus.getInstance().post(new DisplayDeleteEvent(deleteFileList));
                            // moveFileList ,deleteFileList
                            finish();
                        } else {

                        }

                    }
                }, 30);

            }
        });
    }




}