package com.example.happywed.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.BusinessProductDetails;
import com.example.happywed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BusinessProductImageAdapter extends RecyclerView.Adapter<BusinessProductImageAdapter.MyViewHolder> {


    Context context;
    ArrayList<byte[]> modelList;


    public BusinessProductImageAdapter(Context context, ArrayList<byte[]> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public BusinessProductImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusinessProductImageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_product_image_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BusinessProductImageAdapter.MyViewHolder holder, final int position) {


//        Picasso.with(context).load(modelList.get(position)).into(holder.imageView);
        Bitmap bitmap = BitmapFactory.decodeByteArray(modelList.get(position), 0, modelList.get(position).length);
        holder.imageView.setImageBitmap(bitmap);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(position == modelList.size()-1){
//                    BusinessProductDetails pch = (BusinessProductDetails) context;
//                    pch.chooseOptions();
//                }else{
//                    if(modelList.size() != 0){

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to remove this picture?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BusinessProductDetails.productImages.remove(position);
                                BusinessProductDetails.productImageAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

//                    }
//                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
