package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.BusinessShopProfile;
import com.example.happywed.Models.BusinessShopModel;
import com.example.happywed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessShopListAdapter extends RecyclerView.Adapter<BusinessShopListAdapter.MyViewHolder> {

    Context context;
    ArrayList<BusinessShopModel> modelList;


    public BusinessShopListAdapter(Context context, ArrayList<BusinessShopModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView businessImage;
        TextView businessName, businessCategory;
        CardView businessShopListCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            businessImage = (CircleImageView) itemView.findViewById(R.id.businessShopImage);
            businessName = (TextView) itemView.findViewById(R.id.businessShopName);
            businessCategory = (TextView) itemView.findViewById(R.id.businessShopcategory);

            businessShopListCard = (CardView) itemView.findViewById(R.id.businessShopListCard);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_shoplist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final BusinessShopModel businessShopModel  = modelList.get(position);

        if(businessShopModel.getBusinessProfilePic() != null){
            Log.d("abc","not null"+businessShopModel.getBusinessProfilePic());
            Picasso.get().load(businessShopModel.getBusinessProfilePic()).into(holder.businessImage);
        }else{
            Log.d("abc","null"+businessShopModel.getBusinessProfilePic());
            holder.businessImage.setImageResource(R.drawable.defaultshoppic);
        }
        holder.businessName.setText(businessShopModel.getBusinessName());
        holder.businessCategory.setText(businessShopModel.getBusinessCategory());

        holder.businessShopListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, BusinessShopProfile.class).putExtra("shopKey",businessShopModel.getBusinessKey()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


}
