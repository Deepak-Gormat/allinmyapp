package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.myapp.userEntery.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {


    private static int SPLASH_SCREEN_TIME_OUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent SendToMainActivity = new Intent(Splash_Screen.this, SignIn.class);
                startActivity(SendToMainActivity);
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

}