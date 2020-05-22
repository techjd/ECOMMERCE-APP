package com.techjd.shoppyadminfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class name extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String pname, paddress;
    private DatabaseReference Driversdata;
    private String mAuthVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        mAuth = FirebaseAuth.getInstance();

        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");
        Driversdata = FirebaseDatabase.getInstance().getReference().child("Admin");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference reference = database.getReference("Admin");

        FirebaseUser user = mAuth.getCurrentUser();

        String phone = user.getPhoneNumber();
        String uid = user.getUid();

        final EditText name = findViewById(R.id.name);
        final EditText address = findViewById(R.id.address);

        getAdminInfo();

        Button next = findViewById(R.id.save_info);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name1 = name.getText().toString();
                final String address1 = address.getText().toString();


                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Admin").child(userId);
                Map userInfo = new HashMap<>();

                userInfo.put("name",name1);
                userInfo.put("address",address1);



                currentUserDb.updateChildren(userInfo);
                Intent next = new Intent(com.techjd.shoppyadminfinal.name.this,MainActivity.class);
                startActivity(next);
            }
        });


    }



    private void getAdminInfo() {
        {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final EditText name = findViewById(R.id.name);
            final EditText address = findViewById(R.id.address);
            DatabaseReference reference = database.getReference("Admin");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("pname") != null) {
                            pname = map.get("pname").toString();
                            name.setText(pname);
                        }
                        Map<String, Object> map1 = (Map<String, Object>) dataSnapshot.getValue();
                        if (map1.get("paddress") != null) {
                            paddress = map1.get("paddress").toString();
                            address.setText(paddress);
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


        }
    }

    private void saveAdminInfo() {

       EditText name = findViewById(R.id.name);
       EditText address = findViewById(R.id.address);

        FirebaseUser user = mAuth.getCurrentUser();





        paddress = address.getText().toString();
        pname = name.getText().toString();


    }
}
