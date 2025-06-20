package com.carcar.mychat;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private EditText messageInput;
    private Button sendButton;
    private RecyclerView recyclerView;

    private MessageAdapter adapter;
    private List<Message> messageList;

    private DatabaseReference chatRef;

    private Toolbar toolbar;
    private String currentUserPhone, receiverPhone, chatId;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_delete_chat) {
            // Handle delete chat
            return true;
        } else if (id == R.id.menu_chat_theme) {
            // Handle change theme
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Go back to MainActivity
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.neon_violate));



        initializeViews();
        getIntentData();
        setupChatId();
        setupRecyclerView();
        loadMessages();


        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void initializeViews() {
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.chatToolbar);

        // Set toolbar as ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

// Set title as phone number

    }

    private void getIntentData() {
        // Get receiver number from Intent
        receiverPhone = getIntent().getStringExtra("receiverPhone");
        Objects.requireNonNull(getSupportActionBar()).setTitle(receiverPhone);

        // Get current user number from FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhoneNumber() != null) {
            currentUserPhone = user.getPhoneNumber();
        } else {
            // For testing (hardcoded)
            currentUserPhone = "+919812345678";
            Toast.makeText(this, "Using test number. Auth may not be configured.", Toast.LENGTH_SHORT).show();
        }

        Log.d("ChatActivity", "Receiver Phone: " + receiverPhone);
        Log.d("ChatActivity", "Current User Phone: " + currentUserPhone);

        if (receiverPhone == null || currentUserPhone == null) {
            Toast.makeText(this, "Missing user info", Toast.LENGTH_SHORT).show();
            finish(); // prevent crash
        }
    }

    private void setupChatId() {
        // Always store chat in predictable key order
        chatId = currentUserPhone.compareTo(receiverPhone) < 0 ?
                currentUserPhone + "_" + receiverPhone :
                receiverPhone + "_" + currentUserPhone;

        chatRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatId);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList, currentUserPhone);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            long time = System.currentTimeMillis();
            Message msg = new Message(currentUserPhone, receiverPhone, text, time);

            // Store message in the Messages node
            chatRef.push().setValue(msg);

            // Update chatlist for sender and receiver
            DatabaseReference chatListRef = FirebaseDatabase.getInstance().getReference("chatlist");

            // Sender's chat list
            chatListRef.child(currentUserPhone).child(receiverPhone).child("lastMessage").setValue(text);
            chatListRef.child(currentUserPhone).child(receiverPhone).child("timestamp").setValue(time);
            chatListRef.child(currentUserPhone).child(receiverPhone).child("phone").setValue(receiverPhone);

            // Receiver's chat list
            chatListRef.child(receiverPhone).child(currentUserPhone).child("lastMessage").setValue(text);
            chatListRef.child(receiverPhone).child(currentUserPhone).child("timestamp").setValue(time);
            chatListRef.child(receiverPhone).child(currentUserPhone).child("phone").setValue(currentUserPhone);

            messageInput.setText(""); // clear the input
        }
    }






    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Message msg = snap.getValue(Message.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
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
