package com.example.sangeetplayer.ViewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.sangeetplayer.Rapo.SongRapo;
import com.example.sangeetplayer.SharedPref.SharedPref;
import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class SongViewModel extends AndroidViewModel {

    private SongRapo songRapo;
    public MutableLiveData<List<Song>> songsList;
    public MutableLiveData<String> where;
    public MutableLiveData<Song> playNextSong, addToPlaylist;

    private int pos = 0;
    private SharedPref sharedPref;

    public SongViewModel(@NonNull Application application) {
        super(application);

        songRapo = new SongRapo(application);
        songsList = new MutableLiveData<>();
        sharedPref = new SharedPref(application);
        pos = sharedPref.getSongPosPref();
        playNextSong = new MutableLiveData<>();
        addToPlaylist = new MutableLiveData<>();

    }


    public int getPos(){

        return this.pos;
    }

    public void setPos(int p){

        this.pos = p;
    }

    public void setSongPrefWhere(String where, long id){

        sharedPref.setWHERE(where);
        sharedPref.setIF_ID(id);
    }

    public void setSongPos(int pos){

        sharedPref.setSongPos(pos);
    }


    public Maybe<List<Song>> getCurrentSongList(String where, long id){

        return songRapo.getCurrentSongList(where, id);
    }

    public Completable addSongs(List<Song> songs){

       return songRapo.insertSongs(songs);
    }

    public Completable addSong(Song song){

        return songRapo.insertSong(song);
    }

    public Completable deleteSong(long songID){

        return songRapo.deleteSong(songID);
    }

    public Single<Song> getSong(int songId){

        return songRapo.getSong(songId);
    }

    public Completable addAlbums(List<Albums> albums){

      return   songRapo.insertAlbums(albums);
    }

    public Completable addArtist(List<Artist> artists){

        return songRapo.insertArtist(artists);
    }

    public Maybe<List<Song>> getSongs(){

        return songRapo.getAllSongs();
    }

    public Maybe<List<Song>> getRingtones(){

        return songRapo.getRingtones();
    }

    public LiveData<List<Song>> getFavSongs(){

        return songRapo.getFavSongs();
    }

    public Maybe<List<Albums>> getAlbums(){

        return songRapo.getAllAlbums();
    }

    public Maybe<List<Artist>> getArtist(){

        return songRapo.getAllArtist();
    }

    public Maybe<List<Song>> getSongsByArtist(long id){

        return songRapo.getSongByArtist(id);
    }

    public Maybe<List<Song>> getSongsByAlbum(long id){

        return songRapo.getSongByAlbum(id);
    }

    public Maybe<List<Albums>> getAlbumByArtist(long id){

        return songRapo.getAlbumByArtist(id);
    }

    public Completable deleteAllSongs(){

        return songRapo.deleteAllSongs();
    }

    public Completable deleteAllArtist(){

        return songRapo.deleteAllArtist();
    }

    public Completable deleteAllAlbum(){ return songRapo.deleteAllAlbum();}


}
