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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button b;

    public void signUpText() {
        TextView signupText = findViewById(R.id.sign_up_text);

        String fullText = "Don’t have an account? Sign up now";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf("Sign up now");
        int endIndex = startIndex + "Sign up now".length();

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
                Intent intent = new Intent(LoginPage.this, SignUpActivity.class);
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
        loginEmail = findViewById(R.id.email_input_login);
        loginPassword = findViewById(R.id.pwd_input_login);
        b = findViewById(R.id.login_button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String pass = loginPassword.getText().toString().trim();

                if(email.isEmpty()) {
                    loginEmail.setError("Email cannot be Empty !");
                }
                else if(pass.isEmpty()) {
                    loginPassword.setError("Password cannot be Empty !");
                }
                else if(pass.length() < 8) {
                    loginPassword.setError("Password must be at least 8 characters !");
                }
                else if(!isValidPassword(pass)) {
                    loginPassword.setError("Password must contain Uppercase, Lowercase, Number & Special Character !");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    loginEmail.setError("Invalid Email !");
                }
                else {
                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(LoginPage.this, "Login Successful !", Toast.LENGTH_SHORT)
                                            .show();
                                    startActivity(new Intent(LoginPage.this, MainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginPage.this, "Invalid Credentials !", Toast.LENGTH_SHORT)
                                            .show();
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
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        signUpText();
        firebaseConn();
    }
}
