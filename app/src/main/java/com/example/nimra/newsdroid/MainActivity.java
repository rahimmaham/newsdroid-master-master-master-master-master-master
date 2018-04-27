package com.example.nimra.newsdroid;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static int PICK_IMAGE = 100;
    ImageView imageView;

    private Button button;
    private EditText email;
    private  EditText password;
    private  EditText city;
    private  EditText name;
    private  EditText phonenumber;
    private TextView signin;

    private Uri mImageUri;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageURI(mImageUri);

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button buttonLoadImage = (Button) findViewById(R.id.addimage);
        imageView=(ImageView)findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);
            }
        });
        TextView text=(TextView)findViewById(R.id.textView5);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);
            }
        });


        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MyProfile.class));

        }

        progressDialog = new ProgressDialog(this);

        button = (Button) findViewById(R.id.register);

        email = (EditText) findViewById(R.id.email);

        password = (EditText) findViewById(R.id.password);

        phonenumber = (EditText) findViewById(R.id.phonenumber);

        city = (EditText) findViewById(R.id.city);

        name = (EditText) findViewById(R.id.name);

        signin = (TextView) findViewById(R.id.signin);

        button.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    private void registerUser(){
        String emailid = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        final String username = name.getText().toString().trim();
        final String usercity = city.getText().toString().trim();
        final String userphonenumber = phonenumber.getText().toString().trim();


        if(TextUtils.isEmpty(emailid)){
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

        if(TextUtils.isEmpty(username)){
            //password is empty
            Toast.makeText(this, "Please Enter User Name", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(usercity)){
            //password is empty
            Toast.makeText(this, "Please Enter City", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(userphonenumber)){
            //password is empty
            Toast.makeText(this, "Please Enter Phone Number", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }



        progressDialog.setMessage("Registering User...");
        progressDialog.show();






        firebaseAuth.createUserWithEmailAndPassword(emailid,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            userInformation userInfo = new userInformation(username, usercity, userphonenumber);
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            databaseReference.child(user.getUid()).setValue(userInfo);


                        }
                        else{
                            Toast.makeText(MainActivity.this, "Registered not Successfull", Toast.LENGTH_LONG).show();
                        }
                    }


                });
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
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                           String userID = user.getUid();
                            Toast.makeText(MainActivity.this, "pic upload", Toast.LENGTH_SHORT).show();



                            Upload upload = new Upload(taskSnapshot.getDownloadUrl().toString());
                            databaseReference.child("img").child(userID).setValue(upload);
                            finish();
                            final Intent i = new Intent(getApplicationContext(), MyProfile.class);
                            startActivity(i);
                            progressDialog.dismiss();





                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();



                        }
                    });
        }else{
            Toast.makeText(this,"No Image Selected", Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onClick(View view) {
        if(view == button){
            uploadFile();
            registerUser();



        }

        if(view == signin){
            startActivity(new Intent(this, login.class));
        }

    }


}
