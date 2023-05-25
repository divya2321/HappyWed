package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.telephony.PhoneNumberUtils;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;


public class SignUp extends AppCompatActivity {

    private SignInButton googleContinue;
    private GoogleSignInClient googleSignInClient;

    private static int GOOGLE_SIGN_IN = 1;
    private static int FACEBOOK_SIGN_IN ;

    private static String GOOGLE_TAG = "SignInWithGoogle";
    private static String FACEBOOK_TAG = "SignInWithFacebook";
    private static String MOBILE_TAG = "SignInWithMobile";

    private static String signupOption;

    private FirebaseAuth fbAuth;
    private DatabaseReference databaseReference;

    LoginButton facebookButton;
    private CallbackManager callbackManager;

    EditText mobileNumberEditText;

    private Dialog dialog;
    private Button resendBtnDialog;
    private Pinview pinviewDialog;
    private TextView sendCodeInsDialog;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private View signupProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        databaseReference = HappyWedDB.getDBConnection();

        signupOption = getIntent().getStringExtra("signupOption");

        mobileNumberEditText = findViewById(R.id.mobileNo);

        googleContinue = findViewById(R.id.googleContinue);
        facebookButton = findViewById(R.id.signUpFacebook);
        signupProgressBar = findViewById(R.id.signupProgressBar);

        signupProgressBar.setVisibility(View.GONE);

        fbAuth = HappyWedDB.getFirebaseAuth();

        TextView textView = (TextView) googleContinue.getChildAt(0);
        textView.setText(getResources().getString(R.string.sign_up_google));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    signupProgressBar.setVisibility(View.VISIBLE);
                    signInGoogle();
                }else{

                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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
        });


        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    signupProgressBar.setVisibility(View.VISIBLE);
                    signInFacebook();
                }else{

                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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
        });

        dialog= new Dialog(SignUp.this);
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

                signupProgressBar.setVisibility(View.VISIBLE);
                verifyVerificationCode(pinview.getValue());
            }
        });

    }


    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void signInFacebook() {
        FACEBOOK_SIGN_IN = facebookButton.getRequestCode();
        callbackManager = CallbackManager.Factory.create();
        facebookButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(FACEBOOK_TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(FACEBOOK_TAG, "facebook:onCancel");
                signupProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(FACEBOOK_TAG, "facebook:onError", error);
                signupProgressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(GOOGLE_TAG, "Google sign in failed", e);
                signupProgressBar.setVisibility(View.GONE);
            }
        }else if (requestCode == FACEBOOK_SIGN_IN){
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(GOOGLE_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(GOOGLE_TAG, "signInWithCredential:success");
                            FirebaseUser user = fbAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUp.this, "There's something wrong with your account!", Toast.LENGTH_SHORT).show();
                            signupProgressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(FACEBOOK_TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(FACEBOOK_TAG, "signInWithCredential:success");
                            FirebaseUser user = fbAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(FACEBOOK_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUp.this, "There's something wrong with your account!", Toast.LENGTH_SHORT).show();
                            signupProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void updateUI(final FirebaseUser user){
        Toast.makeText(SignUp.this, "Successfully signed-in!!", Toast.LENGTH_SHORT).show();
        if(signupOption.equals("user")){

            databaseReference.child("users").orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            startActivity(new Intent(getApplicationContext(), UserDetails.class));
                            signupProgressBar.setVisibility(View.GONE);
                            SignUp.this.finish();
                        } else {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                            builder.setTitle("Do you want to restore the database");
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("UserDetails", "dataSnapshot not exists");
                                    Toast.makeText(SignUp.this, "Yes", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                    signupProgressBar.setVisibility(View.GONE);
                                    SignUp.this.finish();
                                }
                            });

                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                                        databaseReference.child("users").child(childSnapshot.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                                Log.d("dbNode", "success");
                                            }
                                        });
                                        HappyWed.resetAllDatabase(SignUp.this);
                                        startActivity(new Intent(getApplicationContext(), UserDetails.class));
                                        signupProgressBar.setVisibility(View.GONE);
                                        SignUp.this.finish();

                                    }
                                }

                            });
                            builder.show();
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{


            databaseReference.child("businesses").orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        startActivity(new Intent(getApplicationContext(), BusinessDetails.class));
                        signupProgressBar.setVisibility(View.GONE);
                    }else{
                        startActivity(new Intent(getApplicationContext(), BusinessOwnerHome.class));
                        signupProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    private String newMobileNo;
    public void sendCodeToNo(View view) {

        if(isNetworkAvailable()) {

            newMobileNo = mobileNumberEditText.getText().toString().trim();

            if (isValidatePhoneNumber(newMobileNo)) {

                dialog.show();
                resendBtnDialog.setEnabled(false);
                resendBtnDialog.setTextColor(getResources().getColor(R.color.dark_ash));
                sendVerifycationCode(newMobileNo);
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
            Log.d(MOBILE_TAG, "onVerificationCompleted:" + credential);

            String code = credential.getSmsCode();
            if (code != null) {
                pinviewDialog.setValue(code);
            }

        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w(MOBILE_TAG, "onVerificationFailed", e);
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

            Log.d(MOBILE_TAG, "onCodeSent:" + verificationId);

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
                signupProgressBar.setVisibility(View.GONE);
                signInWithPhoneAuthCredential(credential);
            }else{
                signupProgressBar.setVisibility(View.GONE);
                Log.d(MOBILE_TAG,"User entered wrong pin code ");
                Toast.makeText(SignUp.this, "Wrong code. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
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


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(MOBILE_TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                        } else {
                            Log.w(MOBILE_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUp.this, "Problem in verify code!!", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }


    private boolean isValidatePhoneNumber(String number){

        if (!TextUtils.isEmpty(number)) {

            if(number.matches("[0-9]+")  && number.length() == 9) {

                return true;
            }else{
                mobileNumberEditText.setError("Please enter a valid phone number");
                mobileNumberEditText.requestFocus();
                return false;
            }

        }else{
            return true;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void goToTermsConditions(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        startActivity(i);
    }
}
