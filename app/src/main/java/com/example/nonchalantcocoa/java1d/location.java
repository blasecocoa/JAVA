package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class location extends AppCompatActivity {

    private double location;
    private int num_user;
    private double radius;

    private SeekBar radiusBar;
    private Button locationNextButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSessionDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSessionDatabaseReference = mFirebaseDatabase.getReference().child("Sessions");

        radiusBar = findViewById(R.id.bar_radius);
        locationNextButton = findViewById(R.id.location_next_button);

        locationNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = 1.0;
                num_user = 0;
                radius = radiusBar.getProgress();
                Host host = new Host(location, num_user, radius);
                mSessionDatabaseReference.child(MainActivity.mUsername).setValue(host);
                Intent intent = new Intent(location.this, hostWaiting.class);
                startActivity(intent);
            }
        });
    }
}
