package com.carcar.mychat;

import android.content.Context;
import android.content.Intent;
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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private final List<ChatListItem> chatList;
    private final Context context;

    public ChatListAdapter(List<ChatListItem> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_user, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatListItem chat = chatList.get(position);

        holder.phoneText.setText(chat.phone);
        holder.lastMessage.setText(chat.lastMessage);

        // Format timestamp
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date(chat.timestamp));
        holder.timeText.setText(time);

        // Handle click to open ChatActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiverPhone", chat.phone);

            // Only needed if using application context
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView phoneText, lastMessage, timeText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneText = itemView.findViewById(R.id.textPhone);
            lastMessage = itemView.findViewById(R.id.textLastMessage);
            timeText = itemView.findViewById(R.id.textTime);
        }
    }
}
