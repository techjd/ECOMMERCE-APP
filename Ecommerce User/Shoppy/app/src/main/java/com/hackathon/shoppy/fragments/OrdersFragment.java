package com.hackathon.shoppy.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.Model.Cart;
import com.hackathon.shoppy.Model.Orders;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.ViewHolder.CartViewHolder;
import com.hackathon.shoppy.ViewHolder.OrdersViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment implements View.OnClickListener {
View myfragment;
MaterialSearchBar searchBar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String pname , image ,desc ;
    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myfragment = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = myfragment.findViewById(R.id.myorders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        searchBar = myfragment.findViewById(R.id.searchBar);
        return  myfragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth =FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference orderslistref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Orders");
        DatabaseReference productref = FirebaseDatabase.getInstance().getReference("Products");

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(
                Orders.class,
                R.layout.my_orders,
                OrdersViewHolder.class,
                orderslistref
        ) {
            @Override
            protected void populateViewHolder(OrdersViewHolder ordersViewHolder, Orders orders, int i) {

                try {
                    ordersViewHolder.pname.setText(orders.getpName());
                    ordersViewHolder.pdate.setText(orders.getOrderDate());
                }catch (Exception e){
                    FancyToast.makeText(getActivity(),e.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                }


                productref.child(orders.getPid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        image= String.valueOf(dataSnapshot.child("image1").getValue());
                        pname = String.valueOf(dataSnapshot.child("name").getValue());
                        desc = String.valueOf(dataSnapshot.child("description").getValue());
                        try {
                            Picasso.with(getContext()).load(image).into(ordersViewHolder.pimage);
                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ordersViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}
