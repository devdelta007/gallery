package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.picture.foto.R;

import java.io.File;
import java.util.ArrayList;



public class HeaderListAdapter extends RecyclerView.Adapter<HeaderListAdapter.ViewHolder> {
    Context context;
    ArrayList<String> pathList = new ArrayList<>();
    private static ClickListener listener;

    public HeaderListAdapter(Context context, ArrayList<String> pathList) {
        this.context = context;
        this.pathList = pathList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            if (pathList.get(position).equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                holder.txtStoragePath.setText("Internal storage");
                holder.txt_app_name.setVisibility(View.VISIBLE);

                holder.txt_app_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemHomeClick(position,view);
                    }
                });
                holder.txtStoragePath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemHeaderClick(position, v);
                    }
                });
            } else if (pathList.get(position).equalsIgnoreCase(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS)) {
                holder.txtStoragePath.setText("Download");
                holder.txt_app_name.setVisibility(View.VISIBLE);

            } else if (pathList.get(position).equalsIgnoreCase("Favourites")) {
                holder.txtStoragePath.setText("Favourites");
                holder.txt_app_name.setVisibility(View.VISIBLE);
            } else {
                holder.txtStoragePath.setText("Sd card");
                holder.txt_app_name.setVisibility(View.VISIBLE);
                holder.txtStoragePath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemHeaderClick(position, v);
                    }
                });

                holder.txt_app_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        listener.onItemHomeClick(position, view);
                    }
                });
            }

        } else {
            File file = new File(pathList.get(position));
            holder.txtStoragePath.setText(file.getName());
            holder.txt_app_name.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemHeaderClick(int position, View v);
        void onItemHomeClick(int position, View v);
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        this.listener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatTextView txtStoragePath;

        AppCompatTextView txt_app_name;

        AppCompatImageView iv_right;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtStoragePath = itemView.findViewById(R.id.txt_storage_path);
                    txt_app_name = itemView.findViewById(R.id.txt_app_name);
            iv_right = itemView.findViewById(R.id.iv_right);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition(), view);
        }
    }
}
