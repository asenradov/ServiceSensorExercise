package com.example.robbieginsburg.assignment1;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{

    private TextView accelX, accelY, accelZ, activity, rms;

    private TextView time1, activity1;
    private TextView time2, activity2;
    private TextView time3, activity3;
    private TextView time4, activity4;
    private TextView time5, activity5;
    private TextView time6, activity6;
    private TextView time7, activity7;
    private TextView time8, activity8;
    private TextView time9, activity9;
    private TextView time10, activity10;

    private Button start;

    private boolean startData, bool2, press, bool3 = false;
    private int tmp;

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

        start = (Button) findViewById(R.id.buttonStart);
        start.setOnClickListener(this);

        time1 = (TextView) findViewById(R.id.time1);
        activity1 = (TextView) findViewById(R.id.activity1);

        time2 = (TextView) findViewById(R.id.time2);
        activity2 = (TextView) findViewById(R.id.activity2);

        time3 = (TextView) findViewById(R.id.time3);
        activity3 = (TextView) findViewById(R.id.activity3);

        time4 = (TextView) findViewById(R.id.time4);
        activity4 = (TextView) findViewById(R.id.activity4);

        time5 = (TextView) findViewById(R.id.time5);
        activity5 = (TextView) findViewById(R.id.activity5);

        time6 = (TextView) findViewById(R.id.time6);
        activity6 = (TextView) findViewById(R.id.activity6);

        time7 = (TextView) findViewById(R.id.time7);
        activity7 = (TextView) findViewById(R.id.activity7);

        time8 = (TextView) findViewById(R.id.time8);
        activity8 = (TextView) findViewById(R.id.activity8);

        time9 = (TextView) findViewById(R.id.time9);
        activity9 = (TextView) findViewById(R.id.activity9);

        time10 = (TextView) findViewById(R.id.time10);
        activity10 = (TextView) findViewById(R.id.activity10);

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        // will call the file assignment.txt
        File file = new File(root, "assignment1.txt");
        file.delete();
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
        MediaScannerConnection.scanFile(this, new String[]{Environment.DIRECTORY_DOCUMENTS}, null, null);
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
                if(press == false){
                    press = true;

                    if(connected){
                        startData = true;
                        bool2 = true;
                    }
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

            //I was getting NullPointerExceptions that were crashing the program
            //It is okay for the program to get one, I just don't want it to crash the program
            //To fix the program crashing, I add a try/catch that keeps the program running
            // when there is a NullPointerException

            //if there is a null pointer exception, ignore it
            try{
                // reset all the activity when the start button is pushed
                // this is needed because the service is started upon the app being opened
                if(bool2 == true){
                    bool2 = false;
                    myService.setBlank();
                }

                time1.setText((myService.getTime1()));
                activity1.setText((myService.getActivity1()));

                time2.setText((myService.getTime2()));
                activity2.setText((myService.getActivity2()));

                time3.setText((myService.getTime3()));
                activity3.setText((myService.getActivity3()));

                time4.setText((myService.getTime4()));
                activity4.setText((myService.getActivity4()));

                time5.setText((myService.getTime5()));
                activity5.setText((myService.getActivity5()));

                time6.setText((myService.getTime6()));
                activity6.setText((myService.getActivity6()));

                time7.setText((myService.getTime7()));
                activity7.setText((myService.getActivity7()));

                time8.setText((myService.getTime8()));
                activity8.setText((myService.getActivity8()));

                time9.setText((myService.getTime9()));
                activity9.setText((myService.getActivity9()));

                time10.setText((myService.getTime10()));
                activity10.setText((myService.getActivity10()));


                // every time a new activity is added to the UI, it will be written to
                // the external storage on a new line

                String externalStorage = Environment.getExternalStorageState();

                // this checks to see if external storage is available
                // if it is, then write to it
                if (Environment.MEDIA_MOUNTED.equals(externalStorage)) {

                    // will create a file in the documents folder
                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    // will call the file assignment.txt
                    File file = new File(root, "assignment1.txt");

                    // this is a helper for the following if statement
                    // see the explanation for the if statement
                    if(bool3 == false){
                        tmp = myService.getSeconds();
                        bool3 = true;
                    }

                    // this if statements makes sure that content is written to the file every 2 minutes
                    // without it, the same content would be written multiple during the minutes,
                    //  instead of just once at the 2 minute mark
                    if ((myService.getSeconds() % myService.getTimeCheck()) == 0 && tmp != myService.getSeconds()){

                        try{
                            // the content that will be written to the
                            String content = myService.getTime1() + " " + myService.getActivity1() + "\n";

                            // this is the code that writes the content to the file on the
                            // external device
                            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                            fileOutputStream.write(content.getBytes());
                            fileOutputStream.close();
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // helper variable, see above
                        bool3 = false;
                    }
                }
                // if external storage is not available, write this Toast message
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "External Storage Is Not Available", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
            //do nothing and keep running the program
            catch (NullPointerException e){

            }
        }
    }
}
