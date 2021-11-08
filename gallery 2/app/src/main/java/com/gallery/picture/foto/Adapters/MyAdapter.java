package com.gallery.picture.foto.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.gallery.picture.foto.R;

public class MyAdapter extends PagerAdapter {


    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item,container,false);
        ImageView image = view.findViewById(R.id.image);


        /*String s = "/storage/emulated/0/DCIM/Camera/"+ list[position];
        File file = new File(s);
        Uri imageUri = Uri.fromFile(file);
        container.addView(view);
        Glide.with(container.getContext())
                .load(imageUri)
                .into(image);*/


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}