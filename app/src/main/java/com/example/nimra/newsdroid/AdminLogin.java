package com.example.nimra.newsdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        final EditText userid=(EditText)findViewById(R.id.adminid);
        final EditText pass=(EditText)findViewById(R.id.adminpas);
        Button login=(Button)findViewById(R.id.button2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid=userid.getText().toString().trim();
                String pas= pass.getText().toString().trim();
                if(uid!="admin" && pas!="admin1234" )
                {
                    Toast.makeText(AdminLogin.this, "Plaese Enter Correct ID or Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent i=new Intent(AdminLogin.this,AdminNewsFeed.class);
                    startActivity(i);
                }
            }
        });

    }
}
