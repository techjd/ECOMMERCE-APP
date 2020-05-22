package com.hackathon.shoppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Intro.Introduction;
import com.hackathon.shoppy.Settings.SettingsActivity;
import com.hackathon.shoppy.fragments.CartFragment;
import com.hackathon.shoppy.fragments.CategoriesFragment;
import com.hackathon.shoppy.fragments.MeFragment;
import com.hackathon.shoppy.fragments.HomeFragment;
import com.hackathon.shoppy.fragments.NotificationFragment;
import com.hackathon.shoppy.fragments.OrdersFragment;
import com.hackathon.shoppy.fragments.SearchFragment;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Task<LocationSettingsResponse> result ;

    String city = "";
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                checkLocationPermission();
//            }
//        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


//        final Handler ha=new Handler();
//        ha.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (!isNetworkConnected()){
//                    Intent intent = new Intent(home.this,NetworkActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                }
//                else {
//
//                }
//
//                ha.postDelayed(this, 1000);
//            }
//        }, 1000);


        BottomNavigationView bottomNavigationView = findViewById(R.id.navi_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }
        UpdateNavHeader();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.nav_home: selectedFragment = new HomeFragment();break;
                case R.id.nav_cart: selectedFragment = new CartFragment();break;
                case R.id.nav_profile: selectedFragment= new MeFragment();break;
                case R.id.nav_search: selectedFragment= new SearchFragment();break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;
            case R.id.nav_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CartFragment()).commit();
                break;
            case R.id.nav_category:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CategoriesFragment()).commit();
                break;
            case R.id.nav_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrdersFragment()).commit();
                break;
            case R.id.nav_share:
                FancyToast.makeText(this,"Share",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                break;
            case R.id.nav_privacy:
                FancyToast.makeText(this,"Privacy",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                break;
            case R.id.nav_profile:getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MeFragment()).commit();break;
            case R.id.nav_settings:Intent Intent= new Intent(home.this,SettingsActivity.class);
            startActivity(Intent);break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(home.this, Introduction.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_notification:getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotificationFragment()).commit();break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }
    public void UpdateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        TextView profilename =headerview.findViewById(R.id.profilename);
        TextView email = headerview.findViewById(R.id.emailid);
        ImageView profileimage = headerview.findViewById(R.id.profileimage);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   try {
                       String image = dataSnapshot.child("image").getValue().toString();
                       String name =  dataSnapshot.child("name").getValue().toString();
                       Glide.with(home.this).load(image).into(profileimage);
                       profilename.setText(name);
                   }catch (Exception e){

                   }

               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(postListener);

        email.setText(firebaseUser.getEmail());

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        SearchView searchView;
        getMenuInflater().inflate( R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
               // UserFeedback.show( "SearchOnQueryTextSubmit: " + query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();
        Context context = getApplicationContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        try {
            city = hereLocation(location.getLatitude(),location.getLongitude());
        }catch (Exception e){
            FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("location",city);

        ref.updateChildren(userMap);
        FancyToast.makeText(getApplicationContext(),"Location Updated Successfully ",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        FancyToast.makeText(getApplicationContext(),"GPS Enabled by user",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                        updateLocation();
//                        Intent intentt = new Intent(home.this,home.class);
//                        startActivity(intentt);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        FancyToast.makeText(getApplicationContext()," User rejected GPS request",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
//                        Intent intent = new Intent(home.this,home.class);
//                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void checkLocationPermission(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    updateLocation();
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {

                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        home.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Context context = getApplicationContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(location.getLatitude(),location.getLongitude());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("location",city);

                        ref.updateChildren(userMap);
                        FancyToast.makeText(getApplicationContext(),"Location Updated Successfully ",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();

                    }catch (Exception e){
                        e.printStackTrace();
                        FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }
                }
                else {
                    FancyToast.makeText(getApplicationContext(),"Permission not granted",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
                break;
            }

        }
    }

    public void updateLocation(){

        try {
            if (result.isSuccessful()){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
                }
                else {
                    Context context = getApplicationContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        city = hereLocation(location.getLatitude(),location.getLongitude());
                    }catch (Exception e){
                        FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }

                }
                if (city.isEmpty()){
                    checkLocationPermission();
                    FancyToast.makeText(getApplicationContext(),"Please turn on location services",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                }
                else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("location",city);

                    ref.updateChildren(userMap);
                    FancyToast.makeText(getApplicationContext(),"Location Updated Successfully ",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                }

            }
        }
        catch (Exception e){
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }
    }

    @org.jetbrains.annotations.NotNull
    private String hereLocation(double lat, double lon){
        String cityName ="";
        String countryName="";
        String stateName = "";
        Geocoder geocoder = new Geocoder(home.this, Locale.getDefault());
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
