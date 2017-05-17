package com.uni.antonkluev.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import android.widget.ListView;


public class SongListActivity extends AppCompatActivity {
    //https://androidstudies.wordpress.com/2013/05/26/media-player-and-audio-manager/

    ListView listView;
    ArrayList songs;
    ArrayAdapter adapter;
    Cursor songCursor;
    MediaPlayer mediaPlayer;
    String fileName;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        Intent intent = getIntent();
        String value = intent.getStringExtra(fileName); //if it's a string you stored.

        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.textView);

        // initializing the MediaPlayer
        mediaPlayer = new MediaPlayer();

        //populate the ListView with media files
        loadMusic();

        //create and set the adapter
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        //add the listeners
        listView.setOnItemClickListener(songListener);

    }

    @SuppressWarnings("deprecation")
    private void loadMusic() {

        //String array to hold the media data
        String[] data = {MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME};

        //database query
        songCursor = this.managedQuery(MediaStore.Audio.Media.
                EXTERNAL_CONTENT_URI, data, null, null, null);
        songs = new ArrayList();
        if (songCursor != null) {
            while (songCursor.moveToNext()) {
                //add the filename only to song list
                songs.add(songCursor.getString(1).toString());
            }
        }
    }
    private OnItemClickListener songListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            //move the Cursor to the correct item position
            songCursor.moveToPosition(position);
            textView.setText(songCursor.getString(1).toString());

            //if the file is already being played stop it
            if(fileName != null && fileName.equals(songCursor.getString(0))) {
                mediaPlayer.reset();      //reset the MediaPlayer
                fileName = "";   //reset the fileName
            }
            else {
                fileName = songCursor.getString(0);  //get the fileName
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.reset();                  //reset if already playing
                    }
                    mediaPlayer.setDataSource(fileName);      //provide the source
                    mediaPlayer.prepare();                    //prepare the object
                    mediaPlayer.start();                      //start playback
                }
                catch (Exception e) {
                }
            }
        }

    };
}


