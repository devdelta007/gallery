package com.gallery.picture.foto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;

import java.util.ArrayList;


public class NestedRecyclerViewAdapter extends RecyclerView.Adapter<NestedRecyclerViewAdapter.MyNestedViewHolder> {

    int folderPosition;
    Context context;
    static int j = 0;
    ArrayList<Integer> Images2;
    ArrayList<PhotoData> listByDate;
    ArrayList<PhotoData> listByDate1;
    private static ClickListener listener;
    private static LongClickListener longClickListener;
    PhotoData imageList;
    int i2,i3,h,w;
    int deviceheight, devicewidth;


    public NestedRecyclerViewAdapter(int position, Context context, ArrayList<PhotoData> listByDate) {
        folderPosition = position;
        this.context = context;
        this.listByDate = listByDate;
        int margin = context.getResources().getDimensionPixelSize(R.dimen._6sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        deviceheight = (displaymetrics.widthPixels - margin) / 4;
        devicewidth = (displaymetrics.widthPixels - margin) / 4;
        i2 = displaymetrics.widthPixels;
        i3 = displaymetrics.heightPixels;
        h = (int) ((19.0f * ((float) i2)) / 100.0f);
        w = (int) ((11.0f * ((float) i3)) / 100.0f);
    }

    public NestedRecyclerViewAdapter( Context context, ArrayList<PhotoData> listByDate1, int positon) {

        this.context = context;
        this.listByDate1 = listByDate1;
        folderPosition = positon;
        int margin = context.getResources().getDimensionPixelSize(R.dimen._6sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
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


    @NonNull
    @Override
    public MyNestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
        MyNestedViewHolder mnvw = new MyNestedViewHolder(view);
        return mnvw;
    }

    @Override
    public void onBindViewHolder(@NonNull MyNestedViewHolder holder, int position) {

        final MyNestedViewHolder imageViewHolder = (MyNestedViewHolder) holder;


            /*String s;
            if(parent == null)
                s = "/storage/emulated/0/DCIM/Camera/"+ values.get(j);
            else
                s = parent+list[position];
            Log.d("SPath", s);*/
//            File file = new File(album.get(position));
//            Uri imageUri = Uri.fromFile(file);

//            holder.imageView.setImageURI(imageUri);
        if(listByDate1 == null || listByDate1.size()==0) {
            imageList = (PhotoData) listByDate.get(position);
            Glide.with(context)
                    .load(listByDate.get(position).getFilePath())
                    .override(h,w)
                    .apply(new RequestOptions().centerCrop())
                    .placeholder(R.drawable.ic_image_placeholder_2)
                    .error(R.drawable.delete)
                    .into(imageViewHolder.ivimg);
        } else{
            imageList = (PhotoData) listByDate1.get(position);
            Glide.with(context)
                    .load(listByDate1.get(position).getFilePath())
                    .override(h,w)
                    .apply(new RequestOptions().centerCrop())
                    .placeholder(R.drawable.ic_image_placeholder_2)
                    .error(R.drawable.delete)
                    .into(imageViewHolder.ivimg);
        }



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




    }

    @Override
    public int getItemCount() {

        return (listByDate1 == null || listByDate1.size()==0) ? (listByDate == null) ? 0 : listByDate.size() : listByDate1.size();
    }

    public class MyNestedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView ivimg, iv_fav_image;
        AppCompatImageView iv_un_select, iv_select;

        public MyNestedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivimg = itemView.findViewById(R.id.ivimg);
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
