package com.uni.antonkluev.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import java.util.concurrent.TimeUnit;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private CanvasView customCanvas;
    private SeekBar rateSeekBar;
    private SeekBar windowSizeSeekBar;

    private TextView songName, curTime, maxTime;
    private MediaPlayer mediaPlayer;
    private SeekBar trackProgress;

    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;
    double startTime;

    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rateSeekBar = (SeekBar) findViewById(R.id.rateSeekBar);
        windowSizeSeekBar = (SeekBar) findViewById(R.id.windowSizeSeekBar);
        customCanvas = (CanvasView) findViewById(R.id.accelerometerCanvasView);
        rateSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            }
        });
        windowSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            }
        });

        //https://www.youtube.com/watch?v=YrI2pCZC8cc
        //https://github.com/amyork/Android_Studio_Tut_6_Accelerometer/blob/master/app/src/main/java/com/example/pc/accelerometer/MainActivity.java
        // Create our Sensor Manager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer Sensor
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        // Assign TextView
        songName = (TextView) findViewById(R.id.songName);
        songName.setText("Song.mp3");
        curTime = (TextView) findViewById(R.id.curTime);
        maxTime = (TextView) findViewById(R.id.maxTime);
        //https://www.tutorialspoint.com/android/android_mediaplayer.html
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        trackProgress = (SeekBar) findViewById(R.id.trackProgress);
        trackProgress.setClickable(false);

//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
//                mediaPlayer.start();
//                double finalTime = mediaPlayer.getDuration();
//                startTime = mediaPlayer.getCurrentPosition();
//                if (oneTimeOnly == 0) {
//                    trackProgress.setMax((int) finalTime);
//                    oneTimeOnly = 1;
//                }
//                maxTime.setText(String.format("%d min, %d sec",
//                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
//                                        finalTime)))
//                );
//                curTime.setText(String.format("%d min, %d sec",
//                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
//                                        startTime)))
//                );
//                trackProgress.setProgress((int)startTime);
//                myHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            startTime = mediaPlayer.getCurrentPosition();
//                            curTime.setText(String.format("%d min, %d sec",
//                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                    toMinutes((long) startTime))));
//                            trackProgress.setProgress((int)startTime);
//                            myHandler.postDelayed(this, 100);
//                        }
//                    },100);
//                b1.setEnabled(true);
//                b2.setEnabled(false);
//            }
//        });
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
//                mediaPlayer.pause();
//                b1.setEnabled(false);
//                b2.setEnabled(true);
//            }
//        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            getAccelerometer(event);
    }
    private void getAccelerometer(SensorEvent event) {
        //https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double am = Math.sqrt(
                (Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2)) /
                        Math.pow(SensorManager.GRAVITY_EARTH, 2));
        customCanvas.axis.get(0).addPoint(ax, -20, 20);
        customCanvas.axis.get(1).addPoint(ay, -20, 20);
        customCanvas.axis.get(2).addPoint(az, -20, 20);
        customCanvas.axis.get(3).addPoint(am, -20, 20);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
