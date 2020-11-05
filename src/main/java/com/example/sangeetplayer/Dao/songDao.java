package com.example.sangeetplayer.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface songDao {

    //Songs
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable addSong(Song song);

    @Query("DELETE FROM songs WHERE song_id = :songId")
    Completable deleteSong(long songId);

    @Query("SELECT * FROM songs WHERE song_id = :song_id")
    Single<Song> getSong(int song_id);

    @Insert
    Completable addSongs(List<Song> songs);

    @Query("DELETE FROM songs")
    Completable deleteAllSongs();

    @Query("DELETE FROM artist")
    Completable deleteAllArtist();

    @Query("DELETE FROM albums")
    Completable deleteAllAlbum();

    @Query("SELECT * FROM songs ORDER BY title ASC")
    LiveData<List<Song>> getAllSongs();

    @Query("SELECT * FROM songs WHERE song_id = :songId")
    Maybe<Song> getSongbyId(long songId);

    @Query("SELECT  * FROM songs WHERE album_id = :albumId")
    Maybe<List<Song>> getSongbyAlbum(long albumId);

    @Query("SELECT * FROM songs WHERE artist_id = :artistId")
    Maybe<List<Song>> getSongbyArtist(long artistId);

    @Query("SELECT * FROM songs ORDER BY title ASC")
    Maybe<List<Song>> getAllSongsList();



    @Query("SELECT  * FROM songs WHERE album_id = :albumId")
    Maybe<List<Song>> getSongbyAlbumList(long albumId);

    @Query("SELECT * FROM songs WHERE artist_id = :artistId")
    Maybe<List<Song>> getSongbyArtistList(long artistId);

    //Album
    @Insert
    Completable addAlbums(List<Albums> albums);

    @Query("SELECT * FROM albums WHERE artist_id = :artist_id")
    Maybe<List<Albums>> getAlbumByArtist(long artist_id);

    @Query("SELECT * FROM albums ORDER BY artist ASC ")
    Maybe<List<Albums>> getAlbums();

    //Artist
    @Insert
    Completable addArtist(List<Artist> artists);

    @Query("SELECT * FROM artist ORDER BY artist ASC")
    Maybe<List<Artist>> getArtist();


}
