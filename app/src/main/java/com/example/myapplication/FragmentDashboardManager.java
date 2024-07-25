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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentDashboardManager extends Fragment {

    private TextView dbm_welcome;
    private TextView dbm_label_c;
    private Button dbm_back_c;
    private Button dbm_next_c;
    private TextView dbmg_addr_c;
    private Button dbm_details_c;
    private TextView dbm_label_p;
    private Button dbm_back_p;
    private Button dbm_next_p;
    private TextView dbm_addr_p;
    private Button dbm_details_p;
    private TextView dbm_layout_r;
    private Button dbm_accept_r;
    private Button dbm_back_r;
    private Button dbm_next_r;
    private Button dbm_reject_r;
    private TextView dbm_addr_r;
    private TextView dbm_landlord_r;
    private TextView dbm_share_r;
    private TextView dbm_msg_r;
    private Button dbm_details_r;

    private FirebaseDatabase db;
    private DatabaseReference ref;

    private ActivityResultLauncher<Intent> viewIntent;

    private UserManager user;

    private ArrayList<Request> managerRequestList;

    private ArrayList<Property> propertyList;
    private ArrayList<Property> pastPropertyList;

    private int currentManagerRequestIndex;
    private int currentPropertyIndex;
    private int currentPastPropertyIndex;

    private int targetSize;
    private int completions;

    public void refreshManagerRequestList(boolean depth){
        Log.d("TAG", ""+user.getManagerRequestIdList().size());
        targetSize = user.getManagerRequestIdList().size();
        completions = 0;
        managerRequestList = new ArrayList<>();
        toggleManagerRequestVisibility(targetSize>0);

        if(targetSize == 0)  {
            if(depth) refreshPropertyList();
            return;
        }

        for(int i = 0; i < targetSize; i++){
            ref.child("request").child(user.getManagerRequestIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        completions++;
                        managerRequestList.add(new Request(task.getResult()));
                        if(targetSize <= completions){
                            currentManagerRequestIndex = 0;
                            updateManagerRequestView();
                            if(depth) refreshPropertyList();
                        }
                    }
                }
            });
        }
    }
    public void refreshPropertyList(){
        targetSize = user.getPropertyIdList().size();
        completions = 0;
        propertyList = new ArrayList<>();
        togglePropertyList(targetSize > 0);
        if(targetSize == 0){
            refreshPastPropertyList();
        }
        for(int i = 0; i < targetSize; i++){
            ref.child("property").child(user.getPropertyIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        completions++;
                        propertyList.add(new Property(task.getResult()));
                        if(completions >= targetSize){
                            currentPropertyIndex = 0;
                            updatePropertyList();
                            refreshPastPropertyList();
                        }
                    }
                }
            });
        }

    }
    public void refreshPastPropertyList(){
        targetSize = user.getPastPropertyIdList().size();
        completions = 0;
        pastPropertyList = new ArrayList<>();
        togglePastPropertyList(targetSize > 0);

        for(int i = 0; i < targetSize; i++){
            ref.child("property").child(user.getPastPropertyIdList().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        completions++;
                        pastPropertyList.add(new Property(task.getResult()));
                        if(completions >= targetSize){
                            currentPastPropertyIndex = 0;
                            updatePastPropertyList();
                        }
                    }
                }
            });
        }
    }



    private void toggleManagerRequestVisibility(boolean visible){
        dbm_accept_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_back_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_layout_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_next_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_reject_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_share_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_landlord_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_msg_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_details_r.setVisibility(visible?View.VISIBLE:View.INVISIBLE);

        dbm_accept_r.setEnabled(visible);
        dbm_back_r.setEnabled(visible);
        dbm_details_r.setEnabled(visible);
        dbm_next_r.setEnabled(visible);
        dbm_reject_r.setEnabled(visible);

        if(!visible){
            dbm_addr_r.setText("NO PROPERTY REQUESTS");
        }
    }
    private void togglePropertyList(boolean visible){
        dbm_back_c.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_label_c.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_next_c.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_details_c.setVisibility(visible?View.VISIBLE:View.INVISIBLE);

        dbm_back_c.setEnabled(visible);
        dbm_next_c.setEnabled(visible);
        dbm_details_c.setEnabled(visible);

        if(!visible){
            dbmg_addr_c.setText("NO CURRENT PROPERTIES");
        }
    }
    private void togglePastPropertyList(boolean visible){
        dbm_back_p.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_label_p.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_next_p.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        dbm_details_p.setVisibility(visible?View.VISIBLE:View.INVISIBLE);

        dbm_back_p.setEnabled(visible);
        dbm_next_p.setEnabled(visible);
        dbm_details_p.setEnabled(visible);

        if(!visible){
            dbm_addr_p.setText("NO PAST OWNED PROPERTIES");
        }
    }
    private void updateManagerRequestView(){
        dbm_layout_r.setText((currentManagerRequestIndex+1) + "/" + managerRequestList.size());
        dbm_share_r.setText(Double.parseDouble(managerRequestList.get(currentManagerRequestIndex).getFromData("equity"))*100 + "%");
        dbm_msg_r.setText(managerRequestList.get(currentManagerRequestIndex).getMessage());

        ref.child("property").child(managerRequestList.get(currentManagerRequestIndex).getFromData("propertyId")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot ds = task.getResult();
                    dbm_addr_r.setText(ds.child("type").getValue().toString().toUpperCase() + " AT " + ds.child("address").getValue().toString().toUpperCase());
                }
            }
        });

        ref.child("users").child(managerRequestList.get(currentManagerRequestIndex).getOriginID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot ds = task.getResult();
                    dbm_landlord_r.setText(ds.child("firstName").getValue().toString() + " " + ds.child("lastName").getValue().toString());
                }
            }
        });
    }
    private void updatePropertyList(){
        dbm_label_c.setText((currentPropertyIndex+1) + "/" + propertyList.size());
        dbmg_addr_c.setText(propertyList.get(currentPropertyIndex).getType().toUpperCase() + " AT " + propertyList.get(currentPropertyIndex).getAddress().toUpperCase());
    }
    private void updatePastPropertyList(){
        dbm_label_p.setText((currentPropertyIndex+1) + "/" + pastPropertyList.size());
        dbm_addr_p.setText(pastPropertyList.get(currentPastPropertyIndex).getType().toUpperCase() + " AT " + pastPropertyList.get(currentPastPropertyIndex).getAddress().toUpperCase());
    }



    public FragmentDashboardManager() {
        // Required empty public constructor
    }

    private void findViews(View view){
        dbm_welcome = view.findViewById(R.id.dbm_welcome);
        dbm_label_c = view.findViewById(R.id.dbm_label_c);
        dbm_back_c = view.findViewById(R.id.dbm_back_c);
        dbm_next_c = view.findViewById(R.id.dbm_next_c);
        dbmg_addr_c = view.findViewById(R.id.dbmg_addr_c);
        dbm_details_c = view.findViewById(R.id.dbm_details_c);
        dbm_label_p = view.findViewById(R.id.dbm_label_p);
        dbm_back_p = view.findViewById(R.id.dbm_back_p);
        dbm_next_p = view.findViewById(R.id.dbm_next_p);
        dbm_addr_p = view.findViewById(R.id.dbm_addr_p);
        dbm_details_p = view.findViewById(R.id.dbm_details_p);
        dbm_layout_r = view.findViewById(R.id.dbm_layout_r);
        dbm_accept_r = view.findViewById(R.id.dbm_accept_r);
        dbm_back_r = view.findViewById(R.id.dbm_back_r);
        dbm_next_r = view.findViewById(R.id.dbm_next_r);
        dbm_reject_r = view.findViewById(R.id.dbm_reject_r);
        dbm_addr_r = view.findViewById(R.id.dbm_addr_r);
        dbm_landlord_r = view.findViewById(R.id.dbm_landlord_r);
        dbm_msg_r = view.findViewById(R.id.dbm_msg_r);
        dbm_share_r = view.findViewById(R.id.dbm_share_r);
        dbm_details_r = view.findViewById(R.id.dbm_details_r);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        if(!MemUser.CURRENT_USER.getType().equals("Property Manager")) {
            throw new RuntimeException("manager dashboard appeared with no manager selected, weird :c");
        }

        user = (UserManager) MemUser.CURRENT_USER.copy();

        viewIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {

            }
        });
    }

    public void refreshAllViews(){
        refreshManagerRequestList(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dashboard_manager, container, false);

        findViews(view);
        refreshAllViews();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dbm_details_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propertyList.isEmpty()){ // should never need this but just in case
                    return;
                }

                Property curProperty = propertyList.get(currentPropertyIndex);
                MemProperty.CURRENT_PROPERTY = curProperty.copy();

                ArrayList<Ticket> propertyTicketList = new ArrayList<>();
                Query qu = ref.child("ticket").orderByChild("propertyId").equalTo(curProperty.getUid());
                qu.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Ticket newTicket = new Ticket(ds);
                            if(newTicket.getManagerId().equals(user.getUID())){
                                propertyTicketList.add(newTicket);
                            }
                        }

                        MEM.MEM_TICKET_LIST = propertyTicketList;
                        Intent i = new Intent(getActivity(), IntentManagerPropertyView.class);
                        viewIntent.launch(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        dbm_details_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pastPropertyList.isEmpty()){ // should never need this but just in case
                    return;
                }

                MemProperty.CURRENT_PROPERTY = pastPropertyList.get(currentPastPropertyIndex);
            }
        });
        dbm_details_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("property").child(managerRequestList.get(currentManagerRequestIndex).getFromData("propertyId")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isSuccessful()) return;
                        MemProperty.CURRENT_PROPERTY = new Property(task.getResult());

                        Intent i = new Intent(getActivity(), IntentProperty.class);
                        i.putExtra("type", "view");
                        viewIntent.launch(i);
                    }
                });
            }
        });

        dbm_next_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propertyList.isEmpty()) return;
                currentPropertyIndex = (currentPropertyIndex + 1) % propertyList.size();
                updatePropertyList();
            }
        });
        dbm_back_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propertyList.isEmpty()) return;
                currentPropertyIndex = (currentPropertyIndex + propertyList.size()-1) % propertyList.size();
                updatePropertyList();
            }
        });
        dbm_next_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pastPropertyList.isEmpty()) return;
                currentPastPropertyIndex = (currentPastPropertyIndex + 1) % pastPropertyList.size();
                updatePastPropertyList();
            }
        });
        dbm_back_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pastPropertyList.isEmpty()) return;
                currentPastPropertyIndex = (currentPastPropertyIndex + pastPropertyList.size()-1) % pastPropertyList.size();
                updatePastPropertyList();
            }
        });
        dbm_next_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propertyList.isEmpty()) return;
                currentManagerRequestIndex = (currentManagerRequestIndex + 1) % managerRequestList.size();
                updateManagerRequestView();
            }
        });
        dbm_back_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propertyList.isEmpty()) return;
                currentManagerRequestIndex = (currentManagerRequestIndex + managerRequestList.size()-1) % managerRequestList.size();
                updateManagerRequestView();
            }
        });

        dbm_accept_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request r = managerRequestList.get(currentManagerRequestIndex);

                String propertyID = r.getFromData("propertyId");
                String landlordID = r.getOriginID();

                ref.child("users").child(user.getUID()).child("propertyIdList").child(propertyID).setValue(propertyID);
                ref.child("property").child(propertyID).child("managerUID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot ds = task.getResult();
                        if(ds.exists()){
                            // remove property id value from property
                            ref.child("property").child(propertyID).child("managerUID").setValue(user.getUID());

                            Object o = ds.getValue();
                            if(o != null && !(""+o).isEmpty()){
                                String managerId = ""+o;
                                // handle change state of property in the view of the past manager : id = targetProperty.getManagerUID()
                                ref.child("users").child(managerId).child("propertyIdList").child(propertyID).removeValue();
                                ref.child("users").child(managerId).child("pastPropertyIdList").child(propertyID).setValue(propertyID);
                            }
                        }


                        // query all requests -> disable all that match propertyID
                        Query q = ref.child("request").orderByChild("data/propertyId").equalTo(propertyID);
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Request> rl = new ArrayList<>();
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    Request r = new Request(ds);
                                    if(r.getType().equals("ManagerRequest")){
                                        rl.add(r);
                                    }
                                }

                                targetSize = rl.size()*3;
                                completions = 0;

                                for(int i = 0; i < rl.size(); i++){
                                    Request r = rl.get(i);
                                    ref.child("users").child(r.getOriginID()).child("managerRequestIdList").child(r.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            completions++;
                                            if(completions >= targetSize){
                                                ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        user = new UserManager(task.getResult());
                                                        refreshAllViews();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    ref.child("users").child(r.getReceiverID()).child("managerRequestIdList").child(r.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            completions++;
                                            if(completions >= targetSize){
                                                ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        user = new UserManager(task.getResult());
                                                        refreshAllViews();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    ref.child("request").child(r.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            completions++;
                                            if(completions >= targetSize){
                                                ref.child("users").child(user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        user = new UserManager(task.getResult());
                                                        refreshAllViews();
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
            }
        });
    }
}