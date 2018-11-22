package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {

    private double location;
    private Map users;
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

        // Set hostname for host
        MainActivity.hostName = MainActivity.mUsername;

        locationNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = 1.0;
                users = new HashMap<String,Boolean>();
                users.put(MainActivity.mUsername,true);
                radius = radiusBar.getProgress();

                Host host = new Host(location, users, radius);
                mSessionDatabaseReference.child(MainActivity.mUsername).setValue(host);
                Intent intent = new Intent(LocationActivity.this, HostWaitActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
