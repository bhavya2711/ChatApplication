package com.example.inclass01;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class UserFragment extends Fragment {


    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    StorageReference storageRef;
    Bitmap bitmapToSend = null;
    ArrayList<User> listOfUser;
    User userValue;
    ArrayAdapter<User> adapter;
    ListView listView;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_user,container,false);


        listOfUser = new ArrayList<>();
         listView= view.findViewById(R.id.listViewID);


        mAuth = FirebaseAuth.getInstance();

        currentUser =mAuth.getCurrentUser();

//        Log.d("TAH", currentUser.getUid());

        myRef = FirebaseDatabase.getInstance().getReference("users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                        userValue = child.getValue(User.class);
                        listOfUser.add(userValue);
                        String name = userValue.first + " " + userValue.last;

//                        Log.d("demoM", "User is: " + name);

                    }

                adapter = new UserAdapter(view.getContext(), R.layout.user_item,listOfUser);

                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);

                    }
                });


                adapter.notifyDataSetChanged();

                listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        User userClicked = listOfUser.get(position);
                        Intent intent = new Intent(view.getContext(), UserProfile.class);
                        intent.putExtra("intent_user_id",userClicked.uID);
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("demo", "Failed to read value.", error.toException());
            }

        });

        mDatabase = FirebaseDatabase.getInstance();



        return view;
    }
}
