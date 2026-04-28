package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

    Context context;
    List<UserModel> list;

    public UserAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView name, status;

        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            status = v.findViewById(R.id.status);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        UserModel u = list.get(i);
        h.name.setText(u.name);

        FirebaseFirestore.getInstance()
                .collection("bills")
                .whereEqualTo("userId", u.id)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) return;

                    boolean hasUnpaid = false;
                    boolean hasOverdue = false;

                    for (DocumentSnapshot d : snapshot) {

                        String status = d.getString("status");
                        String dueDate = d.getString("dueDate");

                        if ("unpaid".equals(status)) {
                            hasUnpaid = true;

                            if (isOverdue(dueDate)) {
                                hasOverdue = true;
                            }
                        }
                    }

                    if (hasOverdue) {
                        h.status.setText("❗");
                    } else if (hasUnpaid) {
                        h.status.setText("⚠");
                    } else {
                        h.status.setText("");
                    }
                });

        h.itemView.setOnLongClickListener(v -> {

            PopupMenu menu = new PopupMenu(context, h.itemView);

            menu.getMenu().add("Add Bill");
            menu.getMenu().add("View Previous Bills");

            menu.setOnMenuItemClickListener(item -> {

                if (item.getTitle().equals("Add Bill")) {

                    Intent i1 = new Intent(context, AddBillActivity.class);
                    i1.putExtra("userId", u.id);
                    context.startActivity(i1);

                } else if (item.getTitle().equals("View Previous Bills")) {

                    Intent i2 = new Intent(context, UserBillsActivity.class);
                    i2.putExtra("userId", u.id);
                    context.startActivity(i2);
                }

                return true;
            });

            menu.show();

            return true;
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_user, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔥 DATE CHECK
    private boolean isOverdue(String dueDateStr) {
        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

            Date dueDate = format.parse(dueDateStr);
            return dueDate.before(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}