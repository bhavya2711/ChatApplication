package com.example.inclass01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    TextView textView;
    EditText first, last, city;
    Spinner spinner;
    ImageView imageView;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    StorageReference storageRef;
    Bitmap bitmapToSend = null;

    User userValue;
    Button button;
    User user= new User();
    String getUrl;
    Activity mContext;
    private static final int REQUEST_IMAGE_SELECTOR = 1;


    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=(Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.fragment_profile,container,false);

        textView = view.findViewById(R.id.textViewEmailIDD);
        first = view.findViewById(R.id.firstEditIDD);
        last = view.findViewById(R.id.lastEditIDD);
        city = view.findViewById(R.id.cityEditIDD);
        imageView = view.findViewById(R.id.imageViewIDD);
        button = view.findViewById(R.id.buttonSave);
        spinner = view.findViewById(R.id.spinnerFragmentIDD);
        String[] arraySpinner = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder = new AlertDialog.Builder(mContext);


        builder.setTitle("Loading Profile Information...").setView(inflater.inflate(R.layout.dialog_bar , null));

        dialog = builder.create();
        dialog.show();

        mAuth = FirebaseAuth.getInstance();

        currentUser =mAuth.getCurrentUser();

        Log.d("TAH", currentUser.getUid());

        myRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userValue = dataSnapshot.getValue(User.class);
                Log.d("demoM", "Value is: " + userValue);

                String name = userValue.first + " " + userValue.last;
                Log.d("demoM", "Name is: " + name);
                Log.d("demoM", "URL is: " + userValue.imageURL);

                ;
                textView.setText(userValue.email);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        Picasso.get()
                                .load(userValue.imageURL)
                                .into(imageView);
                        // yourMethod();
                        dialog.dismiss();
                    }
                }, 1000);
                first.setText(userValue.first);
                last.setText(userValue.last);
                city.setText(userValue.city);
                if(userValue.gender.equals("Male"))
                    spinner.setSelection(0);
                else
                    spinner.setSelection(1);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("demo", "Failed to read value.", error.toException());
            }

        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR);


            }
        });


        mDatabase = FirebaseDatabase.getInstance();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("THE", "I'm here");
                final String strfirst = first.getText().toString();
                final String strlast = last.getText().toString();
                final String stremail = textView.getText().toString();

                final String strcity = city.getText().toString();
                final String gender = spinner.getSelectedItem().toString();
                if (stremail.isEmpty() || strfirst.isEmpty() || strlast.isEmpty() || strcity.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else {

                    FirebaseUser fUser = mAuth.getCurrentUser();
                    if (fUser != null) {
                        if(bitmapToSend!=null) {
                            uploadImage(bitmapToSend, mAuth.getCurrentUser().getUid());
                        }


                        String useruid = fUser.getUid();

                        user.uID = useruid;
                        user.first = strfirst;
                        user.last = strlast;
                        user.email = stremail;
                        user.password = userValue.password;
                        user.city = strcity;
                        user.gender = gender;
                        if(bitmapToSend==null)
                        {
                            user.imageURL = userValue.imageURL;
                            mDatabase.getReference("users").child(user.uID).setValue(user);
                            Toast.makeText(mContext, "Successfully updated information.", Toast.LENGTH_SHORT).show();


                        }

                    }
                }
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Context applicationContext = ChatRoom.getContextOfApplication();

            if (requestCode == REQUEST_IMAGE_SELECTOR && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    bitmapToSend = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uri);
//                flag = true;
                    //Drawable d = new BitmapDrawable(getResources(), bitmap);
                    imageView.setImageBitmap(bitmapToSend);


                } catch (IOException e) {
                    e.printStackTrace();
                }
//            Picasso.with(SignupActivity.this).load(uri).noPlaceholder().centerCrop().fit()
//                            .into((ImageView) findViewById(R.id.imageView));

            }


        super.onActivityResult(requestCode, resultCode, data);
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
                    Toast.makeText(mContext, "Successfully updated information.", Toast.LENGTH_SHORT).show();



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
