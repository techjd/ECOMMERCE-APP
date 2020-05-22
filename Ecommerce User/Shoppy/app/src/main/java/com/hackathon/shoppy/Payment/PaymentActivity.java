package com.hackathon.shoppy.Payment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.shoppy.Model.Cart;
import com.hackathon.shoppy.ProductDescription;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.SendGrid.SendGridAsyncTask;
import com.hackathon.shoppy.ViewHolder.CartViewHolder;
import com.hackathon.shoppy.ViewHolder.CheckoutViewHolder;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.AccountApi;
import sibModel.GetAccount;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {


    private String custName = "";
    EditText ship_addr;
    String OrderId = ORDERID() ;
    Bitmap bitmap;
    ImageView productimage;
    TextView productname,productprice,productquantity,totalprice;
    FirebaseAuth mAuth;
    FirebaseUser user;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
private static final String TAG = PaymentActivity.class.getSimpleName();
private Object response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ship_addr = findViewById(R.id.ship_addr);
        recyclerView = findViewById(R.id.checkout);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        String totalamount = getIntent().getExtras().get("totalprice").toString();
        totalprice = findViewById(R.id.totalprice);
        totalprice.setText(totalamount);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Checkout.preload(getApplicationContext());


        Button pay = findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        SendEmail("ssiphackathonproject@gmail.com",user.getEmail(),OrderId+" Successfully Placed !","Your Order is successfully placed.\nThank You for shopping on shoppy , have a great day ahead");
                    }
                });
            }
        });

        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("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");
        // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
        //apiKey.setApiKeyPrefix("Token");

        AccountApi apiInstance = new AccountApi();
        try {
            GetAccount result = apiInstance.getAccount();
            System.out.println(result);
        } catch (ApiException e) {
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadcheckout();
    }

    public void loadcheckout(){

        DatabaseReference uidref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        uidref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String location = dataSnapshot.child("location").getValue(String.class);
                DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Cart");
                DatabaseReference imageref = FirebaseDatabase.getInstance().getReference("Products").child(location);
                DatabaseReference addressref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                FirebaseRecyclerAdapter<Cart, CheckoutViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CheckoutViewHolder>(
                        Cart.class,
                        R.layout.checkout_items,
                        CheckoutViewHolder.class,
                        cartlistref
                ) {
                    @Override
                    protected void populateViewHolder(CheckoutViewHolder holder, Cart cart, int i) {
                        holder.pname.setText(cart.getName());
                        holder.pprice.setText(cart.getPrice());
                        holder.quantity.setText(cart.getQuantity());

                        addressref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String address = String.valueOf(dataSnapshot.child("address").getValue());
                                try {
                                    ship_addr.setText(address);
                                }catch (Exception e){
                                    FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        imageref.child(cart.getPid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String image = String.valueOf(dataSnapshot.child("image1").getValue());
                                try {
                                    Picasso.with(PaymentActivity.this).load(image).into(holder.pimage);
                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void SendEmail(String from ,String to,String subject , String mailbody){
        Hashtable<String,String> parms = new Hashtable<>();
        parms.put("to",to);
        parms.put("from",from);
        parms.put("subject",subject);
        parms.put("text",mailbody);

        SendGridAsyncTask email = new SendGridAsyncTask();
        try {
            email.execute(parms);
        }catch (Exception e){
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
        }
    }
    public void startPayment() {

        String productName = "Order #" + OrderId;
        double productPrice = Double.parseDouble(totalprice.getText().toString());

        Checkout checkout = new Checkout();

        checkout.setKeyID("rzp_live_5rCdhSVeKyqMqc");

        checkout.setImage(R.drawable.supermarket);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Shoppy");
            options.put("description", productName);
            options.put("image", null );
            //options.put("order_id", "order_9A33XWu170gUtm");
            options.put("currency", "INR");
            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", (productPrice*100));

            checkout.open(activity, options);
        } catch(Exception e) {
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }
    @Override
    public void onPaymentSuccess(String s) {


        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                custName = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        FancyToast.makeText(getApplicationContext(),"Payment Success",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        Intent intent =new Intent(PaymentActivity.this,PaymentSuccess.class);
        intent.putExtra("orderid",OrderId);
        startActivity(intent);
        sendSms();
        DatabaseReference ordersref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Orders");
        DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Cart");
        cartlistref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    String name = ds.child("name").getValue(String.class);
                    String pid = ds.child("pid").getValue(String.class);
                    String price = ds.child("price").getValue(String.class);
                    String qty = ds.child("quantity").getValue(String.class);
                    String image = ds.child("image1").getValue(String.class);
                    ordersref.child(pid).child("orderId").setValue(OrderId);
                    ordersref.child(pid).child("pName").setValue(name);
                    ordersref.child(pid).child("pid").setValue(pid);
                    ordersref.child(pid).child("pPrice").setValue(price);
                    ordersref.child(pid).child("pQuantity").setValue(qty);
                    ordersref.child(pid).child("orderDate").setValue(saveCurrentDate);
                    ordersref.child(pid).child("orderTime").setValue(saveCurrentTime);
                    ordersref.child(pid).child("status").setValue("Not Shipped");
                    ordersref.child(pid).child("customerName").setValue(custName);
                    ordersref.child(pid).child("email").setValue(user.getEmail());
                    ordersref.child(pid).child("address").setValue(ship_addr.getText().toString());
                    ordersref.child(pid).child("uid").setValue(getIntent().getExtras().get("uid").toString());

                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Admin").child(getIntent().getExtras().get("uid").toString()).child("Orders");
                    ordersRef.child(pid).child("orderid").setValue(OrderId);
                    ordersRef.child(pid).child("pid").setValue(pid);
                    ordersRef.child(pid).child("pName").setValue(name);
                    ordersRef.child(pid).child("pPrice").setValue(price);
                    ordersRef.child(pid).child("pQuantity").setValue(qty);
                    ordersRef.child(pid).child("orderDate").setValue(saveCurrentDate);
                    ordersRef.child(pid).child("orderTime").setValue(saveCurrentTime);
                    ordersRef.child(pid).child("status").setValue("Not Shipped");
                    ordersRef.child(pid).child("customerName").setValue(custName);
                    ordersRef.child(pid).child("email").setValue(user.getEmail());
                    ordersRef.child(pid).child("address").setValue(ship_addr.getText().toString());
                    ordersRef.child(pid).child("uid").setValue(getIntent().getExtras().get("uid").toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cartlistref.removeValue();


    }

    @Override
    public void onPaymentError(int i, String s) {
        FancyToast.makeText(getApplicationContext(),s,FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        Intent intent = new Intent(PaymentActivity.this,PaymentFailed.class);
        startActivity(intent);

    }

    protected String ORDERID() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 7) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }

    public void sendSms() {
        try {
            // Construct data
            String apiKey = "apikey=" + "PRlYssCqvCQ-jRM4hV77iNQqw4zy3fyfHQorHlPqCK";
            String message = "&message=" + "Your Order No."+OrderId+" has been successfully placed.\nThank You For shopping on Shoppy.";
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + "917016801408";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
            FancyToast.makeText(getApplicationContext(),"Message Sent",FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
        } catch (Exception e) {
            FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }
    }

}
