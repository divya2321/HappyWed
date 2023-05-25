package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.example.happywed.Adapters.BusinessCategoryPopupAdapter;
import com.example.happywed.Adapters.BusinessProductImageAdapter;
import com.example.happywed.Adapters.BusinessShopProductListAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.Product;
import com.example.happywed.Models.BusinessProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BusinessProductDetails extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private  RecyclerView businessProductList;
    private ImageView productEmptyImage;
    private TextView productEmptyText;
    private View progressBar;

    private ArrayList<BusinessProductModel> businessProductModels = new ArrayList<BusinessProductModel>();
    private BusinessShopProductListAdapter businessShopProductListAdapter;

    private Dialog popDialog;
    private Dialog loadingDialog;
    private Animation closeEmptyNote, openEmptyNote;

    private String shopKey = null;

    private Toolbar toolBar;
    private RecyclerView productImageRecyclerView,categoryRecyclerView;
    private Button saveProductBtn,addPhotosBtn,deleteProductBtn;
    private EditText productNameTxt, productDiscriptionTxt, productPriceTxt;
    private ImageView cancelImg;
    private TextView categoryTitle,imgTitle;

    public static ArrayList<byte[]> productImages = new ArrayList();
    public static ArrayList<String> savedProductImages = new ArrayList();
    public static ArrayList<String> images = new ArrayList();
    public static BusinessProductImageAdapter productImageAdapter;

    private ArrayList<String> categories= new ArrayList<String>();
    private BusinessCategoryPopupAdapter categoryPopupAdapter;

    private String productName,productDescription,productPrice;

    private static final int REQUEST_GALLERY_PERMISSION_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 2;
    private static final int GALLERY_ACCESS_CODE = 3;
    private static final int CAMERA_ACCESS_CODE = 4;

    private Timestamp timestamp;
    DatabaseReference dref;

    private RecyclerViewSkeletonScreen skeletonScreen;

    private boolean isEdit = false;

    private static final String PRODUCTADDED = "ProductAdded";
    private static final String PRODUCTUPDATED = "ProductUpdated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_product_details);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        shopKey =  getIntent().getStringExtra("shopKey");


        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        storageReference = HappyWedDB.getStorageReference().child("businesses").child("shop pic").child("product pic");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        businessProductList = (RecyclerView) findViewById(R.id.businessProductList);

        productEmptyImage = (ImageView) findViewById(R.id.productImage);
        productEmptyText = (TextView) findViewById(R.id.oops_shop);
        progressBar =  findViewById(R.id.userProgressBar);

        progressBar.setVisibility(View.GONE);

        toolBar = (Toolbar) findViewById(R.id.my_toolbar);

        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        closeEmptyNote = AnimationUtils.loadAnimation(this, R.anim.close_empty_note);
        openEmptyNote = AnimationUtils.loadAnimation(this, R.anim.open_empty_note);

        popDialog = new Dialog(this);
        popDialog.setCancelable(false);

        loadingDialog = new ProgressDialog(BusinessProductDetails.this);
        loadingDialog.setCancelable(false);
        loadingDialog.setTitle("Uploading...");



        businessShopProductListAdapter = new BusinessShopProductListAdapter(this, businessProductModels);
        businessProductList.setLayoutManager(new LinearLayoutManager(this));
        businessProductList.setAdapter(businessShopProductListAdapter);

        productEmptyImage.setVisibility(View.GONE);
        productEmptyText.setVisibility(View.GONE);

       loadAllProducts();

        popDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popDialog.setContentView(R.layout.business_product_create_popup);

        productImageRecyclerView = (RecyclerView) popDialog.findViewById(R.id.productImageRecyclerView);
        categoryRecyclerView = (RecyclerView) popDialog.findViewById(R.id.categoryRecyclerView);
        saveProductBtn = (Button) popDialog.findViewById(R.id.popupSave);
        deleteProductBtn = (Button) popDialog.findViewById(R.id.popupDelete);
        addPhotosBtn = (Button) popDialog.findViewById(R.id.addPhotos);
        productNameTxt = (EditText) popDialog.findViewById(R.id.productName);
        productDiscriptionTxt = (EditText) popDialog.findViewById(R.id.productDiscription);
        productPriceTxt = (EditText) popDialog.findViewById(R.id.productPrice);
        cancelImg = (ImageView) popDialog.findViewById(R.id.cancel);
        categoryTitle = (TextView) popDialog.findViewById(R.id.categoryTitle);
        imgTitle = (TextView) popDialog.findViewById(R.id.imgTitle);


        categories.clear();
        categoryPopupAdapter = new BusinessCategoryPopupAdapter(this,categories);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryPopupAdapter);

        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> shopCategories = ((Map<String, ArrayList<String>>) dataSnapshot.getValue()).get("shopCategories");
                            for(String category: shopCategories){
                                categories.add(category);
                                categoryPopupAdapter.notifyDataSetChanged();
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

        productImages.clear();

        productImageAdapter = new BusinessProductImageAdapter(this,productImages);
        productImageRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        productImageRecyclerView.setAdapter(productImageAdapter);

        saveProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                productName = productNameTxt.getText().toString().trim();
                productDescription = productDiscriptionTxt.getText().toString().trim();
                productPrice = productPriceTxt.getText().toString().trim();


                if (!productName.isEmpty()) {

                    if (!productDescription.isEmpty()) {

                        if(categoryPopupAdapter.checkedItem.size()!=0){

                        if (productImages.size() != 0) {

                            if (productPrice.isEmpty()) {
                                productPrice = "non";
                            }
                            savedProductImages.clear();
                            loadingDialog.show();

                            if (!isEdit) {

                                databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            dref = databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").push();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                            final Product productModel = new Product()
                                                    .setProductName(productName)
                                                    .setProductDescription(productDescription)
                                                    .setProductPrice(productPrice)
                                                    .setProductCategories(categoryPopupAdapter.checkedItem);

                                            for (int i = 0; i < productImages.size(); i++) {

                                                final byte[] img = productImages.get(i);
                                                timestamp = new Timestamp(System.currentTimeMillis());
                                                final int finalI = i;
                                                storageReference.child("img_" + timestamp).putBytes(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Log.d(PRODUCTADDED, "Add img to storage: " + uri);

                                                                savedProductImages.add(uri.toString());

                                                                productModel.setProductImages(savedProductImages);

                                                                databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").child(dref.getKey()).setValue(productModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(PRODUCTADDED, "Add details to db");

                                                                            if (finalI == productImages.size() - 1) {
                                                                                loadAllProducts();

                                                                                Toast.makeText(BusinessProductDetails.this, "Product Added!", Toast.LENGTH_LONG).show();
                                                                                popDialog.dismiss();
                                                                                loadingDialog.dismiss();
                                                                            }


                                                                        } else {
                                                                            Log.d(PRODUCTADDED, "Cannot add details to db" + task.getException().toString());
                                                                            Toast.makeText(BusinessProductDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                                            loadingDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(PRODUCTADDED, "Cannot add img " + (img) + " to storage " + e);
                                                    }
                                                });
                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }


                                });

                            } else {

//                                Log.d("abc", "edit");
//                                Log.d("abc", productImages.size() + "");

                                for (final String pastImage : pastImgList) {

                                    StorageReference storage = FirebaseStorage.getInstance().getReferenceFromUrl(pastImage);
                                    storage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Log.d(PRODUCTUPDATED, "Delete past img: " + pastImage);
                                            } else {
                                                Log.d(PRODUCTUPDATED, "Cannot delete past img: " + pastImage);
                                            }
                                        }
                                    });

                                }


                                for (int i = 0; i < productImages.size(); i++) {
                                    Log.d(PRODUCTUPDATED, "" + productImages.size());
                                    final byte[] img = productImages.get(i);
                                    timestamp = new Timestamp(System.currentTimeMillis());
                                    final int finalI = i;
                                    Log.d(PRODUCTUPDATED, "" + img);
                                    storageReference.child("img_" + timestamp).putBytes(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                            Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d(PRODUCTUPDATED, "Add img to storage: " + uri);

                                                    savedProductImages.add(uri.toString());

                                                    databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                                                final Map<String, Object> updatedMap = new HashMap<String, Object>();
                                                                updatedMap.put("productName", productName);
                                                                updatedMap.put("productDescription", productDescription);
                                                                updatedMap.put("productPrice", productPrice);
                                                                updatedMap.put("productCategories", categoryPopupAdapter.checkedItem);
                                                                updatedMap.put("productImages", savedProductImages);

                                                                databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").child(productKey).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {
                                                                            Log.d(PRODUCTUPDATED, "Add details to db");

                                                                            if (finalI == productImages.size() - 1) {
                                                                                loadAllProducts();

                                                                                Toast.makeText(BusinessProductDetails.this, "Product Updated!", Toast.LENGTH_LONG).show();
                                                                                popDialog.dismiss();
                                                                                loadingDialog.dismiss();
                                                                            }

                                                                        } else {
                                                                            Log.d(PRODUCTUPDATED, "Cannot add details to db" + task.getException().toString());
                                                                            Toast.makeText(BusinessProductDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                                            loadingDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(PRODUCTUPDATED, "Cannot add img " + (img) + " to storage " + e);
                                        }
                                    });


                                }
                            }
                        } else {
                            imgTitle.setError("Picture is required!");
                        }

                    }else{
                            categoryTitle.setError("Category is required!");
                    }
                }else {
                        productDiscriptionTxt.setError("Description is required!");
                        productDiscriptionTxt.requestFocus();
                }

                }else{
                    productNameTxt.setError("Product name is required!");
                    productNameTxt.requestFocus();
                }
            }
        });

        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BusinessProductDetails.this);
                builder.setMessage("Do you want to delete this product?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        loadingDialog.show();

                            for(int i = 0; i<pastImgList.size(); i++){
                            final String pastImage = pastImgList.get(i);
                            StorageReference storage = FirebaseStorage.getInstance().getReferenceFromUrl(pastImage);
                            storage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Log.d(PRODUCTUPDATED, "Delete past img: " + pastImage);
                                    } else {
                                        Log.d(PRODUCTUPDATED, "Cannot delete past img: " + pastImage);
                                    }
                                }
                            });


                                final int finalI = i;
                                databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                        databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").child(productKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.d(PRODUCTUPDATED, "Delete details");

                                                    if(finalI ==pastImgList.size()-1) {
                                                        Toast.makeText(BusinessProductDetails.this, "Product Deleted!", Toast.LENGTH_LONG).show();
                                                        loadAllProducts();
                                                        popDialog.dismiss();
                                                        loadingDialog.dismiss();
                                                    }
                                                }else{
                                                    Log.d(PRODUCTUPDATED, "Cannot delete details");

                                                    Toast.makeText(BusinessProductDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                    loadingDialog.dismiss();
                                                }
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
                });
                builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();



            }
        });

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDialog.dismiss();

                if (businessProductList.getAdapter().getItemCount() == 0) {

                    productEmptyImage.startAnimation(openEmptyNote);
                    productEmptyImage.setVisibility(View.VISIBLE);
                    productEmptyText.setVisibility(View.VISIBLE);

                }else {
                    productEmptyImage.setVisibility(View.GONE);
                    productEmptyText.setVisibility(View.GONE);

                }
            }
        });

        addPhotosBtn.setOnClickListener(new View.OnClickListener() {
             @Override
              public void onClick(View view) {
                chooseOptions();
             }
        });

    }

    private void loadAllProducts() {

        businessProductModels.clear();

        skeletonScreen = Skeleton.bind(businessProductList)
                .adapter(businessShopProductListAdapter)
                .load(R.layout.layout_default_item_skeleton)
                .show();


        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0) {
                                productEmptyImage.setVisibility(View.GONE);
                                productEmptyText.setVisibility(View.GONE);
                            }else{
                                skeletonScreen.hide();
                                productEmptyImage.startAnimation(openEmptyNote);
                                productEmptyImage.setVisibility(View.VISIBLE);
                                productEmptyText.setVisibility(View.VISIBLE);
                            }
                            for (final DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {



                                String name = ((Map<String, String>) childSnapshot1.getValue()).get("productName");
                                String price = ((Map<String, String>) childSnapshot1.getValue()).get("productPrice");
                                String description = ((Map<String, String>) childSnapshot1.getValue()).get("productDescription");
                                ArrayList<String> images = ((Map<String, ArrayList<String>>) childSnapshot1.getValue()).get("productImages");
                                ArrayList<String> categories = ((Map<String, ArrayList<String>>) childSnapshot1.getValue()).get("productCategories");


                                BusinessProductModel viewModel = new BusinessProductModel()
                                        .setProductKey(childSnapshot1.getKey())
                                        .setProductName(name)
                                        .setProductDescription(description)
                                        .setProductPrice(price+" LKR")
                                        .setProductImages(images)
                                        .setProductCategories(categories)
                                        .setMainProductImage(images.get(0));
                                businessProductModels.add(viewModel);
                                businessShopProductListAdapter.notifyDataSetChanged();

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


    public void godoClickAdd(View view) {


        if (businessProductList.getAdapter().getItemCount() == 0) {
            productEmptyImage.startAnimation(closeEmptyNote);
            productEmptyText.startAnimation(closeEmptyNote);

            productEmptyImage.setVisibility(View.GONE);
            productEmptyText.setVisibility(View.GONE);
        }else {
            productEmptyImage.setVisibility(View.GONE);
            productEmptyText.setVisibility(View.GONE);
        }


        isEdit = false;

        saveProductBtn.setText(getResources().getString(R.string.save));

        deleteProductBtn.setEnabled(false);
        deleteProductBtn.setTextColor(getResources().getColor(R.color.black));
        deleteProductBtn.setBackground(ContextCompat.getDrawable(BusinessProductDetails.this, R.drawable.ash_corner_background));

        categoryPopupAdapter.checkedItem.clear();
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryPopupAdapter);

        productNameTxt.setText("");
        productDiscriptionTxt.setText("");
        productPriceTxt.setText("");

        productNameTxt.setError(null);
        productDiscriptionTxt.setError(null);
        productPriceTxt.setError(null);
        categoryTitle.setError(null);
        imgTitle.setError(null);


        productImages.clear();
        savedProductImages.clear();
        productImageAdapter.notifyDataSetChanged();

        popDialog.show();

    }


    public void chooseOptions(){

        final CharSequence[] options = {"Take Photo","Choose From Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i].equals("Take Photo")){
                    accessCamera();
                }else if(options[i].equals("Choose From Gallery")){
                    accessGallery();
                }
            }
        });

        builder.show();
    }



    private void accessGallery(){

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ACCESS_CODE);
        }else {
            ActivityCompat.requestPermissions(this,permissions,REQUEST_GALLERY_PERMISSION_CODE);
        }
    }

    private Uri outputFileUri = null;
    private  void  accessCamera(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),permissions[1])== PackageManager.PERMISSION_GRANTED){

            outputFileUri = Uri.fromFile(createImageFile());

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
            startActivityForResult(intent,CAMERA_ACCESS_CODE);

        }else{
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CAMERA_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION_CODE:
                if((grantResults.length ==2) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)&& (grantResults[1]==PackageManager.PERMISSION_GRANTED)) {
                    accessCamera();
                }
                break;

            case REQUEST_GALLERY_PERMISSION_CODE:
                if((grantResults.length ==1) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)) {
                    accessGallery();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == GALLERY_ACCESS_CODE || requestCode == CAMERA_ACCESS_CODE) && (resultCode == RESULT_OK)){


            if(requestCode == GALLERY_ACCESS_CODE){

                try {
                    InputStream iStream = getContentResolver().openInputStream(data.getData());
                    byte[] inputData = getBytes(iStream);
                    productImages.add(inputData);
                    productImageAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{

                try {
                    InputStream iStream = getContentResolver().openInputStream(outputFileUri);
                    byte[] inputData = getBytes(iStream);
                    productImages.add(inputData);
                    productImageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private File createImageFile(){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {

            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }


    private ArrayList<String> pastImgList = new ArrayList<String>();
    private String productKey;
    private StorageReference storage;
//    private ArrayList<byte[]> savedImgBytes = new ArrayList<byte[]>();

    public void showDetails(BusinessProductModel productModel){

        isEdit = true;
        categoryPopupAdapter.checkedItem.clear();
        productImages.clear();
        pastImgList.clear();

        productNameTxt.setError(null);
        productDiscriptionTxt.setError(null);
        productPriceTxt.setError(null);
        categoryTitle.setError(null);
        imgTitle.setError(null);

        saveProductBtn.setText(getResources().getString(R.string.edit));

        deleteProductBtn.setEnabled(true);
        deleteProductBtn.setTextColor(getResources().getColor(R.color.white));
        deleteProductBtn.setBackground(ContextCompat.getDrawable(BusinessProductDetails.this, R.drawable.rounded_corners));

        productKey = productModel.getProductKey();
        productNameTxt.setText(productModel.getProductName());
        productDiscriptionTxt.setText(productModel.getProductDescription());
        productPriceTxt.setText(productModel.getProductPrice().split(" LKR")[0]);

        for(String category: productModel.getProductCategories()) {
            Log.d("aaa",category);
            categoryPopupAdapter.checkedItem.add(category);
        }
            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            categoryRecyclerView.setAdapter(categoryPopupAdapter);

        for (String img: productModel.getProductImages()) {
            pastImgList.add(img);
//            productImages.add(Uri.parse(img));

            storage = FirebaseStorage.getInstance().getReferenceFromUrl(img);

            try {
                final File localFile = File.createTempFile("images", "jpg");


                storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bm = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        productImages.add(baos.toByteArray());
                        productImageAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        popDialog.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    private void status(final String status){
        databaseReference.orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    databaseReference.child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
