package com.example.myapplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class UserManager extends User {

    public ArrayList<String> getManagerRequestIdList() {return managerRequestIdList;}
    public void setManagerRequestIdList(ArrayList<String> managerRequestIdList) {this.managerRequestIdList = managerRequestIdList;}
    public ArrayList<String> getPropertyIdList() {return propertyIdList;}
    public void setPropertyIdList(ArrayList<String> propertyIdList) {this.propertyIdList = propertyIdList;}
    public ArrayList<String> getPastPropertyIdList() {return pastPropertyIdList;}
    public void setPastPropertyIdList(ArrayList<String> pastPropertyIdList) {this.pastPropertyIdList = pastPropertyIdList;}

    public double getScore() {return score;}
    public void setScore(double score) {this.score = score;}
    public int getTotalScores() {return totalScores;}
    public void setTotalScores(int totalScores) {this.totalScores = totalScores;}


    private ArrayList<String> managerRequestIdList;
    private ArrayList<String> propertyIdList;
    private ArrayList<String> pastPropertyIdList;
    private double score; // average score
    private int totalScores; // total tickets scored


    public UserManager(String firstName, String lastName, String emailAddress, String password) {
        super(firstName, lastName, emailAddress, password);
        this.score = 0;
        this.totalScores = 0;
        this.setUID("");
        this.managerRequestIdList = new ArrayList<>();
        this.propertyIdList = new ArrayList<>();
        this.pastPropertyIdList = new ArrayList<>();
    }

    public UserManager(DataSnapshot ds){
        super(""+ds.child("firstName").getValue(), ""+ds.child("lastName").getValue(), ""+ds.child("emailAddress").getValue(), "");
        this.score = (Double.parseDouble(""+ds.child("score").getValue()));
        this.totalScores = Integer.parseInt(""+ds.child("totalScores").getValue());
        this.setUID(ds.getKey());

        this.managerRequestIdList = new ArrayList<>();
        if(ds.hasChild("managerRequestIdList")){
            for(DataSnapshot ds2 : ds.child("managerRequestIdList").getChildren()){
                managerRequestIdList.add(ds2.getKey());
            }
        }

        this.propertyIdList = new ArrayList<>();
        if(ds.hasChild("propertyIdList")){
            for(DataSnapshot ds2 : ds.child("propertyIdList").getChildren()){
                propertyIdList.add(ds2.getKey());
            }
        }

        this.pastPropertyIdList = new ArrayList<>();
        if(ds.hasChild("pastPropertyIdList")){
            for(DataSnapshot ds2 : ds.child("pastPropertyIdList").getChildren()){
                pastPropertyIdList.add(ds2.getKey());
            }
        }

    }

    @Override
    public void UserToFirebaseDb(DatabaseReference db, String uid) {
        db.child("users").child(uid).child("firstName").setValue(this.getFirstName());
        db.child("users").child(uid).child("lastName").setValue(this.getLastName());
        db.child("users").child(uid).child("emailAddress").setValue(this.getEmailAddress());
        db.child("users").child(uid).child("type").setValue("Property Manager");

        db.child("users").child(uid).child("score").setValue(score);
        db.child("users").child(uid).child("totalScores").setValue(totalScores);

        for(int i = 0; i < managerRequestIdList.size(); i++){
            db.child("users").child(uid).child("managerRequestIdList").child(managerRequestIdList.get(i)).setValue(managerRequestIdList.get(i));
        }

        for(int i = 0; i < propertyIdList.size(); i++){
            db.child("users").child(uid).child("propertyIdList").child(propertyIdList.get(i)).setValue(propertyIdList.get(i));
        }

        for(int i = 0; i < pastPropertyIdList.size(); i++){
            db.child("users").child(uid).child("pastPropertyIdList").child(pastPropertyIdList.get(i)).setValue(pastPropertyIdList.get(i));
        }

        db.child("Property Manager").child(uid).setValue(uid);

        this.setUID(uid);
    }
    @Override
    public String getType() {
        return "Property Manager";
    }

    @Override
    public User copy() {
        UserManager u = new UserManager(this.getFirstName(), this.getLastName(), this.getEmailAddress(), this.getPassword());
        u.setUID(this.getUID());
        u.setScore(this.getScore());
        u.setTotalScores(this.getTotalScores());
        u.setManagerRequestIdList(new ArrayList<>(this.getManagerRequestIdList()));
        u.setPropertyIdList(new ArrayList<>(this.getPropertyIdList()));
        u.setPastPropertyIdList(new ArrayList<>(this.getPastPropertyIdList()));
        return u;
    }
}
