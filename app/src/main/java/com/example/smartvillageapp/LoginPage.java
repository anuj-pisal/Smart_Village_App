package com.example.smartvillageapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText loginEmail, loginPassword;
    private Button b;

    // 🔹 SIGNUP TEXT
    public void signUpText() {
        TextView signupText = findViewById(R.id.sign_up_text);

        String fullText = "Don’t have an account? Sign up now";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf("Sign up now");
        int endIndex = startIndex + "Sign up now".length();

        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.accent_green)),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginPage.this, SignUpActivity.class));
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan,
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signupText.setHighlightColor(Color.TRANSPARENT);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());
        signupText.setText(spannableString);
    }

    // 🔹 LOGIN FUNCTION
    public void firebaseConn() {

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.email_input_login);
        loginPassword = findViewById(R.id.pwd_input_login);
        b = findViewById(R.id.login_button);

        b.setOnClickListener(view -> {

            String email = loginEmail.getText().toString().trim();
            String pass = loginPassword.getText().toString().trim();

            if (email.isEmpty()) {
                loginEmail.setError("Email cannot be Empty !");
            } else if (pass.isEmpty()) {
                loginPassword.setError("Password cannot be Empty !");
            } else if (pass.length() < 8) {
                loginPassword.setError("Password must be at least 8 characters !");
            } else if (!isValidPassword(pass)) {
                loginPassword.setError("Password must contain Uppercase, Lowercase, Number & Special Character !");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginEmail.setError("Invalid Email !");
            } else {

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(authResult -> {

                            String uid = auth.getCurrentUser().getUid();

                            db.collection("users")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(doc -> {

                                        if (doc.exists()) {

                                            String role = doc.getString("role");

                                            UserSession.userId = uid;
                                            UserSession.username = doc.getString("username");
                                            UserSession.role = doc.getString("role");

                                            if ("admin".equals(role)) {

                                                Toast.makeText(LoginPage.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginPage.this, AdminMainActivity.class));

                                            } else {

                                                Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginPage.this, MainActivity.class));
                                            }

                                            finish();

                                        } else {

                                            // 🔥 USER DELETED BY ADMIN
                                            auth.signOut();

                                            Toast.makeText(LoginPage.this,
                                                    "Your account has been removed by admin",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(LoginPage.this, "Invalid Credentials !", Toast.LENGTH_SHORT).show()
                        );
            }
        });
    }

    // 🔹 PASSWORD VALIDATION
    private boolean isValidPassword(String password) {
        String passwordPattern =
                "^(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*\\d)" +
                        "(?=.*[@$!%*?&])" +
                        ".{8,}$";

        return password.matches(passwordPattern);
    }

    // 🔹 AUTO LOGIN (IMPORTANT FIX HERE)
    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {

            String uid = auth.getCurrentUser().getUid();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {

                        if (doc.exists()) {

                            String role = doc.getString("role");

                            UserSession.userId = uid;
                            UserSession.username = doc.getString("username");
                            UserSession.role = doc.getString("role");

                            if ("admin".equals(role)) {
                                startActivity(new Intent(this, AdminMainActivity.class));
                            } else {
                                startActivity(new Intent(this, MainActivity.class));
                            }

                            finish();

                        } else {

                            // 🔥 AUTO LOGOUT IF USER DELETED
                            auth.signOut();

                            Toast.makeText(this,
                                    "Your account has been removed by admin",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    // 🔹 ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        signUpText();
        firebaseConn();
    }
}