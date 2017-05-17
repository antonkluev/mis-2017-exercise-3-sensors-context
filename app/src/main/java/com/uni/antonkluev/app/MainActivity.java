package com.uni.antonkluev.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.concurrent.TimeUnit;

import static android.R.attr.value;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private CanvasView accelerometerCanvasView, fftCanvasView;
    private SeekBar rateSeekBar, windowSizeSeekBar, trackProgress;
    private TextView songName, curTime, maxTime;
    private MediaPlayer mediaPlayer;
    private int windowSizeUpdate = 64;
    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;
    double startTime;
    static int sensorRate = 10;
    long lastSensorUpdate = System.currentTimeMillis();
    FFT fft = new FFT(windowSizeUpdate);

    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerometerCanvasView = (CanvasView) findViewById(R.id.accelerometerCanvasView);
        accelerometerCanvasView.axis.get(0).setRange(-15.0, 15.0);
        accelerometerCanvasView.axis.get(1).setRange( -5.0, 25.0);
        accelerometerCanvasView.axis.get(2).setRange(-15.0, 15.0);
        accelerometerCanvasView.axis.get(3).setRange(  0.0,  2.0);

        fftCanvasView = (CanvasView) findViewById(R.id.fftCanvasView);
        fftCanvasView.axis.get(2).setRange(-10, 10);

        rateSeekBar = (SeekBar) findViewById(R.id.rateSeekBar);
        windowSizeSeekBar = (SeekBar) findViewById(R.id.windowSizeSeekBar);
        rateSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress ,boolean fromUser) {
                sensorRate = progress;}
        });
        windowSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                windowSizeUpdate = (int)Math.pow(2, progress + 2);
                fft = new FFT(windowSizeUpdate);}
        });

        // open song list
        findViewById(R.id.openSongList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), SongListActivity.class);
                myIntent.putExtra("key", value);
                startActivity(myIntent);
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
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void resumeMusic () {
        mediaPlayer.start();
        double finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        if (oneTimeOnly == 0) {
            trackProgress.setMax((int) finalTime);
            oneTimeOnly = 1;
        }
        maxTime.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );
        trackProgress.setProgress((int)startTime);
        myHandler.postDelayed(new Runnable() {
            public void run() {
                startTime = mediaPlayer.getCurrentPosition();
                curTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime))));
                trackProgress.setProgress((int)startTime);
                myHandler.postDelayed(this, 100);
            }
        },100);
    }
    private void pauseSong () {
        mediaPlayer.pause();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        //https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
        && System.currentTimeMillis() - lastSensorUpdate > sensorRate
        ){
            lastSensorUpdate = System.currentTimeMillis();
            double ax = event.values[0];
            double ay = event.values[1];
            double az = event.values[2];
            double am = Math.sqrt(
                    (Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2)) /
                            Math.pow(SensorManager.GRAVITY_EARTH, 2));
            // update the window Size
            accelerometerCanvasView.windowSize = windowSizeUpdate;
            fftCanvasView.windowSize = windowSizeUpdate;
            // draw axis
            accelerometerCanvasView.axis.get(0).add(ax);
            accelerometerCanvasView.axis.get(1).add(ay);
            accelerometerCanvasView.axis.get(2).add(az);
            accelerometerCanvasView.axis.get(3).add(am);
            // fft
            double [] realPart  = new double[windowSizeUpdate];
            double [] imagePart = new double[windowSizeUpdate];
            for (int i = 0; i < windowSizeUpdate; i ++)
                realPart[i] = accelerometerCanvasView.axis.get(3).data[i];
            fft.fft(realPart, imagePart);
            for (int i = 0; i < windowSizeUpdate; i ++)
                fftCanvasView.axis.get(2).data[i] = imagePart[i];
            fftCanvasView.axis.get(2).update();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }
}
