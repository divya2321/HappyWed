package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.CommonCategoryView;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductModel;
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

public class CommonCategoryAdapter extends RecyclerView.Adapter<CommonCategoryAdapter.MyViewHolder> implements Filterable {

    private Context context;
    public static ArrayList<BusinessProductModel> modelList;
    public static ArrayList<BusinessProductModel> modelListAll;
    private DatabaseReference databaseReference;
    private boolean processFav = false;
    public static String selectedLocation = "All of Sri Lanka";

    public CommonCategoryAdapter(Context context, ArrayList<BusinessProductModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        this.modelListAll = modelList;
        databaseReference = HappyWedDB.getDBConnection().child("businesses");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, favImage;
        TextView  textView;
        CardView productCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageItem);
            favImage = (ImageView) itemView.findViewById(R.id.favImage);
            textView = (TextView) itemView.findViewById(R.id.textItem);
            productCard = (CardView) itemView.findViewById(R.id.productCard);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.common_category_image_item,parent,false));
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


        Picasso.get().load(productModel.getMainProductImage()).into(holder.imageView);
        holder.textView.setText(productModel.getProductName());

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


    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<BusinessProductModel> filterList = new ArrayList<BusinessProductModel>();
            filterList.clear();
            if(charSequence == null || charSequence.length() == 0 ){

                switch (selectedLocation) {
                    case "All of Sri Lanka":
                        filterList.addAll(modelListAll);
                        break;

                    case "Gampaha":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Gampaha") || city.equalsIgnoreCase("Gampaha")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Biyagama":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Biyagama") || city.equalsIgnoreCase("Biyagama")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Delgoda":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Delgoda") || city.equalsIgnoreCase("Delgoda")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Weliveriya":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Weliveriya") || city.equalsIgnoreCase("Weliveriya")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Colombo":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Colombo") || city.equalsIgnoreCase("Colombo")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Piliyandala":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Piliyandala") || city.equalsIgnoreCase("Piliyandala")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Maharagama":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Maharagama") || city.equalsIgnoreCase("Maharagama")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Boralesgamuwa":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Boralesgamuwa") || city.equalsIgnoreCase("Boralesgamuwa")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Nugegoda":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Nugegoda") || city.equalsIgnoreCase("Nugegoda")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Kaduwela":
                        for (BusinessProductModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Kaduwela") || city.equalsIgnoreCase("Kaduwela")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;
                }




            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(BusinessProductModel item: modelList){
                    if(item.getProductName().toLowerCase().startsWith(filterPattern)){
                        filterList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            modelList= (ArrayList<BusinessProductModel>)filterResults.values;
            CommonCategoryView.productModelListAll.clear();
            CommonCategoryView.productModelListAll.addAll( (ArrayList<BusinessProductModel>)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
