package com.example.nimra.newsdroid;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener{

    private Button login;
    private EditText email;
    private EditText password;
    private TextView signup;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MyProfile.class));

        }

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.signup);


        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(this);
        signup.setOnClickListener(this);




    }

    private void userLogin(){
        String emailID = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(TextUtils.isEmpty(emailID)){
            //email is empty
            Toast.makeText(this, "Please Enter Email ID", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(pass)){
            //password is empty
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        progressDialog.setMessage("Logging User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailID,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),MyProfile.class));
                        }
                        else {

                            Toast.makeText(login.this, "Invaild Email or Password.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == login){
            userLogin();
        }
        if(view == signup){
            finish();
            startActivity(new Intent(this, MainActivity.class ));
        }
    }
}
