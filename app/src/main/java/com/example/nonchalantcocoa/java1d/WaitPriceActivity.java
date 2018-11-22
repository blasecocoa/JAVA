package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitPriceActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_price);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(MainActivity.hostName);

        // Create a listener to check if the start signal is true
        mHostDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean go_to_cuisine = (boolean) dataSnapshot.child("go_to_cuisine").getValue();
                Toast.makeText(WaitPriceActivity.this,
                        "go_to_cuisine = " + go_to_cuisine,
                        Toast.LENGTH_LONG).show();

                if (go_to_cuisine) {
                    // go to CuisineActivity
                    Intent intent = new Intent(WaitPriceActivity.this, CuisineActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}
