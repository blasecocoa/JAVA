package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private Cuisines cuisinesObj;

    private ListView cuisineListView;
    private CheckBoxAdapter checkBoxAdapter;

    private ChildEventListener mChildEventListener;

    private boolean allowBack = false;


    private Button setCuisineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisines);

        Globals g = Globals.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName());
        mAvaCuisineDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName()).child("ava_cuisines");

        cuisineListView = (ListView) findViewById(R.id.cuisineListView);
        cuisineListView.setVisibility(View.VISIBLE);

        // Initialize message ListView and its adapter
        List<Cuisine> cuisineList = new ArrayList<>();
        checkBoxAdapter = new CheckBoxAdapter(CuisineActivity.this, R.layout.item_cuisine, cuisineList);
        checkBoxAdapter.notifyDataSetChanged();
        cuisineListView.setAdapter(checkBoxAdapter);

        //////// Temporary code to push a list of mock up available cuisines /////////////
        List<Cuisine> avaCuisinesList = new ArrayList<>();
        avaCuisinesList.add(new Cuisine("Indian"));
        avaCuisinesList.add(new Cuisine("Chinese"));
        avaCuisinesList.add(new Cuisine("Thai"));
        mHostDatabaseReference.child("ava_cuisines").setValue(avaCuisinesList);
        //////////////////////////////////////////////////////////////////////////////////

        attachDatabaseReadListener();


        setCuisineButton = findViewById(R.id.set_cuisine_button);

        setCuisineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// create a Cuisine object
                Map<String,Boolean> cuisinesMap = checkBoxAdapter.getItemStateMap();
                // TODO: get checkbox values in a loop
                cuisinesObj = new Cuisines(cuisinesMap);
                // push the Cuisine object
                mHostDatabaseReference.child("cuisines").child(MainActivity.mUsername + "_cuisines").setValue(cuisinesObj);
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
                        Cuisine cuisine = dataSnapshot.getValue(Cuisine.class);
                        Log.i("Logcat", "Adapter add a cuisine: " + cuisine.getText());
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

    }

    private void detachDatabaseReadListener(){
        if (mChildEventListener != null) {
            mAvaCuisineDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
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
        if (!allowBack) {
            Toast.makeText(this,
                    "Not allowed to go back",
                    Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }
}
