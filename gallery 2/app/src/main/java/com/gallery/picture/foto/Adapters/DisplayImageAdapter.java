package com.gallery.picture.foto.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gallery.picture.foto.Interface.ToggleInterface;
import com.gallery.picture.foto.Interface.ToggleInterface2;
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;


import java.util.ArrayList;
import java.util.List;


public class DisplayImageAdapter extends PagerAdapter {

    List<PhotoData> imageList = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;
    boolean isFirst = false;
    private ToggleInterface2 toggleInterface2;



    public DisplayImageAdapter(ToggleInterface2 toggleInterface2, Context context, List<PhotoData> imageList) {
        this.toggleInterface2 =toggleInterface2;
        this.context = context;
        this.imageList = imageList;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.item_full_screen_image, container, false);

        GestureImageView imageView = itemView.findViewById(R.id.iv_display);

        final RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(900, 900).dontTransform();
        imageView.getController().getSettings()
                .setMaxZoom(6f)
                .setMinZoom(0)
                .setDoubleTapZoom(3f);

        Glide.with(context).setDefaultRequestOptions(options)
                .load(imageList.get(position).getFilePath()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                .into(imageView);



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleInterface2.onToggleclick();
            }
        });


        container.addView(itemView);
        return itemView;
    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


}
