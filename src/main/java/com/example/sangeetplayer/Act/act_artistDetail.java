package com.example.sangeetplayer.Act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Adapter.artistAdapter;
import com.example.sangeetplayer.Adapter.artist_TrackAdapter;
import com.example.sangeetplayer.Adapter.artist_albumAdapter;
import com.example.sangeetplayer.Adapter.common_Adapter;
import com.example.sangeetplayer.Adapter.onClickAdapter;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.Services.MusicService;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class act_artistDetail extends AppCompatActivity implements onClickAdapter {

    private RecyclerView recyclerViewAlbum, recyclerViewTracks;
    private LinearLayoutManager managerAlbum, managerTracks;
    private artist_TrackAdapter artist_trackAdapter;
    private artist_albumAdapter artistAlbumAdapter;
    private List<Song> songList;
    private List<Albums> albumsList;
    private SongViewModel songViewModel;
    private Disposable disposable, disposable1;


    private Artist artist;
    private TextView artist_name;
    private long artis_id;

    private MusicService musicService;
    private boolean isBound = false, firstLoad = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_artist);

        artist_name = findViewById(R.id.artist_name);
        recyclerViewAlbum = findViewById(R.id.recyclerView_album);
        recyclerViewTracks = findViewById(R.id.recyclerView_artist);
        managerAlbum = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        managerTracks = new LinearLayoutManager(this);

        songList = new ArrayList<>();
        albumsList = new ArrayList<>();

        artist_trackAdapter = new artist_TrackAdapter(songList, this);
        artistAlbumAdapter = new artist_albumAdapter(albumsList, this);

        recyclerViewAlbum.setLayoutManager(managerAlbum);
        recyclerViewAlbum.setHasFixedSize(true);
        recyclerViewAlbum.setAdapter(artistAlbumAdapter);

        recyclerViewTracks.setLayoutManager(managerTracks);
        recyclerViewTracks.setHasFixedSize(true);
        recyclerViewTracks.setAdapter(artist_trackAdapter);

        artist = (Artist) getIntent().getSerializableExtra("artist");
        artist_name.setText(artist.getArtist());

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);


        songViewModel.getSongsByArtist(artist.getArtist_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<Song>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Song> songs) {

                        artist_trackAdapter.addSongs(songs);

                        if (!songs.isEmpty()) {
                            Log.e("hhhhh", "" + songs.get(0).getArtist_id());

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        songViewModel.getAlbumByArtist(artist.getArtist_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<Albums>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable1 = d;
                    }

                    @Override
                    public void onSuccess(List<Albums> albums) {

                        artistAlbumAdapter.addAlbums(albums);

                        if (!albums.isEmpty()) {

                            Log.e("hvhhh", "" + albums.get(0).getArtist_id());

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Log.e("artist_id", "" + artist.getArtist_id());

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disposable.dispose();
        disposable1.dispose();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onClick(List<Song> songs, int pos) {

        songViewModel.setSongPos(pos);
        musicService.setCurrentSongPos(pos);

        if (firstLoad){

            songViewModel.songsList.setValue(songs);
            songViewModel.setSongPrefWhere("artist", songs.get(pos).getArtist_id());
            musicService.setSongs(songs);
        }


        musicService.setPlayer();
        musicService.setPrepaire();

        firstLoad = false;
    }

    @Override
    public void setSong(int pos) {

    }

    @Override
    public void addToFav(Song song) {

        addToFavs(song);
    }

    @Override
    public void addToPlay(Song song, boolean shouldPLay) {

        musicService.addToPlay(song, shouldPLay);
    }

    @Override
    public void playNext(Song song) {

        musicService.playNext(song);
    }

    private void addToFavs(Song song) {


        songViewModel.addSong(song)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {


                        Toast.makeText(getApplicationContext(), "Added to favourite.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {


                        Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.ServieBinder binder = (MusicService.ServieBinder)service;

            musicService = binder.getMusicService();
            isBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
