package com.example.smartvillageapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TextNoticeAdapter extends RecyclerView.Adapter<TextNoticeAdapter.ViewHolder> {

    Context context;
    List<NoticeModel> list;

    public TextNoticeAdapter(Context context, List<NoticeModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_text_notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoticeModel model = list.get(position);

        holder.title.setText(model.title);
        holder.description.setText(model.description);

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String postedDateStr = format.format(new Date(model.timestamp));
        
        if (model.expiryTimestamp > 0) {
            String expiryDateStr = format.format(new Date(model.expiryTimestamp));
            holder.date.setText(context.getString(R.string.valid_until, expiryDateStr));
        } else {
            holder.date.setText(context.getString(R.string.posted_on, postedDateStr));
        }

        holder.readMore.setOnClickListener(v -> {
            android.app.Dialog dialog = new android.app.Dialog(context);
            dialog.setContentView(R.layout.dialog_notice_details);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView titleText = dialog.findViewById(R.id.dialog_title);
            TextView detailsText = dialog.findViewById(R.id.dialog_details_text);
            android.widget.Button closeBtn = dialog.findViewById(R.id.btn_close);

            titleText.setText(model.title);
            detailsText.setText(model.description);

            closeBtn.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        });
        
        holder.itemView.setOnClickListener(v -> holder.readMore.performClick());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, date, readMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notice_title);
            description = itemView.findViewById(R.id.notice_description);
            date = itemView.findViewById(R.id.notice_date);
            readMore = itemView.findViewById(R.id.read_more);
        }
    }
}
