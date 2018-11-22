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

import java.util.HashMap;
import java.util.Map;

public class CuisineActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;

    private Cuisines cuisines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisines);

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

    public void go_to_wait_cuisine(View view){
        // create a Cuisine object
        Map<String,Boolean> cuisinesMap = new HashMap<>();
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


}
