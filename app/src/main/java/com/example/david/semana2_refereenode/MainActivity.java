package com.example.david.semana2_refereenode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create communication instance and set this object as observer
        CommunicationManager.getInstance().addObserver(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG,"The activitiy is about to start");
        CommunicationManager.getInstance().sendMessage("start", CommunicationManager.MULTI_GROUP_ADDRESS,CommunicationManager.DEFAULT_PORT);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG,"The activitiy is about to start");
        CommunicationManager.getInstance().sendMessage("stop", CommunicationManager.MULTI_GROUP_ADDRESS,CommunicationManager.DEFAULT_PORT);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof String){
            String message = (String) o;
            updateUI(message);

        }
    }

    public void updateUI(final String change){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView arrives = (TextView)findViewById(R.id.arrives);
                arrives.setText(arrives.getText().toString() + change + "\n");
            }
        });
    }
}
