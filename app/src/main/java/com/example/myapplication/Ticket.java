package com.example.myapplication;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Ticket {
    public static final String[] availableTypes = {"Maintenance", "Security", "Damage", "Infestation"};

    private String type;
    private int urgency;

    private String status; // PENDING ACCEPTED REJECTED RESOLVED
    private double rating;
    private String openingMessage;
    private String openingTime;
    private String closingMessage;
    private String closingTime;

    private ArrayList<String> messageList;
    private ArrayList<String> messageTimeList;
    private ArrayList<String> messageOriginList;
    private int messageCount;

    private String propertyId;
    private String clientId;
    private String managerId;

    private String uid;

    public Ticket(String type, int urgency, String openingMessage, String propertyId, String clientId, String managerId){
        ZonedDateTime currentUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
        String formattedUTC = currentUTC.format(formatter);

        this.type = type;
        this.urgency = urgency;
        this.openingMessage = openingMessage;
        this.closingMessage = "";
        this.openingTime = formattedUTC;
        this.closingTime = "";

        this.rating = -1;
        this.status = "PENDING";

        this.messageList = new ArrayList<>();
        this.messageOriginList = new ArrayList<>();
        this.messageTimeList = new ArrayList<>();
        this.messageCount = 0;

        this.propertyId = propertyId;
        this.clientId = clientId;
        this.managerId = managerId;

        this.uid = "";
    }
    public Ticket(DataSnapshot ds) {
        this.type = ds.child("type").getValue(String.class);
        this.urgency = ds.child("urgency").getValue(Integer.class);
        this.status = ds.child("status").getValue(String.class);
        this.rating = ds.child("rating").getValue(Double.class);
        this.openingMessage = ds.child("openingMessage").getValue(String.class);
        this.openingTime = ds.child("openingTime").getValue(String.class);
        this.closingMessage = ds.child("closingMessage").getValue(String.class);
        this.closingTime = ds.child("closingTime").getValue(String.class);
        this.messageCount = ds.child("messageCount").getValue(Integer.class);
        this.propertyId = ds.child("propertyId").getValue(String.class);
        this.clientId = ds.child("clientId").getValue(String.class);
        this.managerId = ds.child("managerId").getValue(String.class);

        this.messageList = new ArrayList<>();
        this.messageTimeList = new ArrayList<>();
        this.messageOriginList = new ArrayList<>();

        DataSnapshot messagesSnapshot = ds.child("messages");
        List<String> keys = new ArrayList<>();
        for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
            keys.add(messageSnapshot.getKey());
        }

        // Sort keys as integers
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
            }
        });

        for (String key : keys) {
            DataSnapshot messageSnapshot = messagesSnapshot.child(key);
            HashMap<String, String> messageDetails = (HashMap<String, String>) messageSnapshot.getValue();
            this.messageList.add(messageDetails.get("message"));
            this.messageTimeList.add(messageDetails.get("time"));
            this.messageOriginList.add(messageDetails.get("origin"));
        }

        this.uid = ds.getKey();
    }

    public Ticket copy() {
        Ticket copy = new Ticket(this.type, this.urgency, this.openingMessage, this.propertyId, this.clientId, this.managerId);
        copy.setStatus(this.status);
        copy.setRating(this.rating);
        copy.setOpeningTime(this.openingTime);
        copy.setClosingMessage(this.closingMessage);
        copy.setClosingTime(this.closingTime);
        copy.setMessageList(new ArrayList<>(this.messageList));
        copy.setMessageTimeList(new ArrayList<>(this.messageTimeList));
        copy.setMessageOriginList(new ArrayList<>(this.messageOriginList));
        copy.setMessageCount(this.messageCount);
        copy.setUid(this.uid);
        return copy;
    }



    public void addMessage(boolean isClient, String message){
        if(!this.status.equals("ACCEPTED")) return;

        ZonedDateTime currentUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
        String formattedUTC = currentUTC.format(formatter);

        messageList.add(message);
        messageTimeList.add(formattedUTC);
        messageOriginList.add(isClient?"Client":"Manager");
        messageCount++;
    }

    public void acceptTicket(){
        this.status = "ACCEPTED";
        addMessage(false, "Your Property Manager has Accepted Your Ticket Concerning " + this.getType());

    }
    public void rejectTicket(String message){
        if(messageCount != 0){ //
            Log.d("TICKET REJECTION ERROR", "REJECT TICKET SHOULD NOT BE ALLOWED TO HAPPEN");
            return;
        }
        ZonedDateTime currentUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
        String formattedUTC = currentUTC.format(formatter);

        addMessage(false, "Your Property Manager has Rejected Your Ticket Concerning " + this.getType());

        this.status = "REJECTED";
        this.closingMessage = message;
        this.closingTime = formattedUTC;
    }
    public void resolveTicket(String message){
        if(messageCount == 0){ //
            Log.d("TICKET RESOLUTION ERROR", "RESOLVE TICKET SHOULD NOT BE ALLOWED TO HAPPEN");
            return;
        }

        ZonedDateTime currentUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
        String formattedUTC = currentUTC.format(formatter);

        addMessage(false, "Your Property Manager has Resolved Your Ticket Concerning " + this.getType());

        this.status = "RESOLVED";
        this.closingMessage = message;
        this.closingTime = formattedUTC;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("type", this.type);
        map.put("urgency", this.urgency);
        map.put("status", this.status);
        map.put("rating", this.rating);
        map.put("openingMessage", this.openingMessage);
        map.put("openingTime", this.openingTime);
        map.put("closingMessage", this.closingMessage);
        map.put("closingTime", this.closingTime);
        map.put("messageCount", this.messageCount);
        map.put("propertyId", this.propertyId);
        map.put("clientId", this.clientId);
        map.put("managerId", this.managerId);

        HashMap<String, Object> messagesMap = new HashMap<>();
        for (int i = 0; i < messageList.size(); i++) {
            HashMap<String, String> messageDetails = new HashMap<>();
            messageDetails.put("message", messageList.get(i));
            messageDetails.put("time", messageTimeList.get(i));
            messageDetails.put("origin", messageOriginList.get(i));
            messagesMap.put(String.valueOf(i), messageDetails);
        }
        map.put("messages", messagesMap);

        map.put("uid", this.uid);

        return map;
    }


    public String getUid(){return uid;}
    public void setUid(String uid){this.uid = uid;}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getUrgency() { return urgency; }
    public void setUrgency(int urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getOpeningMessage() { return openingMessage; }
    public void setOpeningMessage(String openingMessage) { this.openingMessage = openingMessage; }

    public String getOpeningTime() { return openingTime; }
    public void setOpeningTime(String openingTime) { this.openingTime = openingTime; }

    public String getClosingMessage() { return closingMessage; }
    public void setClosingMessage(String closingMessage) { this.closingMessage = closingMessage; }

    public String getClosingTime() { return closingTime; }
    public void setClosingTime(String closingTime) { this.closingTime = closingTime; }

    public ArrayList<String> getMessageList() { return messageList; }
    public void setMessageList(ArrayList<String> messageList) { this.messageList = messageList; }

    public ArrayList<String> getMessageTimeList() { return messageTimeList; }
    public void setMessageTimeList(ArrayList<String> messageTimeList) { this.messageTimeList = messageTimeList; }

    public ArrayList<String> getMessageOriginList() { return messageOriginList; }
    public void setMessageOriginList(ArrayList<String> messageOriginList) { this.messageOriginList = messageOriginList; }

    public int getMessageCount() { return messageCount; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

}
