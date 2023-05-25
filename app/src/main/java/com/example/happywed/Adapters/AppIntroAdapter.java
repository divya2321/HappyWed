package com.example.happywed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.happywed.R;

import java.util.ArrayList;

public class AppIntroAdapter extends PagerAdapter {

    ArrayList<Integer> appIntroImageList;
    Context appIntroContext;

    public AppIntroAdapter(ArrayList<Integer> appIntroImageList, Context appIntroContext) {
        this.appIntroImageList = appIntroImageList;
        this.appIntroContext = appIntroContext;
    }

    @Override
    public int getCount() {
        return appIntroImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View v = LayoutInflater.from(appIntroContext).inflate(R.layout.app_intro_item, container, false);
        ImageView imgView = v.findViewById(R.id.app_intro_itemImage);
        imgView.setImageResource(appIntroImageList.get(position));
        container.addView(v, 0);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }
}
