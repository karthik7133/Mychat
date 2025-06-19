package com.carcar.mychat;


import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ChatListItem> chatList;
    private ChatListAdapter adapter;
    private String currentUserPhone;
    private DatabaseReference chatListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.neon_violate));

        // Firebase current user phone
        currentUserPhone = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        adapter = new ChatListAdapter(chatList, this);
        recyclerView.setAdapter(adapter);

        // FloatingActionButton to start new chat
        FloatingActionButton fab = findViewById(R.id.fabStartNewChat);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewChatActivity.class));
        });
        Log.d("MainActivity", "Loaded chat list size: " + chatList.size());
        for (ChatListItem item : chatList) {
            Log.d("MainActivity", "Chat with: " + item.phone + ", last message: " + item.lastMessage);
        }
        Log.d("MainActivity", "Current user phone: " + currentUserPhone);


        // Load chat list
        loadChatList();
    }

    private void loadChatList() {
        chatListRef = FirebaseDatabase.getInstance().getReference("chatlist").child(currentUserPhone);

        chatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String phone = ds.getKey();
                    String lastMessage = ds.child("lastMessage").getValue(String.class);
                    Long timestamp = ds.child("timestamp").getValue(Long.class);

                    Log.d("MainActivity", "Found chat with: " + phone + " msg: " + lastMessage);

                    if (phone != null && lastMessage != null && timestamp != null) {
                        chatList.add(new ChatListItem(phone, lastMessage, timestamp));
                    }
                }

                Log.d("MainActivity", "Loaded chat list size: " + chatList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Firebase error: " + error.getMessage());
            }
        });
    }


}
