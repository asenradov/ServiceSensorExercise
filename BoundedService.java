package com.example.robbieginsburg.assignment1;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BoundedService extends Service implements SensorEventListener {

    private final int DELAY = 100;
    private final int SIZE = 100;
    private SensorManager sensorManager_;
    private Sensor accelerometer_;
    private int counter = 0;
    private double[] activityArray = new double[SIZE];
    private double data = 0;

    private double accelX = 0;
    private double accelY = 0;
    private double accelZ = 0;

    private String activity = " ";



    public BoundedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        BoundedService getService(){
            return BoundedService.this;
        }
    }

    public double getData(){
        return data;
    }

    public String getActivity(){
        return activity;
    }

    public double getX(){
        return accelX;
    }
    public double getY(){
        return accelY;
    }
    public double getZ(){
        return accelZ;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        sensorManager_ = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager_.registerListener(this, accelerometer_, SensorManager.SENSOR_DELAY_NORMAL, DELAY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];

            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh-mm");
            String format = simpleDateFormat.format(new Date());*/

            data = Math.sqrt((accelX*accelX + accelY*accelY + accelZ*accelZ)/3);

            //this will determine if the user is lying down
            if (data > 5.55 && data < 5.75 && accelY > -1 && accelY < 1){
                activity = "Lying down";
            }
            //this will determine if the user is sitting
            else if (data > 5.45 && data < 5.54 && accelY > 7 && accelY < 10){
                activity = "Sitting";
            }
            //this will determine if the user walking/running
            else if (data > 6 && accelY > 5){
                activity = "running";
            }
            else {

            }

            /*if(counter < SIZE) {
                myArray[counter] = rms;
                counter++;
            } else {
                data = getAverage(myArray);
                counter = 0;
                myArray[counter] = rms;
                counter++;
            }*/
        }
    }

    // Helper method to find the average of an arbitrary length array of numbers
    /*private double getAverage(double[] anArray) {
        double sum = 0;
        for(int i=0; i < anArray.length; i++){
            sum += anArray[i];
        }
        return sum/anArray.length;
    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
