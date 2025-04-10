package com.example.expensemanage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateWalletActivity extends AppCompatActivity {

    private EditText etWalletName, etBalance;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_account);

        etWalletName = findViewById(R.id.et_wallet_name);
        etBalance = findViewById(R.id.et_balance);
        btnSave = findViewById(R.id.btn_save);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = etWalletName.getText().toString().trim();
                String balance = etBalance.getText().toString().trim();
                btnSave.setEnabled(!name.isEmpty() && !balance.isEmpty());
            }
        };

        etWalletName.addTextChangedListener(watcher);
        etBalance.addTextChangedListener(watcher);

        btnSave.setOnClickListener(view -> {
            String name = etWalletName.getText().toString().trim();
            String balanceStr = etBalance.getText().toString().trim();

            if (name.isEmpty() || balanceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double balance;
            try {
                balance = Double.parseDouble(balanceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số dư không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu lên Firebase
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference("wallets").child(uid);

            walletRef.child("wallet_name").setValue(name);
            walletRef.child("wallet_balance").setValue(balance).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Lưu SharedPreferences sau khi Firebase thành công
                    SharedPreferences prefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("hasCreatedWallet", true);
                    editor.putString("wallet_name", name);
                    editor.putFloat("wallet_balance", (float) balance);
                    editor.apply();

                    Toast.makeText(this, "Tạo ví thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateWalletActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi lưu ví", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
