package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntentManagerItem extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_manager_item);

        UserManager user = (UserManager) MemUser.USER_2.copy();

        TextView id = findViewById(R.id.imi_id);
        TextView name = findViewById(R.id.imi_name);
        TextView total = findViewById(R.id.imi_ticket);
        TextView rating = findViewById(R.id.imi_rating);
        TextView prev = findViewById(R.id.imi_prev);
        TextView cur = findViewById(R.id.imi_cur);

        Log.d("TAG", ""+user.getTotalScores());

        id.setText(user.getUID());
        name.setText(user.getFirstName() + " " + user.getLastName());
        total.setText(""+user.getTotalScores());
        rating.setText(user.getScore()<0?"N/A":(""+user.getScore()));
        prev.setText(""+user.getPastPropertyIdList().size());
        cur.setText(""+user.getPropertyIdList().size());

        Button b = findViewById(R.id.imi_confirm);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(1, i);
                finish();
            }
        });

    }
}
