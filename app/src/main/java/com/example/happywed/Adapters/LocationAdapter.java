package com.example.happywed.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.happywed.LocationExpand;
import com.example.happywed.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationAdapter extends BaseExpandableListAdapter {

    Context context;
    public static HashMap<String,ArrayList<String>> locations;
    HashMap<String,ArrayList<String>> orginalList;
    public static ArrayList<String> locationsKey;

    public LocationAdapter(Context context, HashMap<String, ArrayList<String>> locations){

        this.context = context;
        this.locations = new HashMap<String,ArrayList<String>>();
        this.locations.putAll(locations);
        this.orginalList = new HashMap<String,ArrayList<String>>();
        this.orginalList.putAll(locations);
        this.locationsKey = new ArrayList<String>(locations.keySet());

    }

    @Override
    public int getGroupCount() {
        return locationsKey.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return locations.get(locationsKey.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return locationsKey.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return locations.get(locationsKey.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean b, View view, ViewGroup viewGroup) {

        String districtTxt = (String) getGroup(listPosition);

        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.location_district,viewGroup,false);
        }
        TextView rootTextView = (TextView)view.findViewById(R.id.locationDistrict);
        rootTextView.setTypeface(null, Typeface.BOLD);
        rootTextView.setText(districtTxt);
        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean b, View view, ViewGroup viewGroup) {

        String childText = (String) getChild(listPosition,expandedListPosition);

        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.location_city,viewGroup,false);
        }
        TextView childTextView = (TextView)view.findViewById(R.id.locationCity);
        childTextView.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void filterData(String query){

        query = query.toLowerCase();
        locations.clear();
        locationsKey.clear();

        if(query.isEmpty()){
            for(Map.Entry<String, ArrayList<String>> set: orginalList.entrySet()) {

                ArrayList<String> newCityList = new ArrayList<String>();

                for(String city: set.getValue()){

                    newCityList.add(city);
                }
                locations.put(set.getKey(),newCityList);
                locationsKey.add(set.getKey());
            }

            LocationExpand.unExpandAll();
        }
        else {
            for(Map.Entry<String, ArrayList<String>> set: orginalList.entrySet()) {

                ArrayList<String> newCityList = new ArrayList<String>();
                for(String city: set.getValue()) {
                    if (city.toLowerCase().startsWith(query)) {
                        newCityList.add(city);
                    }
                }

                if (newCityList.size() > 0) {
                    locations.put(set.getKey(), newCityList);
                    locationsKey.add(set.getKey());
                }
            }
            LocationExpand.expandAll();
        }
        notifyDataSetChanged();
    }


}
