package com.example.smartvillageapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileFragment extends Fragment {
    public interface OnProfileUpdatedListener {
        void onProfileUpdated();
    }
    private OnProfileUpdatedListener listener;
    TextView usernameText;
    EditText emailField, nameField;
    TextView joiningDateText;
    EditText birthdateField;
    Button male, female, other;
    Button saveBtn;
    EditText contactField;
    String selectedGender = "";
    private ImageView profileImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnProfileUpdatedListener) {
            listener = (OnProfileUpdatedListener) context;
        }
    }

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    public void getStoredDataFromFirebase(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            // Username
                            String username = documentSnapshot.getString("username");
                            if (username != null) {
                                usernameText.setText(username);
                                nameField.setText(username);
                            }

                            // Email
                            emailField.setText(currentUser.getEmail());

                            // Joining Date
                            Timestamp timestamp =
                                    documentSnapshot.getTimestamp("joiningDate");

                            if (timestamp != null) {
                                Date date = timestamp.toDate();

                                SimpleDateFormat sdf =
                                        new SimpleDateFormat("dd MMMM yyyy",
                                                Locale.getDefault());

                                String formattedDate = sdf.format(date);
                                joiningDateText.setText(getString(R.string.joined_date, formattedDate));
                            }

                            // Contact
                            String contact = documentSnapshot.getString("contact");
                            if (contact != null) {
                                contactField.setText(contact);
                            }

                            // Birthdate
                            String birthdate = documentSnapshot.getString("birthdate");
                            if (birthdate != null) {
                                birthdateField.setText(birthdate);
                            }

                            // Gender
                            String gender = documentSnapshot.getString("gender");

                            if (gender != null) {

                                selectedGender = gender;

                                if (gender.equals("Male")) {
                                    selectGender(male);
                                }
                                else if (gender.equals("Female")) {
                                    selectGender(female);
                                }
                                else if (gender.equals("Other")) {
                                    selectGender(other);
                                }
                            }

                            // Profile Image
                            String imageUrl = documentSnapshot.getString("profileImage");

                            if (imageUrl != null) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .into(profileImage);
                            }
                        }
                    });
        }
    }

    public void calender(View view) {
        birthdateField = view.findViewById(R.id.profile_birthdate);
        birthdateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate =
                                selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        birthdateField.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    public void selectGender(Button selectedButton) {
        male.setBackgroundTintList(
                requireContext().getColorStateList(R.color.primaryColor));
        female.setBackgroundTintList(
                requireContext().getColorStateList(R.color.primaryColor));
        other.setBackgroundTintList(
                requireContext().getColorStateList(R.color.primaryColor));

        selectedButton.setBackgroundTintList(
                requireContext().getColorStateList(R.color.gender_selected));
    }

    public void imageHandler(View view) {
        ImageView addIcon = view.findViewById(R.id.add_profile_icon);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {

                        selectedImageUri = result.getData().getData();
                        uploadImageToFirebase(selectedImageUri);
                    }
                });

        addIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        StorageReference storageRef =
                FirebaseStorage.getInstance()
                        .getReference("profile_images/" + userId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            String imageUrl = uri.toString();

                            // Save URL in Firestore
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .update("profileImage", imageUrl);

                            // Load immediately
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .into(profileImage);

                            Toast.makeText(getContext(),
                                    getString(R.string.profile_image_updated),
                                    Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                getString(R.string.upload_failed),
                                Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        male = view.findViewById(R.id.btn_male);
        female = view.findViewById(R.id.btn_female);
        other = view.findViewById(R.id.btn_other);
        contactField = view.findViewById(R.id.profile_contact);
        saveBtn = view.findViewById(R.id.profile_save_btn);
        usernameText = view.findViewById(R.id.profile_username);
        emailField = view.findViewById(R.id.profile_email);
        joiningDateText = view.findViewById(R.id.profile_joining_date);
        nameField = view.findViewById(R.id.profile_name);
        profileImage = view.findViewById(R.id.profile_image);

        getStoredDataFromFirebase(view);
        calender(view);
        imageHandler(view);

        male.setOnClickListener(v -> {
            selectGender(male);
            selectedGender = "Male";
        });

        female.setOnClickListener(v -> {
            selectGender(female);
            selectedGender = "Female";
        });

        other.setOnClickListener(v -> {
            selectGender(other);
            selectedGender = "Other";
        });

        saveBtn.setOnClickListener(v -> {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) return;

            String userId = currentUser.getUid();

            String name = nameField.getText().toString().trim();
            String contact = contactField.getText().toString().trim();
            String birthdate = birthdateField.getText().toString().trim();

            if (name.isEmpty()) {
                nameField.setError(getString(R.string.error_name_required));
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("username", name);
            updateMap.put("contact", contact);
            updateMap.put("birthdate", birthdate);
            updateMap.put("gender", selectedGender);

            db.collection("users")
                    .document(userId)
                    .update(updateMap)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(getContext(),
                                getString(R.string.profile_updated),
                                Toast.LENGTH_SHORT).show();

                        if (listener != null) {
                            listener.onProfileUpdated();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    getString(R.string.profile_update_failed),
                                    Toast.LENGTH_SHORT).show());
        });
    }
}
