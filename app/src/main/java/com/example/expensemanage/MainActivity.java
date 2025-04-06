package com.example.expensemanage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignup;
    private ProgressDialog mDialog;

    //Firebase....

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }

        mDialog=new ProgressDialog(this);

        loginDetails();
    }
    private void loginDetails(){
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forget_password);
        mSignup = findViewById(R.id.signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email required");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPass.setError("Password Required");

                }
                mDialog.setMessage("Processing..");
                mDialog.show();


                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(),"Login Sucessful",Toast.LENGTH_SHORT).show();
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        //Registration Activity

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
                finish();
            }
        });
        //Reset password activity

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ResetActivity.class));
                finish();
            }
        });
    }
}