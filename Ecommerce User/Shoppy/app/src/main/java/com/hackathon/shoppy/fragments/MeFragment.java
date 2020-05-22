package com.hackathon.shoppy.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hackathon.shoppy.Login.Login;
import com.hackathon.shoppy.Payment.PaymentFailed;
import com.hackathon.shoppy.Payment.PaymentSuccess;
import com.hackathon.shoppy.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {


    Button location,paymentsuccess,paymentfailed;
    TextView currentlocation;
    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initialise your views

        location = view.findViewById(R.id.location);
        currentlocation=view.findViewById(R.id.currentlocation);
        paymentsuccess = view.findViewById(R.id.paymentsuccess);
        paymentfailed = view.findViewById(R.id.paymentfailed);

        paymentsuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentSuccess.class);
                startActivity(intent);
            }
        });

        paymentfailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentFailed.class);
                startActivity(intent);
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
                }
                else {
                    Context context = getContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(location.getLatitude(),location.getLongitude());
                        currentlocation.setText(city);
                    }catch (Exception e){
                        e.printStackTrace();
                        FancyToast.makeText(getContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }

                }
            }
        });

        Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Context context = getContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(location.getLatitude(),location.getLongitude());
                        currentlocation.setText(city);
                    }catch (Exception e){
                        e.printStackTrace();
                        FancyToast.makeText(getContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }
                }
                else {
                    FancyToast.makeText(getContext(),"Permission not granted",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
                break;
            }

        }
    }

    private String hereLocation(double lat, double lon){
        String cityName ="";
        String countryName="";
        String stateName = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat,lon,10);
            if (addresses.size()>0){
                for (Address adr : addresses){
                   if (adr.getLocality()!=null && adr.getLocality().length()>0 && adr.getCountryName() != null && adr.getCountryName().length()>0 && adr.getAddressLine(0)!=null && adr.getAddressLine(0).length()>0){
                       cityName = adr.getLocality();
                       stateName = adr.getAdminArea();
                       countryName = adr.getCountryName();
                       break;
                   }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return cityName+", "+stateName+", "+countryName;
    }

}
