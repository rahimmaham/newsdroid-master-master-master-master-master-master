package com.example.nimra.newsdroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private TextView uname;
    private TextView ucity;
    private TextView uphone;
    private DatabaseReference mDatabase;
    private String userID;
    private ProgressDialog progressDialog;
    private ImageView userimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        userimg= (ImageView)findViewById(R.id.userimage);
        uname=(TextView)findViewById(R.id.username);

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
                    uname.setText(uInfo.getUsername());
                    ucity.setText(uInfo.getUsercity());
                    uphone.setText(uInfo.getUserphonenumber());

                    Upload up = new Upload();
                    up.setmImageUrl(ds.child("img").child(userID).getValue(Upload.class).getmImageUrl());

                    Picasso.with(MyProfile.this).load(up.getmImageUrl().toString()).into(userimg);
                    progressDialog.dismiss();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        uemail.setText(user.getEmail());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_news_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,login.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            Intent i = new Intent (MyProfile.this,UserNewsFeed.class);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent (MyProfile.this,MyProfile.class);
            startActivity(i);
        } else if (id == R.id.nav_adds) {
            Intent i = new Intent (MyProfile.this,NewsAdd.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
