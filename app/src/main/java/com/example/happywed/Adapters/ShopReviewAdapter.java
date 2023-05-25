package com.example.happywed.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.Models.ShopReviewModel;
import com.example.happywed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopReviewAdapter extends RecyclerView.Adapter<ShopReviewAdapter.MyViewHolder> {

    Context context;
    ArrayList<ShopReviewModel> modelList;


    public ShopReviewAdapter(Context context, ArrayList<ShopReviewModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView customerImage;
        TextView customerName,reviewedDate,review;
        RatingBar customerRate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            customerImage = (CircleImageView) itemView.findViewById(R.id.customer_image);
            customerName = (TextView) itemView.findViewById(R.id.customer_name);
            reviewedDate = (TextView) itemView.findViewById(R.id.customer_reviewed_date);
            review = (TextView) itemView.findViewById(R.id.customer_review);
            customerRate = (RatingBar) itemView.findViewById(R.id.customer_rate);



        }
    }

    @NonNull
    @Override
    public ShopReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopReviewAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.shop_review_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopReviewAdapter.MyViewHolder holder, int position) {
        final ShopReviewModel companyModel = modelList.get(position);

        Picasso.get().load(companyModel.getUserProfilePic()).into(holder.customerImage);
        holder.customerName.setText(companyModel.getUserName());
        holder.reviewedDate.setText(companyModel.getReviewedDate());
        holder.review.setText(companyModel.getReview());
        holder.customerRate.setRating(companyModel.getRate());


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}