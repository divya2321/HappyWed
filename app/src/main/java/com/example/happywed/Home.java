package com.example.happywed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity{

    private DrawerLayout drawerNav ;
    private NavigationView navView;
    private ImageView openDrawerBtn;
    private TextView txtDay, txtHour, txtMinute, txtSecond,coupleNameTxt;
    private RelativeLayout weddingDateLayout;
    private LinearLayout editProfile,weddigDayWish;
    private Handler handler;
    private Runnable runnable;
    private FirebaseAuth firebaseAuth;
    private UserInfo profile;
    private DatabaseReference databaseReference;
    private GoogleSignInClient mGoogleSignInClient;
    private Date weddingDate = null;
    private CircleImageView profilePic;
    private TextView greetingTxt, userName;
    private volatile boolean isRunning = true;

    Cursor userSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        openDrawerBtn = (ImageView)findViewById(R.id.openDrawerBtn);
        drawerNav = (DrawerLayout) findViewById(R.id.drawerNav);
        navView = (NavigationView) findViewById(R.id.userNavigationView);
        txtDay = (TextView) findViewById(R.id.txtDay);
        txtHour = (TextView) findViewById(R.id.txtHour);
        txtMinute = (TextView) findViewById(R.id.txtMinute);
        txtSecond = (TextView) findViewById(R.id.txtSecond);
        coupleNameTxt = (TextView) findViewById(R.id.coupleName);
        weddingDateLayout = (RelativeLayout) findViewById(R.id.weddingDateLayout);
        weddigDayWish = (LinearLayout) findViewById(R.id.weddigDayWish);

        weddigDayWish.setVisibility(View.GONE);

        databaseReference = HappyWedDB.getDBConnection();


        View navHeader =  navView.getHeaderView(0);
        profilePic =  (CircleImageView)navHeader.findViewById(R.id.profilePic);
        greetingTxt =  (TextView)navHeader.findViewById(R.id.greetingTxt);
        userName = (TextView) navHeader.findViewById(R.id.userName);
        editProfile = (LinearLayout) navHeader.findViewById(R.id.editProfile);


        firebaseAuth = HappyWedDB.getFirebaseAuth();
        profile = firebaseAuth.getCurrentUser().getProviderData().get(0);
        userName.setText(profile.getDisplayName());
        profilePic.setImageURI(profile.getPhotoUrl());



        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserDetailsEdit.class));
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserDetailsEdit.class));
            }
        });



        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting = null;
        if(hour>= 12 && hour < 17){
            greeting = "Good Afternoon!";
        } else if(hour >= 17 && hour < 21){
            greeting = "Good Evening!";
        } else if(hour >= 21 && hour < 24){
            greeting = "Good Night!";
        } else {
            greeting = "Good Morning!";
        }

       greetingTxt.setText(greeting);

        userSearch = HappyWed.search(Home.this, "SELECT userName,partnerName,weddingDate FROM user");

        if (userSearch.moveToNext()){
            String userName = userSearch.getString(0);
            String partnerName = userSearch.getString(1);
            String wed = userSearch.getString(2);

           userName =  userName.split(" ")[0];

            if(!partnerName.isEmpty()){
                coupleNameTxt.setText(userName.trim()+"+"+partnerName.trim());
            }else{
                coupleNameTxt.setText(userName.trim());
            }

            if(!wed.equals("noDate")){
                weddingDateLayout.setVisibility(View.VISIBLE);
                weddigDayWish.setVisibility(View.GONE);
                try {
                    weddingDate = new SimpleDateFormat("yyyy-MM-dd").parse(wed);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                isRunning = true;
                countDownStart();
            }else{
                noWedDate();
            }


        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        openDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerNav.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerNav.openDrawer(Gravity.LEFT);
                    }
                }, 1000);
            }
        });


        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.services:

                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);
                        startActivity(new Intent(getApplicationContext(), Service.class));
                        break;
                    case R.id.checklist:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);
                        startActivity(new Intent(getApplicationContext(), Checklist.class));
                        break;
                    case R.id.budget:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);
                        startActivity(new Intent(getApplicationContext(), Budget.class));
                        break;
                    case R.id.guest:
                        drawerNav.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(getApplicationContext(), Guest.class));
                        break;
                    case R.id.favourited:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);

                        break;
                    case R.id.custom:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);

                        startActivity(new Intent(getApplicationContext(), Custom.class));

                        break;
                    case R.id.login_as_business:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);
                        Log.d("abc","a");
                        databaseReference.child("businesses").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {

                                    startActivity(new Intent(getApplicationContext(), BusinessDetails.class));
                                    finishAffinity();
                                }else{
                                    if (!profile.getPhoneNumber().isEmpty()) {

                                        startActivity(new Intent(getApplicationContext(), BusinessOwnerHome.class));
                                        finishAffinity();
                                    }else {

                                        startActivity(new Intent(getApplicationContext(), BusinessDetails.class));
                                        finishAffinity();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;
                    case R.id.darkTheme:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);

                        break;
                    case R.id.profile:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);


                        startActivity(new Intent(getApplicationContext(), UserDetailsEdit.class));

                        break;
                    case R.id.terms_conditions:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);


                        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        startActivity(i1);

                        break;
                    case R.id.help_center:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);


                        Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        startActivity(i2);

                        break;
                    case R.id.about_us:
                        drawerNav.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerNav.closeDrawer(Gravity.LEFT);
                            }
                        }, 1000);


                        Intent i3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        startActivity(i3);

                        break;
                    case R.id.logout:

                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setMessage("Do you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                drawerNav.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        drawerNav.closeDrawer(Gravity.LEFT);
                                    }
                                }, 1000);

                                firebaseAuth.signOut();
                                mGoogleSignInClient.signOut();
                                if (LoginManager.getInstance() != null) {
                                    LoginManager.getInstance().logOut();
                                }
                                Home.this.finish();
                                startActivity(new Intent(getApplicationContext(), SignInOption.class));
                            }
                        });
                        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;


                }
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth = HappyWedDB.getFirebaseAuth();
//        profile = firebaseAuth.getCurrentUser().getProviderData().get(0);
//        userName.setText(profile.getDisplayName());
//        profilePic.setImageURI(profile.getPhotoUrl());

    }
