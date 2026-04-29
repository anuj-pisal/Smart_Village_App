package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import android.view.Menu;

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
            menu.getMenu().add(Menu.NONE, 1, Menu.NONE, context.getString(R.string.see_details));
            menu.getMenu().add(Menu.NONE, 2, Menu.NONE, context.getString(R.string.delete_user_title));

            menu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == 1) {
                    Intent intent = new Intent(context, AdminUserDetailActivity.class);
                    intent.putExtra("userId", u.id);
                    intent.putExtra("name", u.name);
                    intent.putExtra("email", u.email);
                    context.startActivity(intent);
                } else if (item.getItemId() == 2) {
                    DialogUtils.showConfirmDialog(context,
                            context.getString(R.string.delete_user_title),
                            context.getString(R.string.confirm_delete_user, u.name),
                            context.getString(R.string.delete),
                            new DialogUtils.DialogCallback() {
                                @Override
                                public void onPositive() {
                                    FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(u.id)
                                            .delete()
                                            .addOnSuccessListener(unused -> {
                                                AppLogger.log(
                                                        "User Deleted",
                                                        u.name + " (id : " + u.id +" )",
                                                        "admin",
                                                        "User: " + u.name + " (" + u.id + ") is deleted"
                                                );
                                                Toast.makeText(context, context.getString(R.string.user_deleted), Toast.LENGTH_SHORT).show();
                                                list.remove(position);
                                                notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, context.getString(R.string.error_prefix) + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                }

                                @Override
                                public void onNegative() {
                                    // Do nothing
                                }
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