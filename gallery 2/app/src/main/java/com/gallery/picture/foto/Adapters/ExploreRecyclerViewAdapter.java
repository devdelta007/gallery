package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.ui.Singlelocation;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.gallery.picture.foto.service.ImageDataService.bucketimagesDataLocationHashMap;

public class ExploreRecyclerViewAdapter extends RecyclerView.Adapter<ExploreRecyclerViewAdapter.MyViewHolder> {

    Context context;
    //ArrayList<PhotoData> AlbumDetails;
    String s;
    Set<String> keys = new LinkedHashSet<>();
    ArrayList<String> listkeys = new ArrayList<>();


    public ExploreRecyclerViewAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_single_view, parent, false);
        MyViewHolder mnvw = new MyViewHolder(view);
        keys = bucketimagesDataLocationHashMap.keySet();
        listkeys.clear();
       listkeys = new ArrayList<>();
        listkeys.addAll(keys);
        return mnvw;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        Glide.with(context).load(bucketimagesDataLocationHashMap.get(listkeys.get(position)).get(0).getFilePath())

                .apply(new RequestOptions().centerCrop())
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                .error(R.drawable.delete)
//                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.CollectionsImageview);
        holder.CollectionName.setText(listkeys.get(position).substring(0, listkeys.get(position).indexOf(",")));
        holder.Subtitle.setText(bucketimagesDataLocationHashMap.get(listkeys.get(position)).size() + " Items");


            holder.CollectionsImageview.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Intent inc = new Intent(context, Singlelocation.class);
                    inc.putExtra("position", position );
                    context.startActivity(inc);
                }
            });


        }


    @Override
    public int getItemCount(){


        return bucketimagesDataLocationHashMap.size();

    }



class MyViewHolder extends RecyclerView.ViewHolder{

        ShapeableImageView CollectionsImageview;
        TextView CollectionName, Subtitle;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            CollectionsImageview = itemView.findViewById(R.id.CollectionsImageview);

            CollectionName= itemView.findViewById(R.id.CollectionName);
            Subtitle = itemView.findViewById(R.id.Subtitle);







        }
    }
}
