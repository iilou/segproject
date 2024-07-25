package com.example.myapplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class Request {
    private String originID;
    private String receiverID;

    private String status; // "ACCEPTED" OR "REJECTED" OR "PENDING"

    private String message;
    private String uid;
    private String type;
    private HashMap<String, String> data;



    public final static String MANAGER_REQUEST = "0";

    public Request(String originID, String receiverID, String message, String type){
        this.originID = originID;
        this.receiverID = receiverID;
        this.message = message;
        this.status = "PENDING";
        this.uid = "";
        this.type = type;
        this.data = new HashMap<>();
    }

    public Request(String originID, String receiverID, String message, String type, HashMap<String, String> data){
        this.originID = originID;
        this.receiverID = receiverID;
        this.message = message;
        this.status = "PENDING";
        this.uid = "";
        this.type = type;
        this.data = data;
    }

    public Request(DataSnapshot ds){
        this.uid = ds.getKey();
        this.receiverID = ""+ds.child("receiverID").getValue();
        this.originID = ""+ds.child("originID").getValue();
        this.status = ""+ds.child("status").getValue();
        this.message = ""+ds.child("message").getValue();
        this.type = ""+ds.child("type").getValue();

        HashMap<String, String> data = new HashMap<>();
        if(ds.hasChild("data")){
            for(DataSnapshot child : ds.child("data").getChildren()){
                data.put(child.getKey(), ""+child.getValue());
            }
        }
        this.data = data;
    }

    /**
     * Ensure setUID is called before running
     * */
    public void toFirebaseDatabase(DatabaseReference db){
        db.child("originID").setValue(originID);
        db.child("receiverID").setValue(receiverID);
        db.child("status").setValue(status);
        db.child("message").setValue(message);
        db.child("uid").setValue(uid);
        db.child("type").setValue(type);

        if(!data.isEmpty()) db.child("data").setValue(data);
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("originID", originID);
        map.put("receiverID", receiverID);
        map.put("status", status);
        map.put("message", message);
        map.put("uid", uid);
        map.put("type", type);
        if(!data.isEmpty()) map.put("data", data);

        return map;
    }

    /**may be null*/
    public String getFromData(String key){
        return data.get(key);
    }

    public void setToData(String key, String value){
        this.data.put(key, value);
    }

    public HashMap<String, String> getData(){
        return data;
    }

    public void setData(HashMap<String, String> data){
        this.data = data;
    }


    // Getters
    public String getOriginID() {
        return originID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUid() {
        return uid;
    }

    public String getType(){return type;}
    public void setType(String s) {this.type = s;}

    // Setters
    public void setOriginID(String originID) {
        this.originID = originID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Copy method
    public Request copy() {
        Request copiedRequest = new Request(this.originID, this.receiverID, this.message, this.type, new HashMap<>(this.data));
        copiedRequest.setStatus(this.status);
        copiedRequest.setUid(this.uid);
        return copiedRequest;
    }
}
