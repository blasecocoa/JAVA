package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    private boolean allowBack = false;

    Button goMainButton;
    Button nextChoiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        goMainButton = findViewById(R.id.goMainButton);
        nextChoiceButton = findViewById(R.id.nextChoiceButton);

        goMainButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        nextChoiceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(ResultActivity.this,
                        "TODO: Please show next choice",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!allowBack) {
            Toast.makeText(this,
                    "Not allowed to go back",
                    Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }

}
