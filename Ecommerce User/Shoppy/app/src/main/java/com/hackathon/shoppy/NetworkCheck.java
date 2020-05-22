package com.hackathon.shoppy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.hackathon.shoppy.Intro.Introduction;

public class NetworkCheck extends AppCompatActivity {
    LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_check);
        animationView = findViewById(R.id.animationview);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetworkConnected()){
            animationView.setVisibility(View.VISIBLE);
            animationView.playAnimation();
        }
        new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isNetworkConnected()){

                                }
                                else {
                                    interrupt();
                                    animationView.setVisibility(View.GONE);
                                    animationView.cancelAnimation();
                                    Intent intent = new Intent(NetworkCheck.this, Introduction.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        }.start();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
