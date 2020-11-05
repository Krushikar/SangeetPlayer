package com.example.sangeetplayer.model;

public class tracks {

    private long song_id;
    private int song_path;

    public tracks(long song_id, int song_path) {
        this.song_id = song_id;
        this.song_path = song_path;
    }

    public long getSong_id() {
        return song_id;
    }

    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }

    public int getSong_path() {
        return song_path;
    }

    public void setSong_path(int song_path) {
        this.song_path = song_path;
    }
}
