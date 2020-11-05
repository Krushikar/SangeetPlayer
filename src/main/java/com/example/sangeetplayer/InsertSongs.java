package com.example.sangeetplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;

public class InsertSongs {

    private Context context;
    private List<Song> songList = new ArrayList<>();
    private List<Albums> albumsList;
    private List<Artist> artistList;

    private Song song;
    private Albums albums;
    private Artist artist;

    Cursor cursor;
    Uri musicUri =  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    ContentResolver resolver;

    InsertSongs(Context context){

        this.context = context;
        resolver  = context.getContentResolver();
    }

    public void scanSongs() {


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


        if (cursor != null && cursor.moveToFirst()){

            do {

                Song song = new Song();

                song.setTitle       (cursor.getString(cursor.getColumnIndexOrThrow(SONG_TITLE)));
                song.setArtist      (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ARTIST)));
                song.setArtist_id   (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ARTIST_ID)));
                song.setAlbum       (cursor.getString(cursor.getColumnIndexOrThrow(SONG_ALBUM)));
                song.setAlbum_id    (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_ALBUM_ID)));
                song.setYear        (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_YEAR)));
                song.setTrack_no    (cursor.getInt(cursor.getColumnIndexOrThrow(SONG_TRACK_NO)));
                song.setFilePath    (cursor.getString(cursor.getColumnIndexOrThrow(SONG_FILEPATH)));
                song.setDuration    (cursor.getLong(cursor.getColumnIndexOrThrow(SONG_DURATION)));

                songList.add(song);

            } while (cursor.moveToFirst());
        }

        cursor.close();
    }

    public void scanAlbums(){}
}
