package com.example.happywed.Adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.happywed.Checklist;
import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.Models.ChecklistModel;
import com.example.happywed.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChecklistModel> checkLists;



    public ChecklistAdapter(Context context, ArrayList<ChecklistModel> checkLists) {
        this.context = context;
        this.checkLists = checkLists;
    }




    static class MyViewHolder extends RecyclerView.ViewHolder{

        public static TextView titleName,completedDate,done,delete;
        ImageView status;
        SwipeLayout swipeLayout;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            titleName = (TextView) itemView.findViewById(R.id.titleName);
            completedDate = (TextView) itemView.findViewById(R.id.setCompleteDate);
            status = (ImageView) itemView.findViewById(R.id.titleState);
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe);
            done = (TextView) itemView.findViewById(R.id.done);
            delete = (TextView) itemView.findViewById(R.id.delete);
        }
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.checklist_item,parent,false));
    }

    Cursor userSearch;
    String userId;


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ChecklistModel checklistModel = checkLists.get(position);

        Log.d("In_ON_BIND_VIEW",checklistModel.getStatusText());

        holder.titleName.setText(checklistModel.getTitle());
        holder.completedDate.setText(checklistModel.getCheckDate());
        holder.status.setImageResource(checklistModel.getStatus());
        holder.done.setText(checklistModel.getStatusText());



        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));


        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Checklist.isPopUpOpen = true;
                Checklist.isEdit = true;

                Checklist.updateId = checklistModel.getId();

                ((Checklist)context).titleText.setText(checklistModel.getTitle());

                String[] checkDate = checklistModel.getCheckDate().split("-");

                int day = Integer.parseInt(checkDate[0].trim());
                int month = Integer.parseInt(checkDate[1].trim())-1;
                int year = Integer.parseInt(checkDate[2].trim());
                ((Checklist)context).completedDate.updateDate(year,month,day);

                ((Checklist)context).bottomBorder.animate().translationY(0).setDuration(800).start();
                ((Checklist)context).addChecklistBtn.setAlpha((float) 0.0);

                ((Checklist)context).checklistList.animate().alpha(0.0f).setStartDelay(500);

                Toast.makeText(context, "List Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });




        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int modelId = checklistModel.getId();
                String modelText = checklistModel.getStatusText();
//                String modelText = holder.done.getText().toString();
                Log.d("GET_STATUS_ID", String.valueOf(modelId)+modelText);


                if(modelText.equals("Done")){
                    HappyWed.iud(context, "UPDATE checklist SET status='1' WHERE itemId='"+modelId+"'");
                    Log.d("GET_STATUS_ID_WHEN_DONE", String.valueOf(modelId)+"Done");
                    checklistModel.setStatus(R.drawable.checklist_done);
                    checklistModel.setStatusText("Undone");
//                    holder.done.setText("Undone");
                    notifyDataSetChanged();
                    ((Checklist)context).loadAllItem();
                    Log.d("GET_STATUS_ID", modelId+"new"+checklistModel.getStatusText().toString());
                }else{
                    HappyWed.iud(context, "UPDATE checklist SET status='0' WHERE itemId='"+modelId+"'");
                    Log.d("GET_STATUS_ID_WHEN_UN", String.valueOf(modelId)+"UnDone");
                    checklistModel.setStatus(R.drawable.checklist_pending);
                    checklistModel.setStatusText("Done");
                    ((Checklist)context).loadAllItem();
//                    holder.done.setText("Done");
                    notifyDataSetChanged();
                    Log.d("GET_STATUS_ID", modelId+"new"+checklistModel.getStatusText().toString());
                }

                ((Checklist)context).loadAllItem();
            }
        });


























        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int deleteId=checklistModel.getId();

                Log.d("OUTPUT", String.valueOf(deleteId));


                HappyWed.iud(context, "DELETE FROM checklist WHERE itemId='"+deleteId+"'");


                Snackbar snackbar = Snackbar.make(holder.swipeLayout, String.valueOf(deleteId)  + " removed!", Snackbar.LENGTH_LONG);
//
//                deletedIndex = position;
                checkLists.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,checkLists.size());


//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        checkLists.add(deletedIndex,checklistModel);
//                        notifyItemInserted(position);
//                    }
//                });
//                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        });

    }

    public static String defaultTextButton = "Done";

    @Override
    public int getItemCount() {
        return checkLists.size();
    }


}
