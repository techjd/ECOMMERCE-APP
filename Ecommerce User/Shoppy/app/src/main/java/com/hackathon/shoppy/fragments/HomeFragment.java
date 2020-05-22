package com.hackathon.shoppy.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.Model.Product;
import com.hackathon.shoppy.ProductDescription;
import com.hackathon.shoppy.ViewHolder.ProductViewHolder;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.ViewPager.OffersViewPagerAdapter;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    String city = "";

    OffersViewPagerAdapter viewPagerAdapter;

    String[] images ;
    ViewPager viewPager;
     private String location ;
    View myFragment;
    RecyclerView listproduct;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference products;
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    Task<LocationSettingsResponse> result ;

    FirebaseUser user;
    FirebaseAuth auth;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_home, container, false);
        listproduct = myFragment.findViewById(R.id.list_products);
        listproduct.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(container.getContext(),2);
        listproduct.setLayoutManager(layoutManager);
        viewPager = myFragment.findViewById(R.id.viewPager);
        DatabaseReference offersRef = FirebaseDatabase.getInstance().getReference("Offers");
        offersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String images1= dataSnapshot.child("Offer1").child("image").getValue(String.class);
               String images2 = dataSnapshot.child("Offer2").child("image").getValue(String.class);
               String images3=dataSnapshot.child("Offer3").child("image").getValue(String.class);
                images = new String[]{images1,images2,images3};
                viewPagerAdapter = new OffersViewPagerAdapter(getActivity(),images);
                viewPager.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        loadProducts();
        return myFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void loadProducts() {
        try {
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.child("location").exists()){
                            location = dataSnapshot.child("location").getValue(String.class);
                            products = FirebaseDatabase.getInstance().getReference("Products").child(location);
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
                                    Picasso.with(getActivity())
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

                                            Intent opendescription = new Intent(getActivity(), ProductDescription.class);
                                            opendescription.putExtra("image1",image1);
                                            opendescription.putExtra("image2",image2);
                                            opendescription.putExtra("image3",image3);
                                            opendescription.putExtra("pid",pid);
                                            opendescription.putExtra("description",description);
                                            opendescription.putExtra("price",price);
                                            opendescription.putExtra("name",name);
                                            opendescription.putExtra("uid",uid);
                                            startActivity(opendescription);
                                            FancyToast.makeText(getActivity(),model.getName(),FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
                                        }
                                    });
                                }
                            };
                            adapter.notifyDataSetChanged();
                            listproduct.setAdapter(adapter);
                        }



                    }
                    catch (Exception e){
                        FancyToast.makeText(getActivity(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            FancyToast.makeText(getActivity(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }

    }







}
