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

            Map<String, Object> map = new HashMap<>();
            map.put("title", title.getText().toString());
            map.put("videoUrl", url.getText().toString());

            db.collection("agriculture_videos").add(map);

            AppLogger.log(
                    "Video Added",
                    "NA",
                    "admin",
                    "Vidde: (" + title.getText().toString() + ") is added"
            );

            Toast.makeText(getContext(), "Video Added", Toast.LENGTH_SHORT).show();
        });

        return v;
    }
}