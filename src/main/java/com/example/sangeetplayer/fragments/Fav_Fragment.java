package com.example.sangeetplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Adapter.tracksAdapter;
import com.example.sangeetplayer.Adapter.tracksFavAdapter;
import com.example.sangeetplayer.MainActivity;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.SharedPref.SharedPref;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Fav_Fragment extends Fragment implements tracksFavAdapter.OnClick{
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private Song song;
    private List<Song> songs;
    private tracksFavAdapter adapter;
    private SongViewModel songViewModel;

    private TextView fav;
    private Disposable disposable;

    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.recyle_fav);
        manager = new LinearLayoutManager(getActivity());

        songs = new ArrayList<>();
        adapter = new tracksFavAdapter(songs, this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        sharedPref = new SharedPref(getActivity());
        fav = getActivity().findViewById(R.id.add_to_fav);

        songViewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);

        songViewModel.getFavSongs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {

                adapter.addSongs(songs);
                if (songs.isEmpty()){

                    fav.setVisibility(View.VISIBLE);
                    return;
                }

                fav.setVisibility(View.INVISIBLE);


            }
        });




    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        if (disposable != null) {
//
//            if (!disposable.isDisposed()) {
//
//                disposable.dispose();
//            }
//        }

    }

    @Override
    public void onClick( List<Song> songs ,int pos) {

        songViewModel.setPos(pos);
        songViewModel.setSongPos(pos);
       // songViewModel.setSongPrefWhere("fav", 1);
        songViewModel.songsList.setValue(songs);

    }

    @Override
    public void removeFav(Song song) {

       // songViewModel.deleteSong(song.getSong_id());

        removeFav(song.getSong_id());
    }

    @Override
    public void playNext(Song song) {

        songViewModel.playNextSong.setValue(song);
    }

    @Override
    public void addToPlay(Song song) {

        songViewModel.addToPlaylist.setValue(song);
    }


    private void removeFav(long id) {

        songViewModel.deleteSong(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {


                        Toast.makeText(getContext(), "Removed ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {


                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
