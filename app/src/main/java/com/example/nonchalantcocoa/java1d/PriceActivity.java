package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PriceActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;

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

    }

    public void go_to_wait_price(View view) {
        // Get price range from seekBar
        int minPrice = seekBar.getSecondaryProgress() + 5;
        int maxPrice = seekBar.getProgress() * 5;
        // push a Price object
        price = new Price(maxPrice,minPrice);
        mHostDatabaseReference.child("priceList").child(MainActivity.mUsername + "_price").setValue(price);
        Intent intent = new Intent(this, WaitPriceActivity.class);
        startActivity(intent);
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
