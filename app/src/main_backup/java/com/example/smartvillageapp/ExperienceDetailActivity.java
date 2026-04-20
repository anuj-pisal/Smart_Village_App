package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperienceDetailActivity extends AppCompatActivity {

    TextView title, desc;
    RecyclerView recyclerView;
    EditText input;
    Button send;

    List<CommentModel> list;
    CommentAdapter adapter;

    String expId, postUser;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_experience_detail);

        title = findViewById(R.id.detail_title);
        desc = findViewById(R.id.detail_desc);
        recyclerView = findViewById(R.id.comment_recycler);
        input = findViewById(R.id.comment_input);
        send = findViewById(R.id.send_btn);

        expId = getIntent().getStringExtra("expId");
        postUser = getIntent().getStringExtra("postingUser");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new CommentAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadExperience();
        loadComments();

        send.setOnClickListener(v -> addComment());
    }

    private void loadExperience() {

        FirebaseFirestore.getInstance()
                .collection("experiences")
                .document(expId)
                .get()
                .addOnSuccessListener(d -> {
                    title.setText(d.getString("title"));
                    desc.setText(d.getString("description"));
                });
    }

    private void loadComments() {

        FirebaseFirestore.getInstance()
                .collection("experiences")
                .document(expId)
                .collection("comments")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        CommentModel c = d.toObject(CommentModel.class);
                        list.add(c);
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void addComment() {

        String text = input.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(userDoc -> {

                    String name = userDoc.getString("username");

                    Map<String, Object> c = new HashMap<>();
                    c.put("text", text);
                    c.put("userName", name);

                    db.collection("experiences")
                            .document(expId)
                            .collection("comments")
                            .add(c);

                    AppLogger.log(
                            "User Comment",
                            UserSession.username + " (id:" + UserSession.userId + ")",
                            "user",
                            "Comment: "+ UserSession.username +"(id: " + UserSession.userId +
                                    ") commented on the post done by " + postUser + " (id: "+ expId +")"
                    );

                    input.setText("");
                    loadComments();
                });
    }
}