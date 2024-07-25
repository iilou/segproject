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

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class IntentRequest extends AppCompatActivity {

    private TextView textViewMessageLabel;
    private EditText editTextMessage;
    private TextView textViewAdditionalFieldsLabel;
    private TextView textViewAttr0Label;
    private EditText editTextAttr0;
    private TextView textViewAttr1Label;
    private EditText editTextAttr1;
    private Button buttonConfirm;
    private Button buttonCancel;

    private Request request;
    private String returnType;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_request);


        textViewMessageLabel = findViewById(R.id.textView6);
        editTextMessage = findViewById(R.id.ireq_message);
        textViewAdditionalFieldsLabel = findViewById(R.id.textView9);
        textViewAttr0Label = findViewById(R.id.ireq_label_0);
        editTextAttr0 = findViewById(R.id.ireq_attr_0);
        textViewAttr1Label = findViewById(R.id.ireq_label_1);
        editTextAttr1 = findViewById(R.id.ireq_attr_1);
        buttonConfirm = findViewById(R.id.ireq_confirm);
        buttonCancel = findViewById(R.id.ireq_cancel);

        request = MEM.MEM_REQUEST.copy();

        Intent i = getIntent();
        String type = i.getStringExtra("type");

        if(type.equals("add")){
            this.returnType="add";
        } else if(type.equals("view")){
            this.returnType = "view";
        }

        if(this.returnType.equals("view")){
            editTextMessage.setEnabled(false);
            editTextAttr0.setEnabled(false);
            editTextAttr1.setEnabled(false);
        }

        HashMap<String, String> data = request.getData();
        if(data.isEmpty()){
            textViewAttr0Label.setVisibility(View.INVISIBLE);
            editTextAttr0.setVisibility(View.INVISIBLE);
            textViewAttr1Label.setVisibility(View.INVISIBLE);
            editTextAttr1.setVisibility(View.INVISIBLE);
            textViewAdditionalFieldsLabel.setVisibility(View.INVISIBLE);
        } else if(data.size() == 1){
            textViewAttr1Label.setVisibility(View.INVISIBLE);
            editTextAttr1.setVisibility(View.INVISIBLE);
            Map.Entry<String, String> entry = data.entrySet().iterator().next();
            textViewAttr0Label.setText(entry.getKey());
            editTextAttr0.setText(entry.getValue()==null?"":entry.getValue());
        } else if(data.size() == 2){
            int j = 0;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if(j == 0){
                    textViewAttr0Label.setText(entry.getKey());
                    editTextAttr0.setText(entry.getValue()==null?"":entry.getValue());
                } else {
                    textViewAttr1Label.setText(entry.getKey());
                    editTextAttr1.setText(entry.getValue()==null?"":entry.getValue());
                }
                j++;
            }
        }


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkField(""+editTextMessage.getText(), "", false)) return;
                request.setMessage(""+editTextMessage.getText());

                if(editTextAttr0.getVisibility() == View.VISIBLE){
                    request.setToData(textViewAttr0Label.getText().toString(), editTextAttr0.getText().toString());
                }
                if(editTextAttr1.getVisibility() == View.VISIBLE){
                    request.setToData(textViewAttr1Label.getText().toString(), editTextAttr1.getText().toString());
                }

                MEM.MEM_REQUEST = request.copy();

                Intent i = getIntent();
                setResult(1, i);
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(-1, i);
                finish();
            }
        });



    }

    private boolean checkField(String str, String def, boolean isDouble){
        if(isDouble){
            try {
                Double.parseDouble(str);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return str.isEmpty() && !str.equals(def);
    }
}
