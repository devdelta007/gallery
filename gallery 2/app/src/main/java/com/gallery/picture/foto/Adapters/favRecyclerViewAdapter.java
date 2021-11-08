package com.gallery.picture.foto.Adapters;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;

import java.util.ArrayList;





public class favRecyclerViewAdapter extends RecyclerView.Adapter<favRecyclerViewAdapter.MyViewHolder> {


    Context context;

    ArrayList<Integer> Images2;
    ArrayList<PhotoData> favList;
    private static ClickListener listener;
    private static LongClickListener longClickListener;


    public favRecyclerViewAdapter(Context context, ArrayList<PhotoData> listByDate) {

        this.context = context;
        this.favList = listByDate;
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
        MyViewHolder mnvw = new MyViewHolder(view);
        return mnvw;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MyViewHolder imageViewHolder = (MyViewHolder) holder;
        PhotoData imageList = (PhotoData) favList.get(position);


            /*String s;
            if(parent == null)
                s = "/storage/emulated/0/DCIM/Camera/"+ values.get(j);
            else
                s = parent+list[position];
            Log.d("SPath", s);*/
//            File file = new File(album.get(position));
//            Uri imageUri = Uri.fromFile(file);

//            holder.imageView.setImageURI(imageUri);
        Glide.with(context)
                .load(imageList.getFilePath()).centerCrop()
                .placeholder(R.drawable.ic_image_placeholder_2)
                .error(R.drawable.delete)
                .into(holder.ivimg);


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


        /*holder.ivimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DisplayFavActivity.class);
                i.putExtra("folderPosition", -2);
                i.putExtra("position", position);

                context.startActivity(i);
            }
        });*/

    }

    @Override
    public int getItemCount() {

        return favList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView ivimg, iv_fav_image;
        AppCompatImageView iv_un_select, iv_select;

        public MyViewHolder(@NonNull View itemView) {
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
