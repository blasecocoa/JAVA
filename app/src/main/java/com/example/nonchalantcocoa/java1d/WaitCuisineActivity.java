package com.example.nonchalantcocoa.java1d;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
    private DatabaseReference mHostDatabaseReference;
    private DatabaseReference mGoToResultDatabaseReference;

    private ValueEventListener mValueEventListener;
    private ValueEventListener mStatusEventListener;

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
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName());


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
                        Intent intent = new Intent(WaitCuisineActivity.this, MainActivity.class);
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
        if (mValueEventListener != null) {
            mGoToResultDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
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

                            mHostDatabaseReference.child("users").child(MainActivity.mUsername).setValue(false);
                            mHostDatabaseReference.child("status").setValue("close");
                            Intent intent = new Intent(WaitCuisineActivity.this,MainActivity.class);
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

                            mHostDatabaseReference.child("users").child(MainActivity.mUsername).setValue(false);
                            Intent intent = new Intent(WaitCuisineActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }
}
