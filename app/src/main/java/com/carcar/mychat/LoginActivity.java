package com.carcar.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText phno, otp;
    Button btn;
    FirebaseAuth auth;
    String verificationId;
    boolean otpSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        auth = FirebaseAuth.getInstance();

        // ✅ Check if already logged in
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_loginpage);

        phno = findViewById(R.id.phno);
        otp = findViewById(R.id.otp);
        btn = findViewById(R.id.btn);

        if (!otpSent) {
            btn.setText("Send OTP");
        }
        btn.setOnClickListener(v -> {
            if (!otpSent) {
                String phoneNumber = phno.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
                    phno.setError("Enter valid phone number");
                    return;
                }

                if (!phoneNumber.startsWith("+")) {
                    phoneNumber = "+91" + phoneNumber;
                }

                sendOTP(phoneNumber);

            } else {

                String code = otp.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    otp.setError("Enter OTP");
                    return;
                }

                verifyOTP(code);
            }
        });
    }

    private void sendOTP(String phoneNumber) {
        Log.d("OTP", "sendOTP: Starting OTP request for number: " + phoneNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    Log.d("OTP", "onVerificationCompleted: Auto verification success");
                    signInWithCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.e("OTP", "onVerificationFailed: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(String verifId, PhoneAuthProvider.ForceResendingToken token) {
                    Log.d("OTP", "onCodeSent: Verification ID received");
                    verificationId = verifId;
                    otpSent = true;
                    btn.setText("Register");
                    Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyOTP(String code) {
        if (verificationId == null) {
            Toast.makeText(this, "Verification ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
