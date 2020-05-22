package com.hackathon.shoppy.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.comix.overwatch.HiveProgressView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hackathon.shoppy.home;
import com.hackathon.shoppy.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    private EditText phoneno , password ;
    private Button login;
    private TextView signup;
    private String parentDb = "Admin";
    HiveProgressView progressView ;
    LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        animationView = findViewById(R.id.animation_view);
        mAuth =FirebaseAuth.getInstance();
        progressView=findViewById(R.id.progress);
        phoneno = findViewById(R.id.uid);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    private void userLogin() {

        String uid = phoneno.getText().toString().trim();
        String pass=password.getText().toString().trim();

        if (uid.isEmpty()){
            phoneno.setError("Email is required.");
            phoneno.requestFocus();
            return;
        }

        if (pass.isEmpty()){
            password.setError("Password is required.");
            password.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uid).matches()){
            phoneno.setError("Please Enter A Valid Email.");
            phoneno.requestFocus();
            return;
        }

        if (pass.length()<6){
            password.setError("Minimum length of password should be 6.");
            password.requestFocus();
            return;
        }

        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        //progressView.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(uid,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    animationView.cancelAnimation();
                    animationView.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);
                    Intent intent = new Intent(Login.this, home.class);
                    startActivity(intent);
                    finish();
                    FancyToast.makeText(getApplicationContext(),"Login Successful!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                }
                else {
                    animationView.cancelAnimation();
                    animationView.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);
                    FancyToast.makeText(getApplicationContext(),task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
            }
        });

    }


}
