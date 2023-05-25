package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class UserDetailsEdit extends AppCompatActivity {

    private Toolbar toolBar;
    private EditText userNameEditText, partnerNameEditText,mobileNoEditText, estimatedBudgetText;
    private CircleImageView profilePicImageView;
    private DatePicker weddingDatePicker;
    private CheckedTextView checkedTextView;
    private Button editBtn;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private  UserInfo profile;
    private Uri selectImgUri;
    private Timestamp timestamp;
    private Dialog dialog;
    private Button resendBtnDialog;
    private Pinview pinviewDialog;
    private TextView sendCodeInsDialog;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private View userProgressBar;

    private final static int REQUEST_GALLERY_PERMISSION_CODE = 1;
    private final static int REQUEST_CAMERA_PERMISSION_CODE = 2;
    private final static int GALLERY_ACCESS_CODE = 3;
    private final static int CAMERA_ACCESS_CODE = 4;

    private static final String IMGUPDATE = "ImgUpdate";
    private static final String DETAILSUPDATE = "DetailsUpdate";
    private static final String PHONETAG = "EnterPhoneNumber";
    private static final String PHONENUMBERUPDATE = "PhoneNumberUpdate";

    Cursor userSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_edit);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


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



        userNameEditText = findViewById(R.id.userName);
        partnerNameEditText = findViewById(R.id.partnerName);
        mobileNoEditText = findViewById(R.id.mobileNo);
        estimatedBudgetText = findViewById(R.id.estBudget);
        weddingDatePicker = findViewById(R.id.weddingDate);
        profilePicImageView = findViewById(R.id.profilePic);
        checkedTextView = findViewById(R.id.noDateChecked);
        editBtn = findViewById(R.id.editBtn);
        userProgressBar = findViewById(R.id.userProgressBar);

        userProgressBar.setVisibility(View.GONE);


        weddingDatePicker.setMinDate(System.currentTimeMillis() - 1000);


        databaseReference = HappyWedDB.getDBConnection().child("users");
        storageReference = HappyWedDB.getStorageReference().child("users").child("profile pic");

        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        profile = currentUser.getProviderData().get(0);

        profilePicImageView.setImageURI(profile.getPhotoUrl());

        databaseReference.orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(final DataSnapshot childDataSnapshot: dataSnapshot.getChildren()) {

                    HashMap<String, String> values = (HashMap<String, String>) childDataSnapshot.getValue();

                        String num = values.get("mobileNo");
                        if(num != null) {
                            if (num.length() > 10) {
                           mobileNoEditText.setText(num.substring(3).trim());
                         }
                     }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userSearch = HappyWed.search(UserDetailsEdit.this, "SELECT * FROM user");

        if (userSearch.moveToNext()){
            String userName = userSearch.getString(1);
            String partnerName = userSearch.getString(2);
            String wed = userSearch.getString(3);
            String budget = userSearch.getString(4);

            userNameEditText.setText(userName);

            partnerNameEditText.setText(partnerName);

            if(!wed.equals("noDate")){
                weddingDatePicker.setEnabled(true);
            String[] wedDate = wed.split("-");

                int day = Integer.parseInt(wedDate[2].trim());
                int month = Integer.parseInt(wedDate[1].trim())-1;
                int year = Integer.parseInt(wedDate[0].trim());
                weddingDatePicker.updateDate(year,month,day);

            }else{
                weddingDatePicker.setEnabled(false);
            checkedTextView.setChecked(true);
            }

            estimatedBudgetText.setText(budget);


        }

        profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseOptions();
            }
        });


        dialog= new Dialog(UserDetailsEdit.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.verify_mobile_no);

        sendCodeInsDialog = (TextView) dialog.findViewById(R.id.sendCodeIns) ;
        pinviewDialog = (Pinview) dialog.findViewById(R.id.pinCode);
        resendBtnDialog = (Button) dialog.findViewById(R.id.resendBtn);
        Button cancel = (Button) dialog.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCodeCount.cancel();
                dialog.dismiss();
            }
        });


        resendBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!newMobileNo.isEmpty()) {
                    resendBtnDialog.setEnabled(false);
                    resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                    resendVerificationCode(newMobileNo, mResendToken);

                    sendCodeCount = new CountDownTimer(20000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            sendCodeInsDialog.setText(getResources().getString(R.string.enter_verification_code_inst) + " " + millisUntilFinished / 1000 + " s");
                        }

                        public void onFinish() {
                            sendCodeInsDialog.setText(getResources().getString(R.string.enter_verification_code_inst));
                            resendBtnDialog.setEnabled(true);
                            resendBtnDialog.setTextColor(getResources().getColor(R.color.button_end));
                        }

                    };
                    sendCodeCount.start();
                }
            }
        });

        pinviewDialog.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {

                userProgressBar.setVisibility(View.VISIBLE);
                verifyVerificationCode(pinview.getValue());
            }
        });
    }

    public void checkNoDate(View view) {

        if(checkedTextView.isChecked()){
            checkedTextView.setChecked(false);
            weddingDatePicker.setEnabled(true);
            weddingDatePicker.setFocusable(true);
        }else{
            checkedTextView.setChecked(true);
            weddingDatePicker.setEnabled(false);
            weddingDatePicker.setFocusable(false);
        }
    }


    public void accessGallery(){
        String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED){

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ACCESS_CODE);

        }else {

            ActivityCompat.requestPermissions(this,permissions, REQUEST_GALLERY_PERMISSION_CODE);

        }

    }


    private Uri captureImageUri=null;

    public void accessCamera(){

        String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),permissions[1])== PackageManager.PERMISSION_GRANTED){


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                captureImageUri = Uri.fromFile(createImageFile());

            } catch (IOException e) {
                e.printStackTrace();
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
            startActivityForResult(intent, CAMERA_ACCESS_CODE);

        }else {
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CAMERA_PERMISSION_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION_CODE:
                if ((grantResults.length==2) && (grantResults[0]==PackageManager.PERMISSION_GRANTED) && (grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                    accessCamera();
                }
                break;

            case REQUEST_GALLERY_PERMISSION_CODE:
                if ((grantResults.length==1) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    accessGallery();
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==GALLERY_ACCESS_CODE || requestCode==CAMERA_ACCESS_CODE) && (resultCode==RESULT_OK)){
            Uri imageUri;
            if(requestCode==GALLERY_ACCESS_CODE){
                imageUri= data.getData();
            }else{
                imageUri = captureImageUri;
            }
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selectImgUri = result.getUri();
                profilePicImageView.setImageURI(selectImgUri);
                saveImg();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void chooseOptions(){
        final CharSequence options[] = {"Take Photo","Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Option");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Take Photo")){
                    accessCamera();
                } else if (options[i].equals("Choose from Gallery")){
                    accessGallery();
                }
            }
        });

        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


    private void saveImg(){

        if(selectImgUri != null){

            userProgressBar.setVisibility(View.VISIBLE);


            timestamp = new Timestamp(System.currentTimeMillis());
            storageReference.child("img_"+timestamp).putFile(selectImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            Log.d(IMGUPDATE,"Update img to storage: "+uri);
                            UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(selectImgUri)
                                    .build();

                            currentUser.updateProfile(changeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d(IMGUPDATE,"Update img to auth");

                                    databaseReference.orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for(final DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){

                                                String pastUrl =  ((HashMap<String, String>)childDataSnapshot.getValue()).get("profilePicUrl");
                                                Log.d(IMGUPDATE,"past url: "+pastUrl);
                                                StorageReference storage=  FirebaseStorage.getInstance().getReferenceFromUrl(pastUrl);
                                                storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(IMGUPDATE,"Delete past img");

                                                        final Map<String,Object> updatedMap = new HashMap<String, Object>();
                                                        updatedMap.put("profilePicUrl",uri.toString());

                                                        databaseReference.child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    profilePicImageView.setImageURI(selectImgUri);
                                                                    Log.d(IMGUPDATE,"Update img to db");
                                                                    Toast.makeText(UserDetailsEdit.this, "Successfully changed", Toast.LENGTH_LONG).show();
                                                                    userProgressBar.setVisibility(View.GONE);
                                                                }else{
                                                                    Log.w(IMGUPDATE,"Cannot update img to db: "+task.getException());
                                                                    Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                    userProgressBar.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(IMGUPDATE,"Cannot delete past img: "+e);
                                                        Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                        userProgressBar.setVisibility(View.GONE);
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.w(IMGUPDATE,"Error on checking "+ databaseError.toException());
                                            Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                            userProgressBar.setVisibility(View.GONE);
                                        }
                                    });


                                }


                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(IMGUPDATE,"Cannot update img to auth: "+e);
                                    Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    userProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(IMGUPDATE,"Cannot update img to storage "+ e);
                    Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    userProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    Cursor activeSearch;

    private String newUserName, newPartnerName, newMobileNo,newEsBudget;
    private int newDay, newMonth, newYear;

     String oldMobileNo;

    public void editUserDetails(View view) {

        if(isNetworkAvailable()) {

            newUserName = userNameEditText.getText().toString().trim();
            newPartnerName = partnerNameEditText.getText().toString().trim();
            newMobileNo = mobileNoEditText.getText().toString().trim();
            newDay = weddingDatePicker.getDayOfMonth();
            newMonth = weddingDatePicker.getMonth() + 1;
            newYear = weddingDatePicker.getYear();
            newEsBudget = estimatedBudgetText.getText().toString().trim();

            if (!newUserName.isEmpty()) {

                if (isValidatePhoneNumber(newMobileNo)) {


                    String weddingDate = newYear + "-" + newMonth + "-" + newDay;

                    if (checkedTextView.isChecked()) {
                        weddingDate = "noDate";
                    }
                    if (newEsBudget.isEmpty()) {
                        newEsBudget = "0.0";
                    }
                    HappyWed.iud(UserDetailsEdit.this, "UPDATE user SET userName= '" + newUserName + "', partnerName = '" + newPartnerName + "', weddingDate = '" + weddingDate + "', estBudget = '" + newEsBudget + "' ");


                    final String newWedDate = newYear + "-" + newMonth + "-" + newDay;
                    final String oldUserName = profile.getDisplayName();

                    databaseReference.orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                                HashMap<String, String> values = (HashMap<String, String>) childDataSnapshot.getValue();
                                String oldPartnerName = values.get("partnerName");
                                String oldWedDate = values.get("weddingDate");

                                String num = values.get("mobileNo");
                                if (num != null) {
                                    if (num.length() > 10) {
                                        oldMobileNo = num.substring(3).trim();
                                    }
                                }

//                        if((!oldUserName.equals(newUserName)) || (!oldPartnerName.equals(newPartnerName)) || (!oldMobileNo.equals(newMobileNo)) || (isDefaultCheck!= checkedTextView.isChecked())){
                                userProgressBar.setVisibility(View.VISIBLE);

                                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUserName)
                                        .build();

                                currentUser.updateProfile(changeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(DETAILSUPDATE, "Update name to auth");

                                        final Map<String, Object> updatedMap = new HashMap<String, Object>();
                                        updatedMap.put("userName", newUserName);

                                        databaseReference.child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(DETAILSUPDATE, "Update name to db");
                                                    Log.d(DETAILSUPDATE, "New NUm  " + newMobileNo);

                                                    Log.d(DETAILSUPDATE, "Old NUm  " + oldMobileNo);
                                                    if ((!newMobileNo.isEmpty()) && (!newMobileNo.equals(oldMobileNo))) {
                                                        userProgressBar.setVisibility(View.GONE);
                                                        dialog.show();
                                                        resendBtnDialog.setEnabled(false);
                                                        resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                                                        sendVerifycationCode(newMobileNo);
                                                    } else {
                                                        startActivity(new Intent(getApplicationContext(), Home.class));
                                                        Toast.makeText(UserDetailsEdit.this, "Successfully changed", Toast.LENGTH_LONG).show();
                                                        userProgressBar.setVisibility(View.GONE);
                                                    }

                                                } else {
                                                    Log.w(DETAILSUPDATE, "Cannot update name to db: " + task.getException());
                                                    Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                    userProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(DETAILSUPDATE, "Cannot update name to auth: " + e);
                                        Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                        userProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(DETAILSUPDATE, "Error on checking " + databaseError.toException());
                            Toast.makeText(UserDetailsEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            userProgressBar.setVisibility(View.GONE);
                        }
                    });

                }
            } else {
                userNameEditText.setError("Your name is required!");
                userNameEditText.setFocusable(true);
            }

        }else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Please check your internet connection and try again");
            builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            builder.show();
        }

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private boolean isValidatePhoneNumber(String number){

        if (!TextUtils.isEmpty(number)) {

            if(number.matches("[0-9]+")  && number.length() == 9) {

                return true;
            }else{
                mobileNoEditText.setError("Please enter a valid phone number");
                mobileNoEditText.requestFocus();
                return false;
            }

        }else{
            return true;
        }
    }

    private CountDownTimer sendCodeCount;

    private void sendVerifycationCode(String number){

        sendCodeCount = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                sendCodeInsDialog.setText(getResources().getString(R.string.enter_verification_code_inst) + " " + millisUntilFinished / 1000 + " s");
            }

            public void onFinish() {
                sendCodeInsDialog.setText(getResources().getString(R.string.enter_verification_code_inst));
                resendBtnDialog.setEnabled(true);
                resendBtnDialog.setTextColor(getResources().getColor(R.color.button_end));
            }

        };
        sendCodeCount.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+94 "+number,        // Phone number to verify
                20,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d(PHONETAG, "onVerificationCompleted:" + credential);

            String code = credential.getSmsCode();
            if (code != null) {
                pinviewDialog.setValue(code);
            }

        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w(PHONETAG, "onVerificationFailed", e);
            Toast.makeText(getApplicationContext(),"An error has occured!",Toast.LENGTH_LONG).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }


        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);

            Log.d(PHONETAG, "onCodeSent:" + verificationId);

            Toast.makeText(getApplicationContext(),"Code sent",Toast.LENGTH_LONG).show();

            mVerificationId = verificationId;
            mResendToken = token;

        }
    };

    public void verifyVerificationCode(String code) {

        if(mVerificationId!=null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            if(credential != null){
                mVerificationId=null;
                userProgressBar.setVisibility(View.GONE);
                saveNumber(credential);
            }else{
                userProgressBar.setVisibility(View.GONE);
                Log.d(PHONENUMBERUPDATE,"User entered wrong pin code ");
                Toast.makeText(UserDetailsEdit.this, "Wrong code. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void saveNumber(final PhoneAuthCredential phoneAuthCredential){


        currentUser.updatePhoneNumber(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(PHONENUMBERUPDATE,"Update phone number to phone auth");

                databaseReference.orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                            final Map<String,Object> updatedMap = new HashMap<String, Object>();
                            updatedMap.put("mobileNo","+94 "+newMobileNo);

                            databaseReference.child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        userProgressBar.setVisibility(View.GONE);
                                        Log.d(PHONENUMBERUPDATE,"Update phone number to db");
                                        Toast.makeText(UserDetailsEdit.this, "Successfully changed!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }else {
                                        userProgressBar.setVisibility(View.GONE);
                                        Log.w(PHONENUMBERUPDATE,"Cannot update phone number to db: "+ task.getException());
                                        Toast.makeText(UserDetailsEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        userProgressBar.setVisibility(View.GONE);
                        Log.d(PHONENUMBERUPDATE,"Error on checking "+ databaseError.toException());
                        Toast.makeText(UserDetailsEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                });


            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userProgressBar.setVisibility(View.GONE);
                Log.w(PHONENUMBERUPDATE,"Cannot update phone number to phone auth: "+e);


                if(e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException){
                    dialog.dismiss();
                    mobileNoEditText.setError("This number already has an account");
                    mobileNoEditText.requestFocus();
                }else{
                    Toast.makeText(UserDetailsEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+94 "+phoneNumber,        // Phone number to verify
                20,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

}
