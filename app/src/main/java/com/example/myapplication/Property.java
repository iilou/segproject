package com.example.myapplication;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

public class Property {
    private String address;
    private String type;
    private int floorLevel;
    private int numRooms;
    private int numBathrooms;
    private int areaInFeetSq;
    private boolean laundryInUnit;
    private int parkingSpots;
    private int rentInCents;
    private boolean[] utilitiesIncluded; // Hydro, Heating, Water

    private String uid;

    private String managerUID;
    private String landlordUID;
    private String clientUID;

    public Property copy() {
        Property newProperty = new Property(landlordUID, address, type, floorLevel, numRooms, numBathrooms,
                areaInFeetSq, laundryInUnit, parkingSpots, rentInCents,
                utilitiesIncluded[0], utilitiesIncluded[1], utilitiesIncluded[2]);
        newProperty.setUid(this.uid); // Preserve the same UID in the copy
        newProperty.setManagerUID(this.managerUID);
        newProperty.setClientUID(this.clientUID);
        return newProperty;
    }

    // Constructor to initialize all properties
    public Property(String landlordUID, String address, String type, int floorLevel, int numRooms, int numBathrooms,
                    int areaInFeetSq, boolean laundryInUnit, int parkingSpots, int rentInCents,
                    boolean hydro, boolean heating, boolean water) {
        this.address = address;
        this.type = type;
        this.floorLevel = floorLevel;
        this.numRooms = numRooms;
        this.numBathrooms = numBathrooms;
        this.areaInFeetSq = areaInFeetSq;
        this.laundryInUnit = laundryInUnit;
        this.parkingSpots = parkingSpots;
        this.rentInCents = rentInCents;
        this.utilitiesIncluded = new boolean[] { hydro, heating, water };
        this.uid = "";
        this.managerUID = "";
        this.clientUID = "";
        this.landlordUID = landlordUID;
    }

    // No-argument constructor initializing with default values
    public Property(String landlordUID) {
        this.address = "";
        this.type = "";
        this.floorLevel = -1;
        this.numRooms = -1;
        this.numBathrooms = -1;
        this.areaInFeetSq = -1;
        this.laundryInUnit = false;
        this.parkingSpots = -1;
        this.rentInCents = -1;
        this.utilitiesIncluded = new boolean[] { false, false, false };
        this.uid = "";
        this.managerUID = "";
        this.landlordUID = landlordUID;
        this.clientUID = "";
    }
    // Constructor using DataSnapshot
    public Property(DataSnapshot ds) {
        this.uid = getValueOrDefault(ds, "uid", "");
        this.landlordUID = getValueOrDefault(ds, "landlordUID", "");
        this.address = getValueOrDefault(ds, "address", "");
        this.type = getValueOrDefault(ds, "type", "");
        this.floorLevel = getValueOrDefault(ds, "floorLevel", -1);
        this.numRooms = getValueOrDefault(ds, "numRooms", -1);
        this.numBathrooms = getValueOrDefault(ds, "numBathrooms", -1);
        this.areaInFeetSq = getValueOrDefault(ds, "areaInFeetSq", -1);
        this.laundryInUnit = getValueOrDefault(ds, "laundryInUnit", false);
        this.parkingSpots = getValueOrDefault(ds, "parkingSpots", -1);
        this.rentInCents = getValueOrDefault(ds, "rentInCents", -1);
        this.utilitiesIncluded = new boolean[] {
                getValueOrDefault(ds.child("utilitiesIncluded"), "hydro", false),
                getValueOrDefault(ds.child("utilitiesIncluded"), "heating", false),
                getValueOrDefault(ds.child("utilitiesIncluded"), "water", false)
        };
        this.managerUID = getValueOrDefault(ds, "managerUID", "");
        this.landlordUID = getValueOrDefault(ds, "landlordUID", "");
        this.clientUID = getValueOrDefault(ds, "clientUID", "");
    }

    private <T> T getValueOrDefault(DataSnapshot ds, String key, T defaultValue) {
        if (ds.hasChild(key)) {
            return (T) ds.child(key).getValue(defaultValue.getClass());
        }
        return defaultValue;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("landlordUID", landlordUID);
        result.put("address", address);
        result.put("type", type);
        result.put("floorLevel", floorLevel);
        result.put("numRooms", numRooms);
        result.put("numBathrooms", numBathrooms);
        result.put("areaInFeetSq", areaInFeetSq);
        result.put("laundryInUnit", laundryInUnit);
        result.put("parkingSpots", parkingSpots);
        result.put("rentInCents", rentInCents);

        Map<String, Boolean> utilities = new HashMap<>();
        utilities.put("hydro", utilitiesIncluded[0]);
        utilities.put("heating", utilitiesIncluded[1]);
        utilities.put("water", utilitiesIncluded[2]);
        result.put("utilitiesIncluded", utilities);

        result.put("managerUID", managerUID);
        result.put("landlordUID", landlordUID);
        result.put("clientUID", clientUID);
        return result;
    }


    public void assignPropertyManagerId(String uid){
        this.managerUID = uid;
    }

    public void removePropertyManager(){

    }

    public void assignClient(){

    }



    public String getManagerUID(){return this.managerUID;}
    public void setManagerUID(String managerUID){this.managerUID = managerUID;}
    public String getLandlordUID(){return this.landlordUID;}
    public void setLandlordUID(String landlordUID){this.landlordUID = landlordUID;}
    public String getClientUID(){return this.clientUID;}
    public void setClientUID(String clientUID){this.clientUID = clientUID;}


    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter and Setter for address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter and Setter for type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter and Setter for floorLevel
    public int getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    // Getter and Setter for numRooms
    public int getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    // Getter and Setter for numBathrooms
    public int getNumBathrooms() {
        return numBathrooms;
    }

    public void setNumBathrooms(int numBathrooms) {
        this.numBathrooms = numBathrooms;
    }

    // Getter and Setter for areaInFeetSq
    public int getAreaInFeetSq() {
        return areaInFeetSq;
    }

    public void setAreaInFeetSq(int areaInFeetSq) {
        this.areaInFeetSq = areaInFeetSq;
    }

    // Getter and Setter for laundryInUnit
    public boolean isLaundryInUnit() {
        return laundryInUnit;
    }

    public void setLaundryInUnit(boolean laundryInUnit) {
        this.laundryInUnit = laundryInUnit;
    }

    // Getter and Setter for parkingSpots
    public int getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(int parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    // Getter and Setter for rentInCents
    public int getRentInCents() {
        return rentInCents;
    }

    public void setRentInCents(int rentInCents) {
        this.rentInCents = rentInCents;
    }

    // Getter and Setter for utilitiesIncluded
    public boolean[] getUtilitiesIncluded() {
        return utilitiesIncluded;
    }
    public boolean hydro() {return utilitiesIncluded[0];}
    public boolean heating(){return utilitiesIncluded[1];}
    public boolean water(){return utilitiesIncluded[2];}

    public void setHydro(boolean b){this.utilitiesIncluded[0]=b;}
    public void setHeater(boolean b){this.utilitiesIncluded[1]=b;}
    public void  setWater(boolean b){this.utilitiesIncluded[2]=b;}

    public void setUtilitiesIncluded(boolean hydro, boolean heating, boolean water) {
        this.utilitiesIncluded = new boolean[] { hydro, heating, water };
    }

}
