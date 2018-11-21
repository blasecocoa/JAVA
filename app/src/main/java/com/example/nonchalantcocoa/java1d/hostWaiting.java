package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class hostWaiting extends AppCompatActivity {

    public final String TAG = "Logcat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_waiting);

        TextView textViewSessionCode = findViewById(R.id.textview_session_code);
        textViewSessionCode.setText(MainActivity.mUsername);

        Log.i(TAG, "You are in hostWaiting");
        Toast.makeText(hostWaiting.this, "You are in hostWaiting", Toast.LENGTH_SHORT).show();

    }
    public void goToPrice(View view) {
        Intent intent = new Intent(this, Price.class);
        startActivity(intent);
    }
}