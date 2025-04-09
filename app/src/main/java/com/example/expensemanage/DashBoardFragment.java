package com.example.expensemanage;

import android.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashBoardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating Action Button

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolen
    private  boolean isOpen = false;

    //Animation

    private Animation FadOpen, FadeClose;

    //DashBoard income and expense result..

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase...
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;



    public DashBoardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //Connecting Floating Action Button
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        //Connecting TextView

        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result
        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        //Recycler
        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        //Animation connection

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
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();
                    String strResult = String.valueOf(totalsum);
                    totalIncomeResult.setText(strResult+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Caculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    totalExpense+=data.getAmount();
                    String strResult = String.valueOf(totalExpense);
                    totalExpenseResult.setText(strResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //RecyclerView
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

        return myview;


    }

    //Floating Action Button

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
                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ouramount, type,id, note, mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                ftAnimation();
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


                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(inamount, tmType, id, tmNote, mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();


                ftAnimation();
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
                        .setQuery(mIncomeDatabase, Data.class)
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
                        .setQuery(mExpenseDatabase, Data.class)
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

    //For Income Data
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

    //For Expense Data
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
}