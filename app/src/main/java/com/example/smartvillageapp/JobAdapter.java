package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    Context context;
    List<JobModel> list;

    public JobAdapter(Context context, List<JobModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, status;
        Button applyBtn, viewBtn;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.job_title);
            desc = v.findViewById(R.id.job_desc);
            status = v.findViewById(R.id.job_status);
            applyBtn = v.findViewById(R.id.apply_btn);
            viewBtn = v.findViewById(R.id.view_app_btn);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        JobModel job = list.get(i);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        h.title.setText(job.title);
        String xyz = job.description + "\n" +job.requirement;
        h.desc.setText(xyz);

        if ("closed".equals(job.status)) {

            h.applyBtn.setVisibility(View.VISIBLE);
            h.applyBtn.setText("Expired");
            h.applyBtn.setEnabled(false);

            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Job Closed");
            h.status.setTextColor(Color.RED);

            return;
        }

        // ✅ CASE 1: OWNER (VERY IMPORTANT FIX)
        if (job.postedBy != null && job.postedBy.equals(userId)) {

            h.applyBtn.setVisibility(View.GONE);

            h.viewBtn.setVisibility(View.VISIBLE);

            // 🔥 OPEN APPLICATIONS
            h.viewBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, ApplicationsActivity.class);
                intent.putExtra("jobId", job.jobId);
                context.startActivity(intent);
            });

            return;
        }

        // 🔥 CHECK IF ALREADY APPLIED
        h.viewBtn.setVisibility(View.GONE);
        db.collection("applications")
                .whereEqualTo("jobId", job.jobId)
                .whereEqualTo("applicantId", userId)
                .get()
                .addOnSuccessListener(query -> {

                    if (!query.isEmpty()) {

                        DocumentSnapshot doc = query.getDocuments().get(0);
                        String status = doc.getString("status");

                        // ✅ Already applied
                        h.applyBtn.setVisibility(View.VISIBLE);
                        h.applyBtn.setText("Applied");
                        h.applyBtn.setEnabled(false);

                        h.status.setVisibility(View.VISIBLE);
                        h.status.setText("Status: " + status.toUpperCase());

                        // Color
                        if ("pending".equals(status)) {
                            h.status.setTextColor(Color.YELLOW);
                        } else if ("approved".equals(status)) {
                            h.status.setTextColor(Color.GREEN);
                        } else {
                            h.status.setTextColor(Color.RED);
                        }

                    } else {

                        // ❌ Not applied yet
                        h.applyBtn.setVisibility(View.VISIBLE);
                        h.applyBtn.setEnabled(true);
                        h.applyBtn.setText("Apply");

                        h.status.setVisibility(View.GONE);
                    }
                });

        // 🔥 APPLY BUTTON
        h.applyBtn.setOnClickListener(v -> {

            db.collection("applications")
                    .whereEqualTo("jobId", job.jobId)
                    .whereEqualTo("applicantId", userId)
                    .get()
                    .addOnSuccessListener(query -> {

                        if (!query.isEmpty()) {
                            Toast.makeText(context, "Already applied", Toast.LENGTH_SHORT).show();
                        } else {

                            db.collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {

                                        String name = userDoc.getString("username");
                                        String email = userDoc.getString("email");

                                        Map<String, Object> app = new HashMap<>();
                                        app.put("jobId", job.jobId);
                                        app.put("applicantId", userId);
                                        app.put("applicantName", name);
                                        app.put("applicantEmail", email);
                                        app.put("status", "pending");

                                        db.collection("applications").add(app);

                                        AppLogger.log(
                                                "Job Application",
                                                UserSession.username + " (id:" + UserSession.userId + ")",
                                                "user",
                                                "Job Application: Applied for the job (id: " + job.jobId + ")"
                                        );

                                        Toast.makeText(context, "Applied", Toast.LENGTH_SHORT).show();

                                        // 🔥 UI update instantly
                                        h.applyBtn.setText("Applied");
                                        h.applyBtn.setEnabled(false);

                                        h.status.setVisibility(View.VISIBLE);
                                        h.status.setText("Status: PENDING");
                                        h.status.setTextColor(Color.YELLOW);
                                    });
                        }
                    });
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_job, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
