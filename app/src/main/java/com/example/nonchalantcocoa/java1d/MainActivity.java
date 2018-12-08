package com.example.nonchalantcocoa.java1d;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import java.util.Arrays;

//import javax.xml.transform.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private EditText textEditCode;
    private long lastClickTime = 0;
    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private boolean mCameraPermissionGranted;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSessionDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ZXingScannerView mScannerView;
    public static String mUsername;
    private static String hostName;
    private Globals g;

    public final String TAG = "Logcat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSessionDatabaseReference = mFirebaseDatabase.getReference().child("Sessions");

        textEditCode =findViewById(R.id.textEditCode);

        // TODO: 1.4 Add a AuthStateListener & attach it in onResume()
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get current firebase user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Toast.makeText(MainActivity.this, "You are now signed in.", Toast.LENGTH_SHORT).show();
                    onSignedInInitialized(user.getDisplayName());


                } else {
                    //user is signed out
                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }
    public void onClick(View v) {
        if (mCameraPermissionGranted) {
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);
            mScannerView.setResultHandler(MainActivity.this);
            mScannerView.startCamera();
        } else {
            getCameraPermission();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mScannerView!=null) {
            mScannerView.stopCamera();
            mScannerView = null;
            setContentView(R.layout.activity_main);
        } else {
            super.onBackPressed();
            this.finishAffinity();
        }
    }


    private void getCameraPermission() {
        /*
         * Request camera permission, so that we can access camera of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mCameraPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraPermissionGranted = true;
                    mScannerView= new ZXingScannerView(this);
                    setContentView(mScannerView);
                    mScannerView.setResultHandler(MainActivity.this);
                    mScannerView.startCamera();
                }
            }
        }
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

    public void create(View view) {
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }


    public void wait_page(View view) {
        // Check if the session exist
        hostName = textEditCode.getText().toString();
        mSessionDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!hostName.isEmpty()) {
                    boolean hasCode = dataSnapshot.hasChild(hostName);
                    Log.i(TAG, "hasCode = " + hasCode);

                    if (hasCode) {
                        String status = dataSnapshot.child(hostName).child("status").getValue(String.class);
                        Log.i(TAG, "Joined a session: status = " + status);
                        if (status.equals("open")){
                            // Set hostName in Globals
                            g = Globals.getInstance();
                            g.setHostName(hostName);
                            Log.i(TAG, "hostName = " + g.getHostName());
                            // Append user_ls with current user
                            mSessionDatabaseReference.child(g.getHostName()).child("users").child(MainActivity.mUsername).setValue(true);
                            g.setHost(false);
                            Intent intent = new Intent(MainActivity.this, WaitActivity.class);
                            startActivity(intent);
                        } else{
                            Toast.makeText(MainActivity.this,
                                    "Session is " + status + ", too late to join. =(",
                                    Toast.LENGTH_LONG).show();
                            Log.i(TAG, "Session is not allowed to join now: status = " + status);
                        }

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Session does not exist",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please enter a session code",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        if(mScannerView!=null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    private void onSignedInInitialized(String username) {
        mUsername = username;
    }

    private void onSignOutCleanup() {
        mUsername = ANONYMOUS;
    }

    @Override
    public void handleResult(final Result result) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Scan QR");
        hostName = result.getText().toString();
        mSessionDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!hostName.isEmpty()) {
                    boolean hasCode = dataSnapshot.hasChild(hostName);
                    Log.i(TAG, "hasCode = " + hasCode);

                    if (hasCode) {
                        String status = dataSnapshot.child(hostName).child("status").getValue(String.class);
                        Log.i(TAG, "Joined a session: status = " + status);
                        if (status.equals("open")){
                            // Set hostName in Globals
                            Globals g = Globals.getInstance();
                            g.setHostName(hostName);
                            Log.i(TAG, "hostName = " + g.getHostName());
                            // Append user_ls with current user
                            mSessionDatabaseReference.child(g.getHostName()).child("users").child(MainActivity.mUsername).setValue(true);
                            g.setHost(false);
                            Intent intent = new Intent(MainActivity.this, WaitActivity.class);
                            startActivity(intent);
                        } else{
                            Toast.makeText(MainActivity.this,
                                    "Session is " + status + ", too late to join. =(",
                                    Toast.LENGTH_LONG).show();
                            Log.i(TAG, "Session is not allowed to join now: status = " + status);
                        }

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Session does not exist",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please scan a valid QR session code",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}
