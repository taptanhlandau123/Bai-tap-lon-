package com.example.expensemanage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanage.model.Budget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateBudgetActivity extends AppCompatActivity {

    private EditText edtBudgetAmount;
    private Button btnSaveBudget;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);

        edtBudgetAmount = findViewById(R.id.edt_budget_amount);
        btnSaveBudget = findViewById(R.id.btn_save_budget);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("budgets").child(uid);

        btnSaveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = edtBudgetAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amountStr)) {
                    edtBudgetAmount.setError("Nhập số tiền ngân sách");
                    return;
                }

                int amount = Integer.parseInt(amountStr);
                String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

                Budget budget = new Budget(amount, currentMonth);
                budgetRef.child(currentMonth).setValue(budget).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateBudgetActivity.this, "Lưu ngân sách thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateBudgetActivity.this, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}