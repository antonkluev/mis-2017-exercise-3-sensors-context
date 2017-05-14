package com.uni.antonkluev.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import java.util.concurrent.TimeUnit;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xText, yText, zText, mText;
    private Sensor mySensor;
    private SensorManager sm;
    private float ax, ay, az;
    private double sumSqr, am;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 20;
    private Button button2,button2,button2;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //https://www.youtube.com/watch?v=YrI2pCZC8cc
        //https://github.com/amyork/Android_Studio_Tut_6_Accelerometer/blob/master/app/src/main/java/com/example/pc/accelerometer/MainActivity.java
        // Create our Sensor Manager
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);

        lastUpdate = System.currentTimeMillis();

        // Accelerometer Sensor
        mySensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        sm.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Assign TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        mText = (TextView)findViewById(R.id.mText);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            getAccelerometer(event);
        }
    }
    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;

        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        sumSqr = Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2);
        am = Math.sqrt(sumSqr / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH));
        //https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

//        long curTime = System.currentTimeMillis();
//
//        if ((curTime - lastUpdate) > 100) {
//            long diffTime = (curTime - lastUpdate);
//            lastUpdate = curTime;
//            float speed = Math.abs(ax + ay + az - last_x - last_y - last_z) / diffTime * 1000;
//
//            if (speed > SHAKE_THRESHOLD) {
//
//            }
//            last_x = ax;
//            last_y = ay;
//            last_z = az;
//        }

        xText.setText("X: " + ax);
        yText.setText("Y: " + ay);
        zText.setText("Z: " + az);
        mText.setText("M: " + am);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onResume() {

        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor
                (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


}
