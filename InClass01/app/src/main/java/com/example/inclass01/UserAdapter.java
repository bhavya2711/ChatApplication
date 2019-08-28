package com.example.inclass01;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserAdapter extends ArrayAdapter<User> {
    List<User> userList;
//    String userId;

    Context mContext;
    DatabaseReference myref;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> mylist) {
        super(context, resource,mylist);
        this.userList=mylist;
        this.mContext=context;
//        this.userId=id;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final User user = getItem(position);
//        Log.d("demo", "Adapter user is: " + user.first);
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        }

            TextView textViewName = convertView.findViewById(R.id.textViewNameID);
            TextView textViewGender = convertView.findViewById(R.id.textViewGenderID);
            TextView textViewCity = convertView.findViewById(R.id.textViewCityID);
            ImageView imageProfile = convertView.findViewById(R.id.imageViewProfileID);

            if (user.imageURL != null) {
                Picasso.get()
                        .load(user.imageURL)
                        .into(imageProfile);
            }

            String name = user.first + " " + user.last;

            textViewName.setText(name);
            textViewGender.setText(user.gender);
            textViewCity.setText(user.city);


        return convertView;
    }



}
