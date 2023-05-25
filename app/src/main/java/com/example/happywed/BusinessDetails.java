package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.happywed.DBModel.BusinessOwner;
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
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class BusinessDetails extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private UserInfo currentProfile;

    private Dialog dialog;
    private Button resendBtnDialog;
    private Pinview pinviewDialog;
    private TextView sendCodeInsDialog;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private CircleImageView profilePic;
    private Button businessOwnerVerify;
    private EditText businessOwnerName, businessOwnerNo;
    private View userProgressBar;

    String alreadyMobileNo = "";

    private static final String VERIFYACCOUNT = "VerifAccount";
    private static final String PHONETAG = "EnterPhoneNumber";
    private static final String PHONENUMBERUPDATE = "PhoneNumberUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_details);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        businessOwnerName = findViewById(R.id.businessOwnerName);
        businessOwnerNo = findViewById(R.id.businessOwnerNo);
        businessOwnerVerify = findViewById(R.id.ownerNoVerify);

        profilePic = findViewById(R.id.profilePic);
        userProgressBar = findViewById(R.id.userProgressBar);

        userProgressBar.setVisibility(View.GONE);

        databaseReference = HappyWedDB.getDBConnection();
//        storageReference = HappyWedDB.getStorageReference().child("businesses").child("profile pic");

        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        currentProfile= currentUser.getProviderData().get(0);

        businessOwnerName.setText(currentProfile.getDisplayName());
        if (currentProfile.getPhoneNumber() != null) {
            if (currentProfile.getPhoneNumber().length() > 10) {
                alreadyMobileNo = currentProfile.getPhoneNumber().substring(3);
                businessOwnerNo.setText(alreadyMobileNo);
            }
        }


        dialog = new Dialog(BusinessDetails.this);
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


    String name;
    String number;

    public void verifyAccount(View view) {

        name = businessOwnerName.getText().toString().trim();
        number = businessOwnerNo.getText().toString().trim();

        if (!name.isEmpty()) {

            if (isValidatePhoneNumber(number)) {
                userProgressBar.setVisibility(View.VISIBLE);
                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                currentUser.updateProfile(changeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(VERIFYACCOUNT, "Add details to auth");

                        final BusinessOwner user = new BusinessOwner()
                                .setUid(currentUser.getUid())
                                .setOwnerName(name)
                                .setStatus("offline")
                                .setProvider(currentUser.getIdToken(false).getResult().getSignInProvider());

                        databaseReference.child("businesses").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {
                                    databaseReference.child("businesses").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(VERIFYACCOUNT, "Add user details to db: " + currentUser);

                                                Log.d(VERIFYACCOUNT, alreadyMobileNo);
                                                Log.d(VERIFYACCOUNT, number);

                                                if (!alreadyMobileNo.equals(number)) {
                                                    userProgressBar.setVisibility(View.GONE);
                                                    dialog.show();
                                                    resendBtnDialog.setEnabled(false);
                                                    resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                                                    sendVerifycationCode(number);
                                                } else {
                                                    Toast.makeText(BusinessDetails.this, "Successfully saved!", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(BusinessDetails.this, BusinessOwnerHome.class));
                                                    userProgressBar.setVisibility(View.GONE);
                                                }


                                            } else {
                                                Log.w(VERIFYACCOUNT, "Cannot add user details to db: " + task.getException());
                                                Toast.makeText(BusinessDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                userProgressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }else{
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {


                                        final Map<String, Object> updatedMap = new HashMap<String, Object>();
                                        updatedMap.put("ownerName", name);
                                        updatedMap.put("provider", currentUser.getIdToken(false).getResult().getSignInProvider());

                                        databaseReference.child("businesses").child(childSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(VERIFYACCOUNT, "Update user details to db: " + currentUser);

                                                    Log.d(VERIFYACCOUNT, alreadyMobileNo);
                                                    Log.d(VERIFYACCOUNT, number);

                                                    if (!alreadyMobileNo.equals(number)) {
                                                        userProgressBar.setVisibility(View.GONE);
                                                        dialog.show();
                                                        resendBtnDialog.setEnabled(false);
                                                        resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                                                        sendVerifycationCode(number);
                                                    } else {
                                                        Toast.makeText(BusinessDetails.this, "Successfully saved!", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(BusinessDetails.this, BusinessOwnerHome.class));
                                                        userProgressBar.setVisibility(View.GONE);
                                                    }
                                                } else {
                                                    Log.w(VERIFYACCOUNT, "Cannot update user details to db: " + task.getException());
                                                    Toast.makeText(BusinessDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                    userProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });

                                }

                            }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        databaseReference.child("users").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    changeUserName(name);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(VERIFYACCOUNT, "Cannot add details to auth: " + e);
                        Toast.makeText(BusinessDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        userProgressBar.setVisibility(View.GONE);
                    }
                });
            }else{
                businessOwnerNo.setError("Number is reuired!");
                businessOwnerNo.requestFocus();
            }
            } else {
                businessOwnerName.setError("Name is reuired!");
                businessOwnerName.requestFocus();
            }

//                if (!alreadyMobileNo.equals(businessOwnerNo.getText().toString())){
//                    dialog.show();
//                }else {
//                    startActivity(new Intent(business_details.this, BusinessOwnerHome.class));
//                }
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
            return false;
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
                Toast.makeText(BusinessDetails.this, "Wrong code. Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveNumber(final PhoneAuthCredential phoneAuthCredential){


        currentUser.updatePhoneNumber(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(PHONENUMBERUPDATE,"Update phone number to phone auth");

                databaseReference.child("businesses").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                            final Map<String,Object> updatedMap = new HashMap<String, Object>();
                            updatedMap.put("mobileNo","+94 "+number);

                            databaseReference.child("businesses").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        userProgressBar.setVisibility(View.GONE);
                                        Log.d(PHONENUMBERUPDATE,"Update phone number to db");
                                        Toast.makeText(BusinessDetails.this, "Successfully changed!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        startActivity(new Intent(BusinessDetails.this, BusinessOwnerHome.class));
                                    }else {
                                        userProgressBar.setVisibility(View.GONE);
                                        Log.w(PHONENUMBERUPDATE,"Cannot update phone number to db: "+ task.getException());
                                        Toast.makeText(BusinessDetails.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            databaseReference.child("users").orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        changeUserNumber(number);
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
                        userProgressBar.setVisibility(View.GONE);
                        Log.d(PHONENUMBERUPDATE,"Error on checking "+ databaseError.toException());
                        Toast.makeText(BusinessDetails.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BusinessDetails.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
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

        HappyWed.iud(BusinessDetails.this, "UPDATE user SET userName= '" + userName + "' ");

        databaseReference.child("users").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    final Map<String, Object> updatedMap = new HashMap<String, Object>();
                    updatedMap.put("userName", userName);

                    databaseReference.child("users").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(VERIFYACCOUNT, "Update user name to db");
                            }else{
                                Log.w(VERIFYACCOUNT, "Cannot update user name to db: " + task.getException());
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

        databaseReference.child("users").orderByChild("uid").equalTo(currentProfile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){
                    final Map<String,Object> updatedMap = new HashMap<String, Object>();
                    updatedMap.put("mobileNo","+94 "+tp);

                    databaseReference.child("users").child(childDataSnapshot.getKey()).updateChildren(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Log.d(PHONENUMBERUPDATE,"Update phone user number to db");
                            }else {
                                Log.w(PHONENUMBERUPDATE,"Cannot update phone user number to db: "+ task.getException());
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