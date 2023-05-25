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
import com.example.happywed.Checklist;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.Models.BudgetModel;
import com.example.happywed.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.MyViewHolder> {

    Context context;
    ArrayList<BudgetModel> budgets;

    BudgetModel deletedItem;
    int deletedIndex;

    Cursor userSearch;
    String userId;


    public BudgetAdapter(Context context, ArrayList<BudgetModel> budgets) {
        this.context = context;
        this.budgets = budgets;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleName,estimatedBudget,done,delete;
        ImageView status;
        SwipeLayout swipeLayout;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            titleName = (TextView) itemView.findViewById(R.id.titleName);
            estimatedBudget = (TextView) itemView.findViewById(R.id.estimatedCostText);
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe);
            done = (TextView) itemView.findViewById(R.id.done);
            delete = (TextView) itemView.findViewById(R.id.delete);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.budget_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final BudgetModel budgetModel = budgets.get(position);

        holder.titleName.setText(budgetModel.getTitle());
        holder.estimatedBudget.setText(String.valueOf(budgetModel.getEstimatedCost()));

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));


        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Budget.isPopUpOpen = true;
                Budget.isEdit = true;
                Budget.updateId = budgetModel.getBudgetId();

                ((Budget)context).titleText.setText(budgetModel.getTitle());
                ((Budget)context).estimateText.setText(String.valueOf(budgetModel.getEstimatedCost()));

                ((Budget)context).bottomBorder.animate().translationY(0).setDuration(800).start();
                ((Budget)context).addBudgetlistBtn.setAlpha((float) 0.0);

                ((Budget)context).budgetList.animate().alpha(0.0f).setStartDelay(500);

                Toast.makeText(context, "Budget Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userSearch.moveToNext()){
                    userId = userSearch.getString(0);
                }

                int deleteId=budgetModel.getBudgetId();

                Log.d("OUTPUT", String.valueOf(deleteId));


                HappyWed.iud(context, "DELETE FROM budget WHERE budgetId='"+deleteId+"'");

                deletedItem = budgets.get(position);
                deletedIndex = position;
                budgets.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,budgets.size());

                ((Budget)context).loadAllItem();

//                Snackbar snackbar = Snackbar.make(holder.swipeLayout, deletedItem.getTitle()  + " removed!", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        budgets.add(deletedIndex,deletedItem);
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
        return budgets.size();
    }


}