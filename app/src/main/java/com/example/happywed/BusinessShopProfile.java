package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.Models.ShopReviewModel;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessShopProfile extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Toolbar toolBar;
    private TextView businessShopNameTxt,productCountTxt,overallRateTxt;
    private CircleImageView shopProPic;
    private TextView shopStatusTitle,deactivateTitle;
    private View progressBar;

    private String currentStatus;

    private Timestamp timestamp;

    private Uri selectImgUri = null;

    private static final String IMGUPDATE = "ImgUpdate";

    private static final int REQUEST_GALLERY_PERMISSION_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 2;
    private static final int GALLERY_ACCESS_CODE = 3;
    private static final int CAMERA_ACCESS_CODE = 4;

    private String shopKey = null;
    private String shopName;

    static Snackbar comformatonAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_shop_profile);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        shopKey =  getIntent().getStringExtra("shopKey");

        databaseReference = HappyWedDB.getDBConnection().child("businesses");
        storageReference = HappyWedDB.getStorageReference().child("businesses").child("shop pic").child("profile pic");
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        businessShopNameTxt = findViewById(R.id.businessShopName);
        shopProPic = findViewById(R.id.shopProPic);
        productCountTxt = findViewById(R.id.productCount);
        shopStatusTitle = findViewById(R.id.shopStatusTitle);
        deactivateTitle = findViewById(R.id.deactivateTitle);
        overallRateTxt = findViewById(R.id.overallRateTxt);
        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        progressBar =  findViewById(R.id.userProgressBar);

        progressBar.setVisibility(View.GONE);

        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        comformatonAlert = Snackbar.make(shopStatusTitle,"Complete your account to publish!",Snackbar.LENGTH_INDEFINITE);

        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {



                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                             shopName = ((Map<String, String>) dataSnapshot.getValue()).get("shopName");
                            String profilePic = ((Map<String, String>) dataSnapshot.getValue()).get("profilePic");
                            String confirmation = ((Map<String, String>) dataSnapshot.getValue()).get("confirmation");
                            currentStatus = ((Map<String, String>) dataSnapshot.getValue()).get("status");
                            businessShopNameTxt.setText(shopName);
                            if(profilePic !=null) {
                                Picasso.get().load(Uri.parse(profilePic)).into(shopProPic);
                            }else{
                                shopProPic.setImageResource(R.drawable.defaultshoppic);
                            }
                            if(currentStatus.equals("1")){
                                shopStatusTitle.setText(getResources().getString(R.string.active_status));
                                shopStatusTitle.setTextColor(getResources().getColor(R.color.dark_ash));
                                deactivateTitle.setText(getResources().getString(R.string.deactive_shop));
                            }else{
                                shopStatusTitle.setText(getResources().getString(R.string.deactive_status));
                                shopStatusTitle.setTextColor(getResources().getColor(R.color.button_end));
                                deactivateTitle.setText(getResources().getString(R.string.active_shop));
                            }

                            if(confirmation.equals("0")){
                                comformatonAlert.show();
                            }else{
                                comformatonAlert.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productCountTxt.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            allRateCount = (int) dataSnapshot.getChildrenCount();

                            for (final DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {

                                String rate = ((Map<String, String>) childSnapshot1.getValue()).get("rate");
                                setOverallRate(Float.parseFloat(rate));
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


    private int allRateCount = 0;
    private float allRateSum = 0;
    private void setOverallRate(float rate){
        allRateSum += rate;
        double overallCount = Math.round(allRateSum/allRateCount * 10) / 10.0;
        overallRateTxt.setText(String.valueOf(overallCount));
    }


    public void gotoBusinessGeneralDetails(View view) {

        startActivity(new Intent(this, BusinessShopGeneralDetail.class).putExtra("shopKey",shopKey));

    }

    public void gotoBusinessProductDetails(View view) {

        startActivity(new Intent(this, BusinessProductDetails.class).putExtra("shopKey",shopKey));
    }

    public void editShopProPic(View view) {

        chooseOptions();

    }


    private void chooseOptions(){

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

    private void accessCamera(){
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

            Uri imageUri;
            if(requestCode == GALLERY_ACCESS_CODE){
                imageUri= data.getData();
            }else{
                imageUri = outputFileUri;;
            }


            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start( this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                selectImgUri = result.getUri();
                shopProPic.setImageURI(selectImgUri);

                saveShopProPic();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
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

    private void saveShopProPic(){

        if(selectImgUri != null){

            progressBar.setVisibility(View.VISIBLE);

            timestamp = new Timestamp(System.currentTimeMillis());
            storageReference.child("img_"+timestamp).putFile(selectImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            Log.d(IMGUPDATE,"Update img to storage: "+uri);

                            databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(final DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                                        final Map<String,Object> updatedMap = new HashMap<String, Object>();
                                        updatedMap.put("profilePic",uri.toString());

                                        databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                String pastUrl = ((Map<String, String>) dataSnapshot.getValue()).get("profilePic");
                                                if(pastUrl != null){
                                                    Log.d(IMGUPDATE,"past url: "+pastUrl);

                                                    StorageReference storage=  FirebaseStorage.getInstance().getReferenceFromUrl(pastUrl);
                                                    storage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d(IMGUPDATE,"Delete past img");

                                                                databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful()){
                                                                            Log.d(IMGUPDATE,"Update img to db "+uri);
                                                                            Toast.makeText(BusinessShopProfile.this, "Successfully changed", Toast.LENGTH_LONG).show();
                                                                            progressBar.setVisibility(View.GONE);
                                                                        }else{
                                                                            Log.w(IMGUPDATE,"Cannot update img to db: "+task.getException());
                                                                            Toast.makeText(BusinessShopProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                            progressBar.setVisibility(View.GONE);
                                                                        }
                                                                    }

                                                                });
                                                            }else{
                                                                Log.w(IMGUPDATE,"Cannot delete past img: "+task.getException());
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){
                                                                Log.d(IMGUPDATE,"Update img to db "+uri);
                                                                Toast.makeText(BusinessShopProfile.this, "Successfully changed", Toast.LENGTH_LONG).show();
                                                                progressBar.setVisibility(View.GONE);
                                                            }else{
                                                                Log.w(IMGUPDATE,"Cannot update img to db: "+task.getException());
                                                                Toast.makeText(BusinessShopProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                progressBar.setVisibility(View.GONE);
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

                    Log.w(IMGUPDATE,"Cannot update img to storage "+ e);
                    Toast.makeText(BusinessShopProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        }

    }

    public void gotoBusinessPreview(View view) {
        startActivity(new Intent(this, BusinessShopPreview.class).putExtra("shopKey",shopKey));
    }

    public void gotoBusinessReviews(View view) {
        startActivity(new Intent(this, BusinessShopReviewPreview.class).putExtra("shopKey",shopKey));
    }

    public void goToDeactivateShop(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BusinessShopProfile.this);

        if (currentStatus.equals("0")) {
            builder.setMessage("Do you want to activate this shop?");
        } else {
            builder.setMessage("Do you want to deactivate this shop?");
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                            final Map<String, Object> updatedMap = new HashMap<String, Object>();
                            if (currentStatus.equals("0")) {
                                updatedMap.put("status", "1");
                            } else {
                                updatedMap.put("status", "0");
                            }


                            databaseReference.child(childSnapshot.getKey()).child("Shops").child(shopKey).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        if (currentStatus.equals("0")) {
                                            shopStatusTitle.setText(getResources().getString(R.string.active_status));
                                            shopStatusTitle.setTextColor(getResources().getColor(R.color.dark_ash));
                                            deactivateTitle.setText(getResources().getString(R.string.deactive_shop));
                                            Toast.makeText(BusinessShopProfile.this, "Activated your shop", Toast.LENGTH_LONG).show();
                                            currentStatus = "1";
                                        } else {
                                            shopStatusTitle.setText(getResources().getString(R.string.deactive_status));
                                            shopStatusTitle.setTextColor(getResources().getColor(R.color.button_end));
                                            deactivateTitle.setText(getResources().getString(R.string.active_shop));
                                            Toast.makeText(BusinessShopProfile.this, "Deactivated your shop", Toast.LENGTH_LONG).show();
                                            currentStatus = "0";
                                        }


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

        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void goToChat(View view) {
        startActivity(new Intent(BusinessShopProfile.this, AllChats.class).putExtra("shopKey",shopKey).putExtra("shopName",shopName));

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
        databaseReference.orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object>  hm = new HashMap<String, Object>();
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
