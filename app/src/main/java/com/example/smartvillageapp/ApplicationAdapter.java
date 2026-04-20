package com.example.smartvillageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    Context context;
    List<ApplicationModel> list;

    public ApplicationAdapter(Context context, List<ApplicationModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, status;
        Button approve, reject;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.applicant_name);
            email = v.findViewById(R.id.applicant_email);
            status = v.findViewById(R.id.app_status);
            approve = v.findViewById(R.id.approve_btn);
            reject = v.findViewById(R.id.reject_btn);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        ApplicationModel app = list.get(i);

        // ✅ Show Name
        h.name.setText(app.applicantName != null ? app.applicantName : "Unknown");

        // ✅ Show Email
        h.email.setText(app.applicantEmail != null ? app.applicantEmail : "");

        // ✅ Show Status
        h.status.setText(context.getString(R.string.status_prefix) + app.status);

        // Disable buttons if already decided
        if ("approved".equals(app.status) || "rejected".equals(app.status)) {
            h.approve.setEnabled(false);
            h.reject.setEnabled(false);
        }

        h.approve.setOnClickListener(v -> {
            updateStatus(app, "approved");
        });

        h.reject.setOnClickListener(v -> {
            updateStatus(app, "rejected");
        });
    }

    private void updateStatus(ApplicationModel app, String status) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1️⃣ Update application status
        db.collection("applications")
                .document(app.docId)
                .update("status", status)
                .addOnSuccessListener(unused -> {

                    // 2️⃣ If APPROVED → CLOSE JOB
                    if ("approved".equals(status)) {
                        db.collection("jobs")
                                .document(app.jobId) // 🔥 important
                                .update("status", "closed");
                    }

                    Toast.makeText(context,
                            "Application " + status,
                            Toast.LENGTH_SHORT).show();

                    app.status = status;
                    notifyDataSetChanged();
                });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_application, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}