package com.example.myapplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class UserClient extends User{
    private String propertyID;
    private ArrayList<String> landlordRequestIdList;
    private ArrayList<String> ticketIdList;
    private int birthYear;

    public String getPropertyID() {return propertyID;}
    public void setPropertyID(String propertyID) {this.propertyID = propertyID;}
    public ArrayList<String> getLandlordRequestIdList() {return landlordRequestIdList;}
    public void setLandlordRequestIdList(ArrayList<String> landlordRequestIdList) {this.landlordRequestIdList = landlordRequestIdList;}
    public void addLandlordRequestId(String requestId) {this.landlordRequestIdList.add(requestId);}
    public ArrayList<String> getTicketIdList(){return this.ticketIdList;}
    public void setTicketIdList(ArrayList<String> ticketIdList){this.ticketIdList = ticketIdList;}


    public UserClient(String firstName, String lastName, String emailAddress, String password, int birthYear){
        super(firstName, lastName, emailAddress, password);
        this.birthYear = birthYear;
        this.propertyID = "";
        this.landlordRequestIdList = new ArrayList<>();
        this.ticketIdList = new ArrayList<>();
    }

    public UserClient(String firstName, String lastName, String emailAddress, String password, int birthYear, String propertyID, ArrayList<String> landlordRequestIdList, ArrayList<String> ticketIdList){
        super(firstName, lastName, emailAddress, password);
        this.birthYear = birthYear;
        this.propertyID = propertyID;
        this.landlordRequestIdList = landlordRequestIdList;
        this.ticketIdList = ticketIdList;
    }

    public UserClient (DataSnapshot ds){
        super(""+ds.child("firstName").getValue(), ""+ds.child("lastName").getValue(), ""+ds.child("emailAddress").getValue(), "");
        this.birthYear = Integer.parseInt(""+ds.child("birthYear").getValue());
        this.propertyID = (ds.hasChild("propertyId")?(""+ds.child("propertyId").getValue()):"");
        this.setUID(ds.getKey());

        this.landlordRequestIdList = new ArrayList<>();
        if(ds.hasChild("landlordRequestIdList")){
            for(DataSnapshot ds2 : ds.child("landlordRequestIdList").getChildren()){
                landlordRequestIdList.add(ds2.getKey());
            }
        }

        this.ticketIdList = new ArrayList<>();
        if(ds.hasChild("ticketIdList")){
            for(DataSnapshot ds2 : ds.child("ticketIdList").getChildren()){
                ticketIdList.add(ds2.getKey());
            }
        }
    }
    @Override
    public void UserToFirebaseDb(DatabaseReference db, String uid) {
        db.child("users").child(uid).child("firstName").setValue(this.getFirstName());
        db.child("users").child(uid).child("lastName").setValue(this.getLastName());
        db.child("users").child(uid).child("emailAddress").setValue(this.getEmailAddress());
        db.child("users").child(uid).child("birthYear").setValue(birthYear);

        db.child("users").child(uid).child("type").setValue("Client");

        db.child("users").child(uid).child("propertyId").setValue(this.propertyID);

        for(int i = 0; i < landlordRequestIdList.size(); i++){
            db.child("users").child(uid).child("landlordRequestIdList").child(landlordRequestIdList.get(i)).setValue(landlordRequestIdList.get(i));
        }

        for(int i = 0; i < ticketIdList.size(); i++){
            db.child("users").child(uid).child("ticketIdList").child(landlordRequestIdList.get(i)).setValue(landlordRequestIdList.get(i));
        }

        db.child("Client").child(uid).setValue(uid);

        this.setUID(uid);
    }

    @Override
    public String getType() {
        return "Client";
    }

    public double getBirthYear(){
        return birthYear;
    }

    @Override
    public User copy() {
        UserClient u = new UserClient(this.getFirstName(), this.getLastName(), this.getEmailAddress(), this.getPassword(), this.birthYear, this.propertyID, new ArrayList<>(this.landlordRequestIdList), new ArrayList<>(this.ticketIdList));
        u.setUID(this.getUID());
        return u;
    }
}
