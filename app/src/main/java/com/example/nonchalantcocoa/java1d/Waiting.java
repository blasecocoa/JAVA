package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Waiting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
    }
    public void goToPrice(View view) {
        Intent intent = new Intent(this, Price.class);
        startActivity(intent);
    }
}
