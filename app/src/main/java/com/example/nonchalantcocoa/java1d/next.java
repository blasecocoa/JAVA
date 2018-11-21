package com.example.nonchalantcocoa.java1d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class next extends AppCompatActivity {
    private EditText joincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
    }
    public void create(View view) {
        Intent intent = new Intent(this, location.class);
        startActivity(intent);
    }


    public void join_session(View view) {
        joincode = (EditText) findViewById(R.id.joincode);
        Toast.makeText(next.this,
                "enter session code",
                Toast.LENGTH_LONG).show();
        joincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

    }
    public void wait_page(View view) {
        Intent intent = new Intent(this, Waiting.class);
        startActivity(intent);
    }
}
