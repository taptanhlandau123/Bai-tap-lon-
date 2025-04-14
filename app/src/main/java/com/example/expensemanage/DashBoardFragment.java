package com.example.expensemanage;

import  android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanage.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;




import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashBoardFragment extends Fragment {

    private Animation FadOpen, FadeClose;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    private BarChart barChart;
    private FirebaseAuth mAuth;
    private DatabaseReference incomeRef, expenseRef;
    private float totalIncome = 0;
    private float totalExpense = 0;

    private TextView income_set_result, expense_set_result;

    private FloatingActionButton fbMainPlusBtn, incomeFtBtn, expenseFtBtn;
    private TextView incomeFtText, expenseFtText;
    private boolean isOpen = false;

    public DashBoardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        barChart = view.findViewById(R.id.barChart);
        income_set_result = view.findViewById(R.id.income_set_result);
        expense_set_result = view.findViewById(R.id.expense_set_result);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        incomeRef = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid);
        expenseRef = FirebaseDatabase.getInstance().getReference("ExpenseDatabase").child(uid);

        loadIncomeData();

        incomeRef.keepSynced(true);
        expenseRef.keepSynced(true);

        fab_main_btn = view.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = view.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = view.findViewById(R.id.expense_Ft_btn);

        fab_income_txt=view.findViewById(R.id.income_ft_text);
        fab_expense_txt=view.findViewById(R.id.expense_ft_text);


        mRecyclerIncome=view.findViewById(R.id.recycler_income);
        mRecyclerExpense=view.findViewById(R.id.recycler_expense);

        FadOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addData();

                if(isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                }else{
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }

        });
        //Caculate total income
        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();
                    String strResult = String.valueOf(totalsum);
                    income_set_result.setText(strResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    totalExpense+=data.getAmount();
                    String strResult = String.valueOf(totalExpense);
                    expense_set_result.setText(strResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return view;
    }
    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        }else{
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }

    }
    private void addData() {
        //Fab Button income

        fab_income_btn.setOnClickListener(new View.OnClickListener () {;
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }
    public void  incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmount = myview.findViewById(R.id.amount_edit);
        EditText edtType = myview.findViewById(R.id.type_edit);
        EditText edtNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String amount = edtAmount.getText().toString().trim();
                String type = edtType.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Amount is Required");
                    return;
                }
                if(TextUtils.isEmpty(type)){
                    edtType.setError("Type is Required");
                    return;
                }
                int ouramount = Integer.parseInt(amount);

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Note is Required");
                    return;
                }
                String id = incomeRef.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ouramount, type,id, note, mDate);
                incomeRef.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                ftAnimation();

                //Cập nhật ví
                updateWalletBalance(+ouramount);

                dialog.dismiss();


            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener(){;
            @Override
            public void onClick(View view){
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText amount = myview.findViewById(R.id.amount_edit);
        EditText type = myview.findViewById(R.id.type_edit);
        EditText note = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmount)){
                    amount.setError("Amount is Required");
                    return;
                }
                if(TextUtils.isEmpty(tmType)){
                    type.setError("Type is Required");
                    return;
                }
                int inamount = Integer.parseInt(tmAmount);

                if(TextUtils.isEmpty(tmNote)){
                    note.setError("Note is Required");
                    return;
                }


                String id = expenseRef.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(inamount, tmType, id, tmNote, mDate);
                expenseRef.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();

                ftAnimation();

                //Cập nhật ví
                // Cập nhật ví
                updateWalletBalance(-inamount);

// Cập nhật ngân sách
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
                DatabaseReference budgetRef = FirebaseDatabase.getInstance()
                        .getReference("budgets")
                        .child(uid)
                        .child(currentMonth);

                budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Long budgetAmount = snapshot.child("amount").getValue(Long.class);
                            Long spent = snapshot.child("spent").getValue(Long.class);

                            long currentSpent = (spent != null) ? spent : 0;
                            long newSpent = currentSpent + inamount;

                            budgetRef.child("spent").setValue(newSpent);

                            if (budgetAmount != null && newSpent > budgetAmount) {
                                Toast.makeText(getActivity(), "⚠️ Bạn đã vượt quá ngân sách tháng này!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Log lỗi nếu cần
                    }
                });

                dialog.dismiss();
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){


                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,IncomeViewHolder> incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(incomeRef, Data.class)
                        .build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(expenseRef, Data.class)
                        .build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setIncomeType(String type){
            TextView mtype=mView.findViewById(R.id.type_income_ds);
            mtype.setText(type);
        }
        public void setIncomeAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_income_ds);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }
        public void setIncomeDate(String date){
            TextView mDate=mView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View myViewExpense;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            myViewExpense = itemView;
        }
        public void setExpenseType(String type){
            TextView mType=myViewExpense.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }
        public void setExpenseAmount(int amount){
            TextView mAmount=myViewExpense.findViewById(R.id.amount_expense_ds);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }
        public void setExpenseDate(String date){
            TextView mDate=myViewExpense.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }


    private void loadIncomeData() {
        totalIncome = 0;

        incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Long amount = item.child("amount").getValue(Long.class);
                    if (amount != null) {
                        totalIncome += amount;
                    }
                }
                loadExpenseData(); // gọi tiếp khi đã xong income
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadExpenseData() {
        totalExpense = 0;

        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Long amount = item.child("amount").getValue(Long.class);
                    if (amount != null) {
                        totalExpense += amount;
                    }
                }

                income_set_result.setText(String.format("%.0f", totalIncome));
                expense_set_result.setText(String.format("%.0f", totalExpense));
                updateBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, totalIncome));
        entries.add(new BarEntry(1f, totalExpense));

        BarDataSet dataSet = new BarDataSet(entries, "Thu vs Chi");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (Float.compare(value, 0f) == 0) {
                    return "Thu";
                } else if (Float.compare(value, 1f) == 0) {
                    return "Chi";
                } else {
                    return "";
                }
            }
        });

        barChart.invalidate();
    }

     //Hàm cập nhật số dư ví
    private void updateWalletBalance(int change) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference("wallets").child(uid).child("wallet_balance");

        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Float currentBalance = snapshot.getValue(Float.class);
                if (currentBalance != null) {
                    float newBalance = currentBalance + change;
                    walletRef.setValue(newBalance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }


}
