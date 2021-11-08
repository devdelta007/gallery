package com.gallery.picture.foto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.ui.HiddenCollection;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;


public class CollectionsNestedRecyclerViewAdapter extends RecyclerView.Adapter<CollectionsNestedRecyclerViewAdapter.MyNestedViewHolder> {

    Context context;
    LinkedHashMap<String, ArrayList<PhotoData>> AlbumDetails = new LinkedHashMap<>();
    String s;
    Set<String> keys = new HashSet<>();
    ArrayList<String> listkeys = new ArrayList<>();


    public CollectionsNestedRecyclerViewAdapter(Context context, LinkedHashMap<String, ArrayList<PhotoData>> images) {
        this.context = context;
        AlbumDetails = images;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public MyNestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_nested_single_view, parent, false);
        MyNestedViewHolder mnvw = new MyNestedViewHolder(view);

        keys = AlbumDetails.keySet();
        listkeys = new ArrayList<String>();
        listkeys.addAll(keys);

        return mnvw;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyNestedViewHolder holder, int position) {
        if(AlbumDetails.get(listkeys.get(position)).size()==0)
            ((Activity)context).finish();
        Glide.with(context).load(AlbumDetails.get(listkeys.get(position)).get(0).getFilePath())

                .apply(new RequestOptions().centerCrop())
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                .error(R.drawable.delete)
//                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.imageView);
        holder.CollectionName.setText(AlbumDetails.get(listkeys.get(position)).get(0).getFolderName());
        holder.Subtitle.setText(Integer.toString(AlbumDetails.get(listkeys.get(position)).size()) + " Items");


        int s = (AlbumDetails.size() / 2) + position + ((AlbumDetails.size() % 2 == 0) ? 0 : 1);

        if (s < AlbumDetails.size()) {

            Log.d("error", String.valueOf(s));

                Glide.with(context).load(AlbumDetails.get(listkeys.get(s)).get(0).getFilePath())

                    .apply(new RequestOptions().centerCrop())
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                    .error(R.drawable.delete)
//                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.imageView2);
            holder.CollectionName2.setText(AlbumDetails.get(listkeys.get(s)).get(0).getFolderName());
            holder.Subtitle2.setText(Integer.toString(AlbumDetails.get(listkeys.get(s)).size()) + " Items");



           /* if(s<24) {
                holder.Subtitle2.setText("217 items");
                holder.CollectionName2.setText("All Images");
            }else {

                holder.Subtitle2.setText("0 items");
                holder.CollectionName2.setText("New Images");
            }

        }
        else if(s==Images.size() && Images.size()%2!=0) {
            holder.imageView2.setImageResource(0);
            holder.CollectionName2.setText("");
            holder.Subtitle2.setText("");






        } else {

        }*/
        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent inc = new Intent(context, HiddenCollection.class);
                inc.putExtra("position", position );
                inc.putExtra("fname", AlbumDetails.get(listkeys.get(position)).get(0).getFolderName());
                context.startActivity(inc);
            }
        });

            holder.imageView2.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Intent inc = new Intent(context, HiddenCollection.class);
                    inc.putExtra("position", position + (AlbumDetails.size()%2==0 ? AlbumDetails.size()/2 : (AlbumDetails.size()/2)+1));
                    inc.putExtra("fname", AlbumDetails.get(listkeys.get(s)).get(0).getFolderName());
                    context.startActivity(inc);
                }
            });

        }
    }






    @Override
    public int getItemCount(){


        return (((AlbumDetails.size()%2)==0) ? AlbumDetails.size()/2 : (AlbumDetails.size()/2)+1);

    }

    public class MyNestedViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, imageView2;
        TextView CollectionName, Subtitle, CollectionName2, Subtitle2;
        RelativeLayout nestedRelative;


        public MyNestedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.CollectionsImageview);
            imageView2 = itemView.findViewById(R.id.CollectionsImageview2);
            CollectionName= itemView.findViewById(R.id.CollectionName);
            Subtitle = itemView.findViewById(R.id.Subtitle);
            CollectionName2= itemView.findViewById(R.id.CollectionName2);
            Subtitle2 = itemView.findViewById(R.id.Subtitle2);
            nestedRelative = itemView.findViewById(R.id.nestedRelative);






        }
    }
}
