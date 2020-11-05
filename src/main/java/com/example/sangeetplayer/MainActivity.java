package com.example.sangeetplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sangeetplayer.Act.act_Now_Playing;
import com.example.sangeetplayer.Adapter.tabAdapter;
import com.example.sangeetplayer.Services.MusicService;
import com.example.sangeetplayer.SharedPref.SharedPref;
import com.example.sangeetplayer.Util.Utilities;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;
import com.example.sangeetplayer.fragments.Album_Fragment;
import com.example.sangeetplayer.fragments.Artist_Fragment;
import com.example.sangeetplayer.fragments.Fav_Fragment;
import com.example.sangeetplayer.fragments.Ringtone_Fragment;
import com.example.sangeetplayer.fragments.Tracks_Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.maybe.MaybeUsing;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private tabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SongViewModel songViewModel;
    private Disposable disposable;

    private MusicService musicService;
    private boolean isBound = false, fistLoad = true;
    private SharedPref sharedPref;
    private int i = 0;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private ProgressBar progressBar;
    private Utilities utilities;
    private Handler handler = new Handler();
    private long total;
    private TextView song_name;
    private FloatingActionButton play_pause;
    private ImageView album_art, next;
    private Song song;

    public static boolean shouldAnimate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        song_name = findViewById(R.id.current_song_name);
        play_pause = findViewById(R.id.floatingActionButton_play);
        next = findViewById(R.id.home_next);
        album_art = findViewById(R.id.view_trackt_song_art);
        progressBar = findViewById(R.id.progressBar);
        tabLayout = findViewById(R.id.main_tab);
        viewPager = findViewById(R.id.home_viewpager);

        tabAdapter = new tabAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabAdapter.addFragmnet(new Tracks_Fragment(), "TRACKS");
        tabAdapter.addFragmnet(new Album_Fragment(), "ALBUMS");
        tabAdapter.addFragmnet(new Artist_Fragment(), "ARTIST");
        tabAdapter.addFragmnet(new Fav_Fragment(), "FAVOURITE");
        tabAdapter.addFragmnet(new Ringtone_Fragment(), "RINGTONE");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        sharedPref = new SharedPref(this);

        //     insertAlbums();
        //    insertArtist();
        //     deleteAllsongs();
        //     insertSongs();
        //     deleteAllArtist();
        //     deleteAllAlbum();


        utilities = new Utilities();
        StartService();
        intentFilter = new IntentFilter("ServiceAction");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);


        songViewModel.songsList.observe(MainActivity.this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {

                initSongs(songs);

            }
        });

        songViewModel.playNextSong.observe(MainActivity.this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {

                musicService.playNext(song);

            }
        });

        songViewModel.addToPlaylist.observe(MainActivity.this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {

                musicService.addToPlay(song, false);

            }
        });
    }



    private void initSongs(List<Song> songs) {
        if (isBound) {

            if (!songs.isEmpty()) {


                musicService.setCurrentSongPos(sharedPref.getSongPosPref());
                musicService.setSongs(songs);
                musicService.setCurrentSongPos(sharedPref.getSongPosPref());
                musicService.setCurrentSong();
                musicService.setPlayer();

                musicService.setPrepaire();

                fistLoad = false;
               // songViewModel.setSongPrefWhere("songs", 1);

                udapteImage();

            }

        }
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

    @Override
    protected void onStart() {
        super.onStart();

        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        if (isBound){

            udapteImage();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (disposable != null) {

            if (!disposable.isDisposed()) {

                disposable.dispose();
            }
        }

        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) {

            unbindService(serviceConnection);
            isBound = false;
        }

        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        shouldAnimate = true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.floatingActionButton_play:

                musicService.togglePlayer();
                if (musicService.isPlaying()) {

                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                } else {

                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));

                }

                break;
            case R.id.constraintLayout:
                Intent intent = new Intent(MainActivity.this, act_Now_Playing.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //  startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                break;
            case R.id.home_next:

                musicService.nextSong();
                udapteImage();

                break;

        }
    }

    private void udapteImage() {

        int pos = musicService.getCurrentSongPos();
        song = musicService.getSongList().get(pos);

        song_name.setText("" + song.getTitle());

        Picasso.get()
                .load(song.getAlbum_art())
                .resize(800, 800)
                .centerCrop()
                .onlyScaleDown()
                .placeholder(R.drawable.album_art1)
                .into(album_art);
    }

    private void onSongPrepared() {
        total = musicService.getTotlalDuration();
        updateProgressBar();

        udapteImage();
    }

    private void onSongResume() {
        updateProgressBar();
    }

    private void onSongPause() {
        handler.removeCallbacks(mUpdateTimeTask);
    }

    private void onSongComplete() {
        handler.removeCallbacks(mUpdateTimeTask);

    }

    public void updateProgressBar() {

        if (musicService.isPlaying()) {
            handler.postDelayed(mUpdateTimeTask, 100);
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {

            //   long total = musicService.getTotlalDuration();
            long current = musicService.getCurrentDuration();

            //   totalDur.setText("" + utilities.milliSecondsToTimer(total));
            //  currentDur.setText("" + utilities.milliSecondsToTimer(current));


            int progress = (int) (utilities.getProgressPercentage(current, total));

            progressBar.setProgress(progress);

            handler.postDelayed(this, 100);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.ServieBinder binder = (MusicService.ServieBinder) service;

            musicService = binder.getMusicService();
            isBound = true;

            //  songViewModel.setPos(sharedPref.getSongPosPref());

            if (musicService.getSongList().isEmpty()) {
                getSong();
                return;
            }


               total = musicService.getTotlalDuration();
               int current_var = musicService.resumePos;
               int progress = (int) (utilities.getProgressPercentage(current_var, total));

               progressBar.setProgress(progress);

               udapteImage();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void getSong() {

        songViewModel.getSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<Song>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Song> songs) {

                        //   musicService.setSongs(songs);
                       // initSongs(songs);

                        musicService.setSongs(songs);
                        musicService.setCurrentSongPos(sharedPref.getSongPosPref());
                        musicService.setCurrentSong();
                        musicService.setPlayer();

                       // musicService.setPrepaire();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getStringExtra("action");


            if (action != null) {
                switch (action) {

                    case "complete":
                        onSongComplete();
                        break;
                    case "pause":
                        onSongPause();
                        break;
                    case "resume":
                        onSongResume();
                        break;
                    case "prepared":
                        onSongPrepared();
                        break;

                }
            }

        }
    };


    public void insertSongs() {

        List<Song> songList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor;
        ContentResolver resolver = getApplicationContext().getContentResolver();

        String SONG_ID = android.provider.MediaStore.Audio.Media._ID;
        String SONG_TITLE = android.provider.MediaStore.Audio.Media.TITLE;
        String SONG_ARTIST = android.provider.MediaStore.Audio.Media.ARTIST;
        String SONG_ARTIST_ID = MediaStore.Audio.Media.ARTIST_ID;
        String SONG_ALBUM = android.provider.MediaStore.Audio.Media.ALBUM;
        String SONG_ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;
        String SONG_YEAR = android.provider.MediaStore.Audio.Media.YEAR;
        String SONG_TRACK_NO = android.provider.MediaStore.Audio.Media.TRACK;
        String SONG_FILEPATH = android.provider.MediaStore.Audio.Media.DATA;
        String SONG_DURATION = android.provider.MediaStore.Audio.Media.DURATION;

        String[] columns = {
                SONG_ID,
                SONG_TITLE,
                SONG_ARTIST,
                SONG_ARTIST_ID,
                SONG_ALBUM,
                SONG_ALBUM_ID,
                SONG_YEAR,
                SONG_TRACK_NO,
                SONG_FILEPATH,
                SONG_DURATION
        };

        final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1";
        cursor = resolver.query(musicUri, columns, musicsOnly, null, null);


        String title, artist, album, file_path;
        long song_id, art_id, album_id, dura;
        int year, tr_no;
        if (cursor != null && cursor.moveToFirst()) {

            do {


                song_id = (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ID)));
                title = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_TITLE)));
                artist = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ARTIST)));
                art_id = (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID)));
                album = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ALBUM)));
                album_id = (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ALBUM_ID)));
                year = (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_YEAR)));
                tr_no = (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_TRACK_NO)));
                file_path = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_FILEPATH)));
                dura = (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_DURATION)));


                songList.add(new Song(title, artist, art_id, album, album_id, year, tr_no, dura, song_id, file_path));
                Log.e("aaaaaaaaaaaa", "size " + songList.size());

            } while (cursor.moveToNext());

            cursor.close();
        }


        songViewModel.addSongs(songList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {


                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {


                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void insertAlbums() {

        List<Albums> albumsList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor;
        ContentResolver resolver = getApplicationContext().getContentResolver();


        String ALBUM_ID = MediaStore.Audio.Albums._ID;
        String ALBUM = MediaStore.Audio.Albums.ALBUM;
        String ALBUM_ARTIST = android.provider.MediaStore.Audio.Albums.ARTIST;
        String ALBUM_ARTIST_ID = MediaStore.Audio.Albums.ARTIST_ID;
        String ALBUM_TRACKS = android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        String columns[] = {
                ALBUM,
                ALBUM_ID,
                ALBUM_ARTIST,
                ALBUM_ARTIST_ID,
                ALBUM_TRACKS
        };


        cursor = resolver.query(musicUri, columns, null, null, null);

        String album, artist, album_art;
        long album_id, artist_id;
        int songs;
        if (cursor != null && cursor.moveToFirst()) {

            do {

                album = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM));
                album_id = cursor.getLong(cursor.getColumnIndexOrThrow(ALBUM_ID));
                artist = cursor.getString(cursor.getColumnIndexOrThrow(ALBUM_ARTIST));
                artist_id = cursor.getLong(cursor.getColumnIndexOrThrow(ALBUM_ARTIST_ID));
                songs = cursor.getInt(cursor.getColumnIndexOrThrow(ALBUM_TRACKS));

                album_art = getAlbumArt(album_id);

                albumsList.add(new Albums(album, album_id, artist, artist_id, songs, album_art));

            } while (cursor.moveToNext());

            cursor.close();
        }


        songViewModel.addAlbums(albumsList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void insertArtist() {

        List<Artist> artistList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor cursor;
        ContentResolver resolver = getApplicationContext().getContentResolver();


        String ARTIST_ID = MediaStore.Audio.Artists._ID;
        String ARTIST = MediaStore.Audio.Artists.ARTIST;
        String ARTIST_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
        String ARTIST_TRACKS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;

        String columns[] = {
                ARTIST,
                ARTIST_ID,
                ARTIST_ALBUMS,
                ARTIST_TRACKS
        };


        cursor = resolver.query(musicUri, columns, null, null, null);

        String artist;
        int artist_id;
        int songs, albums;
        if (cursor != null && cursor.moveToFirst()) {

            do {

                artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST));
                artist_id = cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ID));
                albums = cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_ALBUMS));
                songs = cursor.getInt(cursor.getColumnIndexOrThrow(ARTIST_TRACKS));

                artistList.add(new Artist(artist, artist_id, songs, albums));

            } while (cursor.moveToNext());

            cursor.close();
        }


        songViewModel.addArtist(artistList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void deleteAllsongs() {

        songViewModel.deleteAllSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void deleteAllArtist() {

        songViewModel.deleteAllArtist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void deleteAllAlbum() {

        songViewModel.deleteAllAlbum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


    public String getAlbumArt(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        return String.valueOf(path);
    }


}
