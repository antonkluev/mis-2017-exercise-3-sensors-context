package com.uni.antonkluev.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SongListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
////        finish();
//    }
}
