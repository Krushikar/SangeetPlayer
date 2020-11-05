package com.example.sangeetplayer.Act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Adapter.album_details_Adapter;
import com.example.sangeetplayer.Adapter.onClickAdapter;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.Services.MusicService;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Song;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class act_AlbumDetail extends AppCompatActivity implements onClickAdapter {

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private album_details_Adapter adapter;
    private List<Song> songList;
    private SongViewModel songViewModel;
    private Disposable disposable;

    private Albums albums;

    private TextView album_name, artist_name;
    private ImageView album_art;
    private MusicService musicService;
    private boolean isBound = false, firstLoad = true;
  //  private CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_album);

        album_name = findViewById(R.id.album_dtail_name);
        artist_name = findViewById(R.id.album_artist_name);
        album_art = findViewById(R.id.album_image);
     //   cardView = findViewById(R.id.cardView3);

        albums = (Albums) getIntent().getSerializableExtra("album");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = getIntent().getExtras().getString("transition");
            album_art.setTransitionName(imageTransitionName);
        }

        recyclerView = findViewById(R.id.recyle_album_details);
        manager = new LinearLayoutManager(this);
        songList = new ArrayList<>();
        adapter = new album_details_Adapter(songList, this);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        setAlbumDetail();

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);


        songViewModel.getSongsByAlbum(albums.getAlbum_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<Song>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Song> songs) {

                        adapter.addAlbumSongs(songs);
                    //    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                        Log.e("SIZEEEE", "Size "+songs.size());
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private void setAlbumDetail() {

        postponeEnterTransition();
        album_name.setText(""+albums.getAlbum());
        artist_name.setText(""+albums.getArtist());

        boolean img = albums.getAlbum_art().equals("null") ;

        if (!img){
            Picasso.get()
                    .load(albums.getAlbum_art())
                    .noFade()
                    .into(album_art, new Callback() {
                        @Override
                        public void onSuccess() {

                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError(Exception e) {

                            supportPostponeEnterTransition();
                        }
                    });
        }else {

            Picasso.get()
                    .load(R.drawable.album_art1)
                    .noFade()
                    .into(album_art, new Callback() {
                        @Override
                        public void onSuccess() {

                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError(Exception e) {

                            supportPostponeEnterTransition();
                        }
                    });
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!disposable.isDisposed()){

            disposable.dispose();
        }

        if (isBound =true){
            unbindService(serviceConnection);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //   overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(List<Song> songs, int pos) {

        songViewModel.setSongPos(pos);
        musicService.setCurrentSongPos(pos);

         if (firstLoad){

          //   songViewModel.songsList.setValue(songs);
             songViewModel.setSongPrefWhere("album", songs.get(pos).getAlbum_id());
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

//        if (shouldPLay){
//
//            musicService.setCurrentSongPos(musicService.getTotalSong() - 2);
//            musicService.setCurrentSong();
//            musicService.setPlayer();
//            musicService.setPrepaire();
//        }


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
