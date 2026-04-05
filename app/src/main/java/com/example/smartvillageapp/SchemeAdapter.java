package com.example.smartvillageapp;

import android.content.*;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.VH> {

    Context context;
    List<SchemeModel> list;

    public SchemeAdapter(Context c, List<SchemeModel> l) {
        context = c;
        list = l;
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, desc;

        public VH(View v) {
            super(v);
            img = v.findViewById(R.id.img);
            title = v.findViewById(R.id.title);
            desc = v.findViewById(R.id.desc);
        }
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        SchemeModel s = list.get(i);

        h.title.setText(s.title);
        h.desc.setText(s.shortDesc);

        Glide.with(context).load(s.image).into(h.img);

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SchemeDetailActivity.class);
            intent.putExtra("id", s.id);
            context.startActivity(intent);
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.item_scheme, p, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}