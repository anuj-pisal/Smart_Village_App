package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ContactsModel> contactList = new ArrayList<>();
    ContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerView = findViewById(R.id.contactRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactsAdapter(this, contactList);
        recyclerView.setAdapter(adapter);

        loadContacts();
    }

    private void loadContacts() {

        FirebaseFirestore.getInstance()
                .collection("contacts")
                .orderBy("priority")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contactList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ContactsModel model =
                                doc.toObject(ContactsModel.class);

                        contactList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

}