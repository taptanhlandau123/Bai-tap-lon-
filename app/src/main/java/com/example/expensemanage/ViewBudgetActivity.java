package com.example.expensemanage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewBudgetActivity extends AppCompatActivity {

    private TextView tvMonth, tvBudget, tvSpent, tvRemaining, tvDaysLeft;
    private Button btnDeleteBudget;
    private DatabaseReference budgetRef, expenseRef;
    private FirebaseAuth mAuth;
    private String currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_budget);

        tvMonth = findViewById(R.id.tv_month);
        tvBudget = findViewById(R.id.tv_budget);
        tvSpent = findViewById(R.id.tv_spent);
        tvRemaining = findViewById(R.id.tv_remaining);
        tvDaysLeft = findViewById(R.id.tv_days_left);
        btnDeleteBudget = findViewById(R.id.btn_delete_budget);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        tvMonth.setText("Tháng: " + currentMonth);

        budgetRef = FirebaseDatabase.getInstance().getReference("budgets").child(uid).child(currentMonth);
        expenseRef = FirebaseDatabase.getInstance().getReference("ExpenseDatabase").child(uid);

        loadBudgetInfo();

        btnDeleteBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewBudgetActivity.this, "Đã xóa ngân sách", Toast.LENGTH_SHORT).show();
                        startActivity(new android.content.Intent(ViewBudgetActivity.this, CreateBudgetActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ViewBudgetActivity.this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadBudgetInfo() {
        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer budgetAmount = snapshot.child("amount").getValue(Integer.class);
                if (budgetAmount == null) {
                    Toast.makeText(ViewBudgetActivity.this, "Chưa có ngân sách tháng này", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                tvBudget.setText("Ngân sách: " + String.format(Locale.getDefault(), "%,d VND", budgetAmount));

                expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalSpent = 0;
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Integer amt = item.child("amount").getValue(Integer.class);
                            if (amt != null) totalSpent += amt;
                        }
                        int remaining = budgetAmount - totalSpent;

                        tvSpent.setText("Đã chi: " +String.format(Locale.getDefault(), "%,d VND", totalSpent));
                        tvRemaining.setText("Còn lại: " +String.format(Locale.getDefault(), "%,d VND", remaining));

                        if (remaining < 0) {
                            Toast.makeText(ViewBudgetActivity.this, "⚠️ Bạn đã chi vượt quá ngân sách!", Toast.LENGTH_LONG).show();
                        }

                        Calendar now = Calendar.getInstance();
                        int today = now.get(Calendar.DAY_OF_MONTH);
                        int lastDay = now.getActualMaximum(Calendar.DAY_OF_MONTH);
                        int daysLeft = lastDay - today;

                        tvDaysLeft.setText(daysLeft + " ngày còn lại trong tháng");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
