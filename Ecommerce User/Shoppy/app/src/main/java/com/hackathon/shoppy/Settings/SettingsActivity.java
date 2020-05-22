package com.hackathon.shoppy.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.fragments.HomeFragment;
import com.hackathon.shoppy.home;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    Task<LocationSettingsResponse> result ;
    String city = "";
    FirebaseUser user;
    FirebaseAuth auth;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                checkLocationPermission();
            }
        });

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference("Users").child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn =  findViewById(R.id.profile_image_change);
        closeTextBtn = (TextView) findViewById(R.id.settings_close);
        saveTextButton = (TextView) findViewById(R.id.settings_update);


        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
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
                                        SettingsActivity.this,
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
    private void updateOnlyUserInfo()
    {
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
                    e.printStackTrace();
                    FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }

            }
            if (city.isEmpty()){
                checkLocationPermission();
                FancyToast.makeText(getApplicationContext(),"Please turn on location services",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
            }
            else {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                HashMap<String, Object> userMap = new HashMap<>();
                userMap. put("name", fullNameEditText.getText().toString());
                userMap. put("address", addressEditText.getText().toString());
                userMap. put("phone", userPhoneEditText.getText().toString());
                userMap.put("location",city);
                userMap.put("email",user.getEmail());
                ref.child(user.getUid()).updateChildren(userMap);

                startActivity(new Intent(SettingsActivity.this,home.class));
                FancyToast.makeText(getApplicationContext(), "Profile Info update successfully.", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                finish();
            }

        }

        else {
            FancyToast.makeText(getApplicationContext(), "Profile Info update unsuccessful.", FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            FancyToast.makeText(this, "Error, Try Again.", FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
            startActivity(new Intent(SettingsActivity.this, home.class));
            finish();
        }

        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        FancyToast.makeText(getApplicationContext(),"GPS Enabled by user",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                        Intent intentt = new Intent(SettingsActivity.this,SettingsActivity.class);
                        startActivity(intentt);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        FancyToast.makeText(getApplicationContext()," User rejected GPS request",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                        Intent intent = new Intent(SettingsActivity.this,home.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                break;
        }
    }




    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }



    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(user.getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            String city = "";
                            if (task.isSuccessful())
                            {
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
                                        e.printStackTrace();
                                        FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                                    }

                                }

                                if (city.isEmpty()){
                                    checkLocationPermission();
                                    FancyToast.makeText(getApplicationContext(),"Please turn on location services",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                                }else {
                                    Uri downloadUrl = task.getResult();
                                    myUrl = downloadUrl.toString();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", fullNameEditText.getText().toString());
                                    userMap.put("address", addressEditText.getText().toString());
                                    userMap.put("phone", userPhoneEditText.getText().toString());
                                    userMap.put("image", myUrl);
                                    userMap.put("location",city);
                                    userMap.put("email",user.getEmail());
                                    ref.child(user.getUid()).updateChildren(userMap);

                                    progressDialog.dismiss();

                                    startActivity(new Intent(SettingsActivity.this, home.class));
                                    FancyToast.makeText(getApplicationContext(), "Profile Info update successfully.", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                                    finish();
                                }

                            }
                            else
                            {
                                progressDialog.dismiss();
                                FancyToast.makeText(getApplicationContext(), "Error.", FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Glide.with(getApplicationContext()).load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private String hereLocation(double lat, double lon){
        String cityName ="";
        String countryName="";
        String stateName = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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
