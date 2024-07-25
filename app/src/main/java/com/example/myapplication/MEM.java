package com.example.myapplication;

import java.util.ArrayList;

public class MEM {

    public static Request MEM_REQUEST = new Request("", "", "", "");

    public static Ticket MEM_TICKET = null;
    public static ArrayList<Ticket> MEM_TICKET_LIST = null;
    public static ArrayList<Ticket> MEM_TICKET_LIST_COPY() {
        ArrayList<Ticket> copiedList = new ArrayList<>();
        if (MEM_TICKET_LIST != null) {
            for (Ticket ticket : MEM_TICKET_LIST) {
                copiedList.add(ticket.copy());
            }
        }
        return copiedList;
    }
}
