package com.example.smartvillageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CropAdminFragment extends Fragment {

    ImageView image;
    EditText title, desc;
    Button selectImg, addBtn;

    Uri imageUri;

    FirebaseStorage storage;
    FirebaseFirestore db;

    private static final int PICK_IMAGE = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View v = inflater.inflate(R.layout.fragment_crop_admin, container, false);

        image = v.findViewById(R.id.image);
        title = v.findViewById(R.id.title);
        desc = v.findViewById(R.id.desc);
        selectImg = v.findViewById(R.id.select_img);
        addBtn = v.findViewById(R.id.add_btn);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        selectImg.setOnClickListener(x -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, PICK_IMAGE);
        });

        addBtn.setOnClickListener(x -> {
            addBtn.setEnabled(false);
            uploadCrop();
        });

        return v;
    }

    @Override
    public void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_IMAGE && res == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private void uploadCrop() {

        if (title.getText().toString().trim().isEmpty() || desc.getText().toString().trim().isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            addBtn.setEnabled(true);
            return;
        }
        
        String fileName = "crop_" + System.currentTimeMillis();

        StorageReference ref = storage.getReference()
                .child("agriculture/crop_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            Map<String, Object> map = new HashMap<>();
                            map.put("title", title.getText().toString());
                            map.put("description", desc.getText().toString());
                            map.put("imageUrl", uri.toString());

                            db.collection("agriculture_crops").add(map);

                            AppLogger.log(
                                    "Crop Info. Added",
                                    "NA",
                                    "admin",
                                    "Crop: (" + title.getText().toString() + ") info. is added"
                            );

                            Toast.makeText(getContext(), getString(R.string.crop_added), Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                    addBtn.setEnabled(true);
                });
    }
}