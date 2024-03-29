package com.example.bookingluu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.bookingluu.Customer.RestaurantListPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //this is to make the page continue for 5 seconds
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                fAuth=FirebaseAuth.getInstance();

                if(fAuth.getCurrentUser()!=null){
                    Intent intent=new Intent(SplashActivity.this, RestaurantListPage.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(SplashActivity.this, CustomerLoginPage.class);
                    startActivity(intent);
                    finish();
                }


            }
        },2000);

    }



}