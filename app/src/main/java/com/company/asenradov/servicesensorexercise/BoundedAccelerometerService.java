package com.company.asenradov.servicesensorexercise;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundedAccelerometerService extends Service implements SensorEventListener {

    private final int DELAY = 100;
    private SensorManager sensorManager_;
    private Sensor accelerometer_;
    private int counter = 0;
    private double[] myArray = new double[100];
    private double data = 0;

    public BoundedAccelerometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        BoundedAccelerometerService getService(){
            return BoundedAccelerometerService.this;
        }
    }

    public double getData(){
        return data;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager_ = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager_.registerListener(this, accelerometer_, SensorManager.SENSOR_DELAY_NORMAL, DELAY); // min API = 19
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double accelX = event.values[0];
            double accelY = event.values[1];
            double accelZ = event.values[2];

            double rms = Math.sqrt((accelX*accelX + accelY*accelY + accelZ*accelZ)/3);

            if(counter < 100) {
                myArray[counter] = rms;
                counter++;
            } else {
                counter = 0;
                myArray[counter] = rms;
                counter++;
                data = getAverage(myArray);
                Log.d("DATA COMPUTED", "onSensorChanged: " + data);
            }
        }
    }

    // Helper method to find the average of an arbitrary length array of numbers
    private double getAverage(double[] anArray) {
        double sum = 0;
        for(int i=0; i < anArray.length; i++){
            sum += anArray[i];
        }
        return sum/anArray.length;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
