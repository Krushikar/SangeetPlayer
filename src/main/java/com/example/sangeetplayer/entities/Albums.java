package com.example.sangeetplayer.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "albums")
public class Albums implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String album;
    private long album_id;
    private String artist;
    private long artist_id;
    private int total_songs;
    private String album_art;

    public Albums() {
    }

    public Albums(String album, long album_id, String artist, long artist_id, int songs, String album_art) {
        this.album = album;
        this.album_id = album_id;
        this.artist = artist;
        this.artist_id = artist_id;
        this.total_songs = songs;
        this.album_art = album_art;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(long artist_id) {
        this.artist_id = artist_id;
    }

    public int getTotal_songs() {
        return total_songs;
    }

    public void setTotal_songs(int songs) {
        this.total_songs = songs;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }
}
