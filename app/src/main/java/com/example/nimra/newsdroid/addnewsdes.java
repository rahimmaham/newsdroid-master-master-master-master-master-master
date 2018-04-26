package com.example.nimra.newsdroid;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class addnewsdes extends AppCompatActivity implements View.OnClickListener{

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
        setContentView(R.layout.activity_addnewsdes);

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

        add.setOnClickListener(addnewsdes.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){

        if(mImageUri != null){

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));


            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            Toast.makeText(addnewsdes.this, "News Added", Toast.LENGTH_SHORT).show();


                            String des = description.getText().toString().trim();
                            String ti =  time.getText().toString().trim();
                            String tit = title.getText().toString().trim();
                            newsUpload news = new newsUpload(tit,des,ti,taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child("news").child(uploadId).setValue(news);
                            finish();
                            final Intent i = new Intent(getApplicationContext(), addnews.class);
                            startActivity(i);

                            progressDialog.dismiss();




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addnewsdes.this, e.getMessage(), Toast.LENGTH_LONG).show();



                        }
                    });
        }else{
            Toast.makeText(this,"No Image Selected", Toast.LENGTH_LONG).show();
        }



    }




    @Override
    public void onClick(View view) {
        if(view == add){
            uploadFile();


        }

    }
}
