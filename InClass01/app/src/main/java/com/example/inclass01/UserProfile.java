package com.example.inclass01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class UserProfile extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    User userValue;
    TextView firstTextView, lastTextView, genderTextView, cityTextView, emailTextView;
    ImageView imageView;
    Button buttonBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firstTextView = findViewById(R.id.firstEditIDD);
        lastTextView = findViewById(R.id.lastEditIDD);
        genderTextView = findViewById(R.id.textViewGenderIDD);
        cityTextView = findViewById(R.id.cityEditIDD);
        imageView = findViewById(R.id.imageViewIDD);
        buttonBack = findViewById(R.id.buttonBackIDD);
        emailTextView = findViewById(R.id.textViewEmailIDD);


        Bundle extras = getIntent().getExtras();
        String user_id = extras.getString("intent_user_id");


        mAuth = FirebaseAuth.getInstance();

        currentUser =mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference("users").child(user_id);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    userValue = dataSnapshot.getValue(User.class);
                    String name = userValue.first + " " + userValue.last;

                    firstTextView.setText(userValue.first);
                    lastTextView.setText(userValue.last);
                    genderTextView.setText(userValue.gender);
                    cityTextView.setText(userValue.city);
                    emailTextView.setText(userValue.email);

                    Picasso.get()
                            .load(userValue.imageURL)
                            .into(imageView);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("demo", "Failed to read value.", error.toException());
            }

        });


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
