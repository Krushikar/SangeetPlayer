package com.example.sangeetplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Act.act_artistDetail;
import com.example.sangeetplayer.Adapter.artistAdapter;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.SharedPref.SharedPref;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Artist;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Artist_Fragment extends Fragment implements artistAdapter.OnClick {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private artistAdapter adapter;
    private List<Artist> artistList;
    private SongViewModel songViewModel;
    private Disposable disposable;
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.recyle_artist);
        manager = new LinearLayoutManager(getActivity());
        artistList = new ArrayList<>();
        adapter = new artistAdapter(artistList, getActivity(),this::onClick);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        sharedPref = new SharedPref(getActivity());

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        songViewModel.getArtist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<Artist>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Artist> artists) {

                        adapter.addArtist(artists);
                    }

                    @Override
                    public void onError(Throwable e) {


                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(Artist artist) {
        Intent intent = new Intent(getActivity(), act_artistDetail.class);
        intent.putExtra("artist", artist);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}
