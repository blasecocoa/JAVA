package com.example.nonchalantcocoa.java1d;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.bumptech.glide.Glide;

public class ResultActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHostDatabaseReference;
    private DatabaseReference mResultDatabaseReference;

    private ValueEventListener mValueEventListener;
    private ValueEventListener mCounterEventListener;

    private boolean allowBack = false;
    public final String TAG = "Logcat";

    private Button goMainButton;
    private Button nextChoiceButton;
    private TextView shopNameTextView;
    private TextView shopDescripTextView;
    private ImageView shopImageView;

    private List<Shop> shopList = new ArrayList<>();
    private Shop currentShop;
    private int shopCounter = 0;
    private Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        g = Globals.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHostDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName());
        mResultDatabaseReference = mFirebaseDatabase.getReference().child("Sessions").child(g.getHostName()).child("resultList");


        goMainButton = findViewById(R.id.goMainButton);
        nextChoiceButton = findViewById(R.id.nextChoiceButton);
        shopNameTextView = findViewById(R.id.shopNameTextView);
        shopDescripTextView = findViewById(R.id.shopDescriptionTextView);
        shopImageView = findViewById(R.id.shopImageView);

        attachDatabaseReadListener();

        goMainButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (g.isHost()){
                    mHostDatabaseReference.child("status").setValue("close");
                }
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        nextChoiceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                shopCounter += 1;
                shopCounter = shopCounter % shopList.size();
                mHostDatabaseReference.child("shopCounter").setValue(shopCounter);
            }
        });

        ///////////// Push mock up resultList //////////////////
//        List<Shop> resultList = new ArrayList<>();
//        Shop shop1 = new Shop("Keisuke", "Japanese",
//                "https://firebasestorage.googleapis.com/v0/b/eatwhere-3090c.appspot.com/o/keisuke.png?alt=media&token=0c3e0c72-c446-4427-ab30-0ab5439f28f5"
//                ,new LatLng(37.4219983,-122.084), "15-20", "Ramen");
//        Shop shop2 = new Shop("Macdooners", "Fast Food",
//                "https://firebasestorage.googleapis.com/v0/b/eatwhere-3090c.appspot.com/o/mcdonalds.png?alt=media&token=55833f4a-bd8d-4362-b3b7-ddb590ebb31e"
//                ,new LatLng(37.4219983,-122.084), "10-15", "you sin if you eat here \n urfat");
//        resultList.add(shop1);
//        resultList.add(shop2);
//        mResultDatabaseReference.setValue(resultList);
        ////////////////////////////////////////////////////////
    }

    private void showNextUI() {
        // Update UI for next shop
        Log.i(TAG, "shopCounter: " + shopCounter);
        if (!shopList.isEmpty()) {
            currentShop = shopList.get(shopCounter);
            shopNameTextView.setText(currentShop.getName());
            shopDescripTextView.setText(currentShop.getTags());
            Glide.with(ResultActivity.this)
                    .load(currentShop.getImageID())
                    .into(shopImageView);
        }
    }

    private void attachDatabaseReadListener(){
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get shopList
                    try{
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String name = ds.child("name").getValue(String.class);
                            Log.i(TAG, "name: " + name);
                            String cuisine = ds.child("cuisine").getValue(String.class);
                            Log.i(TAG, "cuisine: " + cuisine);
                            String imageID = ds.child("imageID").getValue(String.class);
                            Log.i(TAG, "imageID: " + imageID);
                            LatLng location = new LatLng(
                                    ds.child("location").child("latitude").getValue(Double.class),
                                    ds.child("location").child("longitude").getValue(Double.class));
                            Log.i(TAG, "location: " + location.toString());
                            String price = ds.child("price").getValue(String.class);
                            Log.i(TAG, "price: " + price);
                            String tags = ds.child("tags").getValue(String.class);
                            Log.i(TAG, "tags: " + tags);
                            Shop tempShop = new Shop(name, cuisine, imageID, location, price, tags);
                            shopList.add(tempShop);
                        }
                        if (shopList.isEmpty()) {
                            // No need remove user from users list
                            mHostDatabaseReference.child("status").setValue("close");
                            Toast.makeText(getApplicationContext(),
                                    "No available shops with given parameters",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        Log.i(TAG, "Get a shopList: " + shopList.toString());
                        currentShop = shopList.get(shopCounter);
                        if (currentShop != null && shopCounter == 0) {
                            shopNameTextView.setText(currentShop.getName());
                            Log.i(TAG, "shopName: " + currentShop.getName());
                            shopDescripTextView.setText(currentShop.getTags());
                            Glide.with(ResultActivity.this)
                                    .load(currentShop.getImageID())
                                    .into(shopImageView);
                        }
                    }catch(RuntimeException ex){
                        ex.printStackTrace();
                        Log.i(TAG, "Failed to get a shopList");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            };
            mResultDatabaseReference.addValueEventListener(mValueEventListener);

        // listener for shopCounter
        if (mCounterEventListener == null) {
            mCounterEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Update shopCounter & update image and text
                    shopCounter = dataSnapshot.getValue(Integer.class);
                    Toast.makeText(ResultActivity.this,
                            "Your friend had clicked next choice",
                            Toast.LENGTH_LONG).show();
                    showNextUI();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            };
            mHostDatabaseReference.child("shopCounter").addValueEventListener(mCounterEventListener);
        }
        }
    }

    private void detachDatabaseReadListener(){
        if (mValueEventListener != null) {
            mResultDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
        if (mCounterEventListener != null) {
            mHostDatabaseReference.child("shopCounter").removeEventListener(mCounterEventListener);
            mCounterEventListener = null;
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
                    .setMessage("Are you sure you want to leave this session?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHostDatabaseReference.child("users").child(MainActivity.mUsername).setValue(false);
                            mHostDatabaseReference.child("status").setValue("close");
                            Intent intent = new Intent(ResultActivity.this,MainActivity.class);
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
                            Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }
}
