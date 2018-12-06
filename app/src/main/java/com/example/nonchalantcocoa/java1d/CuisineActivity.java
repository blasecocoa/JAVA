package com.example.nonchalantcocoa.java1d;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CuisineActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;
    private DatabaseReference mAvaCuisineDatabaseReference;

    private ListView cuisineListView;
    private CheckBoxAdapter checkBoxAdapter;

    private ChildEventListener mChildEventListener;
    private ValueEventListener mStatusEventListener;

    private boolean allowBack = false;


    private Button setCuisineButton;
    private Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisines);

        g = Globals.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName());
        mAvaCuisineDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName()).child("avaCuisineList");

        cuisineListView = (ListView) findViewById(R.id.cuisineListView);
        cuisineListView.setVisibility(View.VISIBLE);

        // Initialize message ListView and its adapter
        List<String> cuisineList = new ArrayList<>();
        checkBoxAdapter = new CheckBoxAdapter(CuisineActivity.this, R.layout.item_cuisine, cuisineList);
        checkBoxAdapter.notifyDataSetChanged();
        cuisineListView.setAdapter(checkBoxAdapter);

        //////// Temporary code to push a list of mock up available cuisines /////////////
        List<String> avaCuisinesList = new ArrayList<>();
        avaCuisinesList.add("Indian");
        avaCuisinesList.add("Chinese");
        avaCuisinesList.add("Thai");
        mHostDatabaseReference.child("avaCuisineList").setValue(avaCuisinesList);
        //////////////////////////////////////////////////////////////////////////////////

        attachDatabaseReadListener();

        setCuisineButton = findViewById(R.id.set_cuisine_button);

        setCuisineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// create a Cuisine object
                Map<String,Boolean> cuisinesMap = checkBoxAdapter.getItemStateMap();
                // TODO: get checkbox values in a loop
                // push the Cuisine object
                mHostDatabaseReference.child("cuisineList").child(MainActivity.mUsername + "_cuisines").setValue(cuisinesMap);
                // go to wait_cuisine page
                Intent intent = new Intent(CuisineActivity.this, WaitCuisineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attachDatabaseReadListener(){
        // TODO: 1.3 Add a childEventListener to listen to msg database
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // TODO: Get cuisine and convert it into a Cuisine object & store it in checkBoxAdapter
                    try{
                        String cuisine = dataSnapshot.getValue(String.class);
                        Log.i("Logcat", "Adapter add a cuisine: " + cuisine);
                        checkBoxAdapter.add(cuisine);
                        Log.i("Logcat", "getCount = " + String.valueOf((checkBoxAdapter.getCount())));
                    }catch(RuntimeException ex){
                        ex.printStackTrace();
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            mAvaCuisineDatabaseReference.addChildEventListener(mChildEventListener);
        }

        if (mStatusEventListener == null) {
            // Create a listener to check if the go_to_cuisine signal is true
            mStatusEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status = (String) dataSnapshot.getValue();

                    if (status.equals("close")) {
                        // Force user to quit
                        Toast.makeText(getApplicationContext(),
                                "Session is closed",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CuisineActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mHostDatabaseReference.child("status").addValueEventListener(mStatusEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if (mChildEventListener != null) {
            mAvaCuisineDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if (mStatusEventListener != null) {
            mHostDatabaseReference.child("status").removeEventListener(mStatusEventListener);
            mStatusEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        checkBoxAdapter.clear();
    }

    @Override
    public void onBackPressed() {
        if (g.isHost()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Session: " + g.getHostName())
                    .setMessage("Are you sure you want to close this session and force everyone out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mHostDatabaseReference.child("users").child(MainActivity.mUsername).removeValue();
                            mHostDatabaseReference.child("status").setValue("close");
                            Intent intent = new Intent(CuisineActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quiting Session: " + g.getHostName())
                    .setMessage("Are you sure you want to quit this session?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mHostDatabaseReference.child("users").child(MainActivity.mUsername).removeValue();
                            Intent intent = new Intent(CuisineActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }
}
