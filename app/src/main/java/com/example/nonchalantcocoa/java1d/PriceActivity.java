package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PriceActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;

    private Price price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(MainActivity.hostName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
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

    public void go_to_wait_price(View view) {
        // push a Price object
        price = new Price(5,15);
        mHostDatabaseReference.child("price").child(MainActivity.mUsername + "_price").setValue(price);
        Intent intent = new Intent(this, WaitPriceActivity.class);
        startActivity(intent);
    }
}
