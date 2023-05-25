package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BusinessProfileEdit extends AppCompatActivity {


    private Toolbar toolBar;
    private EditText businessOwnerName,businessOwnerNo;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo profile;

    private Dialog dialog;
    private Button resendBtnDialog;
    private Pinview pinviewDialog;
    private TextView sendCodeInsDialog;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private View userProgressBar;

    private static final String DETAILSUPDATE = "DetailsUpdate";
    private static final String PHONETAG = "EnterPhoneNumber";
    private static final String PHONENUMBERUPDATE = "PhoneNumberUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_profile_edit);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        businessOwnerName = (EditText) findViewById(R.id.businessOwnerName);
        businessOwnerNo = (EditText) findViewById(R.id.businessOwnerNo);
        userProgressBar = findViewById(R.id.userProgressBar);

        userProgressBar.setVisibility(View.GONE);

        if (getSupportActionBar() != null){
            setSupportActionBar(toolBar);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        databaseReference = HappyWedDB.getDBConnection();
        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        profile = currentUser.getProviderData().get(0);

        businessOwnerName.setText(profile.getDisplayName());
        businessOwnerNo.setText(profile.getPhoneNumber().substring(3).trim());



        dialog= new Dialog(BusinessProfileEdit.this);
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
                if(!number.isEmpty()) {
                    resendBtnDialog.setEnabled(false);
                    resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                    resendVerificationCode(number, mResendToken);

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


    String name,number,oldMobileNo;

    public void editProfile(View view) {

        name = businessOwnerName.getText().toString().trim();
        number = businessOwnerNo.getText().toString().trim();

        if(isNetworkAvailable()) {

            if (!name.isEmpty()) {

                if (isValidatePhoneNumber(number)) {

                    databaseReference.child("businesses").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {


                                HashMap<String, String> values = (HashMap<String, String>) childDataSnapshot.getValue();

                                String num = values.get("mobileNo");
                                if (num != null) {
                                    if (num.length() > 10) {
                                        oldMobileNo = num.substring(3).trim();
                                    }
                                }

                                userProgressBar.setVisibility(View.VISIBLE);

                                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                currentUser.updateProfile(changeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(DETAILSUPDATE, "Update name to auth");

                                        final Map<String, Object> updatedMap = new HashMap<String, Object>();
                                        updatedMap.put("ownerName", name);

                                        databaseReference.child("businesses").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(DETAILSUPDATE, "Update name to db");
                                                    Log.d(DETAILSUPDATE, "New NUm  " + number);

                                                    Log.d(DETAILSUPDATE, "Old NUm  " + oldMobileNo);
                                                    if (!number.equals(oldMobileNo)) {
                                                        userProgressBar.setVisibility(View.GONE);
                                                        dialog.show();
                                                        resendBtnDialog.setEnabled(false);
                                                        resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                                                        sendVerifycationCode(number);
                                                    }

                                                        databaseReference.child("users").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (!dataSnapshot.exists()) {
                                                                    Toast.makeText(BusinessProfileEdit.this, "Successfully changed", Toast.LENGTH_LONG).show();
                                                                    userProgressBar.setVisibility(View.GONE);
                                                                }else{
                                                                    changeUserName(name);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                } else {
                                                    Log.w(DETAILSUPDATE, "Cannot update name to db: " + task.getException());
                                                    Toast.makeText(BusinessProfileEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                    userProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(DETAILSUPDATE, "Cannot update name to auth: " + e);
                                        Toast.makeText(BusinessProfileEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                        userProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(DETAILSUPDATE, "Error on checking " + databaseError.toException());
                            Toast.makeText(BusinessProfileEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            userProgressBar.setVisibility(View.GONE);
                        }
                    });



                }
            } else {

                businessOwnerName.setError("Name is required!");
                businessOwnerName.requestFocus();
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
                businessOwnerNo.setError("Please enter a valid phone number");
                businessOwnerNo.requestFocus();
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
                Toast.makeText(BusinessProfileEdit.this, "Wrong code. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void saveNumber(final PhoneAuthCredential phoneAuthCredential){


        currentUser.updatePhoneNumber(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(PHONENUMBERUPDATE,"Update phone number to phone auth");

                databaseReference.child("businesses").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                            final Map<String,Object> updatedMap = new HashMap<String, Object>();
                            updatedMap.put("mobileNo","+94 "+number);

                            databaseReference.child("businesses").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        databaseReference.child("users").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.exists()) {
                                                    userProgressBar.setVisibility(View.GONE);
                                                    Log.d(PHONENUMBERUPDATE,"Update phone number to db");
                                                    Toast.makeText(BusinessProfileEdit.this, "Successfully changed!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }else{
                                                    changeUserNumber(number);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }else {
                                        userProgressBar.setVisibility(View.GONE);
                                        Log.w(PHONENUMBERUPDATE,"Cannot update phone number to db: "+ task.getException());
                                        Toast.makeText(BusinessProfileEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        userProgressBar.setVisibility(View.GONE);
                        Log.d(PHONENUMBERUPDATE,"Error on checking "+ databaseError.toException());
                        Toast.makeText(BusinessProfileEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
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
                    businessOwnerNo.setError("This number already has an account");
                    businessOwnerNo.requestFocus();
                }else{
                    Toast.makeText(BusinessProfileEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
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


    private void changeUserName(final String userName){

        HappyWed.iud(BusinessProfileEdit.this, "UPDATE user SET userName= '" + userName + "' ");

        databaseReference.child("users").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    final Map<String, Object> updatedMap = new HashMap<String, Object>();
                    updatedMap.put("userName", userName);

                    databaseReference.child("users").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(DETAILSUPDATE, "Update user name to db");
                                Toast.makeText(BusinessProfileEdit.this, "Successfully changed", Toast.LENGTH_LONG).show();
                                userProgressBar.setVisibility(View.GONE);
                            }else{
                                Log.w(DETAILSUPDATE, "Cannot update user name to db: " + task.getException());
                                Toast.makeText(BusinessProfileEdit.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                userProgressBar.setVisibility(View.GONE);
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

    private void changeUserNumber(final String tp){

        databaseReference.child("users").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                    final Map<String,Object> updatedMap = new HashMap<String, Object>();
                    updatedMap.put("mobileNo","+94 "+tp);

                    databaseReference.child("users").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                dialog.dismiss();
                                userProgressBar.setVisibility(View.GONE);
                                Log.d(PHONENUMBERUPDATE,"Update phone user number to db");
                                Toast.makeText(BusinessProfileEdit.this, "Successfully changed!", Toast.LENGTH_SHORT).show();
                            }else {
                                userProgressBar.setVisibility(View.GONE);
                                Log.w(PHONENUMBERUPDATE,"Cannot update phone user number to db: "+ task.getException());
                                Toast.makeText(BusinessProfileEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userProgressBar.setVisibility(View.GONE);
                Log.d(PHONENUMBERUPDATE,"Error on checking "+ databaseError.toException());
                Toast.makeText(BusinessProfileEdit.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
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
        databaseReference.child("businesses").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object>  hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    databaseReference.child("businesses").child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
