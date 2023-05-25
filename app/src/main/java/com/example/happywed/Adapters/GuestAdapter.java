package com.example.happywed.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.happywed.Budget;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.Guest;
import com.example.happywed.Models.BudgetModel;
import com.example.happywed.Models.GuestModel;
import com.example.happywed.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.MyViewHolder> {

        Context context;
        ArrayList<GuestModel> guests;

        GuestModel deletedItem;
        int deletedIndex;

        Cursor userSearch;
        String userId;


public GuestAdapter(Context context, ArrayList<GuestModel> guests) {
        this.context = context;
        this.guests = guests;
        }

class MyViewHolder extends RecyclerView.ViewHolder{

    TextView familyName,adults, children, all,delete;
    SwipeLayout swipeLayout;

    public MyViewHolder(@NonNull View itemView){
        super(itemView);
        familyName = (TextView) itemView.findViewById(R.id.familyName);
        adults = (TextView) itemView.findViewById(R.id.adultCount);
        children = (TextView) itemView.findViewById(R.id.childCount);
        all = (TextView) itemView.findViewById(R.id.allCount);

        swipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe);
        delete = (TextView) itemView.findViewById(R.id.delete);
    }
}
    @NonNull
    @Override
    public GuestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new GuestAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.guest_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GuestAdapter.MyViewHolder holder, final int position) {
        final GuestModel guestModel = guests.get(position);

        holder.familyName.setText(guestModel.getFamilyName());
        holder.adults.setText(String.valueOf(guestModel.getAdultCount()));
        holder.children.setText(String.valueOf(guestModel.getChildCount()));
        holder.all.setText(String.valueOf(guestModel.getAllCount()));

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));


        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Guest.isPopUpOpen = true;
                Guest.isEdit = true;

                Guest.updateId = guestModel.getGuestId();

                ((Guest)context).familyName.setText(guestModel.getFamilyName());
                ((Guest)context).adultCountText.setText(String.valueOf(guestModel.getAdultCount()));
                ((Guest)context).childCountText.setText(String.valueOf(guestModel.getChildCount()));

                ((Guest)context).bottomBorder.animate().translationY(0).setDuration(800).start();
                ((Guest)context).addGuestlistBtn.setAlpha((float) 0.0);

                ((Guest)context).guestList.animate().alpha(0.0f).setStartDelay(500);

                Toast.makeText(context, "Guest Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deleteId=guestModel.getGuestId();

                Log.d("OUTPUT", String.valueOf(deleteId));


                HappyWed.iud(context, "DELETE FROM guest WHERE guestId='"+deleteId+"'");



                deletedItem = guests.get(position);
                deletedIndex = position;
                guests.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,guests.size());


                ((Guest)context).loadAllItem();

                Snackbar snackbar = Snackbar.make(holder.swipeLayout, deletedItem.getFamilyName()  + " removed!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        guests.add(deletedIndex,deletedItem);
                        notifyItemInserted(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return guests.size();
    }


}