package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddExperienceActivity extends BaseActivity {

    EditText title, desc;
    Button postBtn;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_experience);

        title = findViewById(R.id.title_input);
        desc = findViewById(R.id.desc_input);
        postBtn = findViewById(R.id.post_btn);

        postBtn.setOnClickListener(v -> {

            String t = title.getText().toString();
            String d = desc.getText().toString();

            postBtn.setEnabled(false);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(userDoc -> {

                        String name = userDoc.getString("username");

                        Map<String, Object> exp = new HashMap<>();
                        exp.put("title", t);
                        exp.put("description", d);
                        exp.put("userId", userId);
                        exp.put("userName", name);
                        exp.put("timestamp", System.currentTimeMillis());

                        db.collection("experiences").add(exp)
                                .addOnSuccessListener(doc -> {
                                    AppLogger.log(
                                            "User post",
                                            UserSession.username + " (id:" + UserSession.userId + ")",
                                            "user",
                                            "Post: User posted with title - (" + t +")"
                                    );
                                    Toast.makeText(this, getString(R.string.posted), Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                                    postBtn.setEnabled(true);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
                        postBtn.setEnabled(true);
                    });
        });
    }
}