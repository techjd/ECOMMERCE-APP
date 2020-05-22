package com.hackathon.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Model.Product;
import com.hackathon.shoppy.Payment.BuyNowPaymentActivity;
import com.hackathon.shoppy.Payment.PaymentActivity;
import com.hackathon.shoppy.Search.SearchAdapter;
import com.hackathon.shoppy.ViewPager.ViewPagerAdapter;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProductDescription extends AppCompatActivity  {
ViewPagerAdapter viewPagerAdapter;
ViewPager viewPager;
String[] images ;
Button buynow;
TextView pprice,pname,pdescription;
ElegantNumberButton elegantNumberButton;
FirebaseAuth mAuth;
FirebaseUser firebaseUser;
String productid = "";

    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        mAuth= FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        productid = getIntent().getExtras().get("pid").toString();
        pprice = findViewById(R.id.productprice);
        pname = findViewById(R.id.productname);
        pdescription = findViewById(R.id.productdescription);
        buynow=findViewById(R.id.buynow);
        elegantNumberButton=findViewById(R.id.elegantnobutton);
        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = pname.getText().toString();
                String Price = pprice.getText().toString();
                double price = Double.parseDouble(pprice.getText().toString());
                double qty = Double.parseDouble(elegantNumberButton.getNumber());
                double totalprice =  (price * qty);

                Bitmap image= viewPager.getDrawingCache();


                Intent intent =new Intent(ProductDescription.this, BuyNowPaymentActivity.class);
                intent.putExtra("totalprice",totalprice);
                intent.putExtra("name",name);
                intent.putExtra("quantity",qty);
                intent.putExtra("price",Price);
                intent.putExtra("image",image);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });
        pprice.setText(getIntent().getExtras().get("price").toString());
        pname.setText(getIntent().getExtras().get("name").toString());
        pdescription.setText(getIntent().getExtras().get("description").toString());
        ref = FirebaseDatabase.getInstance().getReference().child("Products");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        images = new String[]{getIntent().getExtras().get("image1").toString(),getIntent().getExtras().get("image2").toString(),getIntent().getExtras().get("image3").toString()};
        viewPagerAdapter = new ViewPagerAdapter(ProductDescription.this,images);
        viewPager.setAdapter(viewPagerAdapter);



    }

    private void addingToCartList() {
        String name = getIntent().getExtras().get("name").toString();
        String price = getIntent().getExtras().get("price").toString();
        String pid = getIntent().getExtras().get("pid").toString();
        String qty = String.valueOf(Integer.parseInt(elegantNumberButton.getNumber()));
        String uid = getIntent().getExtras().get("uid").toString();
        DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Cart").child(pid);
        HashMap<String,Object> cartList = new HashMap<>();
        cartList.put("pid",pid);
        cartList.put("name",name);
        cartList.put("price",price);
        cartList.put("quantity",qty);
        cartList.put("uid",uid);
        cartlistref.updateChildren(cartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FancyToast.makeText(getApplicationContext(),"Added To Cart",FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
            }
        });
    }
}
