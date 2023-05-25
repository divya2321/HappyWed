package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.BusinessShopPreview;
import com.example.happywed.BusinessShopProductPreview;
import com.example.happywed.Models.BusinessProductCardModel;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class BusinessShopProductPreviewAdapter extends RecyclerView.Adapter<BusinessShopProductPreviewAdapter.MyViewHolder> {

    Context context;
    ArrayList<BusinessProductModel> modelList;

    public BusinessShopProductPreviewAdapter(Context context, ArrayList<BusinessProductModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView  nameView, priceView;
        CardView productCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.productImage);
            nameView = (TextView) itemView.findViewById(R.id.productName);
            priceView = (TextView) itemView.findViewById(R.id.productPrice);
            productCard = (CardView) itemView.findViewById(R.id.productCard);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_shop_product_preview_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final BusinessProductModel businessModel = modelList.get(position);

        Picasso.get().load(Uri.parse(businessModel.getMainProductImage())).into(holder.imageView);
        holder.nameView.setText(businessModel.getProductName());
        holder.priceView.setText("LKR "+businessModel.getProductPrice());


        holder.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, BusinessShopProductPreview.class).putExtra("productModel", (Serializable) businessModel));
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
