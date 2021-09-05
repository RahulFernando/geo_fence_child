package com.rahul.child;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahul.child.Model.Parent;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Activity activity;
    private List<Parent> parents;
    private SharedPreferences preferences;

    private static final String PARENT_SERIAL = "ParentSerial";

    public ChatAdapter(Activity activity, List<Parent> parents) {
        this.activity = activity;
        this.parents = parents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.chat_tv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_user_name.setText(parents.get(position).getUserName());
        System.out.println(parents.get(0).getSerial());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MessageActivity.class);
                intent.putExtra(PARENT_SERIAL, parents.get(0).getSerial());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_user_name, count;
        RelativeLayout card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card_child_chat);
            txt_user_name = itemView.findViewById(R.id.txt_chat_user_name);
//            count = itemView.findViewById(R.id.txt_count);
        }
    }
}
