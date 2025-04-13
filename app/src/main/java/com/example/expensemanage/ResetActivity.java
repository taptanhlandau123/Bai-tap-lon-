package com.example.expensemanage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        resetEmail = findViewById(R.id.reset_email);
        resetButton = findViewById(R.id.reset_button);

        resetButton.setOnClickListener(v -> {
            String email = resetEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                resetEmail.setError("Email required");
                return;
            }

            mDialog.setMessage("Sending reset email...");
            mDialog.show();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetActivity.this, "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ResetActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
