package com.example.happywed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.R;

import java.util.ArrayList;

public class BusinessCategoryEditAdapter extends RecyclerView.Adapter<BusinessCategoryEditAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> modelList;
    public boolean isEditable = false;

    public static ArrayList<String> checkedItem = new ArrayList<String>();

    public BusinessCategoryEditAdapter(Context context, ArrayList<String> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        Button categoryBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryBtn = (Button) itemView.findViewById(R.id.categoryBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_category_edit_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.categoryBtn.setText(modelList.get(position));

        if(!checkedItem.isEmpty()) {

            for (String selectBtnTxt : checkedItem) {
                if (selectBtnTxt.equalsIgnoreCase(modelList.get(position))) {
                    holder.categoryBtn.setTextColor(context.getResources().getColor(R.color.white));
                    holder.categoryBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.app_color_round_btn));
                }
            }

        }

        holder.categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEditable) {

                    if (!checkedItem.contains(((Button) view).getText().toString())) {
                        ((Button) view).setTextColor(context.getResources().getColor(R.color.white));
                        ((Button) view).setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.app_color_round_btn));
                        checkedItem.add(((Button) view).getText().toString());
                    } else {
                        ((Button) view).setTextColor(context.getResources().getColor(R.color.dark_ash));
                        ((Button) view).setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ash_corner_background));
                        checkedItem.remove(((Button) view).getText().toString());
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
