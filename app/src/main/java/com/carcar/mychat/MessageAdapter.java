package com.carcar.mychat;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int MSG_LEFT = 0;
    private static final int MSG_RIGHT = 1;

    Context context;
    List<Message> messageList;
    String currentUser;

    public MessageAdapter(Context context, List<Message> messageList, String currentUser) {
        this.context = context;
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_RIGHT){

            view= LayoutInflater.from(context).inflate(R.layout.message_right,parent,false);
            return new RightViewHolder(view);


        }else{
            view = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);
            return new LeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messageList.get(position);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int maxWidth = (int) (screenWidth * 0.8f);

        if (holder instanceof RightViewHolder) {
            ((RightViewHolder) holder).text.setText(msg.getMessage());
            ((RightViewHolder) holder).text.setMaxWidth(maxWidth);
        } else {
            ((LeftViewHolder) holder).text.setText(msg.getMessage());
            ((LeftViewHolder) holder).text.setMaxWidth(maxWidth);
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int  getItemViewType(int position){
        if (messageList.get(position).getSender().equals(currentUser)) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    static class RightViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        RightViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textMessage);
        }
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        LeftViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textMessage);
        }
    }
}
