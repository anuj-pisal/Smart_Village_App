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

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;

public class ContactsActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<ContactsModel> contactList = new ArrayList<>();
    List<ContactsModel> fullContactList = new ArrayList<>();
    ContactsAdapter adapter;
    TextInputEditText searchInput;
    View emptyStateLayout;
    TextView emptyStateMsg;
    FloatingActionButton fabAddContact;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerView = findViewById(R.id.contactRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No contacts available");

        adapter = new ContactsAdapter(this, contactList);
        
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        adapter.setAdmin(isAdmin);
        recyclerView.setAdapter(adapter);
        fabAddContact = findViewById(R.id.fab_add_contact);
        
        if (isAdmin) {
            fabAddContact.setVisibility(View.VISIBLE);
            fabAddContact.setOnClickListener(v -> {
                startActivity(new Intent(ContactsActivity.this, AddContactActivity.class));
            });
            setupSwipeToDelete();
        }

        searchInput = findViewById(R.id.searchContact);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadContacts();
    }
    
    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ContactsModel contact = contactList.get(position);
                
                DialogUtils.showConfirmDialog(ContactsActivity.this,
                    "Delete Contact",
                    "Are you sure you want to delete this contact?",
                    "Delete",
                    new DialogUtils.DialogCallback() {
                        @Override
                        public void onPositive() {
                            FirebaseFirestore.getInstance().collection("contacts").document(contact.getId()).delete()
                                .addOnSuccessListener(aVoid -> loadContacts());
                        }

                        @Override
                        public void onNegative() {
                            adapter.notifyItemChanged(position);
                        }
                    });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void filterContacts(String text) {
        List<ContactsModel> filteredList = new ArrayList<>();
        for (ContactsModel contact : fullContactList) {
            if (contact.getName().toLowerCase().contains(text.toLowerCase()) ||
                contact.getPhone().contains(text) ||
                contact.getEmail().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        adapter.updateList(filteredList);
        toggleEmptyState(filteredList.size());
    }

    private void toggleEmptyState(int size) {
        if (size == 0) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadContacts() {

        FirebaseFirestore.getInstance()
                .collection("contacts")
                .orderBy("priority")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contactList.clear();
                    fullContactList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ContactsModel model =
                                doc.toObject(ContactsModel.class);
                        model.setId(doc.getId());

                        contactList.add(model);
                        fullContactList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    toggleEmptyState(contactList.size());
                });
    }

}