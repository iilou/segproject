package com.example.myapplication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;


public class UserLandlord extends User{

    FirebaseDatabase db;
    DatabaseReference ref;

    private String address;

    // only need to store ids, nothing else
    private ArrayList<String> propertyIdList;
    public ArrayList<String> getPropertyIdList() {return propertyIdList;}
    public void setPropertyIdList(ArrayList<String> arr) {this.propertyIdList = arr;}
    public void addPropertyId(String uid){this.propertyIdList.add(uid);}

    private ArrayList<String> managerRequestIdList;
    public ArrayList<String> getManagerRequestIdList() {return managerRequestIdList;}
    public void setManagerRequestIdList(ArrayList<String> arr) {this.managerRequestIdList = arr;}
    public void addManagerRequestId(String uid){this.managerRequestIdList.add(uid);}

    private ArrayList<String> landlordRequestIdList;
    public ArrayList<String> getLandlordRequestIdList() {return landlordRequestIdList;}
    public void setLandlordRequestIdList(ArrayList<String> arr) {this.landlordRequestIdList = arr;}
    public void addLandlordRequestId(String uid) {this.landlordRequestIdList.add(uid);}


    public UserLandlord(String firstName, String lastName, String emailAddress, String password, String address){
        super(firstName, lastName, emailAddress, password);
        this.address = address;
        this.propertyIdList = new ArrayList<>();
        this.managerRequestIdList = new ArrayList<>();
        this.landlordRequestIdList = new ArrayList<>();

    }
    public UserLandlord(String firstName, String lastName, String emailAddress, String password, String address, ArrayList<String> propertyIdList, ArrayList<String> managerRequestIdList, ArrayList<String> landlordRequestIdList){
        super(firstName, lastName, emailAddress, password);
        this.address = address;
        this.propertyIdList = propertyIdList;
        this.managerRequestIdList = managerRequestIdList;
        this.landlordRequestIdList = landlordRequestIdList;
    }

    public UserLandlord(DataSnapshot ds){
        super(""+ds.child("firstName").getValue(), ""+ds.child("lastName").getValue(), ""+ds.child("emailAddress").getValue(), "");
        this.address = ""+ds.child("address").getValue();
        this.setUID(ds.getKey());

        this.propertyIdList = new ArrayList<>();
        if(ds.hasChild("propertyIdList")){
            for(DataSnapshot ds2 : ds.child("propertyIdList").getChildren()){
                propertyIdList.add(ds2.getKey());
            }
        }

        this.managerRequestIdList = new ArrayList<>();
        if(ds.hasChild("managerRequestIdList")){
            for(DataSnapshot ds2 : ds.child("managerRequestIdList").getChildren()){
                managerRequestIdList.add(ds2.getKey());
            }
        }
        this.landlordRequestIdList = new ArrayList<>();
        if(ds.hasChild("landlordRequestIdList")){
            for(DataSnapshot ds2 : ds.child("landlordRequestIdList").getChildren()){
                landlordRequestIdList.add(ds2.getKey());
            }
        }

    }




    @Override
    public void UserToFirebaseDb(DatabaseReference db, String uid) {
        db.child("users").child(uid).child("firstName").setValue(this.getFirstName());
        db.child("users").child(uid).child("lastName").setValue(this.getLastName());
        db.child("users").child(uid).child("emailAddress").setValue(this.getEmailAddress());
        db.child("users").child(uid).child("address").setValue(address);
        db.child("users").child(uid).child("type").setValue("Landlord");

        for(int i = 0; i < propertyIdList.size(); i++){
            db.child("users").child(uid).child("propertyIdList").child(propertyIdList.get(i)).setValue(propertyIdList.get(i));
        }

        for(int i = 0; i < managerRequestIdList.size(); i++){
            db.child("users").child(uid).child("managerRequestIdList").child(managerRequestIdList.get(i)).setValue(managerRequestIdList.get(i));
        }

        for(int i = 0; i < landlordRequestIdList.size(); i++){
            db.child("users").child(uid).child("landlordRequestIdList").child(landlordRequestIdList.get(i)).setValue(landlordRequestIdList.get(i));
        }

        db.child("Landlord").child(uid).setValue(uid);


        this.setUID(uid);
    }


    @Override
    public String getType() {
        return "Landlord";
    }

    public String getAddress() {
        return address;
    }

    @Override
    public User copy() {
        User u = new UserLandlord(this.getFirstName(), this.getLastName(), this.getEmailAddress(), this.getPassword(), this.address, new ArrayList<>(this.propertyIdList), new ArrayList<>(this.managerRequestIdList), new ArrayList<>(this.landlordRequestIdList));
        u.setUID(this.getUID());
        return u;
    }
}
