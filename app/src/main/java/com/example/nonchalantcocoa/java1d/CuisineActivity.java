package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class CuisineActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;

    private Cuisines cuisines;
    private Cuisines ava_cuisines;

    private Button setCuisineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisines);

//        final LinearLayout ll = new LinearLayout(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(MainActivity.hostName);

        //////// Temporary code to push a list of mock up available cuisines /////////////
        final Map<String,Boolean> avaCuisinesMap = new HashMap<>();
        avaCuisinesMap.put("Indian",true);
        avaCuisinesMap.put("Thai",true);
        avaCuisinesMap.put("Chinese",true);
        ava_cuisines = new Cuisines(avaCuisinesMap);
        mHostDatabaseReference.child("ava_cuisines").setValue(avaCuisinesMap);
        //////////////////////////////////////////////////////////////////////////////////

        // Get ava_cuisine from firebase
//        mHostDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Cuisines avaCuisineMap = (Cuisines)dataSnapshot.getValue();
//
//                if (avaCuisineMap != null) {
//                    // Generate a list of checkbox
//                    Iterator iterator = avaCuisineMap.cuisines.entrySet().iterator();
//                    while (iterator.hasNext()) {
//                        Map.Entry pair = (Map.Entry) iterator.next();
//                        System.out.println("Key: "+pair.getKey() + " & Value: " + pair.getValue());
//                        CheckBox ch = new CheckBox(getApplicationContext());
//                        ch.setText(pair.getKey().toString());
//                        ll.addView(ch);
//                    }
//
//                } else {
//                    Toast.makeText(CuisineActivity.this,
//                            "Cannot get available cuisine map",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // ...
//            }
//        });


        setCuisineButton = findViewById(R.id.set_cuisine_button);

        setCuisineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// create a Cuisine object
                Map<String,Boolean> cuisinesMap = new HashMap<>();
                // TODO: get checkbox values in a loop
//                Iterator iterator = avaCuisinesMap.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    Map.Entry pair = (Map.Entry) iterator.next();
//
//                }
                cuisinesMap.put("Indian",true);
                cuisinesMap.put("Thai",true);
                cuisinesMap.put("Chinese",true);
                cuisines = new Cuisines(cuisinesMap);
                // push the Cuisine object
                mHostDatabaseReference.child("cuisines").child(MainActivity.mUsername + "_cuisines").setValue(cuisines);
                // go to wait_cuisine page
                Intent intent = new Intent(CuisineActivity.this, WaitCuisineActivity.class);
                startActivity(intent);
            }
        });
    }



}
