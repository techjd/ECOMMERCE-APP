package com.hackathon.shoppy.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.Model.Cart;
import com.hackathon.shoppy.Model.Product;
import com.hackathon.shoppy.Payment.PaymentActivity;
import com.hackathon.shoppy.ProductDescription;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.ViewHolder.CartViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    String uid = "";
    View myFragment;
RecyclerView recyclerView;
RecyclerView.LayoutManager layoutManager;
FancyButton checkout;
TextView totalamount;
String img1,img2,img3,price,name,desc;
FirebaseUser firebaseUser;
private FirebaseAuth mAuth;
private double totalprice = 0;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = myFragment.findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        checkout = myFragment.findViewById(R.id.checkout);
        totalamount =myFragment.findViewById(R.id.totalamt);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth= FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


                checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                totalamount.setText("Total Amount = "+ totalprice);
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("totalprice",totalprice);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference uidref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        uidref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String location = dataSnapshot.child("location").getValue(String.class);
                DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Cart");
                DatabaseReference imageref = FirebaseDatabase.getInstance().getReference("Products").child(location);

                FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(
                        Cart.class,
                        R.layout.cart_items,
                        CartViewHolder.class,
                        cartlistref
                ) {
                    @Override
                    protected void populateViewHolder(CartViewHolder holder, Cart cart, int i) {
                        holder.pname.setText(cart.getName());
                        holder.pprice.setText(cart.getPrice());
                        holder.quantity.setText(cart.getQuantity());

                        double totalproductprice = Double.valueOf(cart.getPrice()) * Double.valueOf(cart.getQuantity());
                        totalprice = totalprice + totalproductprice;

                        uid=cart.getUid();

                        imageref.child(cart.getPid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String image = String.valueOf(dataSnapshot.child("image1").getValue());
                                try {
                                    Picasso.with(getContext()).load(image).into(holder.pimage);
                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] options = new CharSequence[]{
                                        "Edit",
                                        "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Cart Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (i == 0){
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products").child(cart.getPid());
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    img1= dataSnapshot.child("image1").getValue().toString();
                                                    img2 = dataSnapshot.child("image2").getValue().toString();
                                                    img3 = dataSnapshot.child("image3").getValue().toString();
                                                    price = dataSnapshot.child("price").getValue().toString();
                                                    name = dataSnapshot.child("name").getValue().toString();
                                                    desc = dataSnapshot.child("description").getValue().toString();
                                                    Intent intent = new Intent(getActivity(), ProductDescription.class);
                                                    intent.putExtra("pid",cart.getPid());
                                                    intent.putExtra("description",desc);
                                                    intent.putExtra("price",price);
                                                    intent.putExtra("name",name);
                                                    intent.putExtra("image1",img1);
                                                    intent.putExtra("image2",img2);
                                                    intent.putExtra("image3",img3);
                                                    startActivity(intent);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                        if (i == 1){
                                            cartlistref.child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        FancyToast.makeText(getActivity(),"Item Removed Successfully",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.create().show();
                            }
                        });

                    }

                };

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




}
