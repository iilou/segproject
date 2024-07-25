package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class IntentPropertyList extends AppCompatActivity {

    private TextView textView7;
    private Button ipl_back;
    private TextView ipl_label;
    private Button ipl_next;
    private Button ipl_cancel;

    private Button[] ipl_sel = new Button[10];
    private TextView[] ipl_unit = new TextView[10];
    private TextView[] ipl_rent = new TextView[10];
    private Button[] ipl_details = new Button[10];

    private ArrayList<Property> propertyList;
    private int currentIndex;

    private void findViews(){
        textView7 = findViewById(R.id.textView7);
        ipl_back = findViewById(R.id.ipl_back);
        ipl_label = findViewById(R.id.ipl_label);
        ipl_next = findViewById(R.id.ipl_next);
        ipl_cancel = findViewById(R.id.ipl_cancel);

        ipl_sel[0] = findViewById(R.id.ipl_sel_0);
        ipl_sel[1] = findViewById(R.id.ipl_sel_1);
        ipl_sel[2] = findViewById(R.id.ipl_sel_2);
        ipl_sel[3] = findViewById(R.id.ipl_sel_3);
        ipl_sel[4] = findViewById(R.id.ipl_sel_4);
        ipl_sel[5] = findViewById(R.id.ipl_sel_5);
        ipl_sel[6] = findViewById(R.id.ipl_sel_6);
        ipl_sel[7] = findViewById(R.id.ipl_sel_7);
        ipl_sel[8] = findViewById(R.id.ipl_sel_8);
        ipl_sel[9] = findViewById(R.id.ipl_sel_9);

        ipl_unit[0] = findViewById(R.id.ipl_unit_0);
        ipl_unit[1] = findViewById(R.id.ipl_unit_1);
        ipl_unit[2] = findViewById(R.id.ipl_unit_2);
        ipl_unit[3] = findViewById(R.id.ipl_unit_3);
        ipl_unit[4] = findViewById(R.id.ipl_unit_4);
        ipl_unit[5] = findViewById(R.id.ipl_unit_5);
        ipl_unit[6] = findViewById(R.id.ipl_unit_6);
        ipl_unit[7] = findViewById(R.id.ipl_unit_7);
        ipl_unit[8] = findViewById(R.id.ipl_unit_8);
        ipl_unit[9] = findViewById(R.id.ipl_unit_9);

        ipl_rent[0] = findViewById(R.id.ipl_rent_0);
        ipl_rent[1] = findViewById(R.id.ipl_rent_1);
        ipl_rent[2] = findViewById(R.id.ipl_rent_2);
        ipl_rent[3] = findViewById(R.id.ipl_rent_3);
        ipl_rent[4] = findViewById(R.id.ipl_rent_4);
        ipl_rent[5] = findViewById(R.id.ipl_rent_5);
        ipl_rent[6] = findViewById(R.id.ipl_rent_6);
        ipl_rent[7] = findViewById(R.id.ipl_rent_7);
        ipl_rent[8] = findViewById(R.id.ipl_rent_8);
        ipl_rent[9] = findViewById(R.id.ipl_rent_9);

        ipl_details[0] = findViewById(R.id.ipl_details_0);
        ipl_details[1] = findViewById(R.id.ipl_details_1);
        ipl_details[2] = findViewById(R.id.ipl_details_2);
        ipl_details[3] = findViewById(R.id.ipl_details_3);
        ipl_details[4] = findViewById(R.id.ipl_details_4);
        ipl_details[5] = findViewById(R.id.ipl_details_5);
        ipl_details[6] = findViewById(R.id.ipl_details_6);
        ipl_details[7] = findViewById(R.id.ipl_details_7);
        ipl_details[8] = findViewById(R.id.ipl_details_8);
        ipl_details[9] = findViewById(R.id.ipl_details_9);

    }
    public void drawEmptyRow(int index) {
        if (index >= 0 && index < 10) {
            ipl_sel[index].setText("Select");
            ipl_sel[index].setEnabled(false);
            ipl_unit[index].setText("");
            ipl_rent[index].setText("");
            ipl_details[index].setText("Details");
            ipl_details[index].setEnabled(false);
        }
    }
    public void draw(Property p, int index) {
        if (index >= 0 && index < 10) {
            ipl_sel[index].setText("Select");
            ipl_sel[index].setEnabled(true);
            ipl_unit[index].setText(p.getAddress());
            ipl_rent[index].setText("$" + p.getRentInCents() / 100);
            ipl_details[index].setText("Details");
            ipl_details[index].setEnabled(true);
        }
    }
    private void renderBatch(){
        if(propertyList == null || propertyList.isEmpty()){
            return;
        }

        // label : 1-10 / 65 for example
        ipl_label.setText((currentIndex*10+1) + "-" + Math.min(propertyList.size()+1, currentIndex*10+10) + " / " + propertyList.size());
        // draw functions
        for(int i = currentIndex*10; i < i*currentIndex*10+10; i++){
            if(i >= propertyList.size()){
                drawEmptyRow(i%10);
            } else {
                draw(propertyList.get(i), i%10);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_property_list);

        propertyList = MemProperty.CURRENT_PROPERTY_LIST_COPY();
        if(propertyList.isEmpty()){
            Intent i = getIntent();
            setResult(-1, i);
            finish();
        }

        findViews();

        currentIndex = 0;
        renderBatch();

        ipl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(-1, i);
                finish();
            }
        });

        for(int i = 0; i < 10; i++){
            int j = i;
            ipl_sel[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MemProperty.CURRENT_PROPERTY = propertyList.get(currentIndex*10+j);
                    Intent i = getIntent();
                    setResult(1, i);
                    finish();
                }
            });
        }


        ipl_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = currentIndex + 1 % ((int)Math.ceil((double)propertyList.size()/10));
                renderBatch();
            }
        });

        ipl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = currentIndex + ((int)Math.ceil((double)propertyList.size()/10)-1) % ((int)Math.ceil((double)propertyList.size()/10));
                renderBatch();
            }
        });
    }
}
