package com.hackathon.shoppy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

public class NetworkActivity extends AppCompatActivity {
LottieAnimationView animationView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        animationView = findViewById(R.id.animationview);
        animationView.playAnimation();

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isNetworkConnected()){

                }
                else {
                    animationView.cancelAnimation();
                    Intent intent = new Intent(NetworkActivity.this,home.class);
                    startActivity(intent);
                    finish();
                }

                ha.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
