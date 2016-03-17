package com.example.robbieginsburg.assignment1;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{

    private TextView accelX, accelY, accelZ, activity, rms;

    private Button start;

    private boolean startData = false;

    BoundedService.MyBinder binder_;
    BoundedService myService;
    Boolean connected = false;

    private SensorManager sensormanager_;
    private Sensor accelerometer_;
    private final int DELAY = 100;
    private Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //start the bounded service
        Intent myIntent = new Intent(this, BoundedService.class);
        bindService(myIntent, mConnection, BIND_AUTO_CREATE);
        //bounded service is started

        accelX = (TextView) findViewById(R.id.accelX);
        accelY = (TextView) findViewById(R.id.accelY);
        accelZ = (TextView) findViewById(R.id.accelZ);

        activity = (TextView) findViewById(R.id.activity);
        rms = (TextView) findViewById(R.id.rms);

        start = (Button) findViewById(R.id.buttonStart);
        start.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensormanager_ = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer_ = sensormanager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensormanager_.registerListener(this, accelerometer_, SensorManager.SENSOR_DELAY_NORMAL, DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensormanager_.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.buttonStart:
                if(connected){
                    startData = true;
                }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder_=(BoundedService.MyBinder)service;
            myService = binder_.getService();
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(startData == true){
            Sensor mySensor = event.sensor;
            if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                myHandler.post(new Update(event));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class Update implements Runnable {

        private SensorEvent event_;

        public Update(SensorEvent event) {
            event_ = event;
        }

        @Override
        public void run() {

            //I was getting NullPOinterException that were craching the program
            //It is okay for the program to get one, I just don't want it to crash the program
            //To fix the program crashing, I add a try/catch that keeps the program running
            // when there is a NullPointerException

            //if there is a null point exception, catch it
            try{
                activity.setText((myService.getActivity()));
                rms.setText(Double.toString(myService.getData()));

                accelX.setText(Double.toString(myService.getX()));
                accelY.setText(Double.toString(myService.getY()));
                accelZ.setText(Double.toString(myService.getZ()));
            }
            //do nothing and keep running the program
            catch (NullPointerException e){

            }
        }
    }
}
