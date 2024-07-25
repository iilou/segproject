package com.example.myapplication;

import java.util.ArrayList;

public class MemUser {
    public static User CURRENT_USER = null;
    public static User CURRENT_TARGET_USER = null;

    public static void setCurrentUser(User u){MemUser.CURRENT_USER = u;}

    public static ArrayList<User> CURRENT_USER_LIST = new ArrayList<User>();
    public static ArrayList<User> CURRENT_USER_LIST_COPY(){
            ArrayList<User> u = new ArrayList<>();
            for(int i = 0; i < CURRENT_USER_LIST.size(); i++){
                u.add(CURRENT_USER_LIST.get(i).copy());
            }
            return u;
    }
}
