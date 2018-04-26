package com.example.nimra.newsdroid;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class userprofile extends AppCompatActivity implements View.OnClickListener{



    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private TextView uname;
    private TextView allnews;
    private TextView ucity;
    private TextView uphone;
    private Button logout;
    private DatabaseReference mDatabase;
    private String userID;
    private ProgressDialog progressDialog;
    private ImageView userimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);




        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mfirebaseDatabase.getReference();


        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, login.class));
        }

        FirebaseUser user  = firebaseAuth.getCurrentUser();
        assert user != null;
        userID = user.getUid();


        logout = (Button) findViewById(R.id.logout);

        userimg= (ImageView)findViewById(R.id.userimage);
        uname=(TextView)findViewById(R.id.username);
        allnews=(TextView)findViewById(R.id.upload);
        TextView uemail = (TextView) findViewById(R.id.useremail);
        ucity=(TextView)findViewById(R.id.usercity);
        uphone=(TextView)findViewById(R.id.userphone);

        progressDialog.setMessage("Loading...");
        progressDialog.show();

       mDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for(DataSnapshot ds : dataSnapshot.getChildren()){
                   userInfo uInfo = new userInfo();
                   uInfo.setUsername(ds.child(userID).getValue(userInfo.class).getUsername());
                   uInfo.setUsercity(ds.child(userID).getValue(userInfo.class).getUsercity());
                   uInfo.setUserphonenumber(ds.child(userID).getValue(userInfo.class).getUserphonenumber());
                   uname.setText("Name: " + uInfo.getUsername());
                   ucity.setText("City: "+uInfo.getUsercity());
                   uphone.setText("Phone: "+uInfo.getUserphonenumber());

                   Upload up = new Upload();
                   up.setmImageUrl(ds.child("img").child(userID).getValue(Upload.class).getmImageUrl());

                   Picasso.with(userprofile.this).load(up.getmImageUrl().toString()).into(userimg);
                   progressDialog.dismiss();


               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });






        uemail.setText("Email: " + user.getEmail());


        logout.setOnClickListener(this);
        allnews.setOnClickListener(this);

    }

    private void openImagesActivity(){

        Intent i = new Intent(this, addnews.class);
        startActivity(i);

    }




    @Override
    public void onClick(View view) {

        if(view == logout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, login.class));
        }

        if(view == allnews){
            finish();
            openImagesActivity();


        }



    }
}
