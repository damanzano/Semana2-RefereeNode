package com.example.david.semana2_refereenode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        Log.i(TAG,"The activitiy is about to start");
    }

    @Override
    protected void onPause(){
        Log.i(TAG,"The activitiy is about to start");
    }
}
