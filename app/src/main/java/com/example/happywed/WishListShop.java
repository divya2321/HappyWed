package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.example.happywed.Adapters.CommonCategoryListAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class WishListShop  extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private RecyclerView shopRecyclerView;

    private CommonCategoryListAdapter businessAdapter ;
    private ArrayList<BusinessModel> businessModelList = new ArrayList<BusinessModel>();

    private RecyclerViewSkeletonScreen skeletonScreen;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.wish_list_shop, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        shopRecyclerView = v.findViewById(R.id.shopRecyclerView);

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);


        businessModelList.clear();
        businessAdapter = new CommonCategoryListAdapter(getContext(), businessModelList);
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopRecyclerView.setAdapter(businessAdapter);

        readBusiness(new WishListShop.BusinessCallback(){

            @Override
            public void onCallback(final BusinessModel model) {
                databaseReference.child(model.getOwnerKey()).child("Shops").child(model.getBusinessKey()).child("Reviews").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        allRateCount = (int) dataSnapshot.getChildrenCount();
                        if(allRateCount == 0){
                            model.setRate(0.0f);
                            businessModelList.add(model);
                            businessAdapter.notifyDataSetChanged();
                            skeletonScreen.hide();
                        }else {
                            double overallCount = 0.0;
                            for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                                String rate = ((Map<String, String>) childSnapshot1.getValue()).get("rate");
                                allRateSum += Float.parseFloat(rate);
                                overallCount = Math.round(allRateSum / allRateCount * 10) / 10.0;
                            }
                            model.setRate((float) overallCount);
                            businessModelList.add(model);
                            businessAdapter.notifyDataSetChanged();
                            skeletonScreen.hide();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }

    private float allRateSum = 0;
    int allRateCount = 0;


    interface BusinessCallback {
        void onCallback(BusinessModel model);
    }

    private void readBusiness(final WishListShop.BusinessCallback reviewCallback) {

        skeletonScreen = Skeleton.bind(shopRecyclerView)
                .adapter(businessAdapter)
                .load(R.layout.layout_default_item_skeleton)
                .show();

        databaseReference.orderByChild("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot1: dataSnapshot.getChildren()) {

                    final String ownerKey = childSnapshot1.getKey();
                    databaseReference.child(ownerKey).child("Shops").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot childSnapshot2 : dataSnapshot.getChildren()){

                                final String shopKey = childSnapshot2.getKey();


                                String shopName = ((Map<String, String>) childSnapshot2.getValue()).get("shopName");
                                String profilePic = ((Map<String, String>) childSnapshot2.getValue()).get("profilePic");
                                String address = ((Map<String, String>) childSnapshot2.getValue()).get("address");
                                String district = ((Map<String, String>) childSnapshot2.getValue()).get("district");

                                String allAdd[] = address.trim().split(",");
                                String city = allAdd[allAdd.length-1];

                                final BusinessModel businessModel = new BusinessModel()
                                        .setOwnerKey(ownerKey)
                                        .setBusinessKey(shopKey)
                                        .setBusinessProPic(profilePic)
                                        .setBusinessName(shopName)
                                        .setUid(currentProfile.getUid())
                                        .setBusinessCity(city)
                                        .setBusinessDistrict(district)
                                        .setRate(3.0f);

                                databaseReference.child(ownerKey).child("Shops").child(shopKey).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(currentProfile.getUid())) {

                                            businessModel.setFav(true);
                                            reviewCallback.onCallback(businessModel);
                                        }else{
                                            skeletonScreen.hide();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
