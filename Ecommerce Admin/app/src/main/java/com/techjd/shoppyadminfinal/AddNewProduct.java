package com.techjd.shoppyadminfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddNewProduct extends AppCompatActivity {


    ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private String CategoryName, Description, pprice, pname, savecurrentdate, savecurrenttime, Location, phone;
    private ImageView Image, Image1, Image2;

    private Button Add;
    private Context mContext;
    private EditText Name, Desc, Price, loc, Phone;
    public static Button location;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;


    HashMap<String, String> hashMap = new HashMap<>();
    private static final int CHOOSE_IMAGE = 101;
    private Uri Imageuri, ImageURI;
    private String productkey;
    private static String PREF_NAME = "prefs";
    private StorageReference ProductImagesRef;
    private DatabaseReference Producstsref;
    Uri croppedImageUri;
    public ArrayList<String> imagelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();

        Add = findViewById(R.id.add_product);
        Image = findViewById(R.id.addimage);
        Image1 = findViewById(R.id.addimage1);
        Image2 = findViewById(R.id.addimage2);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();




        Name = findViewById(R.id.product_name);
        Desc = findViewById(R.id.product_desc);
        Price = findViewById(R.id.product_price);
        location = findViewById(R.id.currentlocation);
        loc = findViewById(R.id.location);
        Phone = findViewById(R.id.phonenumber);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                } else {
                    Context context = getApplicationContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(location.getLatitude(), location.getLongitude());
                        loc.setText(city);
                    } catch (Exception e) {
                        e.printStackTrace();
                        FancyToast.makeText(getApplicationContext(), "Not Found!", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                    }

                }
            }
        });


        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        Image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser1();
            }
        });

        Image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser2();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveproductinfo();

                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();

                HashMap<Object, String> hashMap = new HashMap<>();


                hashMap.put("uid", uid);


                Description = Desc.getText().toString();
                pprice = Price.getText().toString();
                pname = Name.getText().toString();
                phone = Phone.getText().toString();


                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                savecurrentdate = simpleDateFormat.format(calendar.getTime());

                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss a");
                savecurrenttime = simpleTimeFormat.format(calendar.getTime());


                CategoryName = getIntent().getExtras().get("category").toString();

                productkey = getRandomString();

                Location = loc.getText().toString();


                ProductImagesRef = FirebaseStorage.getInstance().getReference("Products").child(productkey);
                Producstsref = FirebaseDatabase.getInstance().getReference().child("Products");


                try {


                  /*  int clicks = 0;
                    clicks++;

                    if (clicks >= 4){

                        Add.setEnabled(false);
                    }


                    SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("clicks", clicks);
                    editor.apply(); */

                    uploadimages();

                    updateproductInfo(savecurrentdate, productkey, savecurrenttime, Description, CategoryName, pname, pprice, Location, imagelist.get(0), imagelist.get(1), imagelist.get(2), phone);


                } catch (Exception e) {
                    FancyToast.makeText(getApplicationContext(), "Click Once More", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                }


            }
        });

    }


    private void uploadimages() {
        final StorageReference imageFilePath = ProductImagesRef;

        for (int uploadCount = 0; uploadCount < ImageList.size(); uploadCount++) {
            Uri IndividualImage = ImageList.get(uploadCount);
            final StorageReference ImageName = imageFilePath.child(productkey + " - " + IndividualImage.getLastPathSegment());
            final int finalUploadCount = uploadCount;
            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(productkey).child("image"+finalUploadCount);

                            if (finalUploadCount == 0) {
                                imagelist.add(url);
                                hashMap.put("image" + finalUploadCount, url);
                            } else if (finalUploadCount == 1) {
                                imagelist.add(url);
                                hashMap.put("image" + finalUploadCount, url);
                            } else if (finalUploadCount == 2) {
                                imagelist.add(url);
                                hashMap.put("image" + finalUploadCount, url);
                            }
                            //databaseReference.setValue(hashMap);
                        }
                    });
                }
            });
        }
    }

    private void updateproductInfo(String date, String pid, String time, String description, String categoryName, String name, String price, String location, String img1, String img2, String img3, String phone) {





        Product product = new Product(date, pid, time, description, categoryName, name, price, location, img1, img2, img3, phone);
        FirebaseDatabase.getInstance().getReference("Products").child(productkey).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(getApplicationContext(), "User Details Updated Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


//    private void uploadProductToFirebase() {
//
//        final StorageReference imageFilePath = ProductImagesRef;
//
//        for (int uploadCount = 0; uploadCount<ImageList.size(); uploadCount++){
//            Uri IndividualImage= ImageList.get(uploadCount);
//            final StorageReference ImageName = imageFilePath.child(pname);
//            final int finalUploadCount = uploadCount;
//            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String url = String.valueOf(uri);
//                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductImages").child(productkey);
//                            HashMap<String,String> hashMap = new HashMap<>();
//                            hashMap.put("image"+finalUploadCount,url);
//                            databaseReference.push().setValue(hashMap);
//                        }
//                    });
//                }
//            });
//        }
//    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    private void showImageChooser1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void showImageChooser2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }


    int currentSelectedImage = 0;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 0 || requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            currentSelectedImage = requestCode;
            Imageuri = data.getData();
            CropImage.activity(Imageuri).setGuidelines(CropImageView.Guidelines.ON).start(AddNewProduct.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            croppedImageUri = result.getUri();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageUri);

                if (currentSelectedImage == 0) {
                    Image.setBackground(null);
                    Image.setImageBitmap(bitmap);
                    if (ImageList.size() > 2) {
                        ImageList.clear();
                        //Image.setBackgroundResource(R.drawable.add_image);
                        Image1.setBackgroundResource(R.drawable.add_image);
                        Image2.setBackgroundResource(R.drawable.add_image);
                        //Image.setImageBitmap(null);
                        Image1.setImageBitmap(null);
                        Image2.setImageBitmap(null);
                    }
                    ImageList.add(croppedImageUri);
                } else if (currentSelectedImage == 1) {
                    Image1.setBackground(null);
                    Image1.setImageBitmap(bitmap);
                    if (ImageList.size() > 2) {
                        ImageList.clear();
                        Image.setBackgroundResource(R.drawable.add_image);
                        //Image1.setBackgroundResource(R.drawable.add_image);
                        Image2.setBackgroundResource(R.drawable.add_image);
                        Image.setImageBitmap(null);
                        //Image1.setImageBitmap(null);
                        Image2.setImageBitmap(null);
                    }
                    ImageList.add(croppedImageUri);
                } else if (currentSelectedImage == 2) {
                    Image2.setBackground(null);
                    Image2.setImageBitmap(bitmap);
                    if (ImageList.size() > 2) {
                        ImageList.clear();
                        Image.setBackgroundResource(R.drawable.add_image);
                        Image1.setBackgroundResource(R.drawable.add_image);
                        //Image2.setBackgroundResource(R.drawable.add_image);
                        Image.setImageBitmap(null);
                        Image1.setImageBitmap(null);
                        //Image2.setImageBitmap(null);
                    }
                    ImageList.add(croppedImageUri);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Context context = getApplicationContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(location.getLatitude(), location.getLongitude());
                        loc.setText(city);
                    } catch (Exception e) {
                        e.printStackTrace();
                        FancyToast.makeText(getApplicationContext(), "Not Found!", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                    }
                } else {
                    FancyToast.makeText(getApplicationContext(), "Permission not granted", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
                break;
            }

        }
    }

    private String hereLocation(double lat, double lon) {
        String cityName = "";
        String stateName = "";
        String countryName = "";
        Geocoder geocoder = new Geocoder(AddNewProduct.this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0 && adr.getCountryName() != null && adr.getCountryName().length() > 0 && adr.getAddressLine(0) != null && adr.getAddressLine(0).length() > 0) {
                        cityName = adr.getLocality();
                        stateName = adr.getAdminArea();
                        countryName = adr.getCountryName();
                        //cityName = adr.getLocality();
                        //countryName = adr.getCountryName();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName + ", " + stateName + ", " + countryName;
    }

    protected String getRandomString() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
