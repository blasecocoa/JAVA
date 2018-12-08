package com.example.nonchalantcocoa.java1d;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import com.google.android.maps.GeoPoint;


public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    MapView mapView;
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private LatLng location;
    private Map users;
    private double radius;

    private SeekBar radiusBar;
    private Button locationNextButton;
    private TextView radiusTextView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSessionDatabaseReference;

    public final String TAG = "Logcat";

    private long lastClickTime = 0;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public static String hostName;
    private Globals g;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Circle circle;

    private static long idCounter = 100;

    AutoCompleteTextView mapSearchBox;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    public static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(1.1794, 103.8), new LatLng(1.5089, 103.797)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSessionDatabaseReference = mFirebaseDatabase.getReference().child("Sessions");

        radiusBar = findViewById(R.id.bar_radius);
        locationNextButton = findViewById(R.id.location_next_button);
        radiusTextView = findViewById(R.id.radiusTextView);

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
             }

             @Override
             public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                 // TODO Auto-generated method stub
                 radius = radiusBar.getProgress() + 300;
                 circle.setRadius(radius);
                 radiusTextView.setText(String.valueOf(radius));
//                 t1.setTextSize(progress);
//                 Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();

             }
         }
        );

        locationNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // preventing double, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                hostName = MainActivity.mUsername.toUpperCase().replaceAll("\\s+","") + createID();
                // Set global variable hostName as current userName
                g = Globals.getInstance();
                g.setHostName(hostName); //formated hostName
                location = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                users = new HashMap<String,Boolean>();
                users.put(MainActivity.mUsername,true);
                radius = radiusBar.getProgress() + 300;

                Host host = new Host(location, users, radius);
                mSessionDatabaseReference.child(g.getHostName()).setValue(host);
                g.setHost(true);
                Intent intent = new Intent(LocationActivity.this, HostWaitActivity.class);
                startActivity(intent);
            }
        });

        // Map
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapSearchBox = (AutoCompleteTextView) findViewById(R.id.search);



    }
    public void init(){
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        mapSearchBox.setAdapter(mPlaceAutocompleteAdapter);

        mapSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Log.e(TAG, "You pressed ENTER");

                    // hide virtual keyboard
                    geoLocate();
                }
                return false;
            }
        });
    }
    public void geoLocate(){
        String searchString=mapSearchBox.getText().toString();
        Geocoder geocoder= new Geocoder(LocationActivity.this);
        List<Address> list= new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }catch(IOException e){
            Log.e(TAG,"geoLocate:IOexception:"+e.getMessage());
        }
        if(list.size()>0){
            Address address=list.get(0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(address.getLatitude(),
                            address.getLongitude()), DEFAULT_ZOOM));
            hideSoftkeyboard();
        }
    }
public void hideSoftkeyboard(){
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
}
    public static synchronized String createID()
    {
        return String.valueOf(idCounter++);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        getDeviceLocation();
        updateLocationUI();
        setOnCameraMovelisterner(mMap);
            // Get the current location of the device and set the position of the map.

    }
    public void setOnCameraMovelisterner(final GoogleMap map){
                map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        if (currentLocationMarker != null) {
                            currentLocationMarker.remove();
                        }
                        if (mLastKnownLocation != null) {
                            mLastKnownLocation.setLatitude( mMap.getCameraPosition().target.latitude);
                            mLastKnownLocation.setLongitude( mMap.getCameraPosition().target.longitude);
                            circle.setCenter(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()));
                            String snippet = String.format(Locale.getDefault(),
                                    "Lat: %1$.5f, Long: %2$.5f",
                                    mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            currentLocationMarker= mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()))
                                    .title("HERE?")
                                    .snippet(snippet));
                        }
                        else{
                            Log.e(TAG, "mLastKnownLocation is null");

                        }
                    }

        });
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e(TAG, e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
            }
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e)  {
            Log.e(TAG, e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location)task.getResult();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            String snippet = String.format(Locale.getDefault(),
                                    "Lat: %1$.5f, Long: %2$.5f",
                                    mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            currentLocationMarker= mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()))
                                    .title("HERE?")
                                    .snippet(snippet));
                            init();
                            circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                                    .radius(500)
                                    .strokeWidth(0)
                                    .fillColor(0x220000FF));

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
