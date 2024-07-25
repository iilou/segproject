package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


public class FragmentDashboardClient extends Fragment {
    private TextView welcome;
    private TextView propertySubtitle;
    private TextView noPropertyLabel;
    private Button requestButton;

    private TextView addressLabel;
    private TextView address;
    private TextView typeLabel;
    private TextView type;
    private TextView separator;
    private TextView floorLabel;
    private TextView floor;
    private TextView roomsLabel;
    private TextView rooms;
    private TextView bathroomsLabel;
    private TextView bathrooms;
    private TextView floorsLabel;
    private TextView floors;
    private TextView areaLabel;
    private TextView area;
    private TextView laundryLabel;
    private TextView laundry;
    private TextView parkingLabel;
    private TextView parking;
    private TextView rentLabel;
    private TextView rent;
    private TextView utilsLabel;
    private TextView utils;

    private TextView ticketsLabel;
    private Button newTicketButton;
    private Button backButton;
    private TextView ticketPageIndicator;
    private Button nextButton;
    private Button viewButton;

    private TextView ticketTypeLabel;
    private TextView ticketUrgencyLabel;
    private TextView ticketMessageLabel;
    private TextView ticketInteractionsLabel;

    private EditText ticketTypeValue;
    private EditText ticketUrgencyValue;
    private EditText ticketMessageValue;
    private EditText ticketInteractionsValue;
    private Button ticketTypeNext;
    private int ticketTypeIndex;
    private EditText ticketReviewMsg;

    private TextView scoreLabel;
    private EditText scoreInput;
    private Button rateButton;

    private FirebaseDatabase db;
    private DatabaseReference ref;

    private UserClient user;


    private ActivityResultLauncher<Intent> propertySelectionLauncher;
    private ActivityResultLauncher<Intent> landlordRequestIntent;
    private ActivityResultLauncher<Intent> ticketIntentLauncher;


    private ArrayList<Ticket> ticketList;
    private int currentTicketIndex;
    private boolean isCreatingNewTicket;


    public FragmentDashboardClient() {
        // Required empty public constructor
    }

    private void togglePropertyInfo(int visibility, int visibilityNot, boolean activateQueryButton) {
        addressLabel.setVisibility(visibility);
        address.setVisibility(visibility);
        typeLabel.setVisibility(visibility);
        type.setVisibility(visibility);
        separator.setVisibility(visibility);
        floorLabel.setVisibility(visibility);
        floor.setVisibility(visibility);
        roomsLabel.setVisibility(visibility);
        rooms.setVisibility(visibility);
        bathroomsLabel.setVisibility(visibility);
        bathrooms.setVisibility(visibility);
        floorsLabel.setVisibility(visibility);
        floors.setVisibility(visibility);
        areaLabel.setVisibility(visibility);
        area.setVisibility(visibility);
        laundryLabel.setVisibility(visibility);
        laundry.setVisibility(visibility);
        parkingLabel.setVisibility(visibility);
        parking.setVisibility(visibility);
        rentLabel.setVisibility(visibility);
        rent.setVisibility(visibility);
        utilsLabel.setVisibility(visibility);
        utils.setVisibility(visibility);

        requestButton.setEnabled(activateQueryButton);
        requestButton.setVisibility(visibilityNot);
        noPropertyLabel.setVisibility(visibilityNot);
    }

    public void fillPropertyViews(Property property) {
        address.setText(property.getAddress());
        type.setText(property.getType());
        floor.setText(String.valueOf(property.getFloorLevel()));
        rooms.setText(String.valueOf(property.getNumRooms()));
        bathrooms.setText(String.valueOf(property.getNumBathrooms()));
        area.setText(String.valueOf(property.getAreaInFeetSq()) + " sq ft");
        laundry.setText(property.isLaundryInUnit() ? "Yes" : "No");
        parking.setText(String.valueOf(property.getParkingSpots()));
        rent.setText("$" + String.valueOf(property.getRentInCents() / 100.0));

        StringBuilder utilsText = new StringBuilder();
        boolean[] utilities = property.getUtilitiesIncluded();
        if (utilities[0]) {
            utilsText.append("Hydro ");
        }
        if (utilities[1]) {
            utilsText.append("Heating ");
        }
        if (utilities[2]) {
            utilsText.append("Water ");
        }
        utils.setText(utilsText.toString().trim());
    }

