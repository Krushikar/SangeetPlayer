package com.example.sangeetplayer.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Act.act_AlbumDetail;
import com.example.sangeetplayer.Adapter.albumAdapter;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Albums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Album_Fragment extends Fragment implements com.example.sangeetplayer.Adapter.albumAdapter.OnClick {

    private RecyclerView recyclerView;
    private GridLayoutManager manager;
    private albumAdapter albumAdapter;
    private List<Albums> albumsList;
    private SongViewModel songViewModel;
    private Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.recyle_album);
        manager = new GridLayoutManager(getActivity(),3 );

        albumsList = new ArrayList<>();
        albumAdapter = new albumAdapter(albumsList, getActivity(), this::onClick);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(albumAdapter);

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);

        songViewModel.getAlbums()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<Albums>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Albums> albums) {

                        albumAdapter.addAlbums(albums);
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
    public void onDestroy() {
        super.onDestroy();

        if (!disposable.isDisposed()){

            disposable.dispose();
        }
    }

    @Override
    public void onClick(Albums albums, ImageView imageView, TextView albumName) {
        String transition_name = ViewCompat.getTransitionName(imageView);
        Intent intent = new Intent(getActivity(), act_AlbumDetail.class);
//                intent.putExtra("album_id", albums.getAlbum_id());
//                intent.putExtra("album_name", albums.getAlbum());
//                intent.putExtra("album_art", albums.getAlbum_art());
        intent.putExtra("album", albums);
        intent.putExtra("transition", transition_name);

        Pair<View, String> pair = new Pair<>(imageView, ViewCompat.getTransitionName(imageView));
        Pair<View, String> pair1 = new Pair<>(albumName, ViewCompat.getTransitionName(albumName));



        assert transition_name != null;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), imageView,
                transition_name);

        //ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair,pair1);

        startActivity(intent, options.toBundle());
     //   getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}
