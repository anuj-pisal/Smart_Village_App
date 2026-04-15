package com.example.smartvillageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminUserManagementAdapter extends RecyclerView.Adapter<AdminUserManagementAdapter.VH> {

    Context context;
    List<UserModel> list;

    public AdminUserManagementAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView name, email;

        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            email = v.findViewById(R.id.email);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int position) {

        UserModel u = list.get(position);

        h.name.setText(u.name);
        h.email.setText(u.email);

        // 🔥 LONG PRESS MENU
        h.itemView.setOnLongClickListener(v -> {

            PopupMenu menu = new PopupMenu(context, h.itemView);
            menu.getMenu().add("Delete User");

            menu.setOnMenuItemClickListener(item -> {

                if (item.getTitle().equals("Delete User")) {

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(u.id)
                            .delete()
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();

                                list.remove(position);
                                notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
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
                .inflate(R.layout.item_admin_user, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}