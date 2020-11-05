package com.example.sangeetplayer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.sangeetplayer.Preference.dataPref;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import io.reactivex.schedulers.Schedulers;

public class StarterAct extends AppCompatActivity {

    private static final int REQUEST_STORAGE_CODE = 101;
    private SongViewModel songViewModel;
    private dataPref dataPref;
    private Song song;
    private List<Song> songList;
    Uri musicUri =  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    Cursor cursor;
    ContentResolver resolver;

    private Disposable disposable;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_starter);



        dataPref = new dataPref(this);
      //  songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);


//        dialog = new ProgressDialog(this);
//        dialog.setTitle("Please Waite...");
//        dialog.setCancelable(false);

//        if (dataPref.shouldScan()){
//
//            checkPermission();
//        //    resolver = getApplicationContext().getContentResolver();
//        }else {
//
//            startAct();
//        }

        checkPermission();
    }



    private void startAct(){
        Intent intent = new Intent(StarterAct.this, MainActivity.class);
        startActivity(intent);
        finish();
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void checkPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permission, REQUEST_STORAGE_CODE);
                return;
            }


            startAct();
//            scanSongs();

            return;
        }

        dataPref.shouldScan(false);
        dataPref.setScanned(true);
        dataPref.setFisrt_load(false);
        startAct();
     //   scanSongs();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (!disposable.isDisposed()){
//
//            disposable.dispose();
//        }
    }

    private void scanSongs() {
        dialog.show();

        songList = new ArrayList<>();


        String SONG_ID       = android.provider.MediaStore.Audio.Media._ID;
        String SONG_TITLE    = android.provider.MediaStore.Audio.Media.TITLE;
        String SONG_ARTIST   = android.provider.MediaStore.Audio.Media.ARTIST;
        String SONG_ARTIST_ID   = MediaStore.Audio.Media.ARTIST_ID;
        String SONG_ALBUM    = android.provider.MediaStore.Audio.Media.ALBUM;
        String SONG_ALBUM_ID    = MediaStore.Audio.Media.ALBUM_ID;
        String SONG_YEAR     = android.provider.MediaStore.Audio.Media.YEAR;
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


        String  title, artist, album, file_path; long song_id, art_id, album_id, dura; int year, tr_no;
        if (cursor != null && cursor.moveToFirst()){

            do {

//                Song song = new Song();
//
//                song.setSong_id     (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ID)));
//                song.setTitle       (cursor.getString(cursor.getColumnIndexOrThrow(SONG_TITLE)));
//                song.setArtist      (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ARTIST)));
//                song.setArtist_id   (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID)));
//                song.setAlbum       (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ALBUM)));
//                song.setAlbum_id    (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ALBUM_ID)));
//                song.setYear        (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_YEAR)));
//                song.setTrack_no    (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_TRACK_NO)));
//                song.setFilePath    (cursor.getString(cursor.getColumnIndexOrThrow(SONG_FILEPATH)));
//                song.setDuration    (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_DURATION)));
//                String song_id    = cursor.getString(cursor.getColumnIndexOrThrow(SONG_ID));
//                song.setAlbum_art("content://media/external/audio/media/" + song_id + "/albumart");


                song_id     = (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ID)));
                title       = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_TITLE)));
                artist      = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ARTIST)));
                art_id      = (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID)));
                album       = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ALBUM)));
                album_id    = (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ALBUM_ID)));
                year        = (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_YEAR)));
                tr_no       = (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_TRACK_NO)));
                file_path   = (cursor.getString(cursor.getColumnIndexOrThrow(SONG_FILEPATH)));
                dura        = (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_DURATION)));



                songList.add(new Song(title, artist, art_id, album, album_id, year, tr_no, dura, song_id, file_path));
                Log.e("aaaaaaaaaaaa", "size "+songList.size() );

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

                        dialog.dismiss();
                        dataPref.shouldScan(false);
                        dataPref.setScanned(true);
                        dataPref.setFisrt_load(false);

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                        startAct();
                    }

                    @Override
                    public void onError(Throwable e) {

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

//        songViewModel.addSongs(songList)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new SingleObserver<List<Song>>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
//
//                        dialog.dismiss();
//                        dataPref.shouldScan(false);
//                        dataPref.setScanned(true);
//                        dataPref.setFisrt_load(false);
//
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//
//                        Toast.makeText(getApplicationContext(), "Failed"+e.getMessage(), Toast.LENGTH_LONG).show();
//
//                    }
//                });
    }

    private void scanAlbums(){

        String ALBUM_ARTIST   = MediaStore.Audio.Albums.ARTIST;
        String ALBUM_ARTIST_ID   = MediaStore.Audio.Albums.ARTIST_ID;
        String ALBUM    = android.provider.MediaStore.Audio.Albums.ALBUM;
        String ALBUM_ID    = MediaStore.Audio.Albums.ALBUM_ID;
        String ALBUM_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case REQUEST_STORAGE_CODE:
                if (grantResults.length > 0){

                    for (int i = 0; i < grantResults.length; i++) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                      //  scanSongs();
                        checkPermission();
                    }
                }
                break;
        }
    }
}
