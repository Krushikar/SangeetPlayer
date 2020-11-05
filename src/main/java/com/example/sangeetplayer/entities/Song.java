package com.example.sangeetplayer.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {

//    @PrimaryKey(autoGenerate = true)
//    private int id;

    public void setId(int id) {
        this.song_id = id;
    }

    @PrimaryKey
    private long song_id;

    private String title;
    private String artist;
    private long artist_id;
    private String album;
    private long album_id;
    private int year;
    private int track_no;
    private long duration;
    private String filePath;
    private String genre;
    private String album_art;


    public Song() {

    }

    public Song( String title, String artist, long artist_id, String album, long album_id, int year, int track_no, long duration_ms, long song_id, String filePath) {
        this.title = title;
        this.artist = artist;
        this.artist_id = artist_id;
        this.album = album;
        this.album_id = album_id;
        this.year = year;
        this.track_no = track_no;
        this.duration = duration_ms;
        this.song_id = song_id;
        this.filePath = filePath;
        this.album_art = "content://media/external/audio/media/" + song_id + "/albumart";
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        return this.song_id == ((Song)obj).song_id;
    }

    public long getId() {
        return song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTrack_no() {
        return track_no;
    }

    public void setTrack_no(int track_no) {
        this.track_no = track_no;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration_ms) {
        this.duration = duration_ms;
    }

    public long getSong_id() {
        return song_id;
    }

    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }
}
