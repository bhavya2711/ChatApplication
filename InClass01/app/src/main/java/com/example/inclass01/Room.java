package com.example.inclass01;

import java.util.ArrayList;
import java.util.List;

public class Room {



    public String roomID, name;

    public List<User> list = new ArrayList<>();


    public Room(String roomID, String name, List<User> mylist) {
        this.roomID = roomID;
        this.name = name;
        this.list = mylist;
    }

    public Room()
    {

    }
}
