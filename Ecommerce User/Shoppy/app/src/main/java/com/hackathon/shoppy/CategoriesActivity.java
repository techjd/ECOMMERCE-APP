package com.hackathon.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.Model.Product;
import com.hackathon.shoppy.ViewHolder.ProductViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

public class CategoriesActivity extends AppCompatActivity {
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    DatabaseReference products;
    private String location ;
    String category ;
    RecyclerView listproduct;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        category = getIntent().getExtras().get("category").toString();
        listproduct = findViewById(R.id.recycler_view);
        listproduct.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(),2);
        listproduct.setLayoutManager(layoutManager);
        loadCategories();

    }

    private void loadCategories() {
        try {
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.child("location").exists()){
                            location = dataSnapshot.child("location").getValue(String.class);
                            products = FirebaseDatabase.getInstance().getReference("Categories").child(category).child(location);
                            adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                                    Product.class,
                                    R.layout.product_layout,
                                    ProductViewHolder.class,
                                    products
                            ) {
                                @Override
                                protected void populateViewHolder(ProductViewHolder viewHolder, final Product model, int position) {
                                    viewHolder.product_name.setText(model.getName());
                                    viewHolder.price.setText(model.getPrice());
                                    Picasso.with(CategoriesActivity.this)
                                            .load(model.getImage1())
                                            .into(viewHolder.product_image);
                                    final String pid = model.getPid();
                                    final String name = model.getName();
                                    final String price = model.getPrice();
                                    final String description = model.getDescription();
                                    final String image1 = model.getImage1();
                                    final String image2 = model.getImage2();
                                    final String image3 = model.getImage3();
                                    final String uid = model.getUid();

                                    viewHolder.setItemClickListener(new ItemClickListener() {
                                        @Override
                                        public void onClick(View view, int position, boolean isLongClick) {

                                            Intent opendescription = new Intent(CategoriesActivity.this, ProductDescription.class);
                                            opendescription.putExtra("image1",image1);
                                            opendescription.putExtra("image2",image2);
                                            opendescription.putExtra("image3",image3);
                                            opendescription.putExtra("pid",pid);
                                            opendescription.putExtra("description",description);
                                            opendescription.putExtra("price",price);
                                            opendescription.putExtra("name",name);
                                            opendescription.putExtra("uid",uid);
                                            startActivity(opendescription);
                                            FancyToast.makeText(getApplicationContext(),model.getName(),FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
                                        }
                                    });
                                }
                            };
                            adapter.notifyDataSetChanged();
                            listproduct.setAdapter(adapter);
                        }
//                        else {
//                            location = "";
//                            products = FirebaseDatabase.getInstance().getReference("Products").child(location);
//                            adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
//                                    Product.class,
//                                    R.layout.product_layout,
//                                    ProductViewHolder.class,
//                                    products
//                            ) {
//                                @Override
//                                protected void populateViewHolder(ProductViewHolder viewHolder, final Product model, int position) {
//                                    viewHolder.product_name.setText(model.getName());
//                                    viewHolder.price.setText(model.getPrice());
//                                    Picasso.with(getActivity())
//                                            .load(model.getImage1())
//                                            .into(viewHolder.product_image);
//                                    final String pid = model.getPid();
//                                    final String name = model.getName();
//                                    final String price = model.getPrice();
//                                    final String description = model.getDescription();
//                                    final String image1 = model.getImage1();
//                                    final String image2 = model.getImage2();
//                                    final String image3 = model.getImage3();
//                                    final String uid = model.getUid();
//
//                                    viewHolder.setItemClickListener(new ItemClickListener() {
//                                        @Override
//                                        public void onClick(View view, int position, boolean isLongClick) {
//
//                                            Intent opendescription = new Intent(getActivity(), ProductDescription.class);
//                                            opendescription.putExtra("image1",image1);
//                                            opendescription.putExtra("image2",image2);
//                                            opendescription.putExtra("image3",image3);
//                                            opendescription.putExtra("pid",pid);
//                                            opendescription.putExtra("description",description);
//                                            opendescription.putExtra("price",price);
//                                            opendescription.putExtra("name",name);
//                                            opendescription.putExtra("uid",uid);
//                                            startActivity(opendescription);
//                                            FancyToast.makeText(getActivity(),model.getName(),FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
//                                        }
//                                    });
//                                }
//                            };
//                            adapter.notifyDataSetChanged();
//                            listproduct.setAdapter(adapter);
//                        }


                    }
                    catch (Exception e){
                        FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){

        }

    }



}