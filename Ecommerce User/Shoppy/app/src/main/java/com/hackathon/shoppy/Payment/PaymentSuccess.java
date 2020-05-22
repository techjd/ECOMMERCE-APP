package com.hackathon.shoppy.Payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.hackathon.shoppy.R;

public class PaymentSuccess extends AppCompatActivity {
LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        animationView = findViewById(R.id.animationview);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
    }
}
