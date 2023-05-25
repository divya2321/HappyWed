package com.example.happywed.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.happywed.Checklist;
import com.example.happywed.Models.ChecklistModel;
import com.example.happywed.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class BusinessCategoryTextAdapter extends RecyclerView.Adapter<BusinessCategoryTextAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> textList;


    public BusinessCategoryTextAdapter(Context context, ArrayList<String> textList) {
        this.context = context;
        this.textList = textList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView categoryName;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.categoryText);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_category_text_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.categoryName.setText(textList.get(position));

    }

    @Override
    public int getItemCount() {
        return textList.size();
    }


}
