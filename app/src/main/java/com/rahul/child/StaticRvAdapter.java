package com.rahul.child;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.MapFragment;
import com.rahul.child.Model.StaticRvModel;

import java.util.ArrayList;

public class StaticRvAdapter extends  RecyclerView.Adapter<StaticRvAdapter.StaticRVViewHolder>{
    private ArrayList<StaticRvModel> items;
    private int row_index = -1;
    private Context context;

    public StaticRvAdapter(Context context, ArrayList<StaticRvModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public StaticRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_layout, parent, false);
        StaticRVViewHolder sta = new StaticRVViewHolder(view);
        return sta;
    }

    @Override
    public void onBindViewHolder(@NonNull StaticRVViewHolder holder, int position) {
        StaticRvModel current = items.get(position);
        holder.imageView.setImageResource(current.getImage());
        holder.textView.setText(current.getText());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if (row_index == position){
//            holder.linearLayout.setBackgroundResource(R.drawable.static_rv_selected_bg);
            if (row_index == 0) {
                context.startActivity(new Intent(context, ChatActivity.class));
            } else if (row_index == 1) {
//                context.startActivity(new Intent(context, AddChildActivity.class));
            }
        }else {
            holder.linearLayout.setBackgroundResource(R.drawable.static_rv_bg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class StaticRVViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;

        public StaticRVViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            textView = itemView.findViewById(R.id.txt);
            linearLayout = itemView.findViewById(R.id.static_rv);
        }
    }
}
