package com.example.happywed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.happywed.Adapters.AppIntroAdapter;
import com.example.happywed.DBCon.HappyWedDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AppIntro extends AppCompatActivity {

    ArrayList<Integer> imageList =new ArrayList<Integer>();
    ViewPager viewPager;

    LinearLayout dotLinear;
    TextView dots[], titleText, descriptionText;

    String titleTexts[];
    String descriptionTexts[];
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_intro);

        titleText = (TextView) findViewById(R.id.titleIntro);
        descriptionText = (TextView) findViewById(R.id.descriptionIntro);

        dotLinear = (LinearLayout) findViewById(R.id.introDot);

        viewPager = (ViewPager) findViewById(R.id.introView);
        imageList.add(R.drawable.couple);
        imageList.add(R.drawable.couple);
        imageList.add(R.drawable.couple);
        imageList.add(R.drawable.couple);
        imageList.add(R.drawable.couple);

        titleTexts = getResources().getStringArray(R.array.introTitles);
        descriptionTexts = getResources().getStringArray(R.array.introDescriptions);

        firebaseAuth = HappyWedDB.getFirebaseAuth();

        AppIntroAdapter appIntroAdapter = new AppIntroAdapter(imageList, this);

        viewPager.setAdapter(appIntroAdapter);
        viewPager.setOnPageChangeListener(viewChange);

        addDots(0);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {

    }

    public void addDots(int position){
        dots = new TextView[5];
        dotLinear.removeAllViews();

        for (int i=0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.ash));

            dotLinear.addView(dots[i]);
        }

        if (dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.black));
        }

    }

    ViewPager.OnPageChangeListener viewChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDots(position);

            titleText.setText(titleTexts[position]);
            descriptionText.setText(descriptionTexts[position]);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void gotoSignInOption(View view){

        startActivity(new Intent(this, SignInOption.class));
        AppIntro.this.finish();
    }

}
