package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductCardModel;
import com.example.happywed.Models.BusinessProductModel;
import com.example.happywed.Models.CommonCategoryModel;
import com.example.happywed.ProductViewImage;
import com.example.happywed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class BusinessProductCardAdapter extends RecyclerView.Adapter<BusinessProductCardAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<BusinessProductModel> modelList;
    private DatabaseReference databaseReference;
    private boolean processFav = false;

    public BusinessProductCardAdapter(Context context, ArrayList<BusinessProductModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        databaseReference = HappyWedDB.getDBConnection().child("businesses");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, favImage;
        TextView  nameView, priceView;
        CardView productCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.productImage);
            favImage = (ImageView) itemView.findViewById(R.id.productFav);
            nameView = (TextView) itemView.findViewById(R.id.productName);
            priceView = (TextView) itemView.findViewById(R.id.productPrice);
            productCard = (CardView) itemView.findViewById(R.id.productCard);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_product_card_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final BusinessProductModel productModel = modelList.get(position);

        if(productModel.isFav()){
            holder.favImage.setImageResource(R.drawable.favourite);
            holder.favImage.setTag("fav");

        }else{
            holder.favImage.setImageResource(R.drawable.unfavourite);
            holder.favImage.setTag("unFav");
        }

        Picasso.get().load(Uri.parse(productModel.getMainProductImage())).into(holder.imageView);
        holder.nameView.setText(productModel.getProductName());
        holder.priceView.setText("LKR "+productModel.getProductPrice());

        holder.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                processFav = true;

                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(processFav) {
                            if (dataSnapshot.hasChild(productModel.getUid())) {
                                processFav = false;
                                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").child(productModel.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Snackbar.make(view,"Item Removed from WishList",Snackbar.LENGTH_LONG).show();
                                            holder.favImage.setImageResource(R.drawable.unfavourite);
                                            holder.favImage.setTag("unFav");

                                        }
                                    }
                                });

                            } else {
                                processFav = false;
                                databaseReference.child(productModel.getOwnerKey()).child("Shops").child(productModel.getBusinessKey()).child("Products").child(productModel.getProductKey()).child("Favorite").child(productModel.getUid()).setValue("random value").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Snackbar.make(view,"Item Added to WishList",Snackbar.LENGTH_LONG).show();
                                            holder.favImage.setImageResource(R.drawable.favourite);
                                            holder.favImage.setTag("fav");

                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        holder.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProductViewImage.class).putExtra("productModel", (Serializable) productModel));
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
