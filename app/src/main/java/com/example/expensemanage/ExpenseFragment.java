package com.example.expensemanage;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expensemanage.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview
    private RecyclerView recyclerView;

    //TextView
    private TextView expenseSumResult;

    //Edit data item
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    //Data variable
    private String type;
    private String note;
    private int amount;
    private String post_key;


    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid= mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        expenseSumResult= myview.findViewById(R.id.expense_txt_result);



        recyclerView= myview.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int expenseSum = 0;
                for (DataSnapshot mySnapshot : snapshot.getChildren()){
                    Data data = mySnapshot.getValue(Data.class);

                    expenseSum += data.getAmount();
                    String strExpensesum = String.valueOf(expenseSum);
                    expenseSumResult.setText(strExpensesum+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());

                int currentposition = holder.getAdapterPosition();

                holder.myview.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        post_key = getRef(currentposition).getKey();
                        type= model.getType();
                        note= model.getNote();
                        amount= model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private static class MyViewHolder extends RecyclerView.ViewHolder{
        View myview;
        public MyViewHolder(View itemView) {
            super(itemView);
            myview=itemView;
        }
        private void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setType(String type){
            TextView mType=myview.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote=myview.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private void setAmount(int amount){
            TextView mAmount=myview.findViewById(R.id.amount_txt_expense);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }
    }

    private void updateDataItem() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View mView = inflater.inflate(R.layout.update_data_item, null);

        mydialog.setView(mView);

        edtAmount = mView.findViewById(R.id.amount_edit);
        edtType = mView.findViewById(R.id.type_edit);
        edtNote = mView.findViewById(R.id.note_edit);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate=mView.findViewById(R.id.btn_Update);
        btnDelete=mView.findViewById(R.id.btn_Delete);

        AlertDialog dialog=mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type= edtType.getText().toString().trim();
                note= edtNote.getText().toString().trim();
                String stAmount= String.valueOf(amount);
                stAmount=edtAmount.getText().toString().trim();

                int intAmount= Integer.parseInt(stAmount);

                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intAmount, type,post_key,note, mDate);

                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });
        dialog.show();
    }
}