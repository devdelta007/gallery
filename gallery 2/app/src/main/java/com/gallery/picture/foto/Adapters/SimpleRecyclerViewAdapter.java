package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.gallery.picture.foto.Fragments.SecondFragment.folderListArray;


public class SimpleRecyclerViewAdapter extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.MyNestedViewHolder> {

        Context context;
    LinkedHashMap<String, ArrayList<PhotoData>> albumDetails = new LinkedHashMap<>();
    Set<String> keys = new HashSet<>();
    ArrayList<String> listkeys = new ArrayList<>();


        @RequiresApi(api = Build.VERSION_CODES.N)
        public SimpleRecyclerViewAdapter(Context context, ArrayList<PhotoData> images) {
            this.context = context;
            albumDetails = folderListArray;
        }



        @NonNull
        @Override
        public MyNestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_viewall_nested_single_view, parent, false);
            MyNestedViewHolder mnvw = new MyNestedViewHolder(view);

            keys = albumDetails.keySet();
            listkeys = new ArrayList<String>();
            listkeys.addAll(keys);

            return mnvw;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(@NonNull MyNestedViewHolder holder, int position) {
            Glide.with(context)
                    .load(albumDetails.get(listkeys.get(position)).get(0).getFilePath()).centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder_2)
                    .error(R.drawable.delete)
                    .into(holder.imageView);
            holder.CollectionName.setText(albumDetails.get(listkeys.get(position)).get(0).getFolderName());
            holder.Subtitle.setText(Integer.toString(albumDetails.get(listkeys.get(position)).size()) + " Items");



        }

        @Override
        public int getItemCount() {
                return albumDetails.size();
        }

        public class MyNestedViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView CollectionName, Subtitle;
            public MyNestedViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.CollectionsImageview);

                CollectionName= itemView.findViewById(R.id.CollectionName);
                Subtitle = itemView.findViewById(R.id.Subtitle);

            }
        }
    }