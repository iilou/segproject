package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


public class IntentProperty extends AppCompatActivity{
    private TextView ipropStatus;
    private TextView ipropResidentialType;
    private Button ipropButtonPrev;
    private TextView ipropResidentialTypeValue;
    private Button ipropButtonNext;
    private EditText ipropEditAddress;
    private EditText ipropEditFloorLevel;
    private EditText ipropEditNumRooms;
    private EditText ipropEditNumBathrooms;
    private EditText ipropEditAreaSqft;
    private CheckBox ipropCheckboxLaundryInUnit;
    private EditText ipropEditParkingSpots;
    private EditText ipropEditRent;
    private CheckBox ipropCheckboxHydroIncluded;
    private CheckBox ipropCheckboxHeatingIncluded;
    private CheckBox ipropCheckboxWaterIncluded;
    private Button ipropButtonConfirm;
    private Button ipropButtonCancel;

    private ActivityResultLauncher<Intent> resultLauncher;

    private Property property;
    private String returnType;

    private final static String[] TYPES = {"Basement", "Studio", "Apartment", "Townhouse", "House"};
    private int typeInd;

    private void updateTypeTextView(){
        ipropResidentialTypeValue.setText(IntentProperty.TYPES[typeInd]);
    }

    private void setTextViewWithCheck(EditText e, String s, String defaultString){
        if(!s.equals(defaultString)){
            e.setText(s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_property); // Ensure this matches your XML layout file name

        property = MemProperty.CURRENT_PROPERTY.copy();

        // Bind your views
        ipropStatus = findViewById(R.id.iprop_status);
        ipropResidentialType = findViewById(R.id.iprop_residential_type);
        ipropButtonPrev = findViewById(R.id.iprop_button_prev);
        ipropResidentialTypeValue = findViewById(R.id.iprop_residential_type_value);
        ipropButtonNext = findViewById(R.id.iprop_button_next);
        ipropEditAddress = findViewById(R.id.iprop_edit_address);
        ipropEditFloorLevel = findViewById(R.id.iprop_edit_floor_level);
        ipropEditNumRooms = findViewById(R.id.iprop_edit_num_rooms);
        ipropEditNumBathrooms = findViewById(R.id.iprop_edit_num_bathrooms);
        ipropEditAreaSqft = findViewById(R.id.iprop_edit_area_sqft);
        ipropCheckboxLaundryInUnit = findViewById(R.id.iprop_checkbox_laundry_in_unit);
        ipropEditParkingSpots = findViewById(R.id.iprop_edit_parking_spots);
        ipropEditRent = findViewById(R.id.iprop_edit_rent);
        ipropCheckboxHydroIncluded = findViewById(R.id.iprop_checkbox_hydro_included);
        ipropCheckboxHeatingIncluded = findViewById(R.id.iprop_checkbox_heating_included);
        ipropCheckboxWaterIncluded = findViewById(R.id.iprop_checkbox_water_included);
        ipropButtonConfirm = findViewById(R.id.iprop_button_confirm);
        ipropButtonCancel = findViewById(R.id.iprop_button_cancel);

        typeInd = 0;
        String resType = property.getType();
        for(int i = 0; i < 5; i++){
            if(TYPES[i].equals(resType)) {
                typeInd = i;
            }
        }
        ipropResidentialTypeValue.setText(TYPES[typeInd]);
        setTextViewWithCheck(ipropEditAddress, property.getAddress(), "");
        setTextViewWithCheck(ipropEditFloorLevel, ""+property.getFloorLevel(), "-1");
        setTextViewWithCheck(ipropEditNumRooms, ""+property.getNumRooms(), "-1");
        setTextViewWithCheck(ipropEditNumBathrooms, ""+property.getNumBathrooms(), "-1");
        setTextViewWithCheck(ipropEditAreaSqft, ""+property.getAreaInFeetSq(), "-1");
        setTextViewWithCheck(ipropEditParkingSpots, ""+property.getParkingSpots(), "-1");
        if(property.getRentInCents()!=-1) ipropEditRent.setText(""+(double)(property.getRentInCents()/100));

        ipropCheckboxLaundryInUnit.setChecked(property.isLaundryInUnit());
        ipropCheckboxHydroIncluded.setChecked(property.hydro());
        ipropCheckboxHeatingIncluded.setChecked(property.heating());
        ipropCheckboxWaterIncluded.setChecked(property.water());



        Intent i = getIntent();
        String type = i.getStringExtra("type");
        if(type.equals("add")){
            this.returnType="add";
        } else if (type.equals("edit")){
            this.returnType = "edit";
        } else if(type.equals("view")){
            this.returnType = "view";
        }






        if(returnType.equals("view")){
            ipropEditAddress.setEnabled(false);
            ipropEditFloorLevel.setEnabled(false);
            ipropEditNumRooms.setEnabled(false);
            ipropEditNumBathrooms.setEnabled(false);
            ipropEditAreaSqft.setEnabled(false);
            ipropCheckboxLaundryInUnit.setEnabled(false);
            ipropEditParkingSpots.setEnabled(false);
            ipropEditRent.setEnabled(false);
            ipropCheckboxHydroIncluded.setEnabled(false);
            ipropCheckboxHeatingIncluded.setEnabled(false);
            ipropCheckboxWaterIncluded.setEnabled(false);
            ipropButtonPrev.setEnabled(false);
            ipropButtonNext.setEnabled(false);
            ipropButtonCancel.setEnabled(false);
        } else {
            ipropButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeInd = (typeInd+1)%5;
                    updateTypeTextView();
                }
            });
            ipropButtonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeInd = (typeInd+4)%5;
                    updateTypeTextView();
                }
            });
        }


        // Set up any listeners or other initialization logic here
        ipropButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emptyCheck()){
                    Toast.makeText(IntentProperty.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!returnType.equals("view")){
                    property.setAddress(ipropEditAddress.getText().toString());
                    property.setType(TYPES[typeInd]);
                    property.setFloorLevel(Integer.parseInt(ipropEditFloorLevel.getText().toString()));
                    property.setNumRooms(Integer.parseInt(ipropEditNumRooms.getText().toString()));
                    property.setNumBathrooms(Integer.parseInt(ipropEditNumBathrooms.getText().toString()));
                    property.setAreaInFeetSq(Integer.parseInt(ipropEditAreaSqft.getText().toString()));
                    property.setLaundryInUnit(ipropCheckboxLaundryInUnit.isChecked());
                    property.setParkingSpots(Integer.parseInt(ipropEditParkingSpots.getText().toString()));
                    property.setRentInCents((int)(Double.parseDouble(ipropEditRent.getText().toString())*100));
                    property.setHydro(ipropCheckboxHydroIncluded.isChecked());
                    property.setHeater(ipropCheckboxHeatingIncluded.isChecked());
                    property.setWater(ipropCheckboxWaterIncluded.isChecked());
                }

                MemProperty.CURRENT_PROPERTY = property.copy();

                //handle send back
                Intent returnIntent = getIntent();
                int resultCode = 1;
                if(returnType.equals("add")){
                    resultCode = 2;
                } else if(returnType.equals("edit")){
                    resultCode = 1;
                } else if(returnType.equals("view")){
                    resultCode = 0;
                }
                setResult(resultCode, returnIntent);
                finish();
            }
        });

        ipropButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent r = getIntent();
                setResult(-1, r);
                finish();
            }
        });


    }

    // Method to check if any EditText is empty
    private boolean emptyCheck() {
        EditText[] editTexts = {
                ipropEditAddress,
                ipropEditFloorLevel,
                ipropEditNumRooms,
                ipropEditNumBathrooms,
                ipropEditAreaSqft,
                ipropEditParkingSpots,
                ipropEditRent
        };

        for (EditText editText : editTexts) {
            String text = editText.getText().toString().trim();
            if (text.isEmpty()) {
                return true; // Check if any field is empty
            }

            if (editText != ipropEditRent && editText != ipropEditAddress) {
                try {
                    Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    Toast.makeText(IntentProperty.this, "Please enter valid integers for numerical fields.", Toast.LENGTH_SHORT).show();
                    return true; // Invalid integer found
                }
            }
        }
        return false;
    }


}
