package com.gallery.picture.foto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<Object> photoList = new ArrayList<>();

    private static ClickListener listener;


    private static final int ITEM_PHOTOS_TYPE = 2;
    public static final int ITEM_HEADER_TYPE = 1;
    public static final int TYPE_AD = 3;
    int i2, i3, h, w;
    int deviceheight, devicewidth;

    public PlacesAdapter(Context context1, List<Object> photoList) {
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

                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                Date c = Calendar.getInstance().getTime();
                String formattedDate = format.format(c);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                cal.getTime();

                Date d2 = cal.getTime();
                String s2 = format.format(d2);

                cal.add(Calendar.DATE, -1);
                Date d3 = cal.getTime();
                String s3 = format.format(d3);

                if (formattedDate.equalsIgnoreCase(imageHeader.getTitle())) {
                    viewHolder.txtDate.setText("Today");

                } else if (s2.equalsIgnoreCase(imageHeader.getTitle())) {
                    viewHolder.txtDate.setText("Yesterday");

                } else if (s3.equalsIgnoreCase(imageHeader.getTitle())) {
                    viewHolder.txtDate.setText("2 days ago");
                } else {
                    viewHolder.txtDate.setText(imageHeader.getTitle());
                }


                break;

            case ITEM_PHOTOS_TYPE:
                final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                PhotoData imageList = (PhotoData) photoList.get(position);

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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImg, iv_fav_image;
        AppCompatImageView iv_un_select, iv_select;

        RelativeLayout ll_main;
        RelativeLayout ll_select;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImg = itemView.findViewById(R.id.ivimg);
            iv_fav_image = itemView.findViewById(R.id.iv_fav_image);
            iv_un_select = itemView.findViewById(R.id.iv_un_select);
            iv_select = itemView.findViewById(R.id.iv_select);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(getAdapterPosition(), view);
        }


    }
}
