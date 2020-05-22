package com.hackathon.shoppy.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackathon.shoppy.CategoriesActivity;
import com.hackathon.shoppy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    View myFragment;
    private TextView grocery;
    private TextView medicines ;
    private TextView  cosmetics;
    private TextView  clothes;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragment =  inflater.inflate(R.layout.fragment_categories, container, false);
        grocery = myFragment.findViewById(R.id.textView4);
        medicines = myFragment.findViewById(R.id.textView5);
        cosmetics =  myFragment.findViewById(R.id.textView6);
        clothes =  myFragment.findViewById(R.id.textView7);
        return myFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getActivity(),CategoriesActivity.class);
                intent.putExtra("category", "grocery");
                startActivity(intent);
            }
        });

        medicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent1 = new Intent(getActivity(), CategoriesActivity.class);
                intent1.putExtra("category", "medicines");
                startActivity(intent1);
            }
        });

        cosmetics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent2 = new Intent(getActivity(),CategoriesActivity.class);
                intent2.putExtra("category", "cosmetics");
                startActivity(intent2);
            }
        });

        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent3 = new Intent(getActivity(),CategoriesActivity.class);
                intent3.putExtra("category", "clothes");
                startActivity(intent3);
            }
        });
    }

}
