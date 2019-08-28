package com.example.inclass01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";


    private EditText firstName, lastName, email, password, confirmPassword, city;
    private Button register;
    private Spinner myspinner;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    DatabaseReference myref;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    StorageReference storageRef;
    Bitmap bitmapToSend = null;
    private static final int REQUEST_IMAGE_SELECTOR = 1;

    String getUrl=null;
    User user = new User();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailID);
        password = findViewById(R.id.passwordID);
        confirmPassword = findViewById(R.id.confirmPasswordID);
        city = findViewById(R.id.cityID);
        register = findViewById(R.id.registerID);
        imageView = findViewById(R.id.imageView);
        myspinner = findViewById(R.id.spinnerID);
        register = findViewById(R.id.registerID);

        String[] arraySpinner = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(adapter);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


//                image = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR);


            }

        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////

                Log.d("THE", "I'm here");
                final String strfirst = firstName.getText().toString();
                final String strlast = lastName.getText().toString();
                final String stremail = email.getText().toString();
                final String strpassword = password.getText().toString();
                String strconfirm = confirmPassword.getText().toString();
                final String strcity = city.getText().toString();
                final String gender = myspinner.getSelectedItem().toString();


                if (stremail.isEmpty() || strpassword.isEmpty() || strconfirm.isEmpty() || strfirst.isEmpty() || strlast.isEmpty() || strcity.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all the details", Toast.LENGTH_LONG).show();
                } else if (strpassword.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password Length", Toast.LENGTH_LONG).show();
                } else if (!(strpassword.equals(strconfirm))) {
                    Toast.makeText(SignupActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                }
                else if(bitmapToSend == null){
                    Toast.makeText(SignupActivity.this, "Please attach your profile photo", Toast.LENGTH_SHORT).show();
                }
                        else {

                    Log.d("THE", "Well inside else");
                    // [START create_user_with_email]
                    mAuth.createUserWithEmailAndPassword(stremail, strpassword)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("THE", "I'm here llala");

                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser fUser = mAuth.getCurrentUser();
                                        if (fUser != null) {
                                            uploadImage(bitmapToSend, mAuth.getCurrentUser().getUid());



                                            String useruid = fUser.getUid();

                                            user.uID = useruid;
                                            user.first = strfirst;
                                            user.last = strlast;
                                            user.email = stremail;
                                            user.password = strpassword;
                                            user.city = strcity;
                                            user.gender = gender;


                                        } else {
                                            Log.d("THE", "I'm here lolo");

                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });


                }
                ////
            }
        });
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_SELECTOR && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmapToSend = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                flag = true;
                //Drawable d = new BitmapDrawable(getResources(), bitmap);
                imageView.setImageBitmap(bitmapToSend);


            } catch (IOException e) {
                e.printStackTrace();
            }
//            Picasso.with(SignupActivity.this).load(uri).noPlaceholder().centerCrop().fit()
//                            .into((ImageView) findViewById(R.id.imageView));

        }
    }


    private void signup(String email, String password) {


    }



    private void uploadImage(Bitmap bitmap, String UUID) {
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(UUID + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = ref.putBytes(data);



        Log.d("THE","Uploading image");
/*
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

            }
        });
        */


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                     getUrl = downloadUri.toString();

                    user.imageURL = getUrl;




                    mDatabase.getReference("users").child(user.uID).setValue(user);
                    Intent intent = new Intent(SignupActivity.this, ChatRoom.class);
                    startActivity(intent);


                    //Make The bitmap null again
                    bitmapToSend = null;

                } else {
                    // Handle failures
                    // ...
                }
            }
        });



    }

}
