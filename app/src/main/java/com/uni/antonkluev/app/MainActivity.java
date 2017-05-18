package com.uni.antonkluev.app;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private CanvasView accelerometerCanvasView, fftCanvasView;
    private SeekBar rateSeekBar, windowSizeSeekBar, trackProgress;
    private TextView curTime, maxTime, frequencyValue;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int windowSizeUpdate = 16;
    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;
    double startTime;
    static int sensorRate = 10;
    long lastSensorUpdate = System.currentTimeMillis();
    FFT fft = new FFT(windowSizeUpdate);
    Cursor songCursor;
    String fileName;
    private SensorManager sm;
    LocationManager locationManager;
    private double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGraphs();
        initPlayer();
        initLocationServices();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void initGraphs () {
        // accelerometer graph
        accelerometerCanvasView = (CanvasView) findViewById(R.id.accelerometerCanvasView);
        accelerometerCanvasView.coordinatePlaneType = "horizontal";
        accelerometerCanvasView.axis.get(0).setRange(-15.0, 15.0);
        accelerometerCanvasView.axis.get(1).setRange( -5.0, 25.0);
        accelerometerCanvasView.axis.get(2).setRange(-15.0, 15.0);
        accelerometerCanvasView.axis.get(3).setRange(  0.0,  2.0);
        // fft graph
        fftCanvasView = (CanvasView) findViewById(R.id.fftCanvasView);
        fftCanvasView.coordinatePlaneType = "vertical";
        fftCanvasView.axis.get(2).setRange(0, 20);
        // seekbars
        rateSeekBar = (SeekBar) findViewById(R.id.rateSeekBar);
        windowSizeSeekBar = (SeekBar) findViewById(R.id.windowSizeSeekBar);
        rateSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress ,boolean fromUser) {
                sensorRate = progress;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
        });
        windowSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                windowSizeUpdate = (int)Math.pow(2, progress + 2);
                fft = new FFT(windowSizeUpdate);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
        });
        frequencyValue = (TextView) findViewById(R.id.frequencyValue);
    }
    private void initPlayer () {
        // Assign TextView
        curTime = (TextView) findViewById(R.id.curTime);
        maxTime = (TextView) findViewById(R.id.maxTime);
        // https://www.tutorialspoint.com/android/android_mediaplayer.html
        trackProgress = (SeekBar) findViewById(R.id.trackProgress);
        // create Adapter
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.select_dialog_item, loadMusic());
        // make spinner
        Spinner spinner = (Spinner) findViewById(R.id.songList);
        spinner.setAdapter(adapter);
        // on click event
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                // move the Cursor to the correct item position
                songCursor.moveToPosition(pos);
                fileName = songCursor.getString(0); //get the fileName
                try {
                    if (mediaPlayer.isPlaying()) mediaPlayer.reset(); //reset if already playing
                    mediaPlayer.setDataSource(fileName); //provide the source
                    mediaPlayer.prepare(); //prepare the object
                    mediaPlayer.start(); //start playback
                }
                catch (Exception e) {}
            }
            public void onNothingSelected(AdapterView <?> parent){}
        });
    }
    private void initLocationServices () {
        //https://www.youtube.com/watch?v=YrI2pCZC8cc
        //https://github.com/amyork/Android_Studio_Tut_6_Accelerometer/blob/master/app/src/main/java/com/example/pc/accelerometer/MainActivity.java
        // Create our Sensor Manager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer Sensor
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        //https://www.youtube.com/watch?v=qS1E-Vrk60E&t=1s
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
                ) {return;}
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    // get the speed
                    speed = location.getSpeed();
                    // change the value for speed; from m/s to km/h
                    speed = ((speed * 3600)/1000);
                    Log.v("speed", String.valueOf(speed));
                    Toast.makeText(getApplicationContext(), "Your speed is " + speed + "latitude" + latitude + "longitude" + longitude , Toast.LENGTH_LONG).show();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    // get the speed
                    speed = location.getSpeed();
                    // change the value for speed; from m/s to km/h
                    speed = ((speed * 3600)/1000);
                    Log.v("speed", String.valueOf(speed));
                    Toast.makeText(getApplicationContext(), "Your speed is " + speed + "latitude" + latitude + "longitude" + longitude , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }




    }
    // sensor logic
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
            // cut fft into half
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
            for (int i = 0; i < fftCanvasView.windowSize; i ++)
                fftCanvasView.axis.get(2).data[i] =
                        Math.sqrt(Math.pow(realPart[i], 2) + Math.pow(imagePart[i], 2));
            fftCanvasView.axis.get(2).update();
            // now call logic to turn the music on or off
            playerLogic();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
    // player logic
    private void playerLogic () {
        if (mediaPlayer != null) {
            double  fftValue    = fftCanvasView.axis.get(2).data[8];
            double  speed       = 5;
            boolean fftWindow   = 2 < fftValue && fftValue < 15;
            boolean speedWindow = 4 < speed && speed < 6;
            if (fftWindow && speedWindow) {
                if (!mediaPlayer.isPlaying()) resumeMusic();
            } else {
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            }
            frequencyValue.setText(String.valueOf(Math.round(fftValue * 100.0) / 100.0));
        }
//        for (int i = 0; i < windowSizeUpdate; i ++)
//            Log.v("values", String.valueOf(i) +" : "+ String.valueOf(fftCanvasView.axis.get(2).data[i]));
    }
    private void resumeMusic () {
        // https://www.tutorialspoint.com/android/android_mediaplayer.htm
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
        }, 100);
    }
    @SuppressWarnings("deprecation")
    private ArrayList loadMusic() {
        // https://androidstudies.wordpress.com/2013/05/26/media-player-and-audio-manager/
        // String array to hold the media data
        String[] data = {MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME};
        // database query
        songCursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, null);
        ArrayList songs = new ArrayList();
        Log.v("songs", String.valueOf(songCursor.getCount()));
        if (songCursor != null)
            while (songCursor.moveToNext())
                songs.add(songCursor.getString(1).toString());
        return songs;
    }


}
