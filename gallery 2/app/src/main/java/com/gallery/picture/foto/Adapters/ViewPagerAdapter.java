package com.gallery.picture.foto.Adapters;

import android.content.Context;
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
import com.gallery.picture.foto.Model.PhotoData;
import com.gallery.picture.foto.R;

import java.util.List;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

    //Context object
    Context context;
    private ToggleInterface toggleInterface;

    //Array of images
    List<PhotoData>  images;

    //Layout Inflater
    LayoutInflater mLayoutInflater;


    //Viewpager Constructor
    public ViewPagerAdapter(ToggleInterface toggleInterface, Context context, List<PhotoData> images) {

        this.toggleInterface = toggleInterface;
                this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //return the number of images
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ( object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        //inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item, container, false);

        //referencing the image view from the item.xml file
        GestureImageView imageView = itemView.findViewById(R.id.image);

        //setting the image in the imageView
                    final RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(900, 900).dontTransform();
        imageView.getController().getSettings()
                .setMaxZoom(6f)
                .setMinZoom(0)
                .setDoubleTapZoom(3f);

        Glide.with(context).setDefaultRequestOptions(options)
                .load(images.get(position).getFilePath()).placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_placeholder_2))
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleInterface.onToggleclick();
            }
        });

        //Adding the View
        Objects.requireNonNull(container).addView(itemView);




        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView( (RelativeLayout) object);
    }
}
