package com.example.sangeetplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Adapter.tracksAdapter;
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
import kotlin.jvm.internal.markers.KMutableSet;

public class Tracks_Fragment extends Fragment implements tracksAdapter.OnClick {
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private Song song;
    private List<Song> songs;
    private tracksAdapter adapter;
    private SongViewModel songViewModel;

    private Disposable disposable;

    private SharedPref sharedPref;

    private boolean animate = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.recyle_tracks);
        manager = new LinearLayoutManager(getActivity());

        songs = new ArrayList<>();
        adapter = new tracksAdapter(songs, this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        sharedPref = new SharedPref(getActivity());

        songViewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);


        songViewModel.getSongs()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<List<Song>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Song> songs) {

                        if (MainActivity.shouldAnimate){
                            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            adapter.addSongs(songs);
                            recyclerView.scheduleLayoutAnimation();
                            MainActivity.shouldAnimate = false;
                            return;
                        }

                        adapter.addSongs(songs);
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!disposable.isDisposed()){

            disposable.dispose();
        }

    }

    @Override
    public void onClick( List<Song> songs ,int pos) {

        songViewModel.setPos(pos);
        songViewModel.setSongPos(pos);
      //  songViewModel.setSongPrefWhere("songs", 1);
        songViewModel.songsList.setValue(songs);


    }

    @Override
    public void addToFav(Song song) {

        addToFavs(song);
    }

    @Override
    public void playNext(Song song) {

        songViewModel.playNextSong.setValue(song);
    }

    @Override
    public void addToPlay(Song song) {

        songViewModel.addToPlaylist.setValue(song);
    }

    private void addToFavs(Song song) {


        songViewModel.addSong(song)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onComplete() {


                        Toast.makeText(getContext(), "Added to favourite.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {


                          Toast.makeText(getContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
