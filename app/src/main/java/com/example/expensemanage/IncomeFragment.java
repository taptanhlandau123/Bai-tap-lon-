package com.example.expensemanage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensemanage.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class IncomeFragment extends Fragment {

    //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    //Recyclerview
    private RecyclerView recyclerView;


    public IncomeFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myview = inflater.inflate(R.layout.fragment_income, container, false);
        mAuth= FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid= mUser.getUid();
        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        recyclerView=myview.findViewById(R.id.recycler_id_income);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getActivity());
        LayoutManager.setReverseLayout(true);
        LayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(LayoutManager);
        return myview;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNot(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private void setNot(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private void setAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_income);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }
    }
}