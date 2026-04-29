package com.example.smartvillageapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class DialogUtils {

    public interface DialogCallback {
        void onPositive();
        void onNegative();
    }

    public static void showConfirmDialog(Context context, String title, String message, String positiveBtnText, DialogCallback callback) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        View view = LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        if (window != null) {
            int width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.80);
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleTv = view.findViewById(R.id.dialog_title);
        TextView messageTv = view.findViewById(R.id.dialog_message);
        MaterialButton positiveBtn = view.findViewById(R.id.btn_positive);
        MaterialButton negativeBtn = view.findViewById(R.id.btn_negative);

        titleTv.setText(title);
        messageTv.setText(message);
        positiveBtn.setText(positiveBtnText);

        positiveBtn.setOnClickListener(v -> {
            callback.onPositive();
            dialog.dismiss();
        });

        negativeBtn.setOnClickListener(v -> {
            callback.onNegative();
            dialog.dismiss();
        });

        dialog.setOnCancelListener(d -> callback.onNegative());

        dialog.show();
    }
}
