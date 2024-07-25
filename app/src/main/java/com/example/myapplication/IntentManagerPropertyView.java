package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IntentManagerPropertyView extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference ref;

    private TextView impAddr;
    private TextView impLandlord;
    private TextView impClient;
    private Button impDetails;
    private TextView textView29;
    private Button impBackC;
    private TextView impLabelC;
    private Button impNextC;
    private Button impViewC;
    private TextView impStatusC;
    private TextView impTypeC;
    private TextView impClientC;
    private TextView impUrgencyC;
    private TextView impMsgC;
    private TextView textView39;
    private Button impBackP;
    private TextView impLabelP;
    private Button impNextP;
    private Button impViewP;
    private TextView impStatusP;
    private TextView impTypeP;
    private TextView impDateP;
    private TextView impRatingP;
    private TextView impMsgP;
    private Button impReturn;

    private Property property;
    private ArrayList<Ticket> ticketList;
    private ArrayList<Ticket> currentTicketList;
    private ArrayList<Ticket> previousTicketList;

    private int currentTicketListIndex;
    private int previousTicketListIndex;

    private ActivityResultLauncher<Intent> propertyViewIntent;
    private ActivityResultLauncher<Intent> ticketViewIntent;



    private void seperateTicketList(){
        currentTicketList = new ArrayList<>();
        previousTicketList = new ArrayList<>();
        currentTicketListIndex = 0;
        previousTicketListIndex = 0;

        for(Ticket ticket : ticketList){
            if(ticket.getStatus().equals("PENDING") || ticket.getStatus().equals("ACCEPTED")){
                currentTicketList.add(ticket);
            } else {
                previousTicketList.add(ticket);
            }
        }
    }

    private void renderProperty(){
        impAddr.setText(property.getType().toString().toUpperCase() + " AT " + property.getAddress().toString().toUpperCase());
        ref.child("users").child(property.getLandlordUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                UserLandlord ul = new UserLandlord(task.getResult());
                impLandlord.setText("Current Landlord : " + ul.getFirstName() + " " + ul.getLastName());
            }
        });

        if(property.getClientUID().isEmpty()){
            impClient.setText("NO CURRENT TENANT");
        }
        ref.child("users").child(property.getClientUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                UserClient ul = new UserClient(task.getResult());
                impClient.setText("Current Tenant : " + ul.getFirstName() + " " + ul.getLastName());
            }
        });
    }

    private void updateCurrentTicket(){
        if(currentTicketList.isEmpty()){
            impStatusC.setVisibility(View.INVISIBLE);
            impTypeC.setVisibility(View.INVISIBLE);
            impClientC.setVisibility(View.INVISIBLE);
            impUrgencyC.setVisibility(View.INVISIBLE);
            impMsgC.setVisibility(View.INVISIBLE);
        } else {
            Ticket t = currentTicketList.get(currentTicketListIndex);
            impStatusC.setText("Status: " + t.getStatus());
            impTypeC.setText("Type: " + t.getType());

            ref.child("users").child(t.getClientId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    UserClient ul = new UserClient(task.getResult());
                    impClientC.setText("Tenant : " + ul.getFirstName() + " " + ul.getLastName());
                }
            });

            impUrgencyC.setText("Urgency: " + t.getUrgency());
            impMsgC.setText("Message :\n" + t.getOpeningMessage());
        }
    }

    private void updatePreviousTicket(){
        if(previousTicketList.isEmpty()){
            impStatusP.setVisibility(View.INVISIBLE);
            impTypeP.setVisibility(View.INVISIBLE);
            impDateP.setVisibility(View.INVISIBLE);
            impRatingP.setVisibility(View.INVISIBLE);
            impMsgP.setVisibility(View.INVISIBLE);
        } else {
            Ticket t = previousTicketList.get(previousTicketListIndex);
            impStatusP.setText("Status: " + t.getStatus());
            impTypeP.setText("Type: " + t.getType());
            impDateP.setText("Date Resolved: " + t.getClosingTime());
            impRatingP.setText("Rating: " + (t.getRating()<0?"Not Set":t.getRating()));
            impMsgP.setText("Message :\n" + t.getOpeningMessage());
        }
    }

    private void findViews(){
        impAddr = findViewById(R.id.imp_addr);
        impLandlord = findViewById(R.id.imp_landlord);
        impClient = findViewById(R.id.imp_client);
        impDetails = findViewById(R.id.imp_details);
        textView29 = findViewById(R.id.textView29);
        impBackC = findViewById(R.id.imp_back_c);
        impLabelC = findViewById(R.id.imp_label_c);
        impNextC = findViewById(R.id.imp_next_c);
        impViewC = findViewById(R.id.imp_view_c);
        impStatusC = findViewById(R.id.imp_status_c);
        impTypeC = findViewById(R.id.imp_type_c);
        impClientC = findViewById(R.id.imp_client_c);
        impUrgencyC = findViewById(R.id.imp_urgency_c);
        impMsgC = findViewById(R.id.imp_msg_c);
        textView39 = findViewById(R.id.textView39);
        impBackP = findViewById(R.id.imp_back_p);
        impLabelP = findViewById(R.id.imp_label_p);
        impNextP = findViewById(R.id.imp_next_p);
        impViewP = findViewById(R.id.imp_view_p);
        impStatusP = findViewById(R.id.imp_status_p);
        impTypeP = findViewById(R.id.imp_type_p);
        impDateP = findViewById(R.id.imp_date_p);
        impRatingP = findViewById(R.id.imp_rating_p);
        impMsgP = findViewById(R.id.imp_msg_p);
        impReturn = findViewById(R.id.imp_return);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_manager_property_view);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        property = MemProperty.CURRENT_PROPERTY.copy();
        ticketList = MEM.MEM_TICKET_LIST_COPY();

        findViews();

        seperateTicketList();
        updateCurrentTicket();
        updatePreviousTicket();
        renderProperty();

        propertyViewIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {

            }
        });

        ticketViewIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == 1){
                    Ticket returnedTicket = MEM.MEM_TICKET.copy();
                    ref.child("ticket").child(returnedTicket.getUid()).setValue(returnedTicket.toHashMap());
                    for(int i = 0; i < ticketList.size(); i++){
                        if(returnedTicket.getUid().equals(ticketList.get(i).getUid())){
                            ticketList.set(i, returnedTicket);
                            seperateTicketList();
                            updateCurrentTicket();
                            updatePreviousTicket();
                            return;
                        }
                    }
                }
            }
        });

        impViewC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTicketList.isEmpty()) return;
                Intent i = new Intent(IntentManagerPropertyView.this, IntentTicket.class);
                i.putExtra("type", "edit");
                i.putExtra("userType", "Property Manager");
                MEM.MEM_TICKET = currentTicketList.get(currentTicketListIndex).copy();
                ticketViewIntent.launch(i);
            }
        });

        impViewP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previousTicketList.isEmpty()) return;
                Intent i = new Intent(IntentManagerPropertyView.this, IntentTicket.class);
                i.putExtra("type", "edit");
                i.putExtra("userType", "Property Manager");
                MEM.MEM_TICKET = previousTicketList.get(previousTicketListIndex).copy();
                ticketViewIntent.launch(i);
            }
        });


        impDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IntentManagerPropertyView.this, IntentProperty.class);
                i.putExtra("type", "view");
                propertyViewIntent.launch(i);
            }
        });

        impReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(1);
                finish();
            }
        });
    }


}
