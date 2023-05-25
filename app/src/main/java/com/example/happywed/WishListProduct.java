package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.happywed.Adapters.BusinessProductCardAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.BusinessProductModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class WishListProduct extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private RecyclerView productRecyclerView;

    private BusinessProductCardAdapter productAdapter;
    private ArrayList<BusinessProductModel> productList = new ArrayList<BusinessProductModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.wish_list_product, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        productRecyclerView = v.findViewById(R.id.productRecyclerView);

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        productList.clear();
        productAdapter = new BusinessProductCardAdapter(getContext(), productList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        productRecyclerView.setAdapter(productAdapter);

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
                                databaseReference.child(ownerKey).child("Shops").child(shopKey).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot childSnapshot3 : dataSnapshot.getChildren()) {

                                            final String productKey = childSnapshot3.getKey();
                                            String name = ((Map<String, String>) childSnapshot3.getValue()).get("productName");
                                            String price = ((Map<String, String>) childSnapshot3.getValue()).get("productPrice");
                                            ArrayList<String> images = ((Map<String, ArrayList<String>>) childSnapshot3.getValue()).get("productImages");

                                            final BusinessProductModel productModel = new BusinessProductModel()
                                                    .setOwnerKey(ownerKey)
                                                    .setBusinessKey(shopKey)
                                                    .setProductKey(productKey)
                                                    .setUid(currentProfile.getUid())
                                                    .setProductName(name)
                                                    .setProductPrice(price)
                                                    .setProductImages(images)
                                                    .setMainProductImage(images.get(0));


                                            databaseReference.child(ownerKey).child("Shops").child(shopKey).child("Products").child(productKey).child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.hasChild(currentProfile.getUid())) {

                                                        productModel.setFav(true);
                                                        productList.add(productModel);
                                                        productAdapter.notifyDataSetChanged();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }
}