Thread thread;
    public void countDownStart() {
        handler = new Handler();
        thread = new Thread(runnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    handler.postDelayed(this, 1000);
                    try {

                        Date currentDate = new Date();
                        if (!currentDate.after(weddingDate)) {
                            long diff = weddingDate.getTime()
                                    - currentDate.getTime();
                            long days = diff / (24 * 60 * 60 * 1000);
                            diff -= days * (24 * 60 * 60 * 1000);
                            long hours = diff / (60 * 60 * 1000);
                            diff -= hours * (60 * 60 * 1000);
                            long minutes = diff / (60 * 1000);
                            diff -= minutes * (60 * 1000);
                            long seconds = diff / 1000;
                            txtDay.setText("" + String.format("%02d", days));
                            txtHour.setText("" + String.format("%02d", hours));
                            txtMinute.setText(""
                                    + String.format("%02d", minutes));
                            txtSecond.setText(""
                                    + String.format("%02d", seconds));
                        } else {
                            weddingDate();
                            isRunning = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        thread.start();
        handler.postDelayed(runnable, 1 * 1000);
    }


    private void noWedDate(){
        isRunning = false;
        weddingDateLayout.setVisibility(View.INVISIBLE);
        weddigDayWish.setVisibility(View.GONE);
    }

    private void weddingDate(){
        weddingDateLayout.setVisibility(View.INVISIBLE);
        weddigDayWish.setVisibility(View.VISIBLE);
    }

    public void goToService(View view) {
        startActivity(new Intent(getApplicationContext(), Service.class));
    }

    public void goToCheckList(View view) {
        startActivity(new Intent(getApplicationContext(), Checklist.class));
    }

    public void goToBudgetList(View view) {
        startActivity(new Intent(getApplicationContext(), Budget.class));
    }

    public void gotoGuest(View view) {
        startActivity(new Intent(getApplicationContext(), Guest.class));
    }

    public void gotoFavourite(View view) {
        startActivity(new Intent(getApplicationContext(), WishList.class));
    }

    public void gotoCustom(View view) {
        startActivity(new Intent(getApplicationContext(), Custom.class));
    }

    public void gotoChat(View view){
        startActivity(new Intent(getApplicationContext(), AllChatsForUser.class));
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
        databaseReference.child("users").orderByChild("uid").equalTo(profile.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()){
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("status", status);

                    databaseReference.child("users").child(snaps.getKey()).updateChildren(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
