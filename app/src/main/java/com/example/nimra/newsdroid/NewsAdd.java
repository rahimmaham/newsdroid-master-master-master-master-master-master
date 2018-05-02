package com.example.nimra.newsdroid;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewsAdd extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText description;
    private EditText title;
    private EditText time;
    private static int PICK_IMAGE = 100;
    private ImageView newsimg;
    private Uri mImageUri;
    private Button add;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            newsimg.setImageURI(mImageUri);

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,login.class));
        }

        newsimg = (ImageView) findViewById(R.id.newsimg);
        newsimg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        description = (EditText) findViewById(R.id.description);
        title = (EditText)  findViewById(R.id.title);
        time = (EditText) findViewById(R.id.time);
        newsimg = findViewById(R.id.newsimg);
        add=findViewById(R.id.add);
        progressDialog = new ProgressDialog(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

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
            finishAffinity();
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
            Intent i = new Intent (NewsAdd.this,UserNewsFeed.class);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent (NewsAdd.this,MyProfile.class);
            startActivity(i);
        } else if (id == R.id.nav_adds) {
            Intent i = new Intent (NewsAdd.this,NewsAdd.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
       final String des = description.getText().toString().trim();
       final String ti =  time.getText().toString().trim();
       final String tit = title.getText().toString().trim();



        if(TextUtils.isEmpty(tit)){
            //password is empty
            title.setError("Please Enter Tittle");
            // Toast.makeText(this, "Please Enter Password", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(des)){
            //email is empty
            description.setError("Please Enter Description");
            //Toast.makeText(this, "Please Enter Correct Email ID", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(ti)){
            //password is empty
            time.setError("Please Enter Time");
            //Toast.makeText(this, "Please Enter City", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        if(mImageUri != null){

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));


            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            Toast.makeText(NewsAdd.this, "News Added", Toast.LENGTH_SHORT).show();


                            newsUpload news = new newsUpload(tit,des,ti,taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child("news").child(uploadId).setValue(news);
                            finish();
                            final Intent i = new Intent(getApplicationContext(), UserNewsFeed.class);
                            startActivity(i);

                            progressDialog.dismiss();




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewsAdd.this, e.getMessage(), Toast.LENGTH_LONG).show();



                        }
                    });
        }
        else{
            Toast.makeText(this,"Please Select Any Image ", Toast.LENGTH_LONG).show();
        }

    }


}