    private void updateProperty(){
        if(user.getPropertyID().isEmpty()){
            togglePropertyInfo(View.INVISIBLE, View.VISIBLE, true);
            Log.d("TAG", user.getPropertyID() + " no property id");

        } else {
            togglePropertyInfo(View.VISIBLE, View.INVISIBLE, false);
            Log.d("TAG", user.getPropertyID() + " property id");
            ref.child("property").child(user.getPropertyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        fillPropertyViews(new Property(task.getResult()));
                    }
                }
            });
        }
    }


    public void fillTicketView(){
        if(ticketList.isEmpty()){
            isCreatingNewTicket = true;
        }

        if(isCreatingNewTicket){ // newticketmode
            ticketTypeIndex = 0;

            backButton.setEnabled(false);
            nextButton.setEnabled(false);
            viewButton.setEnabled(false);
            ticketPageIndicator.setText("");
            ticketTypeValue.setEnabled(false);
            ticketTypeNext.setEnabled(true);
            ticketTypeNext.setVisibility(View.VISIBLE);
            ticketUrgencyValue.setEnabled(true);
            ticketMessageValue.setEnabled(true);
            ticketMessageLabel.setText("Message");
            ticketInteractionsValue.setEnabled(true);
            ticketTypeValue.setText(Ticket.availableTypes[ticketTypeIndex]);
            ticketUrgencyValue.setText("");
            ticketMessageValue.setText("");
            ticketInteractionsValue.setVisibility(View.INVISIBLE);
            ticketInteractionsLabel.setVisibility(View.INVISIBLE);
            ticketReviewMsg.setVisibility(View.INVISIBLE);
            scoreLabel.setVisibility(View.INVISIBLE);
            scoreInput.setVisibility(View.INVISIBLE);
            rateButton.setVisibility(View.VISIBLE);
            rateButton.setText("Create Ticket");
            rateButton.setEnabled(true);
        } // create
        else{
            Ticket t = ticketList.get(currentTicketIndex);

            backButton.setEnabled(true);
            nextButton.setEnabled(true);
            viewButton.setEnabled(true);
            ticketPageIndicator.setText((currentTicketIndex+1) + "/" + ticketList.size());
            ticketTypeValue.setEnabled(false);
            ticketTypeNext.setEnabled(false);
            ticketTypeNext.setVisibility(View.INVISIBLE);
            ticketUrgencyValue.setEnabled(false);
            ticketMessageValue.setEnabled(false);
            ticketInteractionsValue.setEnabled(false);
            ticketTypeValue.setText(t.getType());
            ticketUrgencyValue.setText(""+t.getUrgency());
            ticketMessageValue.setText(t.getOpeningMessage());
            ticketInteractionsLabel.setVisibility(View.VISIBLE);
            ticketInteractionsValue.setVisibility(View.VISIBLE);
            ticketInteractionsValue.setText(""+t.getStatus());

            boolean rateable = !t.getStatus().equals("PENDING") && !t.getStatus().equals("ACCEPTED") && t.getRating() < 0;
            scoreLabel.setVisibility(rateable?View.VISIBLE:View.INVISIBLE);
            scoreInput.setVisibility(rateable?View.VISIBLE:View.INVISIBLE);
            rateButton.setEnabled(rateable);
            rateButton.setText("Rate Manager");
            rateButton.setVisibility(rateable?View.VISIBLE:View.INVISIBLE);
            ticketReviewMsg.setVisibility(rateable?View.VISIBLE:View.INVISIBLE);
        } //view
    }

    /** pretense: getting ids from current user object info, reading from ticket database */
    private void updateTicket(){
        currentTicketIndex = 0;
        ticketList = new ArrayList<>();
        isCreatingNewTicket = false;

        if(user.getTicketIdList().isEmpty()){
            fillTicketView();
            return;
        }

        int[] m = {0,user.getTicketIdList().size()};
        for(int i = 0; i < m[1]; i++){
            ref.child("ticket").child(user.getTicketIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    m[0]++;
                    if(!task.isSuccessful()) return;

                    Ticket t = new Ticket(task.getResult());
                    ticketList.add(t);
                    if(m[0] >= m[1]){
                        fillTicketView();
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();



        if(!MemUser.CURRENT_USER.getType().equals("Client")) {
            throw new RuntimeException("client dashboard appeared with no client selected, this is not right");
        }

        user = (UserClient) MemUser.CURRENT_USER.copy();


        landlordRequestIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == 1){
                    // return : request -> MEM.MEM_REQUEST
                    Request newRequest = MEM.MEM_REQUEST.copy();
                    // allowed since landlordRequestIntent is only called after property selection
                    newRequest.setToData("propertyId", MemProperty.CURRENT_PROPERTY.getUid());
                    newRequest.setStatus("PENDING");


                    DatabaseReference newRef = ref.child("request").push();
                    newRef.setValue(newRequest.toHashMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                newRef.child("uid").setValue(newRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ref.child("users").child(user.getUID()).child("landlordRequestIdList").child(newRef.getKey()).setValue(newRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                ref.child("users").child(newRequest.getReceiverID()).child("landlordRequestIdList").child(newRef.getKey()).setValue(newRef.getKey());

                                                user.addLandlordRequestId(newRequest.getUid());
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });


                }
            }
        });

        propertySelectionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == 1){
                    // return : property -> MemProperty.CURRENT_PROPERTY
                    Property newProperty = MemProperty.CURRENT_PROPERTY.copy();

                    Request r = new Request(user.getUID(), newProperty.getLandlordUID(), "", "LANDLORD_REQUEST");
                    MEM.MEM_REQUEST = r.copy();

                    Intent i = new Intent(getActivity(), IntentRequest.class);
                    i.putExtra("type", "add");
                    landlordRequestIntent.launch(i);

                }
            }
        });

        ticketIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == 1){
                    Ticket t = MEM.MEM_TICKET.copy();
                    ref.child("ticket").child(t.getUid()).setValue(t.toHashMap());
                    ticketList.set(currentTicketIndex, t);
                    fillTicketView();
                }
            }
        });
    }

    private void findViews(View view) {
        welcome = view.findViewById(R.id.dbcl_welcome);
        propertySubtitle = view.findViewById(R.id.dbcl_property_subtitle);
        noPropertyLabel = view.findViewById(R.id.dbcl_no_property_label);
        requestButton = view.findViewById(R.id.dbcl_request_button);

        addressLabel = view.findViewById(R.id.dbcl_address_label);
        address = view.findViewById(R.id.dbcl_address);
        typeLabel = view.findViewById(R.id.dbcl_type_label);
        type = view.findViewById(R.id.dbcl_type);
        separator = view.findViewById(R.id.dbcl_separator);
        floorLabel = view.findViewById(R.id.dbcl_floor_label);
        floor = view.findViewById(R.id.dbcl_floor);
        roomsLabel = view.findViewById(R.id.dbcl_rooms_label);
        rooms = view.findViewById(R.id.dbcl_rooms);
        bathroomsLabel = view.findViewById(R.id.dbcl_bathrooms_label);
        bathrooms = view.findViewById(R.id.dbcl_bathrooms);
        floorsLabel = view.findViewById(R.id.dbcl_floors_label);
        floors = view.findViewById(R.id.dbcl_floors);
        areaLabel = view.findViewById(R.id.dbcl_area_label);
        area = view.findViewById(R.id.dbcl_area);
        laundryLabel = view.findViewById(R.id.dbcl_laundry_label);
        laundry = view.findViewById(R.id.dbcl_laundry);
        parkingLabel = view.findViewById(R.id.dbcl_parking_label);
        parking = view.findViewById(R.id.dbcl_parking);
        rentLabel = view.findViewById(R.id.dbcl_rent_label);
        rent = view.findViewById(R.id.dbcl_rent);
        utilsLabel = view.findViewById(R.id.dbcl_utils_label);
        utils = view.findViewById(R.id.dbcl_utils);

        ticketsLabel = view.findViewById(R.id.dbcl_tickets_label);
        newTicketButton = view.findViewById(R.id.dbcl_new_ticket_button);
        backButton = view.findViewById(R.id.dbcl_back_button);
        ticketPageIndicator = view.findViewById(R.id.dbcl_ticket_page_indicator);
        nextButton = view.findViewById(R.id.dbcl_next_button);
        viewButton = view.findViewById(R.id.dbcl_view_button);

        ticketTypeLabel = view.findViewById(R.id.dbcl_ticket_type_label);
        ticketUrgencyLabel = view.findViewById(R.id.dbcl_ticket_urgency_label);
        ticketMessageLabel = view.findViewById(R.id.dbcl_ticket_message_label);
        ticketInteractionsLabel = view.findViewById(R.id.dbcl_ticket_interactions_label);

        ticketTypeValue = view.findViewById(R.id.dbcl_ticket_type_value);
        ticketTypeNext = view.findViewById(R.id.dbcl_ticket_type_next);
        ticketUrgencyValue = view.findViewById(R.id.dbcl_ticket_urgency_value);
        ticketMessageValue = view.findViewById(R.id.dbcl_ticket_message_value);
        ticketInteractionsValue = view.findViewById(R.id.dbcl_ticket_interactions_value);
        ticketReviewMsg = view.findViewById(R.id.dbcl_review_msg);

        scoreLabel = view.findViewById(R.id.dbcl_score_label);
        scoreInput = view.findViewById(R.id.dbcl_score_input);
        rateButton = view.findViewById(R.id.dbcl_rate_button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_client, container, false);

        findViews(view);
        updateProperty();
        updateTicket();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        newTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCreatingNewTicket = !isCreatingNewTicket;
                fillTicketView();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTicketIndex = (currentTicketIndex + 1) % ticketList.size();
                fillTicketView();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTicketIndex = (currentTicketIndex +ticketList.size()-1) % ticketList.size();
                fillTicketView();
            }
        });

        ticketTypeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketTypeIndex = (ticketTypeIndex + 1) % Ticket.availableTypes.length;
                ticketTypeValue.setText(Ticket.availableTypes[ticketTypeIndex]);
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ticketList.isEmpty()) return;
                Ticket t = ticketList.get(currentTicketIndex);
                Intent i = new Intent(getActivity(), IntentTicket.class);
                i.putExtra("type", "edit");
                i.putExtra("userType", "Client");
                MEM.MEM_TICKET = t.copy();
                ticketIntentLauncher.launch(i);
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCreatingNewTicket){ // ticket creation
                    if((""+ticketTypeValue.getText()).equals("") || (""+ticketUrgencyValue.getText()).equals("") || (""+ticketMessageValue.getText()).equals("")) return;
                    try{
                        int intint = Integer.parseInt(""+ticketUrgencyValue.getText());
                        if(intint >5 || intint<0) return;
                    } catch (NumberFormatException e){
                        return;
                    }
                    // get property manager id
                    ref.child("property").child(user.getPropertyID()).child("managerUID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Ticket t = new Ticket(""+ticketTypeValue.getText(),
                                    Integer.parseInt(""+ticketUrgencyValue.getText()),
                                    ""+ticketMessageValue.getText(),
                                    user.getPropertyID(),
                                    user.getUID(),
                                    ""+task.getResult().getValue());

                            DatabaseReference newRef = ref.child("ticket").push();
                            // add ticket to db
                            newRef.setValue(t.toHashMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    t.setUid(newRef.getKey());

                                    // add uid to ticket in db
                                    newRef.child("uid").setValue(t.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            // push ticket id to manager ticket list
                                            ref.child("users").child(t.getManagerId()).child("ticketIdList").child(t.getUid()).setValue(t.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // push ticket id to client ticket list
                                                    ref.child("users").child(user.getUID()).child("ticketIdList").child(t.getUid()).setValue(t.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // retrieve new client user object
                                                            ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                    user = new UserClient(task.getResult());
                                                                    updateTicket();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                    });
                } else { // ticket rate
//                    Ticket curTicket = ticketList.get(currentTicketIndex);
//                    if(!curTicket.getStatus().equals("PENDING") && !curTicket.getStatus().equals("ACCEPTED") && curTicket.getRating() < 0){
//                        try{
//                            double rating = Double.parseDouble(""+scoreInput.getText());
//                            if(0 < rating || 5 > rating) return;
//                            if(s)
//                        }catch (NumberFormatException e){
//                            Toast.makeText(requireContext(), "INVALID INPUT", Toast.LENGTH_LONG).show();
//                        }
//                    }
                }
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //double checking
                if(user.getPropertyID().isEmpty()){

                    Query query = ref.child("property").orderByChild("clientUID").equalTo("");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<Property> pl = new ArrayList<>();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                pl.add(new Property(ds));
                            }

                            MemProperty.CURRENT_PROPERTY_LIST = pl;

                            Intent i = new Intent(getActivity(), IntentPropertyList.class);
                            i.putExtra("type", "add");
                            propertySelectionLauncher.launch(i);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}