package com.example.myapplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;

    private String uid;

    public User(String firstName, String lastName, String emailAddress, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.uid = "";
    }

    /**
     * p0 -> fname, p1 -> lname ... p5 -> addr
     * ASSUMES P4 -> <BIRTHYEAR IS OF TYPE INT> IF <TYPE = 0>
     * */
    public static User UserBuilder(int type, String p0, String p1, String p2, String p3, String p4, String p5) {
        try {
            if (type == 0) {
                return new UserClient(p0, p1, p2, p3, Integer.parseInt(p4));
            }
            if (type == 1) {
                return new UserLandlord(p0, p1, p2, p3, p5);
            }
            if (type == 2) {
                return new UserManager(p0, p1, p2, p3);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }


    // to be overridden
    public void UserToFirebaseDb(DatabaseReference db, String uid){}

    private static int typeStringToInt(String s){
        switch(s){
            case("Client"):
                return 0;
            case("Landlord"):
                return 1;
            case("Property Manager"):
                return 2;
        }
        return -1;
    }


    /**
     *  UserBuilder but with firebase datasnapshot
     * */
    public static User FromFirebaseData(DataSnapshot dataSnapshot){
        HashMap<String, String> hash = new HashMap<>();
        ArrayList<String> pIdList = new ArrayList<>();

        for(DataSnapshot child : dataSnapshot.getChildren()){
            if((child.getKey()).equals("propertyIdList")){
                for(DataSnapshot propertyID : child.getChildren()){
                    pIdList.add(""+propertyID.getValue());
                }
                continue;
            }
            hash.put(child.getKey(), ""+child.getValue());
        }

        String type = hash.get("type");
        String fname = hash.get("firstName");
        String lname = hash.get("lastName");
        String email = hash.get("emailAddress");
        String birthYear = hash.get("birthYear");
        String address = hash.get("address");

        switch(type){
            case "Client":
                return new UserClient(dataSnapshot);
            case "Landlord":
                return new UserLandlord(dataSnapshot);
            case "Property Manager":
                return new UserManager(dataSnapshot);
        }

//        try{


        return User.UserBuilder(User.typeStringToInt(hash.get("type")),
                hash.get("firstName"), hash.get("lastName"),
                hash.get("emailAddress"), "",
                hash.get("birthYear"), hash.get("address"));
    }

    public String getType(){return "";}

    public String getFirstName() {
        return firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getUID() {
        return uid;
    }
    public void setUID(String uid) { this.uid = uid; }

    // to be overriden
    public User copy(){
        return null;
    }
}
