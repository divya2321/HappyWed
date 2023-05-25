package com.example.happywed.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.happywed.Budget;
import com.example.happywed.Custom;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.Models.CustomModel;
import com.example.happywed.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>  {


    Context context;
    ArrayList<CustomModel> customs;

    CustomModel deletedItem;
    int deletedIndex;

    Date toDay;
    SimpleDateFormat simpleDateFormat;

    Cursor userSearch;
    String userId;


    public CustomAdapter(Context context, ArrayList<CustomModel> customs) {
        this.context = context;
        this.customs = customs;


        simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
        toDay = new Date();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleText,savedDate,done,delete;
        SwipeLayout swipeLayout;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            savedDate = (TextView) itemView.findViewById(R.id.savedDate);
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe);
            done = (TextView) itemView.findViewById(R.id.done);
            delete = (TextView) itemView.findViewById(R.id.delete);
        }
    }
    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CustomAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull final CustomAdapter.MyViewHolder holder,  final int position) {
        final CustomModel customModel = customs.get(position);

        holder.titleText.setText(customModel.getCustomTitle());
        holder.savedDate.setText(simpleDateFormat.format(toDay));

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));


        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Custom.isPopUpOpen = true;
                Custom.isEdit = true;
                Custom.updateId = customModel.getCustomId();
                Log.d("JUST_CLICKED_ID", String.valueOf(customModel.getCustomId()));

                ((Custom)context).customTitle.setText(customModel.getCustomTitle());
                ((Custom)context).addrContact.setText(customModel.getAddrContact());
                ((Custom)context).addrNo.setText(customModel.getAddrNo());
                ((Custom)context).addrStreet.setText(customModel.getAddrStreet());
                ((Custom)context).addrCity.setText(customModel.getAddrCity());
                ((Custom)context).customDescription.setText(customModel.getAddrDescription());
                ((Custom)context).customBudget.setText(customModel.getCustomBudget());

                ((Custom)context).bottomBorder.animate().translationY(0).setDuration(800).start();
                ((Custom)context).addCustomlistBtn.setAlpha((float) 0.0);

                ((Custom)context).customList.animate().alpha(0.0f).setStartDelay(500);

                Toast.makeText(context, "Custom Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deleteId=customModel.getCustomId();

                Log.d("OUTPUT", String.valueOf(deleteId));


                HappyWed.iud(context, "DELETE FROM custom WHERE customId='"+deleteId+"'");

                deletedItem = customs.get(position);
                deletedIndex = position;
                customs.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,customs.size());

                ((Custom)context).loadAllItem();

//                Snackbar snackbar = Snackbar.make(holder.swipeLayout, deletedItem.getCustomTitle()  + " removed!", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        customs.add(deletedIndex,deletedItem);
//                        notifyItemInserted(position);
//                    }
//                });
//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return customs.size();
    }



}
