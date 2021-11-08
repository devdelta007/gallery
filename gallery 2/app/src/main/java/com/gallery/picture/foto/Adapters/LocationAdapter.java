package com.gallery.picture.foto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.Model.PhotoHeader;
import com.gallery.picture.foto.R;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.gallery.picture.foto.service.ImageDataService.folderListArray;


public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Object> photoList = new ArrayList<>();

    private static ClickListener listener;
    private static LongClickListener longClickListener;

    private static final int ITEM_PHOTOS_TYPE = 2;
    public static final int ITEM_HEADER_TYPE = 1;
    public static final int TYPE_AD = 3;
    int i2, i3, h, w;
    int deviceheight, devicewidth;

    public LocationAdapter(Context context1, List<Object> photoList) {
        this.context = context1;
        this.photoList = photoList;

        int margin = context1.getResources().getDimensionPixelSize(R.dimen._6sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context1).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        deviceheight = (displaymetrics.widthPixels - margin) / 4;
        devicewidth = (displaymetrics.widthPixels - margin) / 4;
        i2 = displaymetrics.widthPixels;
        i3 = displaymetrics.heightPixels;
        h = (int) ((19.0f * ((float) i2)) / 100.0f);
        w = (int) ((11.0f * ((float) i3)) / 100.0f);
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
        try {

            if (photoList.get(position) == null) {
                return TYPE_AD;
            } else if (photoList.get(position) instanceof PhotoData) {
                return ITEM_PHOTOS_TYPE;
            } else {
                return ITEM_HEADER_TYPE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ITEM_PHOTOS_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if (viewType == ITEM_HEADER_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_header, parent, false);
            viewHolder = new HeaderViewHolder(v);
        } else {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
            viewHolder = new ImageViewHolder(v2);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case ITEM_HEADER_TYPE:
                final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;

                PhotoHeader imageHeader = (PhotoHeader) photoList.get(position);


                List<String> locationData = imageHeader.getPhotoList().get(0).getLocationData();
                if(locationData==null)
                    viewHolder.txtDate.setText("No Location found !!");
                else {
                    String o = locationData.get(0);

                    viewHolder.txtDate.setText(o.toString());
                }



                break;

            case ITEM_PHOTOS_TYPE:
                final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                PhotoData imageList = (PhotoData) this.photoList.get(position);

              /*  File file = new File(imageList.getFilePath());

                Uri imageUri = Uri.fromFile(file);*/

                Glide.with(context).load(imageList.getFilePath())
                        .dontTransform().override(h, w)
                        .apply(new RequestOptions().centerCrop())
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                        .error(R.drawable.ic_image_placeholder_2)
//                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imageViewHolder.ivImg);


               

                if (imageList.isCheckboxVisible()) {
                    imageViewHolder.iv_un_select.setVisibility(View.VISIBLE);
                    if (imageList.isSelected()) {
                        imageViewHolder.iv_select.setVisibility(View.VISIBLE);


                    } else {
                        imageViewHolder.iv_select.setVisibility(View.GONE);
                    }
                } else {
                    imageViewHolder.iv_select.setVisibility(View.GONE);
                    imageViewHolder.iv_un_select.setVisibility(View.GONE);

                }

                break;


        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView txtDate;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);


            txtDate = itemView.findViewById(R.id.txt_date);


        }


    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ImageViewHolder) {
            Glide.with(context).clear(((ImageViewHolder) holder).ivImg);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{

        ImageView ivImg, iv_fav_image;
        AppCompatImageView iv_un_select, iv_select;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImg = itemView.findViewById(R.id.ivimg);
            iv_fav_image = itemView.findViewById(R.id.iv_fav_image);
            iv_un_select = itemView.findViewById(R.id.iv_un_select);
            iv_select = itemView.findViewById(R.id.iv_select);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(getAdapterPosition(), view);
        }


        public boolean onLongClick(View v) {
            longClickListener.onItemLongClick(getAdapterPosition(), v);
            return true;
        }



    }
}
