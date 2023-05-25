package com.example.happywed.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.happywed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductViewAdapter extends PagerAdapter {

    ArrayList<String> productViewImageList;
    Context productViewContext;

    public ProductViewAdapter(ArrayList<String> productViewImageList, Context productViewContext) {
        this.productViewImageList = productViewImageList;
        this.productViewContext = productViewContext;
    }

    @Override
    public int getCount() {
        return productViewImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View v = LayoutInflater.from(productViewContext).inflate(R.layout.product_view_item, container, false);
        ImageView imgView = v.findViewById(R.id.itemImageSlide);
        Picasso.get().load(productViewImageList.get(position)).into(imgView);
        container.addView(v, 0);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }
}
