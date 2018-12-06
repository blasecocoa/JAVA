package com.example.nonchalantcocoa.java1d;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PriceActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;
    private ValueEventListener mValueEventListener;

    private Price price;
    private SeekBar seekBar;
    private boolean allowBack = false;

    Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        g = Globals.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName());

        seekBar = findViewById(R.id.seekBar);

        attachDatabaseReadListener();
    }

    public void go_to_wait_price(View view) {
        // Get price range from seekBar
        int minPrice = seekBar.getSecondaryProgress() + 5;
        int maxPrice;
        switch(seekBar.getProgress()){
            case 0:
                maxPrice = 5;
                break;
            case 1:
                maxPrice = 10;
                break;
            case 2:
                maxPrice = 25;
                break;
            case 3:
                maxPrice = 50;
                break;
            case 4:
                maxPrice = 100;
                break;
            default:
                maxPrice = 25;
        }
        // push a Price object
        price = new Price(maxPrice,minPrice);
        mHostDatabaseReference.child("priceList").child(MainActivity.mUsername + "_price").setValue(price);
        Intent intent = new Intent(this, WaitPriceActivity.class);
        startActivity(intent);
    }

    private void attachDatabaseReadListener(){
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status = (String) dataSnapshot.getValue();

                    if (status.equals("close")) {
                        // Force user to quit
                        Toast.makeText(getApplicationContext(),
                                "Session is closed",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PriceActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mHostDatabaseReference.child("status").addValueEventListener(mValueEventListener);
        }

    }

    private void detachDatabaseReadListener(){
        if (mValueEventListener != null) {
            mHostDatabaseReference.child("status").removeEventListener(mValueEventListener);
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
                            Intent intent = new Intent(PriceActivity.this,MainActivity.class);
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
                            Intent intent = new Intent(PriceActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }
}
