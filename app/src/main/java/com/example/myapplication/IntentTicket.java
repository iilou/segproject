package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class IntentTicket extends AppCompatActivity {

    private TextView tTicketId;
    private TextView tTicketReason;
    private TextView tTicketStatus;
    private TextView tOpeningMessageLabel;
    private TextView tOpeningMessage;
    private Button tBackButton;
    private TextView tMessageCount;
    private Button tNextButton;
    private TextView tMessageSender;
    private TextView tMessageContent;
    private EditText tNewMessage;
    private Button tSendMessageButton;
    private Button tAcceptButton;
    private Button tRejectButton;
    private Button tResolveButton;
    private EditText tResolutionMessage;
    private Button tConfirmButton;
    private Button tDoneButton;

    private String type;
    private String userType;

    private Ticket ticket;

    private int currentMessageIndex = 0;
    private String originalStatus = "";


    private void toggleMessageListVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.INVISIBLE;
//        if(!isVisible) tMessageCount.setText("NO MESSAGES");
        tMessageCount.setVisibility(visibility);
        tBackButton.setEnabled(isVisible);
        tNextButton.setEnabled(isVisible);

        tMessageSender.setVisibility(visibility);
        tMessageContent.setVisibility(visibility);

        tNewMessage.setVisibility(visibility);
        tSendMessageButton.setVisibility(visibility);
    }

    private void toggleStatusVisibility(boolean isVisible){
        tAcceptButton.setVisibility(isVisible ? View.VISIBLE:View.INVISIBLE);
        tRejectButton.setVisibility(isVisible ? View.VISIBLE:View.INVISIBLE);
        tResolveButton.setVisibility(isVisible ? View.VISIBLE:View.INVISIBLE);
        tResolutionMessage.setVisibility(isVisible ? View.VISIBLE:View.INVISIBLE);
        tConfirmButton.setVisibility(isVisible ? View.VISIBLE:View.INVISIBLE);
    }

    private void renderMessage(){
        if(ticket.getMessageList().isEmpty()) return;

        tMessageCount.setText("Message " + (currentMessageIndex+1) + "/" + ticket.getMessageList().size());
        tMessageSender.setText("- " + ticket.getMessageOriginList().get(currentMessageIndex) + " \tCreated on: " + ticket.getMessageTimeList().get(currentMessageIndex));
        tMessageContent.setText(ticket.getMessageList().get(currentMessageIndex));
    }

    private void renderClientView(Ticket ticket){
        currentMessageIndex = 0;
        renderMessage();


        tTicketId.setText("Ticket at ID: " + ticket.getUid());
        tTicketReason.setText("Reason for Ticket: " + ticket.getType());
        // CLOSING MESSAGE TAKES PRECADENT OVER OPENING MESSAGE
        if(ticket.getClosingMessage().isEmpty()){
            tOpeningMessage.setText(ticket.getOpeningMessage());
            tOpeningMessageLabel.setText("Ticket Opened At: " + ticket.getOpeningTime());
        } else {
            tOpeningMessage.setText(ticket.getClosingMessage());
            tOpeningMessageLabel.setText("Ticket Closed At: " + ticket.getClosingTime());
        }

        // IF PENDING HIDE MESSAGE LIST
        toggleMessageListVisibility(!ticket.getStatus().equals("PENDING"));
        // NOT PROPERTY MANAGER CANT CHANGE STATUS
        toggleStatusVisibility(false);

        // CANT SEND MESSAGES IF PENDING OR RESOLVED OR REJECTED
        if(ticket.getStatus().equals("ACCEPTED")){
            tNewMessage.setEnabled(true);
            tSendMessageButton.setEnabled(true);
        } else {
            tNewMessage.setEnabled(false);
            tSendMessageButton.setEnabled(false);
        }


    }

    private void renderManagerView(Ticket ticket){
        currentMessageIndex = 0;
        renderMessage();

        tTicketId.setText("Ticket at ID: " + ticket.getUid());
        tTicketReason.setText("Reason for Ticket: " + ticket.getType());
        // CLOSING MESSAGE TAKES PRECADENT OVER OPENING MESSAGE
        if(ticket.getClosingMessage().isEmpty()){
            tOpeningMessage.setText(ticket.getOpeningMessage());
            tOpeningMessageLabel.setText("Ticket Opened At: " + ticket.getOpeningTime());
        } else {
            tOpeningMessage.setText(ticket.getClosingMessage());
            tOpeningMessageLabel.setText("Ticket Closed At: " + ticket.getClosingTime());
        }


        // IF PENDING HIDE MESSAGE LIST
        toggleMessageListVisibility(!ticket.getStatus().equals("PENDING"));
        // NOT PROPERTY MANAGER CANT CHANGE STATUS
        toggleStatusVisibility(true);

        // CANT SEND MESSAGES IF PENDING OR RESOLVED OR REJECTED
        if(ticket.getStatus().equals("ACCEPTED")){
            tNewMessage.setEnabled(true);
            tSendMessageButton.setEnabled(true);
        } else {
            tNewMessage.setEnabled(false);
            tSendMessageButton.setEnabled(false);
        }


        switch(ticket.getStatus()){
            case "PENDING":
                tRejectButton.setEnabled(true);
                tResolutionMessage.setEnabled(true);
                tConfirmButton.setEnabled(true);
                tAcceptButton.setEnabled(true);
                tResolveButton.setEnabled(false);
                break;
            case "ACCEPTED":
                tRejectButton.setEnabled(true);
                tResolutionMessage.setEnabled(true);
                tResolveButton.setEnabled(true);
                tConfirmButton.setEnabled(true);
                tAcceptButton.setEnabled(false);
                break;
            case "REJECTED":
                tRejectButton.setEnabled(false);
                tResolutionMessage.setEnabled(false);
                tResolveButton.setEnabled(false);
                tConfirmButton.setEnabled(false);
                tAcceptButton.setEnabled(false);
                break;
            case "RESOLVED":
                tRejectButton.setEnabled(false);
                tResolutionMessage.setEnabled(false);
                tResolveButton.setEnabled(false);
                tConfirmButton.setEnabled(false);
                tAcceptButton.setEnabled(false);
                break;
        }

    }

    private void renderTicket(Ticket ticket, String type, String userType){
        if(type.equals("edit")){
            if(userType.equals("Client")){
                renderClientView(ticket); // client edit
            } else if (userType.equals("Property Manager")){
                renderManagerView(ticket);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_ticket);

        Intent i = getIntent();
        type = i.getStringExtra("type");
        userType = i.getStringExtra("userType");
        // options: "edit"

        findViews();

        this.ticket = MEM.MEM_TICKET.copy();
        originalStatus = "" + ticket.getStatus();

        renderTicket(this.ticket, type, userType);

        tAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticket.acceptTicket();
                renderTicket(ticket, type, userType);
            }
        });

        tRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tResolutionMessage.getText().toString().equals("")) return;
                ticket.rejectTicket(tResolutionMessage.getText().toString());
                renderTicket(ticket, type, userType);
            }
        });

        tResolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tResolutionMessage.getText().toString().equals("")) return;
                ticket.resolveTicket(tResolutionMessage.getText().toString());
                renderTicket(ticket, type, userType);
            }
        });


        tNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMessageIndex = (currentMessageIndex + 1) % ticket.getMessageList().size();
                renderMessage();
            }
        });
        tBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMessageIndex = (currentMessageIndex + ticket.getMessageList().size() - 1) % ticket.getMessageList().size();
                renderMessage();
            }
        });

        tSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tNewMessage.getText().toString().isEmpty()) return;
                ticket.addMessage(userType.equals("Client"), tNewMessage.getText().toString());
                renderTicket(ticket, type, userType);
            }
        });

        tDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MEM.MEM_TICKET = ticket.copy();
                Intent i = getIntent();
                i.putExtra("stat", originalStatus);
                setResult(1, i);
                finish();
            }
        });
    }
    private void findViews(){
        tTicketId = findViewById(R.id.t_ticket_id);
        tTicketReason = findViewById(R.id.t_ticket_reason);
        tTicketStatus = findViewById(R.id.t_ticket_status);
        tOpeningMessageLabel = findViewById(R.id.t_opening_message_label);
        tOpeningMessage = findViewById(R.id.t_opening_message);
        tBackButton = findViewById(R.id.t_back_button);
        tMessageCount = findViewById(R.id.t_message_count);
        tNextButton = findViewById(R.id.t_next_button);
        tMessageSender = findViewById(R.id.t_message_sender);
        tMessageContent = findViewById(R.id.t_message_content);
        tNewMessage = findViewById(R.id.t_new_message);
        tSendMessageButton = findViewById(R.id.t_send_message_button);
        tAcceptButton = findViewById(R.id.t_accept_button);
        tRejectButton = findViewById(R.id.t_reject_button);
        tResolveButton = findViewById(R.id.t_resolve_button);
        tResolutionMessage = findViewById(R.id.t_resolution_message);
        tConfirmButton = findViewById(R.id.t_confirm_button);
        tDoneButton = findViewById(R.id.t_done_button);
    }

}
