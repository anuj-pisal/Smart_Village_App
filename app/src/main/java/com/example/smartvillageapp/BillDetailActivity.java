package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.List;

public class BillDetailActivity extends AppCompatActivity implements PaymentResultListener {

    TextView title, amount, date, status, desc;
    Button payBtn;
    ViewPager2 slider;

    String billId;
    long billAmount = 0; // 🔥 store amount safely

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_bill_detail);

        // 🔥 preload Razorpay
        Checkout.preload(getApplicationContext());

        title = findViewById(R.id.title);
        amount = findViewById(R.id.amount);
        date = findViewById(R.id.date);
        status = findViewById(R.id.status);
        desc = findViewById(R.id.desc);
        payBtn = findViewById(R.id.pay_btn);
        slider = findViewById(R.id.slider);

        billId = getIntent().getStringExtra("id");

        if (billId == null) {
            Toast.makeText(this, "Error: No Bill ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔥 LOAD DATA
        FirebaseFirestore.getInstance()
                .collection("bills")
                .document(billId)
                .get()
                .addOnSuccessListener(d -> {

                    if (d.exists()) {

                        title.setText(d.getString("title"));

                        Long amt = d.getLong("amount");
                        if (amt != null) {
                            billAmount = amt;
                            amount.setText("₹" + billAmount);
                        }

                        date.setText("Due: " + d.getString("dueDate"));
                        status.setText(d.getString("status"));
                        desc.setText(d.getString("description"));

                        List<String> images = (List<String>) d.get("images");

                        if (images != null && !images.isEmpty()) {
                            slider.setAdapter(new DevImageAdapter(this, images));
                        }

                        // 🔥 HANDLE PAID STATE
                        if ("paid".equals(d.getString("status"))) {
                            payBtn.setText("Paid ✔");
                            payBtn.setEnabled(false);
                        }
                    }
                });

        // 🔥 PAY BUTTON CLICK
        payBtn.setOnClickListener(v -> {
            if (billAmount <= 0) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            startPayment(title.getText().toString(), billAmount);
        });
    }

    // 🔥 RAZORPAY PAYMENT
    private void startPayment(String title, long amount) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_SZN4lFvtVjv7V8");

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Smart Village App");
            options.put("description", title);
            options.put("currency", "INR");

            // 🔥 amount in paise
            options.put("amount", amount * 100);

            checkout.open(this, options);

        } catch (Exception e) {
            Toast.makeText(this, "Payment error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ SUCCESS
    @Override
    public void onPaymentSuccess(String paymentId) {

        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

        FirebaseFirestore.getInstance()
                .collection("bills")
                .document(billId)
                .update(
                        "status", "paid",
                        "paymentId", paymentId
                );

        payBtn.setText("Paid ✔");
        payBtn.setEnabled(false);
    }

    // ❌ FAILURE
    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}