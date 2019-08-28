package com.example.inclass01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoomAdapter extends ArrayAdapter<Room> {

    List<Room> roomList;
//    String userId;

    Context mContext;private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    DatabaseReference myref;
    List<User> niceRoomUserList;




    public RoomAdapter(@NonNull Context context, int resource, @NonNull List<Room> mylist) {
        super(context, resource, mylist);
        this.roomList = mylist;
        this.mContext=context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Room room = getItem(position);
//        Log.d("demo", "Adapter user is: " + user.first);
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.room_item, parent, false);

        }

        TextView textViewRoomName = convertView.findViewById(R.id.textViewRoomNameID);
        final Button buttonJoin = convertView.findViewById(R.id.buttonJoinID);

        textViewRoomName.setText(room.name);


        mAuth = FirebaseAuth.getInstance();
        currentUser =mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("rooms").child(room.roomID);

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonJoin.setText("Joined");
            }
        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Room niceRoom= dataSnapshot.getValue(Room.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return convertView;
    }
}
