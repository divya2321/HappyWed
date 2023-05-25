package com.example.happywed;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignInOption extends AppCompatActivity {

    private Button signupAsUser,signupAsBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_option);

        signupAsUser = findViewById(R.id.optionSignUpAsUser);
        signupAsBusiness = findViewById(R.id.optionSignUpAsBusiness);

        signupAsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {

                    startActivity(new Intent(getApplicationContext(), SignUp.class).putExtra("signupOption","user"));
                    SignInOption.this.finish();
                }else{

                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignInOption.this);
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

        signupAsBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable()) {

                    startActivity(new Intent(getApplicationContext(), SignUp.class).putExtra("signupOption","business"));
                    SignInOption.this.finish();
                }else{

                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignInOption.this);
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
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
