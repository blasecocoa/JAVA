package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class WaitActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mStartDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;

    private ValueEventListener mValueEventListener;
    private ValueEventListener usersEventListener;

    private int numOfPpl;

    TextView numOfPplTextView;

    Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        g = Globals.getInstance();

        numOfPplTextView = findViewById(R.id.numOfPplTextView);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStartDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName()).child("signal").child("start");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName()).child("users");

        attachDatabaseReadListener();

    }

    private void attachDatabaseReadListener(){
        // TODO: 1.3 Add a childEventListener to listen to msg database
        if (mValueEventListener == null) {
            // Create a listener to check if the start signal is true
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean start = (boolean) dataSnapshot.getValue();
                    if (start) {
                        // go to PriceActivity
                        Intent intent = new Intent(WaitActivity.this, PriceActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mStartDatabaseReference.addValueEventListener(mValueEventListener); // Listen to start signal

        }

        if (usersEventListener == null) {
            // Create a listener to check the users list
            usersEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        numOfPpl = users.size();
                        numOfPplTextView.setText("Number of People: "+String.valueOf(numOfPpl));
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                        Log.i("Logcat","Cannot count number of ppl");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mUsersDatabaseReference.addValueEventListener(usersEventListener); // Listen to num of ppl
        }

    }

    private void detachDatabaseReadListener(){
        if (mValueEventListener != null) {
            mStartDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
        if (usersEventListener != null) {
            mUsersDatabaseReference.removeEventListener(usersEventListener);
            usersEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location, menu);
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
