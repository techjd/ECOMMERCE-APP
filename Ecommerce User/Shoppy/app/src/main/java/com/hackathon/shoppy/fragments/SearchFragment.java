package com.hackathon.shoppy.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Interface.ItemClickListener;
import com.hackathon.shoppy.Model.Product;
import com.hackathon.shoppy.Payment.PaymentActivity;
import com.hackathon.shoppy.ProductDescription;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.Search.SearchAdapter;
import com.hackathon.shoppy.ViewHolder.ProductViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{


    ArrayList<Product> list;
    MaterialSearchBar searchBar;
    RecyclerView recyclerView;

    String pid ,name,price,description,image1,image2,image3;
    DatabaseReference ref;
    Product product;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        product = new Product();
//        pid = product.getPid();
//        name = product.getName();
//        price = product.getPrice();
//        description = product.getDescription();
//        image1 = product.getImage1();
//        image2 = product.getImage2();
//        image3 = product.getImage3();



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.INVISIBLE);
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchBar.getText().isEmpty()){
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                search(charSequence);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchBar.getText().isEmpty()){
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView textView = view.findViewById(R.id.pid);
                        String pid = (String) textView.getText();
                        if (pid.equals(list.get(position).getPid())){
                            String image1 = list.get(position).getImage1();
                            String image2 = list.get(position).getImage2();
                            String image3 = list.get(position).getImage3();
                            String description = list.get(position).getDescription();
                            String price = list.get(position).getPrice();
                            String name = list.get(position).getName();


                            Intent opendescription = new Intent(getActivity(), ProductDescription.class);
                            opendescription.putExtra("image1",image1);
                            opendescription.putExtra("image2",image2);
                            opendescription.putExtra("image3",image3);
                            opendescription.putExtra("pid",pid);
                            opendescription.putExtra("description",description);
                            opendescription.putExtra("price",price);
                            opendescription.putExtra("name",name);
                            startActivity(opendescription);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        search(text);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference uidref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        uidref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String location = dataSnapshot.child("location").getValue(String.class);
                ref = FirebaseDatabase.getInstance().getReference("Products").child(location);
                if (ref != null ){
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                list = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    list.add(ds.getValue(Product.class));

                                }
                                SearchAdapter adapter = new SearchAdapter(list);
                                recyclerView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            FancyToast.makeText(getContext(),databaseError.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                        }
                    });
                }

                if (searchBar != null){
                    searchBar.addTextChangeListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            search(s);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void search(CharSequence s) {
        ArrayList<Product> myList = new ArrayList<>();
        for (Product product : list){
            if (product.getName().toLowerCase().contains(s.toString().toLowerCase())){
                myList.add(product);
                SearchAdapter searchAdapter = new SearchAdapter(myList);
                recyclerView.setAdapter(searchAdapter);
            }
        }
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
    }
}
