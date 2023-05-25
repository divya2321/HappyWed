package com.example.happywed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.BusinessProfile;
import com.example.happywed.CommonCategoryView;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessModel;
import com.example.happywed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommonCategoryListAdapter extends RecyclerView.Adapter<CommonCategoryListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    public static ArrayList<BusinessModel> modelList;
    public static ArrayList<BusinessModel> modelListAll;
    private DatabaseReference databaseReference;
    private boolean processFav = false;
    public static String selectedLocation = "All of Sri Lanka";

    public CommonCategoryListAdapter(Context context, ArrayList<BusinessModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        this.modelListAll = modelList;
        databaseReference = HappyWedDB.getDBConnection().child("businesses");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView proPic;
        ImageView favImage;
        TextView  nameView, city, district;
        RatingBar rateView;
        CardView commonCategoryCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            proPic = (CircleImageView) itemView.findViewById(R.id.businessImage);
            favImage = (ImageView) itemView.findViewById(R.id.favImage);
            nameView = (TextView) itemView.findViewById(R.id.businessName);
            city = (TextView) itemView.findViewById(R.id.city);
            district = (TextView) itemView.findViewById(R.id.district);
            rateView = (RatingBar) itemView.findViewById(R.id.ratingStar);
            commonCategoryCard = (CardView) itemView.findViewById(R.id.categoryCard);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.common_category_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final BusinessModel commonModel = modelList.get(position);

        if(commonModel.isFav()){
            holder.favImage.setImageResource(R.drawable.favourite);
            holder.favImage.setTag("fav");

        }else{
            holder.favImage.setImageResource(R.drawable.unfavourite);
            holder.favImage.setTag("unFav");
        }


        if(commonModel.getBusinessProPic() != null){
            Picasso.get().load(commonModel.getBusinessProPic()).into(holder.proPic);
        }else{
            holder.proPic.setImageResource(R.drawable.defaultshoppic);
        }
        holder.nameView.setText(commonModel.getBusinessName());
        holder.city.setText(commonModel.getBusinessCity());
        holder.district.setText(commonModel.getBusinessDistrict());
        holder.rateView.setRating(commonModel.getRate());

        holder.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                processFav = true;

                databaseReference.child(commonModel.getOwnerKey()).child("Shops").child(commonModel.getBusinessKey()).child("Favorite").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(processFav) {
                            if (dataSnapshot.hasChild(commonModel.getUid())) {
                                processFav = false;
                                databaseReference.child(commonModel.getOwnerKey()).child("Shops").child(commonModel.getBusinessKey()).child("Favorite").child(commonModel.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("abc","uuuu");
                                            Snackbar.make(view,"Item Removed from WishList",Snackbar.LENGTH_LONG).show();
                                            holder.favImage.setImageResource(R.drawable.unfavourite);
                                            holder.favImage.setTag("unFav");

                                        }
                                    }
                                });

                            } else {
                                processFav = false;
                                databaseReference.child(commonModel.getOwnerKey()).child("Shops").child(commonModel.getBusinessKey()).child("Favorite").child(commonModel.getUid()).setValue("random value").addOnCompleteListener(new OnCompleteListener<Void>() {
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


        holder.commonCategoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, BusinessProfile.class).putExtra("ownerKey",commonModel.getOwnerKey().trim()).putExtra("shopKey",commonModel.getBusinessKey().trim()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    @Override
    public Filter getFilter() {
        return businesFilter;
    }

    private Filter businesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<BusinessModel> filterList = new ArrayList<BusinessModel>();
            filterList.clear();
            if(charSequence == null || charSequence.length() == 0 ){


                switch (selectedLocation) {
                    case "All of Sri Lanka":
                        filterList.addAll(modelListAll);
                        break;

                    case "Gampaha":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Gampaha") || city.equalsIgnoreCase("Gampaha")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Biyagama":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Biyagama") || city.equalsIgnoreCase("Biyagama")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Delgoda":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Delgoda") || city.equalsIgnoreCase("Delgoda")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Weliveriya":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Weliveriya") || city.equalsIgnoreCase("Weliveriya")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Colombo":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Colombo") || city.equalsIgnoreCase("Colombo")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Piliyandala":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Piliyandala") || city.equalsIgnoreCase("Piliyandala")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Maharagama":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Maharagama") || city.equalsIgnoreCase("Maharagama")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Boralesgamuwa":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Boralesgamuwa") || city.equalsIgnoreCase("Boralesgamuwa")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Nugegoda":
                        for (BusinessModel model : modelListAll) {
                            String distict = model.getBusinessDistrict();
                            String city = model.getBusinessCity();
                            if (distict.equalsIgnoreCase("Nugegoda") || city.equalsIgnoreCase("Nugegoda")) {
                                filterList.add(model);
                            }
                        }
//                        filterList.addAll(filterByCategory(locationFilterList));
                        break;

                    case "Kaduwela":
                        for (BusinessModel model : modelListAll) {
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
                for(BusinessModel item: modelList){
                    if(item.getBusinessName().toLowerCase().startsWith(filterPattern)){
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
            modelList= (ArrayList<BusinessModel>)filterResults.values;
            CommonCategoryView.businessModelListAll.clear();
            CommonCategoryView.businessModelListAll.addAll( (ArrayList<BusinessModel>)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
