package com.example.happywed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happywed.Models.CommonCategoryModel;
import com.example.happywed.R;

import java.util.ArrayList;

public class BusinessCategoryPopupAdapter extends RecyclerView.Adapter<BusinessCategoryPopupAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> modelList;

    public ArrayList<String> checkedItem = new ArrayList<String>();

    public BusinessCategoryPopupAdapter(Context context, ArrayList<String> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CheckedTextView checkedTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedTextView = (CheckedTextView) itemView.findViewById(R.id.categoryCheckedText);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.business_category_popup_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.checkedTextView.setText(modelList.get(position));


        if(!checkedItem.isEmpty()) {

            for (String item : checkedItem) {
                if (item.equalsIgnoreCase(modelList.get(position))) {
                    holder.checkedTextView.setChecked(true);
                }
            }

        }

        holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkedTextView.isChecked()){
                    holder.checkedTextView.setChecked(false);
                    checkedItem.remove(holder.checkedTextView.getText().toString());
                }else {
                    holder.checkedTextView.setChecked(true);
                    checkedItem.add(holder.checkedTextView.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
