package com.example.smartvillageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<CommentModel> list;

    public CommentAdapter(Context context, List<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text, user;

        public ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.comment_text);
            user = v.findViewById(R.id.comment_user);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        CommentModel c = list.get(i);
        h.text.setText(c.text);
        h.user.setText(c.userName);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_comment, p, false));
    }

    @Override
    public int getItemCount() { return list.size(); }
}
