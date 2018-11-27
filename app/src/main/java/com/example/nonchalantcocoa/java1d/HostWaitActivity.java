package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class HostWaitActivity extends AppCompatActivity {

    public final String TAG = "Logcat";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSessionDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;

    private ValueEventListener usersEventListener;

    private int numOfPpl;

    TextView numOfPplTextView;
    Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_waiting);

        g = Globals.getInstance();

        numOfPplTextView = findViewById(R.id.numOfPplTextView);

        TextView textViewSessionCode = findViewById(R.id.textview_session_code);
        textViewSessionCode.setText(g.getHostName());

        Log.i(TAG, "You are in hostWaiting");
        Toast.makeText(HostWaitActivity.this, "Created a session: " + g.getHostName(), Toast.LENGTH_SHORT).show();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSessionDatabaseReference = mFirebaseDatabase.getReference().child("Sessions");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName()).child("users");

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
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
        if (usersEventListener != null) {
            mUsersDatabaseReference.removeEventListener(usersEventListener);
            usersEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
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

    public void goToPrice(View view) {
        // send a start signal to database & go to PriceActivity
        mSessionDatabaseReference.child(g.getHostName()).child("signal").child("start").setValue(true);
        Intent intent = new Intent(this, PriceActivity.class);
        startActivity(intent);
    }
}