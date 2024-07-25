package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class IntentManager extends AppCompatActivity {
    // View variables
    private TextView textView3;
    private Button iman_back;
    private TextView iman_batchCounter;
    private Button iman_next;
    private TextView[] iman_names = new TextView[5];
    private TextView[] iman_ratings = new TextView[5];
    private Button[] iman_chooses = new Button[5];

    private Button cancel;


    private ArrayList<UserManager> umList;

    private int currentIndex = 0;


    private void cancel(){
        Intent i = getIntent();
    }


    private void renderBatch(){
        if(currentIndex >= umList.size()){
            cancel();
            return;
        }

        iman_batchCounter.setText(currentIndex*5 + "-" + Math.max(currentIndex*5+5, umList.size()) + " / " + umList.size());
        for(int i = currentIndex*5; i<currentIndex*5+5; i++){
            if(i < umList.size()) { // regular
                iman_names[i%5].setText(umList.get(i).getFirstName() + " " + umList.get(i).getLastName());
                iman_ratings[i%5].setText("Rating: " + Math.round(Math.random()*5)); // IMPLEMENT LATER

                iman_names[i%5].setVisibility(View.VISIBLE);
                iman_ratings[i%5].setVisibility(View.VISIBLE);
                iman_chooses[i%5].setVisibility(View.VISIBLE);
            } else { // hide
                iman_names[i%5].setVisibility(View.INVISIBLE);
                iman_ratings[i%5].setVisibility(View.INVISIBLE);
                iman_chooses[i%5].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void addOnClick(Button b, int index){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User targetUser = umList.get(currentIndex*5+index);
                MemUser.CURRENT_TARGET_USER = targetUser.copy();
                Intent i = getIntent();
                setResult(1, i);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_manager);

        ArrayList<User> u = MemUser.CURRENT_USER_LIST_COPY();
        umList = new ArrayList<>();
        for (int i = 0; i < u.size(); i++) {
            umList.add((UserManager) u.get(i));
        }

        Intent i = getIntent();

        // Initialize view variables
        iman_back = findViewById(R.id.iman_back);
        iman_batchCounter = findViewById(R.id.iman_batchCounter);
        iman_next = findViewById(R.id.iman_next);
        cancel = findViewById(R.id.iman_cancel);

        iman_names[0] = findViewById(R.id.iman_name_0);
        iman_ratings[0] = findViewById(R.id.iman_rating_0);
        iman_chooses[0] = findViewById(R.id.iman_choose_0);

        iman_names[1] = findViewById(R.id.iman_name_1);
        iman_ratings[1] = findViewById(R.id.iman_rating_1);
        iman_chooses[1] = findViewById(R.id.iman_choose_1);

        iman_names[2] = findViewById(R.id.iman_name_2);
        iman_ratings[2] = findViewById(R.id.iman_rating_2);
        iman_chooses[2] = findViewById(R.id.iman_choose_2);

        iman_names[3] = findViewById(R.id.iman_name_3);
        iman_ratings[3] = findViewById(R.id.iman_rating_3);
        iman_chooses[3] = findViewById(R.id.iman_choose_3);

        iman_names[4] = findViewById(R.id.iman_name_4);
        iman_ratings[4] = findViewById(R.id.iman_rating_4);
        iman_chooses[4] = findViewById(R.id.iman_choose_4);

        for(int j = 0; j < 5; j++){
            addOnClick(iman_chooses[j], j);
        }

        iman_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex+1)%((int)Math.ceil((double)umList.size()/5));
                renderBatch();
            }
        });

        iman_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex+((int)Math.ceil((double)umList.size()/5))-1)%((int)Math.ceil((double)umList.size()/5));
                renderBatch();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(-1, i);
                finish();
            }
        });

        currentIndex = 0;
        renderBatch();
    }
}
