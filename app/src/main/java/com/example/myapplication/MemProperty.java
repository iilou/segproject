package com.example.myapplication;

import java.util.ArrayList;

public class MemProperty {

    public static Property CURRENT_PROPERTY = null;
    public static String CURRENT_PROPERTY_UID = "";

    public static ArrayList<Property> CURRENT_PROPERTY_LIST = null;

    public static ArrayList<Property> CURRENT_PROPERTY_LIST_COPY() {
        ArrayList<Property> copiedList = new ArrayList<>();
        if (CURRENT_PROPERTY_LIST != null) {
            for (Property property : CURRENT_PROPERTY_LIST) {
                copiedList.add(property.copy());
            }
        }
        return copiedList;
    }

}
