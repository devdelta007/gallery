package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ui.DisplayImageActivity;

import java.io.File;
import java.util.ArrayList;

public class ExploreFavRVAdapter extends RecyclerView.Adapter<ExploreFavRVAdapter.MyViewHolder> {

    Context context;
    //ArrayList<PhotoData> AlbumDetails;
    String s;
    ArrayList<String> favList = new ArrayList<String>();

    public ExploreFavRVAdapter(Context context, ArrayList<String> a) {
        this.context = context;
        favList = a;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid1, parent, false);
        MyViewHolder mnvw = new MyViewHolder(view);
/*

        favList.clear();
        for (int i = 0; i < Constant.displayImageList.size(); i++) {

            if (Constant.displayImageList.get(i).isFavorite())
                favList.add(Constant.displayImageList.get(i));
        }
*/

        return mnvw;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        File f = new File(favList.get(position).toString());
        Glide.with(context).load(f.getPath())

                .apply(new RequestOptions().centerCrop())
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                .error(R.drawable.delete)
//                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.ivimg);


        holder.ivimg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DisplayImageActivity.class);
                i.putExtra("folderPosition", -3);
                i.putExtra("position", position);

                context.startActivity(i);
            }
        });


    }


    @Override
    public int getItemCount(){


        return (favList.size()>4) ? 4 : favList.size();

    }



    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivimg;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivimg = itemView.findViewById(R.id.ivimg);







        }
    }
}
