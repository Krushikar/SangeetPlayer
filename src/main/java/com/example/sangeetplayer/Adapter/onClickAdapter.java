package com.example.sangeetplayer.Adapter;

import com.example.sangeetplayer.entities.Song;

import java.util.List;

public interface onClickAdapter {

     void onClick(List<Song> songs, int pos);

     void setSong(int pos);

     void addToFav(Song song);

     void addToPlay(Song song, boolean shouldPLay);

     void playNext(Song song);

}
