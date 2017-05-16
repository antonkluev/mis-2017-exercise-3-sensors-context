package com.uni.antonkluev.app;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.util.Log;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CanvasView customCanvas;
    private SeekBar    rateSeekBar;
    private SeekBar    windowSizeSeekBar;

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
                customCanvas.axis.get(0).addPoint(progress);
            }
        });

        windowSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                customCanvas.axis.get(1).addPoint(progress);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
