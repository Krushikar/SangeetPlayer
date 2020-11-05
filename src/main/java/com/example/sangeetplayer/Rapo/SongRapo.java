package com.example.sangeetplayer.Rapo;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.sangeetplayer.Dao.songDao;
import com.example.sangeetplayer.Database.SongDatabase;
import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Completable;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SongRapo {

    private songDao songDao;
    private List<Song> addSongs;
    private Maybe<List<Song>> getSongs;
    private Maybe<List<Albums>> getAlbums;
    private Maybe<List<Artist>> getArtist;
    private ContentResolver resolver;


    public SongRapo(Application application) {
        SongDatabase database = SongDatabase.getDatabase(application);
        songDao = database.songDao();
        resolver = application.getContentResolver();
    }

    public Maybe<List<Song>> getCurrentSongList(String where, long id) {

        return Maybe.create(new MaybeOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Song>> emitter) throws Exception {

                String select;

                 if (where.equals("album")){

                    select = MediaStore.Audio.Media.ALBUM_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                }else if (where.equals("artist")){

                   select = MediaStore.Audio.Media.ARTIST_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                }else {

                     select = MediaStore.Audio.Media.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.Media.DURATION + " > " + "75000";
                 }


                List<Song> songList = getSongList(select);

                if (!emitter.isDisposed()){

                    emitter.onSuccess(songList);
                }

            }
        });
    }

    public Maybe<List<Song>> getAllSongs() {
        //  return songDao.getAllSongs();

        return Maybe.create(new MaybeOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Song>> emitter) throws Exception {

                final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.Media.DURATION + " > " + "75000";
                List<Song> songList = getSongList(musicsOnly);

                if (!emitter.isDisposed()) {

                    emitter.onSuccess(songList);
                }
            }
        });

    }


    public Maybe<List<Song>> getRingtones() {
        //  return songDao.getAllSongs();

        return Maybe.create(new MaybeOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Song>> emitter) throws Exception {

                final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.Media.DURATION + " < " + "75000";
                List<Song> songList = getSongList(musicsOnly);

                if (!emitter.isDisposed()) {

                    emitter.onSuccess(songList);
                }
            }
        });

    }

    public LiveData<List<Song>> getFavSongs() {

          return songDao.getAllSongs();
    }

    @NotNull
    private List<Song> getSongList(String select) {
        List<Song> songList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor;


        String SONG_ID = MediaStore.Audio.Media._ID;
        String SONG_TITLE = MediaStore.Audio.Media.TITLE;
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;
        String SONG_ARTIST_ID = MediaStore.Audio.Media.ARTIST_ID;
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;
        String SONG_ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;
        String SONG_YEAR = MediaStore.Audio.Media.YEAR;
        String SONG_TRACK_NO = MediaStore.Audio.Media.TRACK;
        String SONG_FILEPATH = MediaStore.Audio.Media.DATA;
        String SONG_DURATION = MediaStore.Audio.Media.DURATION;

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

     //   String where = MediaStore.Audio.Media.ALBUM_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";

     //   final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1";
        cursor = resolver.query(musicUri, columns, select, null, null);


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
        return songList;
    }

    public Maybe<List<Albums>> getAllAlbums() {
        //  return songDao.getAlbums();

        return Maybe.create(new MaybeOnSubscribe<List<Albums>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Albums>> emitter) throws Exception {

                List<Albums> albumsList = getAlbumsList();

                if (!emitter.isDisposed()) {

                    emitter.onSuccess(albumsList);
                }

            }
        });
    }

    @NotNull
    private List<Albums> getAlbumsList() {
        List<Albums> albumsList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor;


        String ALBUM_ID = MediaStore.Audio.Albums._ID;
        String ALBUM = MediaStore.Audio.Albums.ALBUM;
        String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;
        String ALBUM_ARTIST_ID = MediaStore.Audio.Albums.ARTIST_ID;
        String ALBUM_TRACKS = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

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
        return albumsList;
    }

    public String getAlbumArt(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        return String.valueOf(path);
    }

    public Maybe<List<Artist>> getAllArtist() {
        //return songDao.getArtist();

        return Maybe.create(new MaybeOnSubscribe<List<Artist>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Artist>> emitter) throws Exception {

                List<Artist> artistList = getArtistList();

                if (!emitter.isDisposed()){

                    emitter.onSuccess(artistList);
                }

            }
        });
    }

    @NotNull
    private List<Artist> getArtistList() {
        List<Artist> artistList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor cursor;

//        final StringBuilder selection = new StringBuilder();
//        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
//        selection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
//        selection.append(" AND " + MediaStore.Audio.AudioColumns.ALBUM_ID + "=" + albumId);

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
        return artistList;
    }


    public Maybe<List<Song>> getSongByArtist(long id) {
        return Maybe.create(new MaybeOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Song>> emitter) throws Exception {

                final String select = MediaStore.Audio.AudioColumns.ARTIST_ID + "="  + id + " AND " + MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.Media.DURATION + " > " + "75000";

                List<Song> songList = getSongList(select);

                if (!emitter.isDisposed()) {

                    emitter.onSuccess(songList);
                }
            }
        });
    }

    public Maybe<List<Song>> getSongByAlbum(long id) {

        return Maybe.create(new MaybeOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Song>> emitter) throws Exception {

                final String select = MediaStore.Audio.Media.ALBUM_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.Media.DURATION + " > " + "75000";

                List<Song> songList = getSongList(select);

                if (!emitter.isDisposed()) {

                    emitter.onSuccess(songList);
                }
            }
        });
    }

    public Maybe<List<Albums>> getAlbumByArtist(long id) {
        return Maybe.create(new MaybeOnSubscribe<List<Albums>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Albums>> emitter) throws Exception {

                final String select = MediaStore.Audio.Albums.ARTIST_ID + "=" + id;
                List<Albums> albumsList = getAlbumByArtis(select);


                if (!emitter.isDisposed()) {

                    emitter.onSuccess(albumsList);
                }

            }
        });
    }

    @NotNull
    private List<Albums> getAlbumByArtis(String art_id) {
        List<Albums> albumsList = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor;


        String ALBUM_ID = MediaStore.Audio.Albums._ID;
        String ALBUM = MediaStore.Audio.Albums.ALBUM;
        String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;
        String ALBUM_ARTIST_ID = MediaStore.Audio.Albums.ARTIST_ID;
        String ALBUM_TRACKS = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        String columns[] = {
                ALBUM,
                ALBUM_ID,
                ALBUM_ARTIST,
                ALBUM_ARTIST_ID,
                ALBUM_TRACKS
        };


        cursor = resolver.query(musicUri, columns, art_id, null, null);

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
        return albumsList;
    }

    public Completable deleteAllSongs() {

        return songDao.deleteAllSongs();
    }

    public Completable deleteAllArtist() {

        return songDao.deleteAllArtist();
    }

    public Completable deleteAllAlbum() {
        return songDao.deleteAllAlbum();
    }

    public Completable insertSongs(List<Song> songs) {

        return songDao.addSongs(songs);
    }

    public Completable insertSong(Song song) {

        return songDao.addSong(song);
    }

    public Completable deleteSong(long songId) {

        return songDao.deleteSong(songId);
    }

    public Single<Song> getSong(int songId){

        return songDao.getSong(songId);
    }

    public Completable insertAlbums(List<Albums> albums) {

        return songDao.addAlbums(albums);
    }

    public Completable insertArtist(List<Artist> artists) {

        return songDao.addArtist(artists);
    }
}
