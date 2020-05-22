package com.hackathon.shoppy.Payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hackathon.shoppy.R;
import com.hackathon.shoppy.SendGrid.SendGridAsyncTask;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Random;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.AccountApi;
import sibModel.GetAccount;

public class BuyNowPaymentActivity extends AppCompatActivity implements PaymentResultListener {
    String OrderId = ORDERID() ;
    Bitmap bitmap;
    ImageView productimage;
    TextView productname,productprice,productquantity,totalprice;
    FirebaseAuth mAuth;
    FirebaseUser user;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now_payment);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        productname=findViewById(R.id.productname);
        productprice=findViewById(R.id.productprice);
        productquantity=findViewById(R.id.productquantity);
        totalprice = findViewById(R.id.totalprice);
        productimage = findViewById(R.id.product_image);

        productname.setText(getIntent().getExtras().get("name").toString());
        productquantity.setText(getIntent().getExtras().get("quantity").toString());
        productprice.setText(getIntent().getExtras().get("price").toString());
        totalprice.setText(getIntent().getExtras().get("totalprice").toString());
        bitmap = (Bitmap) getIntent().getParcelableExtra("image");
        productimage.setImageBitmap(bitmap);
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
            options.put("image", R.drawable.supermarket );
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
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        FancyToast.makeText(getApplicationContext(),"Payment Success",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        Intent intent =new Intent(BuyNowPaymentActivity.this,PaymentSuccess.class);
        intent.putExtra("orderid",OrderId);
        startActivity(intent);
        sendSms();

    }

    @Override
    public void onPaymentError(int i, String s) {
        FancyToast.makeText(getApplicationContext(),s,FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        Intent intent = new Intent(BuyNowPaymentActivity.this,PaymentFailed.class);
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
