package com.hackathon.shoppy.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.comix.overwatch.HiveProgressView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.hackathon.shoppy.home;
import com.hackathon.shoppy.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    EditText email,password,uid;
    TextView back;
    Button signup ;
    HiveProgressView progressBar ;
    CheckBox tc;
    LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password =findViewById(R.id.password);
        uid = findViewById(R.id.uid);
        signup = findViewById(R.id.signup);
        tc=findViewById(R.id.TandC);

        animationView=findViewById(R.id.animation_view);
        signup.setOnClickListener(this);


        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup:
                RegisterUser();
                break;
        }
    }

    private void RegisterUser() {
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);

        //progressBar.setVisibility(View.VISIBLE);
        final String Uid = email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        final String ud= uid.getText().toString().trim();

        if (!tc.isChecked()){
            tc.setError("Please agree Terms and Conditions");
            tc.requestFocus();
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
            return;
        }
        if (Uid.isEmpty()){
            email.setError("Email is required.");
            email.requestFocus();
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
            return;
        }

        if (pass.isEmpty()){
            password.setError("Password is required.");
            password.requestFocus();
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
            return;
        }

        if (ud.isEmpty()){
            uid.setError("User Name is Required");
            uid.requestFocus();
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Uid).matches()){
            email.setError("Please Enter A Valid Email.");
            email.requestFocus();
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
            return;
        }

        if (pass.length()<6){
            password.setError("Minimum length of password should be 6.");
            password.requestFocus();
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(Uid,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(getApplicationContext(),"Registration Successfull !",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            animationView.cancelAnimation();
                            animationView.setVisibility(View.GONE);
                            Intent intent = new Intent(Register.this, home.class);
                            startActivity(intent);
                            updateUserInfo(ud);
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                progressBar.setVisibility(View.GONE);
                                animationView.cancelAnimation();
                                animationView.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(),"Email already registered!....Please Sign In",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register.this,Login.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                animationView.cancelAnimation();
                                progressBar.setVisibility(View.GONE);
                                animationView.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }

                        // ...
                    }
                });


    }

    private void updateUserInfo( String uid) {
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        User user = new User(uid);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    animationView.cancelAnimation();
                    progressBar.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);
                    FancyToast.makeText(getApplicationContext(),"User Details Updated Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                }
                else {
                    animationView.cancelAnimation();
                    progressBar.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);
                    FancyToast.makeText(getApplicationContext(),task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
            }
        });

    }



}
