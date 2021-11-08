package com.gallery.picture.foto.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gallery.picture.foto.Fragments.FirstFragment;
import com.gallery.picture.foto.Fragments.SecondFragment;
import com.gallery.picture.foto.Fragments.ThirdFragment;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    Context context;
    int TotalTabs;
    public MyFragmentAdapter(@NonNull FragmentManager fm, Context context, int TotalTabs) {
        super(fm);
        this.context = context;
        this.TotalTabs = TotalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
            FirstFragment firstFragment = new FirstFragment();
            return firstFragment;
            case 1:
                SecondFragment secondFragment = new SecondFragment();
                return secondFragment;
            case 2:
                ThirdFragment thirdFragment = new ThirdFragment();
                return thirdFragment;
            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return TotalTabs;
    }
}
