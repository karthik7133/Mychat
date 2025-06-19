package com.carcar.mychat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    EditText messageInput;
    Button sendButton;
    RecyclerView recyclerView;

    MessageAdapter adapter;
    List<Message> messageList;

    DatabaseReference chatRef;
    String currentUser, receiverUser;
    String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);


        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber(); // or UID
        receiverUser = getIntent().getStringExtra("receiverPhone");

        assert receiverUser != null;
        chatId = currentUser.compareTo(receiverUser) < 0 ?
                currentUser + "_" + receiverUser :
                receiverUser + "_" + currentUser;

        chatRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatId);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        // Load messages
        loadMessages();

        // Send message
        sendButton.setOnClickListener(v -> sendMessage());
    }
    private  void sendMessage(){
        String text=messageInput.getText().toString().trim();
        if(!text.isEmpty()){
            long time=System.currentTimeMillis();
            Message msg=new Message(currentUser,receiverUser,text,time);
            chatRef.push().setValue(msg);
            messageInput.setText("");
        }
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Message m = snap.getValue(Message.class);
                    messageList.add(m);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}