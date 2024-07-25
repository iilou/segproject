package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class FragmentDashboardLandlord extends Fragment {

    FirebaseDatabase db;
    DatabaseReference ref;

    private TextView dbllWelcome;
    private Button dbllAdd;
    private Button dbllBack;
    private Button dbllNext;
    private Button dbllEdit;
    private TextView dbllAddrDisplay;
    private TextView dbllAddr;
    private TextView dbllType;
    private TextView dbllFloor;
    private TextView dbllRooms;
    private TextView dbllBathrooms;
    private TextView dbllFloors;
    private TextView dbllArea;
    private TextView dbllLaundry;
    private TextView dbllParking;
    private TextView dbllRent;
    private TextView dbllUtils;
    private TextView dbllManager;
    private TextView dbllClient;
    private Button dbllAddR;
    private Button dbllBackR;
    private TextView dbllCurR;
    private Button dbllNextR;
    private Button dbllRejectR;
    private TextView dbllAddrR;
    private TextView dbllNameR;
    private TextView dbllAgeR;
    private TextView dbllEmailR;
    private TextView dbllMsgR;
    private TextView dbllPageIndex;

    private UserLandlord user;

    ActivityResultLauncher<Intent> launcher;
    ActivityResultLauncher<Intent> propertyLauncher;
    ActivityResultLauncher<Intent> requestLauncher;
    private int currentPropertyIndex;
    private ArrayList<Property> propertyList = new ArrayList<>();


    private int currentClientRequestIndex;
    private ArrayList<Request> clientRequestList = new ArrayList<>();


    private HashMap<String, ArrayList<Request>> managerRequestList = new HashMap<>(); // dont need to iterate


    // helper variables
    private boolean systemInDatabaseRead = false;
    private int targetSize;
    private int completions;

    public FragmentDashboardLandlord() {
        // Required empty public constructor
    }

    public Property findPropertyFromId(String id){
        for(int i = 0; i < propertyList.size(); i++){
            if(propertyList.get(i).getUid().equals(id)){
                return propertyList.get(i);
            }
        }
        return null;
    }

    /**
     * only call after property list is refreshed
     * */
    public void filterManagerRequests(){
        targetSize = user.getManagerRequestIdList().size();
        completions = 0;
        systemInDatabaseRead = true;
        for(int i = 0; i < targetSize; i++){
            int j = i;
            ref.child("request").child(user.getManagerRequestIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    completions++;
                    if(task.isSuccessful()){
                        Request r = new Request(task.getResult());
                        if(managerRequestList.containsKey(r.getFromData("propertyId"))){
                            managerRequestList.get(r.getFromData("propertyId")).add(r);
                        } else {
                            ArrayList<Request> rlist = new ArrayList<>();
                            rlist.add(r);
                            managerRequestList.put(r.getFromData("propertyId"), rlist);
                        }

                        if(completions >= targetSize){

                            // check requests map
                            for(Map.Entry<String, ArrayList<Request>> req : managerRequestList.entrySet()){
                                String rKey = req.getKey();
                                ArrayList<Request> rList = req.getValue();
                                for(int k = 0; k < rList.size(); k++){
                                    if(rList.get(k).getStatus().equals("REJECTED")){
                                        // remove request
                                        ref.child("request").child(rKey).removeValue();
                                        ref.child("users").child(rList.get(k).getReceiverID()).child("managerRequestIdList").child(rKey).removeValue();
                                        ref.child("users").child(rList.get(k).getOriginID()).child("managerRequestIdList").child(rKey).removeValue();
                                    } else if(rList.get(k).getStatus().equals("ACCEPTED")){
                                        // accept current request
                                        // add to property
                                        String previousManager = "";
                                        for(Property p : propertyList){
                                            if(p.getUid().equals(rKey)){
                                                previousManager = p.getManagerUID();
                                                p.assignPropertyManagerId(rList.get(k).getReceiverID());

                                                if(!previousManager.isEmpty()){

                                                }
                                            }
                                        }
                                        // property database
                                        ref.child("property").child(rKey).child("managerUID").setValue(rList.get(k).getReceiverID());

                                        dbllWelcome.setText("remove " + rList.get(k).getReceiverID());
                                        // add to manager property list in database
                                        ref.child("users").child(rList.get(k).getReceiverID()).child("managerPropertyIdList").child(rKey).setValue(rKey);


                                        // remove everything
                                        for(int l = 0; l < rList.size(); l++){
                                            ref.child("request").child(rList.get(l).getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                            ref.child("users").child(rList.get(l).getReceiverID()).child("managerRequestIdList").child(rList.get(l).getUid()).removeValue();
                                            ref.child("users").child(rList.get(l).getOriginID()).child("managerRequestIdList").child(rList.get(l).getUid()).removeValue();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    public void refreshPropertyList(boolean refreshRequests){
        Log.d("TAG", "property size: " + user.getLandlordRequestIdList().size());

        targetSize = user.getPropertyIdList().size();
        completions = 0;
        systemInDatabaseRead = true;
        currentPropertyIndex = 0;
        propertyList = new ArrayList<>();
        if(targetSize == 0){
            updatePropertyView();
            refreshClientRequestList();
        }

        for(int i = 0; i < targetSize; i++){
            int j = i;
            ref.child("property").child(user.getPropertyIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    completions += 1;
                    if(!task.isSuccessful()){
                        //random class in case of not found
                        propertyList.add(new Property(user.getUID()));
                    }
                    else{
                        Property p = new Property(task.getResult());
                        p.setUid(user.getPropertyIdList().get(j));
                        propertyList.add(p);
                    }

                    if(completions >= targetSize){
                        systemInDatabaseRead = false;
                        updatePropertyView();
                        refreshClientRequestList();
                    }
                }
            });
        }
    }

    public void refreshClientRequestList(){
        Log.d("TAG", "client size: " + user.getLandlordRequestIdList().size());
        targetSize = user.getLandlordRequestIdList().size();
        completions = 0;
        systemInDatabaseRead = true;
        currentClientRequestIndex = 0;
        clientRequestList = new ArrayList<>();

        if(targetSize == 0){
            dbllCurR.setText("NONE");
            dbllAddrR.setText("");
            dbllMsgR.setText("");
            dbllNameR.setText("");
            dbllAgeR.setText("");
            dbllEmailR.setText("");
            dbllAddR.setEnabled(false);
            dbllRejectR.setEnabled(false);
            dbllBackR.setEnabled(false);
            dbllNextR.setEnabled(false);
            dbllAddr.setVisibility(View.INVISIBLE);
            dbllRejectR.setVisibility(View.INVISIBLE);
            dbllBackR.setVisibility(View.INVISIBLE);
            dbllNextR.setVisibility(View.INVISIBLE);
            return;
        }

        // wont run if empty
        for(int i = 0; i < targetSize; i++){
            int j = i;
            ref.child("request").child(user.getLandlordRequestIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    completions += 1;
                    if(task.isSuccessful()){
                        Request r = new Request(task.getResult());
                        clientRequestList.add(r);
                    }

                    if(completions >= targetSize){
                        systemInDatabaseRead = false;
                        updateClientRequestView();
                    }
                }
            });
        }
    }

    public void updatePropertyView() {
        if (user.getPropertyIdList().isEmpty()) {
            Toast.makeText(requireContext(), "No properties available.", Toast.LENGTH_LONG).show();

            dbllPageIndex.setText("NONE");
            dbllAddr.setVisibility(View.INVISIBLE);
            dbllType.setVisibility(View.INVISIBLE);
            dbllFloor.setVisibility(View.INVISIBLE);
            dbllRooms.setVisibility(View.INVISIBLE);
            dbllBathrooms.setVisibility(View.INVISIBLE);
            dbllArea.setVisibility(View.INVISIBLE);
            dbllLaundry.setVisibility(View.INVISIBLE);
            dbllParking.setVisibility(View.INVISIBLE);
            dbllRent.setVisibility(View.INVISIBLE);
            dbllUtils.setVisibility(View.INVISIBLE);
            dbllManager.setVisibility(View.INVISIBLE);
            dbllClient.setVisibility(View.INVISIBLE);

            dbllBack.setEnabled(false);
            dbllNext.setEnabled(false);
            dbllEdit.setEnabled(false);

            return;
        } else {
            dbllAddr.setVisibility(View.VISIBLE);
            dbllType.setVisibility(View.VISIBLE);
            dbllFloor.setVisibility(View.VISIBLE);
            dbllRooms.setVisibility(View.VISIBLE);
            dbllBathrooms.setVisibility(View.VISIBLE);
            dbllArea.setVisibility(View.VISIBLE);
            dbllLaundry.setVisibility(View.VISIBLE);
            dbllParking.setVisibility(View.VISIBLE);
            dbllRent.setVisibility(View.VISIBLE);
            dbllUtils.setVisibility(View.VISIBLE);
            dbllManager.setVisibility(View.VISIBLE);
            dbllClient.setVisibility(View.VISIBLE);

            dbllBack.setEnabled(true);
            dbllNext.setEnabled(true);
            dbllEdit.setEnabled(true);
        }


        Property p = propertyList.get(currentPropertyIndex);
        // set all text views
        dbllPageIndex.setText((currentPropertyIndex+1)+"/"+user.getPropertyIdList().size());
        dbllAddr.setText(p.getAddress());
        dbllType.setText(p.getType());
        dbllFloor.setText(""+p.getFloorLevel());
        dbllRooms.setText(""+p.getNumRooms());
        dbllBathrooms.setText(""+p.getNumBathrooms());
        dbllArea.setText(""+p.getAreaInFeetSq());
        dbllLaundry.setText(p.isLaundryInUnit() ? "YES" : "NO");
        dbllParking.setText(""+p.getParkingSpots());
        dbllRent.setText(""+(p.getRentInCents() / 100));
        dbllUtils.setText((p.hydro() ? "Hydro " : "") + (p.heating() ? "Heating " : "") + (p.water() ? "Water" : ""));

        if(p.getManagerUID().equals("")){
            dbllManager.setText("Manager not set (Click to Request)");
        } else {
            ref.child("users").child(p.getManagerUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        dbllManager.setText(task.getResult().child("firstName").getValue() + " " + task.getResult().child("lastName").getValue());
                    }
                }
            });
        }
        if(p.getClientUID().equals("")){
            dbllClient.setText("Client not set");
        } else {
            ref.child("users").child(p.getClientUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        dbllClient.setText(task.getResult().child("firstName").getValue() + " " + task.getResult().child("lastName").getValue());
                    }
                }
            });
        }

    }

    public void updateClientRequestView(){

        if (user.getLandlordRequestIdList().isEmpty()) {
            Toast.makeText(requireContext(), "No properties available.", Toast.LENGTH_LONG).show();
            return;
        }

        Request r = clientRequestList.get(currentClientRequestIndex);
        dbllCurR.setText((currentClientRequestIndex+1) + " / " + clientRequestList.size());
        for(Property p : propertyList){
            if(p.getUid().equals(r.getFromData("propertyId"))){
                dbllAddrR.setText(p.getType().toUpperCase() + " AT " + p.getAddress().toUpperCase());
            }
        }
        dbllMsgR.setText(r.getMessage());
        ref.child("users").child(r.getOriginID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot ds = task.getResult();
                    dbllNameR.setText(ds.child("firstName").getValue() + " " + ds.child("lastName").getValue());
                    dbllAgeR.setText("BirthYear: " + ds.child("birthYear").getValue());
                    dbllEmailR.setText("Email: " + ds.child("emailAddress").getValue());
                }
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        System.out.println(MemUser.CURRENT_USER);

        if(!MemUser.CURRENT_USER.getType().equals("Landlord")) {
            throw new RuntimeException("landlord dashboard appeared with no landlord selected");
        }

        user = (UserLandlord) MemUser.CURRENT_USER.copy();

        //intent for property creation and editing
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        // successful + no need to change anything
                        if(o.getResultCode() == 1){
                            Property returnedProperty = MemProperty.CURRENT_PROPERTY.copy();
                            propertyList.set(currentPropertyIndex, returnedProperty);
                            updatePropertyView();

                            ref.child("property").child(user.getPropertyIdList().get(currentPropertyIndex)).setValue(returnedProperty.toHashMap());

                        }

                        // new
                        if(o.getResultCode() == 2){
                            Property returnedProperty = MemProperty.CURRENT_PROPERTY.copy();
                            propertyList.add(returnedProperty);

                            systemInDatabaseRead = true;
                            DatabaseReference newRef = ref.child("property").push();
                            // add new property - uid
                            newRef.setValue(returnedProperty.toHashMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //set uid
                                    newRef.child("uid").setValue(newRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ref.child("users").child(user.getUID()).child("propertyIdList").child(newRef.getKey()).setValue(newRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    user.addPropertyId(newRef.getKey());
                                                    systemInDatabaseRead = false;
                                                    currentPropertyIndex = propertyList.size()-1;
                                                    refreshPropertyList(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }

                        // cancelled
                        if(o.getResultCode() == -1){
//                            updatePropertyView();
                            return;
                        }
                    }
                }
            );

        // intent for MANAGER REQUEST HANDLING | WRONG NAMING
        propertyLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == 1){ // SUCCESS
                            UserManager newUserManager = (UserManager) MemUser.CURRENT_TARGET_USER.copy();
                            HashMap<String, String> data = new HashMap<>();
                            data.put("equity", "0.05");

//                            dbllWelcome.setText(newUserManager.getUID());
//                            Toast.makeText(requireContext(), newUserManager.getUID(), Toast.LENGTH_LONG).show();

                            Request r = new Request(user.getUID(), newUserManager.getUID(), "", "ManagerRequest", data);
                            //move to request intent
                            MEM.MEM_REQUEST = r.copy();
                            Intent i = new Intent(getActivity(), IntentRequest.class);
                            i.putExtra("type", "add");
                            requestLauncher.launch(i);

                        }
                    }
                });

        requestLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == 1){ // success
                            Request newRequest = MEM.MEM_REQUEST.copy();
                            newRequest.setToData("propertyId", propertyList.get(currentPropertyIndex).getUid());
                            newRequest.setStatus("PENDING");

                            if(propertyList.get(currentPropertyIndex).getUid().isEmpty()){
                                Log.d("TAG", "proeprty list error");
                                return;
                            }

                            systemInDatabaseRead = true;
                            DatabaseReference newRef = ref.child("request").push();
                            newRef.setValue(newRequest.toHashMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        newRef.child("uid").setValue(newRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                ref.child("users").child(user.getUID()).child("managerRequestIdList").child(newRef.getKey()).setValue(newRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        user.addManagerRequestId(newRef.getKey());
                                                        ref.child("users").child(newRequest.getReceiverID()).child("managerRequestIdList").child(newRef.getKey()).setValue(newRef.getKey());
                                                        systemInDatabaseRead = false;
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

        currentPropertyIndex = 0;
    }

    public void findViews(View view){
        dbllWelcome = view.findViewById(R.id.dbll_welcome);
        dbllAdd = view.findViewById(R.id.dbll_add);
        dbllBack = view.findViewById(R.id.dbll_back);
        dbllNext = view.findViewById(R.id.dbll_next);
        dbllEdit = view.findViewById(R.id.dbll_edit);
        dbllAddrDisplay = view.findViewById(R.id.dbll_addr_display);
        dbllAddr = view.findViewById(R.id.dbll_addr);
        dbllType = view.findViewById(R.id.dbll_type);
        dbllFloor = view.findViewById(R.id.dbll_floor);
        dbllRooms = view.findViewById(R.id.dbll_rooms);
        dbllBathrooms = view.findViewById(R.id.dbll_bathrooms);
        dbllFloors = view.findViewById(R.id.dbll_floors);
        dbllArea = view.findViewById(R.id.dbll_area);
        dbllLaundry = view.findViewById(R.id.dbll_laundry);
        dbllParking = view.findViewById(R.id.dbll_parking);
        dbllRent = view.findViewById(R.id.dbll_rent);
        dbllUtils = view.findViewById(R.id.dbll_utils);
        dbllManager = view.findViewById(R.id.dbll_manager);
        dbllClient = view.findViewById(R.id.dbll_client);
        dbllAddR = view.findViewById(R.id.dbll_add_r);
        dbllBackR = view.findViewById(R.id.dbll_back_r);
        dbllCurR = view.findViewById(R.id.dbll_cur_r);
        dbllNextR = view.findViewById(R.id.dbll_next_r);
        dbllRejectR = view.findViewById(R.id.dbll_reject_r);
        dbllAddrR = view.findViewById(R.id.dbll_addr_r);
        dbllNameR = view.findViewById(R.id.dbll_name_r);
        dbllAgeR = view.findViewById(R.id.dbll_age_r);
        dbllEmailR = view.findViewById(R.id.dbll_email_r);
        dbllMsgR = view.findViewById(R.id.dbll_msg_r);
        dbllPageIndex = view.findViewById(R.id.textView5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_dashboard_landlord, container, false);

        findViews(view);

        dbllWelcome.setText("Welcome " + user.getFirstName() + " " + user.getLastName() + " :D");

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshPropertyList(false);


        dbllAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemUser.CURRENT_USER = user.copy();
                MemProperty.CURRENT_PROPERTY = new Property(user.getUID());
                Intent intent = new Intent(getActivity(), IntentProperty.class);
                intent.putExtra("type", "add");
                launcher.launch(intent);
            }
        });
        dbllEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                MemUser.CURRENT_USER = user.copy();
                MemProperty.CURRENT_PROPERTY = propertyList.get(currentPropertyIndex).copy();
                Intent intent = new Intent(getActivity(), IntentProperty.class);
                intent.putExtra("type", "edit");
                launcher.launch(intent);
            }
        });

        dbllNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPropertyIndex = (currentPropertyIndex+1)%propertyList.size();
                updatePropertyView();
            }
        });
        dbllBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPropertyIndex = (currentPropertyIndex+propertyList.size()-1)%propertyList.size();
                updatePropertyView();
            }
        });

        dbllNextR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentClientRequestIndex = (currentClientRequestIndex + 1) % (user.getLandlordRequestIdList().size());
                updateClientRequestView();
            }
        });
        dbllBackR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentClientRequestIndex = (currentClientRequestIndex + user.getLandlordRequestIdList().size()-1) % user.getLandlordRequestIdList().size();
                updateClientRequestView();
            }
        });

        dbllManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemUser.CURRENT_USER = user.copy(); // ensure static remains
                String currentPropertyManagerId = propertyList.get(currentPropertyIndex).getManagerUID();
                Log.d("TAG", currentPropertyManagerId);

                ArrayList<User> u = new ArrayList<>();
                ref.child("Property Manager").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            targetSize = (int)task.getResult().getChildrenCount();
                            if(targetSize == 0) {
                                Toast.makeText(requireContext(), "No Property Managers Available", Toast.LENGTH_LONG).show();
                                return;
                            }
                            completions = 0;
                            for(DataSnapshot d : task.getResult().getChildren()){
                                ref.child("users").child(d.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if(task.isSuccessful()){
                                            completions++;
                                            User newUser = User.FromFirebaseData(task.getResult());
                                            Log.d("TAG", newUser.getUID());
                                            if( currentPropertyManagerId.isEmpty() || !newUser.getUID().equals(currentPropertyManagerId)){
                                                u.add(newUser);
                                            }

                                            if(completions >= targetSize){
                                                if(u.isEmpty()){
                                                    Toast.makeText(requireContext(), "No Property Managers Available", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                MemUser.CURRENT_USER_LIST = u;
                                                Intent i = new Intent(getActivity(), IntentManager.class);
                                                propertyLauncher.launch(i);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });



        dbllAddR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request r = clientRequestList.get(currentClientRequestIndex);
                String propertyId = r.getFromData("propertyId");
                String clientId = r.getOriginID();

                HashMap<String, Request> toBeDeleted = new HashMap<>();

                ref.child("users").child(clientId).child("propertyId").setValue(propertyId);
                ref.child("property").child(propertyId).child("clientUID").setValue(clientId);

                // delete from request
                for(int i = clientRequestList.size()-1; i >= 0; i--){
                    Request curRequest = clientRequestList.get(i);
                    if(curRequest.getFromData("propertyId").equals(propertyId)){
                        toBeDeleted.put(curRequest.getUid(), curRequest);
                    }
                }

                Query q = ref.child("request").orderByChild("originID").equalTo(clientId);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for( DataSnapshot ds : snapshot.getChildren()){
                            Request curRequest = new Request(ds);
                            if(curRequest.getType().equals("LANDLORD_REQUEST")){
                                toBeDeleted.put(curRequest.getUid(), curRequest);
                            }
                        }

                        targetSize = toBeDeleted.size()*3;
                        completions = 0;
                        for(Map.Entry<String, Request> entry : toBeDeleted.entrySet()){
                            Request curRequest = entry.getValue();

                            ref.child("users").child(curRequest.getOriginID()).child("landlordRequestIdList").child(curRequest.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    completions++;
                                    if(completions >= targetSize){
                                        ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                user = new UserLandlord(task.getResult());
                                                refreshPropertyList(true);
                                            }
                                        });
                                    }
                                }
                            });
                            ref.child("users").child(curRequest.getReceiverID()).child("landlordRequestIdList").child(curRequest.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    completions++;
                                    if(completions >= targetSize){
                                        ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                user = new UserLandlord(task.getResult());
                                                refreshPropertyList(true);
                                            }
                                        });
                                    }
                                }
                            });
                            ref.child("request").child(curRequest.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    completions++;
                                    if(completions >= targetSize){
                                        ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                user = new UserLandlord(task.getResult());
                                                refreshPropertyList(true);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        dbllRejectR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request req = clientRequestList.get(currentClientRequestIndex);

                ref.child("users").child(req.getOriginID()).child("landlordRequestIdList").child(req.getUid()).removeValue();
                ref.child("users").child(req.getReceiverID()).child("landlordRequestIdList").child(req.getUid()).removeValue();
                ref.child("request").child(req.getUid()).removeValue();

                ArrayList<String> arr = user.getLandlordRequestIdList();
                for(int i = 0; i < arr.size(); i++){
                    if(arr.get(i).equals(req.getUid())){
                        arr.remove(i);
                        break;
                    }
                }
                user.setLandlordRequestIdList(arr);
                clientRequestList.remove(currentClientRequestIndex);

                currentClientRequestIndex = 0;
                updateClientRequestView();

            }
        });
    }
}
