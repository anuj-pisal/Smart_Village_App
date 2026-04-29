package com.example.smartvillageapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VideoAdminFragment extends Fragment {

    EditText title, url;
    Button addBtn;

    FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View v = inflater.inflate(R.layout.fragment_video_admin, container, false);

        title = v.findViewById(R.id.title);
        url = v.findViewById(R.id.url);
        addBtn = v.findViewById(R.id.add_btn);

        db = FirebaseFirestore.getInstance();

        addBtn.setOnClickListener(x -> {
            
            if (title.getText().toString().trim().isEmpty() || url.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
                return;
            }

            addBtn.setEnabled(false);

            Map<String, Object> map = new HashMap<>();
            map.put("title", title.getText().toString());
            map.put("videoUrl", url.getText().toString());

            db.collection("agriculture_videos").add(map)
                .addOnSuccessListener(d -> {
                    AppLogger.log(
                            "Video Added",
                            "NA",
                            "admin",
                            "Video: (" + title.getText().toString() + ") is added"
                    );

                    Toast.makeText(getContext(), getString(R.string.video_added), Toast.LENGTH_SHORT).show();
                    // Clear inputs after success
                    title.setText("");
                    url.setText("");
                    addBtn.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), getString(R.string.failed_to_add_video), Toast.LENGTH_SHORT).show();
                    addBtn.setEnabled(true);
                });

        });

        return v;
    }
}