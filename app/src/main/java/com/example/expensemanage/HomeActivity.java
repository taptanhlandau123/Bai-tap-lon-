package com.example.expensemanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragment
    private DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    private FirebaseAuth mAuth;
    private TextView tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);

        // Kiểm tra đăng nhập
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Ánh xạ View
        tvBalance = findViewById(R.id.tv_balance); // Đảm bảo ID này tồn tại trong layout activity_home.xml
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        frameLayout = findViewById(R.id.main_frame);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.naView);

        // Navigation Drawer setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Fragment setup
        dashBoardFragment = new DashBoardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        setFragment(dashBoardFragment);

        // Lấy dữ liệu từ Firebase
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference("wallets").child(uid);



        walletRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("wallet_name").getValue(String.class);
                Float balance = snapshot.child("wallet_balance").getValue(Float.class);
                if (name != null && balance != null) {
                    tvBalance.setText(String.format("%s: %,.0f VND", name, balance));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Không thể lấy dữ liệu ví", Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });


        // Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.dashboard) {
                setFragment(dashBoardFragment);
                bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                return true;
            } else if (itemId == R.id.income) {
                setFragment(incomeFragment);
                bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                return true;
            } else if (itemId == R.id.expense) {
                setFragment(expenseFragment);
                bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                return true;
            }
            else if (itemId == R.id.create_budget) {
                checkBudgetThenNavigate(); // 👈 gọi hàm kiểm tra ngân sách
                bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                return true;
            }
            return false;
        });


    }
    private void checkBudgetThenNavigate() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        DatabaseReference budgetRef = FirebaseDatabase.getInstance().getReference("budgets").child(uid).child(currentMonth);

        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Đã có ngân sách -> mở trang xem
                    startActivity(new Intent(HomeActivity.this, ViewBudgetActivity.class));
                } else {
                    // Chưa có -> mở trang tạo
                    startActivity(new Intent(HomeActivity.this, CreateBudgetActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Không thể kiểm tra ngân sách", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId) {
        Fragment fragment = null;

        if (itemId == R.id.dashboard) {
            fragment = dashBoardFragment;
        } else if (itemId == R.id.income) {
            fragment = incomeFragment;
        } else if (itemId == R.id.expense) {
            fragment = expenseFragment;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if (itemId == R.id.wallet) {
            showWalletOptions();
            return true;
        }

        //  Khi click vào "Ngân Sách" trên nav menu
        if (itemId == R.id.create_budget) {
            checkBudgetThenNavigate(); // gọi hàm điều hướng tự động như bên dưới
            return true;
        }

        displaySelectedListener(item.getItemId());
        return true;
    }
    private void showWalletOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ví của tôi")
                .setItems(new CharSequence[]{"Sửa ví", "Xóa ví"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(HomeActivity.this, CreateWalletActivity.class));
                                break;
                            case 1:
                                confirmDeleteWallet();
                                break;
                        }
                    }
                });
        builder.show();
    }
    private void confirmDeleteWallet() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn xóa ví không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference("wallets").child(uid)
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Đã xóa ví", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, CreateWalletActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Lỗi khi xóa ví", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    public void updateBalanceText(String newBalanceText) {
        TextView tvBalance = findViewById(R.id.tv_balance);
        if (tvBalance != null) {
            tvBalance.setText("Số dư của bạn: " + newBalanceText);
        }
    }


}
