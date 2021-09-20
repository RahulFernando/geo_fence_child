package com.rahul.child;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahul.child.Model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;
    private static final String MY_SERIAL = "Serial";
    private static final String MY_PREFERENCE = "childPref";

    private List<Message> messages;
    private Activity activity;
    private SharedPreferences preferences;

    public MessageAdapter(List<Message> messages, Activity activity) {
        this.messages = messages;
        this.activity = activity;
        preferences = activity.getSharedPreferences(MY_PREFERENCE, activity.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println(viewType);
        if (viewType == MESSAGE_TYPE_RIGHT) {
            View view = LayoutInflater.from(activity).inflate(R.layout.sender_message_layout, parent, false);
            return new ViewHolder(view);
        } else if (viewType == MESSAGE_TYPE_LEFT) {
            View view = LayoutInflater.from(activity).inflate(R.layout.receiver_message_layout, parent, false);
            return new ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        System.out.println(message.getDescription());
        holder.message.setText(message.getDescription());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String serial = preferences.getString(MY_SERIAL, "");
        System.out.println(serial);
        System.out.println(messages.get(position).getSender());
        if(messages.get(position).getSender().equals(serial)) {
            return MESSAGE_TYPE_RIGHT;
        } else {
            return MESSAGE_TYPE_LEFT;
        }
    }
}
