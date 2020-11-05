package com.example.sangeetplayer.Act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Adapter.common_Adapter;
import com.example.sangeetplayer.Adapter.onClickAdapter;
import com.example.sangeetplayer.Adapter.playlistAdapter;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.Services.MusicService;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;

public class act_Playlist extends AppCompatActivity implements playlistAdapter.OnClick {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private com.example.sangeetplayer.Adapter.playlistAdapter playlistAdapter;
    private List<Song> songList;
    private MusicService musicService;
    private boolean isBound = false;

    private TextView total, current;
    private int current_song, total_song;
    public static int CURRENT_SONG_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playlist);

        total = findViewById(R.id.total);
        current = findViewById(R.id.current_song);

        recyclerView = findViewById(R.id.recycle_playlist);
        layoutManager = new LinearLayoutManager(this);
        songList = new ArrayList<>();
        playlistAdapter = new playlistAdapter(songList, this, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(playlistAdapter);

        StartService();

    }

    private void StartService() {

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

        BindService();
    }

    private void BindService() {

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.ServieBinder binder = (MusicService.ServieBinder) service;

            musicService = binder.getMusicService();
            isBound = true;



            letRecyle();
            recyclerView.getLayoutManager().scrollToPosition(current_song-3);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void letRecyle() {

        current_song = musicService.getCurrentSongPos()+1;
        total_song = musicService.getTotalSong();

        CURRENT_SONG_ID = musicService.getCurrentSongPos();

        playlistAdapter.addSongs(musicService.getSongList());
        total.setText(""+total_song);
        current.setText(""+current_song);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {

            unbindService(serviceConnection);
            isBound = !isBound;
        }
    }




    @Override
    public void onClick( int pos) {


        musicService.setCurrentSongPos(pos);
        musicService.setSong();
        musicService.setPlayer();
        musicService.setPrepaire();

        current_song = pos+1;
        current.setText(""+current_song);


    }

    @Override
    public void removeFromPlay(int pos) {

        musicService.removeSongPlay(pos);
        letRecyle();
    }


    @Override
    public void playNext(Song song, int pos) {

      //  musicService.removeSongPlay(pos);
        musicService.playNext(song);

        letRecyle();
    }
}
