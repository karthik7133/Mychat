package com.carcar.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class NewChatActivity extends AppCompatActivity {

    EditText editPhone;
    Button btnStartChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_chat);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.neon_violate));

        editPhone = findViewById(R.id.editPhone);
        btnStartChat = findViewById(R.id.btnStartChat);

        btnStartChat.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.startsWith("+")) {
                Toast.makeText(this, "Please enter phone number in international format (e.g., +91...)", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(NewChatActivity.this, ChatActivity.class);
            intent.putExtra("receiverPhone", phone);
            startActivity(intent);
            finish(); // Optional: close NewChatActivity
        });
    }
}
