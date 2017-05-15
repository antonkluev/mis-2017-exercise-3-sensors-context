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

    private TextView xText, yText, zText, mText, tv1, tx1,tx2;
    private Sensor mySensor;
    private SensorManager sm;
    private float ax, ay, az;
    private double sumSqr, am;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 20;


    private Button b1,b2;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private SeekBar seekbar;

    public static int oneTimeOnly = 0;


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
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        tv1 = (TextView)findViewById(R.id.textView);
        tv1.setText("Song.mp3");
        tx1 = (TextView)findViewById(R.id.textView2);
        tx2 = (TextView)findViewById(R.id.textView3);

        //https://www.tutorialspoint.com/android/android_mediaplayer.htm
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        b1.setEnabled(false);

       // mediaPlayer();

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                tx2.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime)))
                );

                tx1.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime)))
                );
                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                b1.setEnabled(true);
                b2.setEnabled(false);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                b1.setEnabled(false);
                b2.setEnabled(true);
            }
        });

    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


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
    public void MediaPlayer(){

    }


}
