package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class WaitCuisineActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGoToResultDatabaseReference;

    private ValueEventListener mValueEventListener;

    private boolean allowBack = false;

    Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_cuisine);

        g = Globals.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mGoToResultDatabaseReference =
                mFirebaseDatabase.getReference()
                        .child("Sessions")
                        .child(g.getHostName())
                        .child("signal")
                        .child("go_to_result");

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        // TODO: 1.3 Add a childEventListener to listen to msg database
        if (mValueEventListener == null) {
            // Create a listener to check if the go_to_cuisine signal is true
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean go_to_result = (boolean) dataSnapshot.getValue();
                    if (go_to_result) {
                        // go to PriceActivity
                        Intent intent = new Intent(WaitCuisineActivity.this, ResultActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mGoToResultDatabaseReference.addValueEventListener(mValueEventListener);
        }

    }

    private void detachDatabaseReadListener(){
        if (mValueEventListener != null) {
            mGoToResultDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
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
