package com.example.inclass01;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {

    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    Context mContext;
    View view;
    ListView listView;
    ArrayList<Room> roomList;
    Room room;
    ArrayAdapter<Room> adapter;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_chat,container,false);

        listView= view.findViewById(R.id.listViewRoomID);
        roomList= new ArrayList<>();
        Button button = view.findViewById(R.id.buttonAddRoom);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                

            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUser =mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("rooms");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    room = child.getValue(Room.class);
                    roomList.add(room);
                }


                adapter = new RoomAdapter(view.getContext(), R.layout.room_item,roomList);

                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);

                    }
                });


                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        return view;
    }
}
