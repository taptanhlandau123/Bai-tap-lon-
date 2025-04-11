package com.example.expensemanage;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private DatabaseReference incomeRef, expenseRef;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ID người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_chart);

        barChart = findViewById(R.id.barChart);
        incomeRef = FirebaseDatabase.getInstance().getReference("IncomeData").child(userId);
        expenseRef = FirebaseDatabase.getInstance().getReference("ExpenseDatabase").child(userId);

        loadChartData();
    }

    private void loadChartData() {
        Map<String, Float> incomeMap = new HashMap<>();
        Map<String, Float> expenseMap = new HashMap<>();

        // Đọc dữ liệu Income
        incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String date = item.child("date").getValue(String.class);  // Bạn cần có field "date"
                    float amount = item.child("amount").getValue(Float.class);
                    incomeMap.put(date, incomeMap.getOrDefault(date, 0f) + amount);
                }

                // Sau khi đọc income, tiếp tục đọc expense
                expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String date = item.child("date").getValue(String.class);
                            float amount = item.child("amount").getValue(Float.class);
                            expenseMap.put(date, expenseMap.getOrDefault(date, 0f) + amount);
                        }

                        Log.d("ChartCheck", "IncomeMap: " + incomeMap.toString());
                        Log.d("ChartCheck", "ExpenseMap: " + expenseMap.toString());
                        showBarChart(incomeMap, expenseMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showBarChart(Map<String, Float> incomeMap, Map<String, Float> expenseMap) {
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        List<String> allDates = new ArrayList<>(new HashSet<String>() {{
            addAll(incomeMap.keySet());
            addAll(expenseMap.keySet());
        }});
        Collections.sort(allDates); // Sắp xếp theo ngày

        for (int i = 0; i < allDates.size(); i++) {
            String date = allDates.get(i);
            float income = incomeMap.getOrDefault(date, 0f);
            float expense = expenseMap.getOrDefault(date, 0f);

            incomeEntries.add(new BarEntry(i, income));
            expenseEntries.add(new BarEntry(i, expense));
            xAxisLabels.add(date);
        }

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Income");
        incomeSet.setColor(Color.GREEN);
        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Expense");
        expenseSet.setColor(Color.RED);

        BarData data = new BarData(incomeSet, expenseSet);
        data.setBarWidth(0.4f);

        barChart.setData(data);
        barChart.groupBars(0f, 0.2f, 0.02f); // Group các cột lại
        barChart.getDescription().setEnabled(false);

        // Label trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setDrawGridLines(false);

        barChart.invalidate(); // refresh
    }
}
