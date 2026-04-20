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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupUsername;
    private Button b;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    public void logInText() {
        TextView signupText = findViewById(R.id.log_in_text);

        String fullText = getString(R.string.already_have_account_full);
        String actionText = getString(R.string.log_in_link);
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf(actionText);
        int endIndex = startIndex + actionText.length();

        spannableString.setSpan(
                new StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableString.setSpan(
                new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.accent_green)
                ),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SignUpActivity.this, LoginPage.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(
                clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        signupText.setHighlightColor(Color.TRANSPARENT);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());
        signupText.setText(spannableString);
    }

    public void firebaseConn() {
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.email_input_signup);
        signupPassword = findViewById(R.id.pwd_input_signup);
        signupUsername = findViewById(R.id.username_input_signup);
        b = findViewById(R.id.signup_button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = signupUsername.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();

                if(email.isEmpty()) {
                    signupEmail.setError(getString(R.string.error_email_empty));
                }
                else if(pass.isEmpty()) {
                    signupPassword.setError(getString(R.string.error_password_empty));
                }
                else if(pass.length() < 8) {
                    signupPassword.setError(getString(R.string.error_password_short));
                }
                else if(!isValidPassword(pass)) {
                    signupPassword.setError(getString(R.string.error_password_weak));
                }
                else if(username.isEmpty()) {
                    signupUsername.setError(getString(R.string.error_username_empty));
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signupEmail.setError(getString(R.string.error_invalid_email));
                }
                else {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userId = firebaseUser.getUid();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);
                                userMap.put("role", "user");
                                userMap.put("joiningDate", FieldValue.serverTimestamp());

                                db.collection("users")
                                        .document(userId)
                                        .set(userMap)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(SignUpActivity.this,
                                                    getString(R.string.signup_success),
                                                    Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(SignUpActivity.this, LoginPage.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignUpActivity.this,
                                                    "Error: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        });

                            }
                            else {
                                Toast.makeText(SignUpActivity.this, getString(R.string.signup_failed, task.getException().getMessage()),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern =
                "^(?=.*[a-z])" +      // at least 1 lowercase
                        "(?=.*[A-Z])" +       // at least 1 uppercase
                        "(?=.*\\d)" +         // at least 1 digit
                        "(?=.*[@$!%*?&])" +   // at least 1 special character
                        ".{8,}$";             // minimum 8 characters

        return password.matches(passwordPattern);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        logInText();
        firebaseConn();
    }
}