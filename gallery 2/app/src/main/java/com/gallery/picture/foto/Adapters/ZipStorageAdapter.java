package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.Model.InternalStorageFilesModel;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ZipStorageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private static ClickListener listener;
    private static LongClickListener longClickListener;
    boolean isGrid = false;
    ArrayList<InternalStorageFilesModel> internalStorageList = new ArrayList<>();

    public static final int TYPE_AD = 2;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_ITEM_GRID = 1;


    public ZipStorageAdapter(Context context, ArrayList<InternalStorageFilesModel> internalStorageList, boolean isGrid) {
        this.context = context;
        this.internalStorageList = internalStorageList;
        this.isGrid = isGrid;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.listener = clickListener;
    }

    public interface LongClickListener {
        void onItemLongClick(int position, View v);
    }

    public void setOnLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (internalStorageList.get(position) == null) {
            return TYPE_AD;
        } else if (isGrid) {
            return TYPE_ITEM_GRID;
        } else
            return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_storage_list, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_AD) {
          /*  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_ads, parent, false);
            return new BannerViewHolder(view);*/

        } else if (viewType == TYPE_ITEM_GRID) {
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_storage_grid, parent, false);
            return new GridHolder(view1);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case TYPE_ITEM:
                    try {

                        ViewHolder viewholder = (ViewHolder) holder;
                        InternalStorageFilesModel filesModel = internalStorageList.get(position);

                        viewholder.txtFolderName.setText(filesModel.getFileName());

                        File file = new File(filesModel.getFilePath());

                        String mimeType = Utils.getFilenameExtension(file.getName());

                        viewholder.iv_image.setVisibility(View.GONE);

                        if (internalStorageList.get(position).isCheckboxVisible()) {
                            viewholder.ll_check.setVisibility(View.VISIBLE);
                            if (internalStorageList.get(position).isSelected()) {
                                viewholder.ivCheck.setVisibility(View.VISIBLE);
                            } else {
                                viewholder.ivCheck.setVisibility(View.GONE);
                            }
                        } else {
                            viewholder.ll_check.setVisibility(View.GONE);
                        }

                        viewholder.lout_bottom_data.setVisibility(View.GONE);

                        if (filesModel.isDir()) {

                            int fileLength = getFilesList(file.getPath());

                            viewholder.txtFolderItem.setText(fileLength + " item");
                           /* if (!internalStorageList.get(position).isCheckboxVisible()) {
                                viewholder.ivCheck.setVisibility(View.VISIBLE);
                                viewholder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand_less_gray));
                            }*/

                           /* if (fileLength == 0) {
                                viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder_empty));
                            } else*/
                            viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder));
                            viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                            viewholder.ivFolder.setVisibility(View.VISIBLE);
                            viewholder.cardView.setVisibility(View.GONE);


                        } else {
                            /*if (!internalStorageList.get(position).isCheckboxVisible()) {
                                viewholder.ivCheck.setVisibility(View.GONE);
                            }*/
                            String type = filesModel.getMineType();
                            if (type != null) {

                                Log.e("storage", "type: " + type + "  name: " + file.getName());

                                if (type.equalsIgnoreCase("image/jpeg") || type.equalsIgnoreCase("image/png") || type.equalsIgnoreCase("image/webp")) {

                                    Uri imageUri = Uri.fromFile(file);

                                    Glide.with(context).load(imageUri).placeholder(R.drawable.ic_image_placeholder_2)
                                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(viewholder.iv_image);
                                    viewholder.iv_image.setVisibility(View.VISIBLE);
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);


                                } else if (type.equalsIgnoreCase("video/mp4") || type.equalsIgnoreCase("video/x-matroska")) {

                                    Glide.with(context).load(file.getPath()).placeholder(R.drawable.delete)
                                            .error(R.drawable.delete)
                                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(viewholder.iv_image);
                                    viewholder.iv_image.setVisibility(View.VISIBLE);
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (type.equalsIgnoreCase("audio/mpeg") || type.equalsIgnoreCase("audio/aac") || type.equalsIgnoreCase("audio/ogg") || type.equalsIgnoreCase("video/3gpp")) {

                                    viewholder.txtMimeType.setText(mimeType);

                                   /* if (mimeType.equalsIgnoreCase("mp3")) {
                                        viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pink_audio_bg));
                                    } else {
                                        viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue_audio_bg));
                                    }*/

                                  /*  viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_audio_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (type.equalsIgnoreCase("application/zip")) {
                                   /* viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);


                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.zip_bg));*/
//                                    viewholder.txtMimeType.setText(mimeType);

                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                } else if (type.equalsIgnoreCase("application/rar")) {
                                  /*  viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);
                                    viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.rar_bg));*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar_file));

                                } else if (type.equalsIgnoreCase("application/vnd.android.package-archive")) {
                                    viewholder.iv_image.setVisibility(View.GONE);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_apk_file));

//                                    PackageManager pm = context.getPackageManager();
//                                    PackageInfo info = pm.getPackageArchiveInfo(filesModel.getFilePath(), 0);
//
//
//                                    try {
////                                        viewholder.ivFolder.setImageDrawable(context.getPackageManager().getApplicationIcon(info.packageName));
//                                        viewholder.iv_image.setImageDrawable(context.getPackageManager().getApplicationIcon(info.packageName));
//                                    } catch (Exception e) {
//                                        viewholder.iv_image.setImageDrawable(info.applicationInfo.loadIcon(pm));
////                                        viewholder.ivFolder.setImageDrawable(info.applicationInfo.loadIcon(pm));
//                                    }


                                } else if (mimeType.equalsIgnoreCase("pdf")) {
                                    viewholder.txtMimeType.setText(mimeType);
                                   /* viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pdf_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("doc") || mimeType.equalsIgnoreCase("docx")) {
//                                    viewholder.txtMimeType.setText(mimeType);
                                   /* viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.doc_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doc_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("xlsx") || mimeType.equalsIgnoreCase("xls") || mimeType.equalsIgnoreCase("xlc") || mimeType.equalsIgnoreCase("xld")) {
                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.xls_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("ppt") || mimeType.equalsIgnoreCase("pptx") || mimeType.equalsIgnoreCase("ppsx") || mimeType.equalsIgnoreCase("pptm")) {
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.ppt_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("txt") || mimeType.equalsIgnoreCase("tex") || mimeType.equalsIgnoreCase("text") || mimeType.equalsIgnoreCase("pptm")) {
                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.txt_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_text_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("xml")) {
                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue_audio_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);
*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                } else if (mimeType.equalsIgnoreCase("html") || mimeType.equalsIgnoreCase("htm")) {
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pink_audio_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/

                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("java")) {

                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.java_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                                } else if (mimeType.equalsIgnoreCase("php")) {

                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.xml_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/

                                } else {
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                }

                            } else {
                                viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                viewholder.ivFolder.setVisibility(View.VISIBLE);
                                viewholder.cardView.setVisibility(View.GONE);
                            }

//                            String fileSize = Utils.formateSize(file.length());
//                            viewholder.txtFolderItem.setText(fileSize);

                        }

                        if (filesModel.isFavorite()) {
                            if (viewholder.ivFolder.getVisibility() == View.VISIBLE) {
                                if (filesModel.isDir()) {
                                    viewholder.iv_fav_file.setVisibility(View.VISIBLE);
                                    viewholder.iv_fav_image.setVisibility(View.GONE);
                                    viewholder.iv_fav_other_file.setVisibility(View.GONE);
                                } else {
                                    viewholder.iv_fav_file.setVisibility(View.GONE);
                                    viewholder.iv_fav_image.setVisibility(View.GONE);
                                    viewholder.iv_fav_other_file.setVisibility(View.VISIBLE);
                                }

                            } else {
                                viewholder.iv_fav_image.setVisibility(View.VISIBLE);
                                viewholder.iv_fav_file.setVisibility(View.GONE);
                                viewholder.iv_fav_other_file.setVisibility(View.GONE);
                            }
                        } else {
                            viewholder.iv_fav_image.setVisibility(View.GONE);
                            viewholder.iv_fav_file.setVisibility(View.GONE);
                            viewholder.iv_fav_other_file.setVisibility(View.GONE);
                        }


                     /*   SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                        String strDate = sdf.format(file.lastModified());

                        viewholder.txtDateTime.setText(strDate);*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case TYPE_ITEM_GRID:
                    try {

                        GridHolder viewholder = (GridHolder) holder;
                        InternalStorageFilesModel filesModel = internalStorageList.get(position);

                        viewholder.txtFolderName.setText(filesModel.getFileName());

                        File file = new File(filesModel.getFilePath());

                        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                        String strDate = format.format(file.lastModified());
                        viewholder.txtDate.setText(strDate);

                        viewholder.iv_folder_image.setVisibility(View.GONE);
                        String mimeType = Utils.getFilenameExtension(file.getName());

                        if (internalStorageList.get(position).isCheckboxVisible()) {
                            viewholder.ll_check_grid.setVisibility(View.VISIBLE);
                            if (internalStorageList.get(position).isSelected()) {
                                viewholder.ivCheck.setVisibility(View.VISIBLE);
                            } else {
                                viewholder.ivCheck.setVisibility(View.GONE);
                            }
                        } else {
                            viewholder.ll_check_grid.setVisibility(View.GONE);
                        }


                        viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);

                        if (file.isDirectory()) {
                            viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder));
                            viewholder.ivFolder.setVisibility(View.VISIBLE);
                            viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                            viewholder.ivImage.setVisibility(View.GONE);
                            viewholder.cardViewImage.setVisibility(View.GONE);
                            viewholder.cardView.setVisibility(View.GONE);

                        } else {
                         /*   if (!internalStorageList.get(position).isCheckboxVisible()) {
                                viewholder.ivCheck.setVisibility(View.GONE);
                            }*/
                            String type = filesModel.getMineType();
                            if (type != null) {

                                if (type.equalsIgnoreCase("image/jpeg") || type.equalsIgnoreCase("image/png") || type.equalsIgnoreCase("image/webp")) {

                                    Uri imageUri = Uri.fromFile(file);
                                    /*Glide.with(context)
                                            .load(imageUri)
                                            .into(viewholder.ivImage);*/
                                    Glide.with(context).load(imageUri).placeholder(R.drawable.ic_image_placeholder_2).
                                            apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(viewholder.iv_folder_image);

                                    // viewholder.ivImage.setImage(ImageSource.uri(imageUri));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.iv_folder_image.setVisibility(View.VISIBLE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    //  viewholder.llDetails.setBackgroundColor(context.getResources().getColor(R.color.lite_trans_color));
//                                    viewholder.llDetails.setBackground(context.getResources().getDrawable(R.drawable.trans_bg));

                                } else if (type.equalsIgnoreCase("video/mp4") || type.equalsIgnoreCase("video/x-matroska")) {

                                  /*  Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
                                    Glide.with(context).load(bmThumbnail)
                                            .skipMemoryCache(false)
                                            .into(viewholder.ivImage); */

                                    Glide.with(context).load(file.getPath()).placeholder(R.drawable.delete)
                                            .error(R.drawable.delete).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                            .into(viewholder.iv_folder_image);

                                    // viewholder.ivImage.setImage(ImageSource.bitmap(bmThumbnail));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.iv_folder_image.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
//                                    viewholder.llDetails.setBackground(context.getResources().getDrawable(R.drawable.trans_bg));


                                } else if (type.equalsIgnoreCase("audio/mpeg") || type.equalsIgnoreCase("audio/aac") || type.equalsIgnoreCase("audio/ogg") || type.equalsIgnoreCase("video/3gpp")) {

                                  /*  viewholder.txtMimeType.setText(mimeType);

                                    if (mimeType.equalsIgnoreCase("mp3")) {
                                        viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pink_audio_bg));
                                    } else {
                                        viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue_audio_bg));
                                    }

                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_audio_file));
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    // viewholder.llDetails.setBackground(context.getResources().getDrawable(R.drawable.trans_bg));

                                } else if (type.equalsIgnoreCase("application/zip")) {
                                   /* viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);

                                    viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.zip_bg));*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    // viewholder.llDetails.setBackground(context.getResources().getDrawable(R.drawable.trans_bg));

                                } else if (type.equalsIgnoreCase("application/rar")) {
                                   /* viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);
                                    viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.rar_bg));*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    // viewholder.llDetails.setBackground(context.getResources().getDrawable(R.drawable.trans_bg));

                                } else if (type.equalsIgnoreCase("application/vnd.android.package-archive")) {
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.iv_folder_image.setVisibility(View.VISIBLE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                    // viewholder.ivImage.setImage(ImageSource.resource(R.mipmap.ic_launcher));

                                    PackageManager pm = context.getPackageManager();
                                    PackageInfo info = pm.getPackageArchiveInfo(filesModel.getFilePath(), 0);


                                    try {
//                                        viewholder.ivImage.setImageDrawable(context.getPackageManager().getApplicationIcon(info.packageName));
                                        viewholder.iv_folder_image.setImageDrawable(context.getPackageManager().getApplicationIcon(info.packageName));
                                    } catch (Exception e) {
                                        viewholder.iv_folder_image.setImageDrawable(info.applicationInfo.loadIcon(pm));
//                                        viewholder.ivImage.setImageDrawable(info.applicationInfo.loadIcon(pm));
                                    }
                                    // viewholder.ivImage.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));

                                } else if (mimeType.equalsIgnoreCase("pdf")) {
                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pdf_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/

                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("doc") || mimeType.equalsIgnoreCase("docx")) {
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.doc_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doc_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("xlsx") || mimeType.equalsIgnoreCase("xls") || mimeType.equalsIgnoreCase("xlc") || mimeType.equalsIgnoreCase("xld")) {
                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.xls_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/

                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("ppt") || mimeType.equalsIgnoreCase("pptx") || mimeType.equalsIgnoreCase("ppsx") || mimeType.equalsIgnoreCase("pptm")) {
                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.ppt_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/

                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("txt") || mimeType.equalsIgnoreCase("tex") || mimeType.equalsIgnoreCase("text") || mimeType.equalsIgnoreCase("pptm")) {
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.txt_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_text_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("xml")) {
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue_audio_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("html") || mimeType.equalsIgnoreCase("htm")) {
                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pink_audio_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html_file));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("java")) {

                                  /*  viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.java_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else if (mimeType.equalsIgnoreCase("php")) {

                                   /* viewholder.txtMimeType.setText(mimeType);
                                    viewholder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.xml_bg));
                                    viewholder.ivFolder.setVisibility(View.GONE);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.VISIBLE);*/
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);

                                } else {
                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                    // viewholder.ivImage.setImage(ImageSource.resource(R.drawable.ic_all_type_document));
                                    viewholder.ivFolder.setVisibility(View.VISIBLE);
//                                    viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                    viewholder.ivImage.setVisibility(View.GONE);
                                    viewholder.cardViewImage.setVisibility(View.GONE);
                                    viewholder.cardView.setVisibility(View.GONE);
                                }

                            } else {
                                viewholder.ivFolder.setColorFilter(context.getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
                                viewholder.ivFolder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_all_type_document));
                                //  viewholder.ivImage.setImage(ImageSource.resource(R.drawable.ic_all_type_document));
                                viewholder.ivFolder.setVisibility(View.VISIBLE);

                                viewholder.ivImage.setVisibility(View.GONE);
                                viewholder.cardViewImage.setVisibility(View.GONE);
                                viewholder.cardView.setVisibility(View.GONE);
                            }

                        }

                        if (filesModel.isFavorite()) {
//                            viewholder.iv_fav_image.setVisibility(View.VISIBLE);
//                            viewholder.iv_fav_image_file.setVisibility(View.VISIBLE);

                            if (viewholder.ivFolder.getVisibility() == View.VISIBLE) {
                                if (filesModel.isDir()) {
                                    viewholder.iv_fav_image_file.setVisibility(View.VISIBLE);
                                    viewholder.iv_fav_image.setVisibility(View.GONE);
                                } else {
                                    viewholder.iv_fav_image_file.setVisibility(View.GONE);
                                    viewholder.iv_fav_image.setVisibility(View.VISIBLE);
                                }

                            } else {
                                viewholder.iv_fav_image_file.setVisibility(View.GONE);
                                viewholder.iv_fav_image.setVisibility(View.VISIBLE);
                            }
                        } else {
                            viewholder.iv_fav_image.setVisibility(View.GONE);
                            viewholder.iv_fav_image_file.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case TYPE_AD:
                    final BannerViewHolder bannerView = (BannerViewHolder) holder;


                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int getFilesList(String filePath) {

        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files != null) {

            return files.length;
        } else
            return 0;
    }


    public String size(int size) {
        String hrSize = "";
        double m = size / 1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }

    @Override
    public int getItemCount() {
        return internalStorageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        AppCompatImageView ivFolder;
        AppCompatImageView iv_image;
        AppCompatTextView txtFolderName;
        AppCompatImageView ivCheck;
        RelativeLayout ll_check;
        AppCompatImageView ivUncheck;
        AppCompatTextView txtDateTime;
        AppCompatTextView txtFolderItem;
        CardView cardView;
        AppCompatTextView txtMimeType;

        ImageView iv_fav_image;
        ImageView iv_fav_file;
        ImageView iv_fav_other_file;
        LinearLayout lout_bottom_data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFolder = itemView.findViewById(R.id.iv_folder);

            iv_image = itemView.findViewById(R.id.iv_image);

            txtFolderName = itemView.findViewById(R.id.txt_folder_name);

            ivCheck = itemView.findViewById(R.id.iv_check);

            ll_check = itemView.findViewById(R.id.ll_check);

            ivUncheck = itemView.findViewById(R.id.iv_uncheck);

            txtDateTime = itemView.findViewById(R.id.txt_date_time);

            txtFolderItem = itemView.findViewById(R.id.txt_folder_item);

            cardView = itemView.findViewById(R.id.card_view);

            txtMimeType = itemView.findViewById(R.id.txt_mime_type);


            iv_fav_image = itemView.findViewById(R.id.iv_fav_image);

            iv_fav_file = itemView.findViewById(R.id.iv_fav_file);

            iv_fav_other_file = itemView.findViewById(R.id.iv_fav_other_file);

            lout_bottom_data = itemView.findViewById(R.id.lout_bottom_data);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            longClickListener.onItemLongClick(getAdapterPosition(), view);
            return true;
        }
    }

    public class GridHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        AppCompatImageView ivFolder;
        AppCompatImageView iv_folder_image;
        AppCompatImageView ivImage;
        AppCompatTextView txtFolderName;
        AppCompatTextView txtDate;
        AppCompatImageView ivCheck;
        RelativeLayout ll_check_grid;
        CardView cardView;
        CardView cardViewImage;
        AppCompatTextView txtMimeType;
        LinearLayout llDetails;
        ImageView iv_fav_image;
        ImageView iv_fav_image_file;

        public GridHolder(View itemView) {
            super(itemView);

            ivFolder = itemView.findViewById(R.id.iv_folder_grid);

             iv_folder_image = itemView.findViewById(R.id.iv_folder_image);

             ivImage = itemView.findViewById(R.id.iv_image);

             txtFolderName = itemView.findViewById(R.id.txt_folder_name_grid);

             txtDate = itemView.findViewById(R.id.txt_date_grid);

             ivCheck = itemView.findViewById(R.id.iv_check_grid);

             ll_check_grid = itemView.findViewById(R.id.ll_check_grid);

            cardView = itemView.findViewById(R.id.card_view_grid);

            cardViewImage = itemView.findViewById(R.id.card_view_image);

             txtMimeType = itemView.findViewById(R.id.txt_mime_type_grid);

             llDetails = itemView.findViewById(R.id.ll_details);

             iv_fav_image = itemView.findViewById(R.id.iv_fav_image);

             iv_fav_image_file = itemView.findViewById(R.id.iv_fav_image_file);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            longClickListener.onItemLongClick(getAdapterPosition(), view);
            return true;
        }
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {


        public BannerViewHolder(View view) {
            super(view);

        }
    }
}
