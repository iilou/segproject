package com.example.myapplication;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class FirebaseDatabase {

    // Static method to convert DataSnapshot to HashMap
    public static HashMap<String, String> snapshotToHashMap(DataSnapshot dataSnapshot) {
        HashMap<String, String> result = new HashMap<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            result.put(snapshot.getKey(), snapshot.getValue(String.class));
        }
        return result;
    }
}
