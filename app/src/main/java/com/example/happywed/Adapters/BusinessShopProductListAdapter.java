package com.example.happywed.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.BusinessProductDetails;
import com.example.happywed.BusinessShopProfile;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.Models.BusinessShopModel;
import com.example.happywed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BusinessShopProductListAdapter extends RecyclerView.Adapter<BusinessShopProductListAdapter.MyViewHolder> {

    Context context;
    ArrayList<BusinessProductModel> modelList;



    public BusinessShopProductListAdapter(Context context, ArrayList<BusinessProductModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productName, productPrice;
        CardView businessProductListCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.businessProductImage);
            productName = (TextView) itemView.findViewById(R.id.businessProductName);
            productPrice = (TextView) itemView.findViewById(R.id.businessProductPrice);

            businessProductListCard = (CardView) itemView.findViewById(R.id.businessProductListCard);


        }
    }

    ImageView cancelBtn;
    private RecyclerView productImageRecyclerView;
    private EditText productNameTxt, productDiscriptionTxt, productPriceTxt;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_productlist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final BusinessProductModel businessShopModel  = modelList.get(position);

        Picasso.get().load(businessShopModel.getMainProductImage()).into(holder.productImage);
        holder.productName.setText(businessShopModel.getProductName());
        holder.productPrice.setText(businessShopModel.getProductPrice());

        holder.businessProductListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((BusinessProductDetails)context).showDetails(businessShopModel);

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


}
