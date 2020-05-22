package com.techjd.shoppyadminfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView  grocery;
    private TextView medicines ;
    private TextView  cosmetics;
    private TextView  clothes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView BottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        grocery = (TextView) findViewById(R.id.textView4);
        medicines = (TextView) findViewById(R.id.textView5);
        cosmetics = (TextView) findViewById(R.id.textView6);
        clothes = (TextView) findViewById(R.id.textView7);


        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this,AddNewProduct.class);
                intent.putExtra("category", "grocery");
                startActivity(intent);
            }
        });

        medicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent1 = new Intent(MainActivity.this,AddNewProduct.class);
                intent1.putExtra("category", "medicines");
                startActivity(intent1);
            }
        });

        cosmetics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent2 = new Intent(MainActivity.this,AddNewProduct.class);
                intent2.putExtra("category", "cosmetics");
                startActivity(intent2);
            }
        });

        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent3 = new Intent(MainActivity.this,AddNewProduct.class);
                intent3.putExtra("category", "clothes");
                startActivity(intent3);
            }
        });



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;


                    switch (item.getItemId()) {

                        case R.id.own_products_nav:
                            selected = new myproducts();
                            break;

                        case R.id.orders_nav:
                            selected = new orders();
                            break;


                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
                    return true;
                }
            };
}
