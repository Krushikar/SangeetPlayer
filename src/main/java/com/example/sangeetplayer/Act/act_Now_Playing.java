package com.example.sangeetplayer.Act;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sangeetplayer.Adapter.common_Adapter;
import com.example.sangeetplayer.Adapter.onClickAdapter;
import com.example.sangeetplayer.Adapter.tracksAdapter;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.Services.MusicService;
import com.example.sangeetplayer.Util.Utilities;
import com.example.sangeetplayer.ViewModel.SongViewModel;
import com.example.sangeetplayer.entities.Song;
import com.jackandphantom.blurimage.BlurImage;
import com.jackandphantom.paletteshadowview.PaletteShadowView;
import com.mikhaellopez.circleview.CircleView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import io.github.armcha.coloredshadow.ShadowImageView;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class act_Now_Playing extends AppCompatActivity  {

    private ImageView btn_menu, btn_shuffle, btn_equilizer, btn_repeat, btn_que,  btn_fav;
    private boolean btn_hidden = true, que_hidden = true;

    private Animation fade_out, fade_in, rotate, rotate_again;
    private ImageView play_pause, next, prev, next_song_img;
    private TextView totalDur, currentDur, text_song, text_artist, next_song;

    private SeekBar seekBar;
    private CircularImageView artView;
    private ImageView blur_image;
  //  private CircleView artview_shadow;

    private SongViewModel songViewModel;
    private MusicService musicService;
    private boolean isBound = false, isSongPrepare = false, firstLoad = true, is_repeat = false, is_shuffle = false, is_fav = false, is_fav_waite = false;

    private Song song;
    private Handler handler;
    private Utilities utilities;
    private long total, current_var;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private List<Song> songList;

    private ShadowImageView shadowImageView;
    private PaletteShadowView paletteShadowView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_now_playing1);

        handler = new Handler();
        utilities = new Utilities();

        shadowImageView = findViewById(R.id.sh_image_art);
        paletteShadowView = findViewById(R.id.pl_image_art);
        blur_image = findViewById(R.id.imageView_blur);
        artView = findViewById(R.id.circular_image_art);
        next_song = findViewById(R.id.next_song_name);
        next_song_img = findViewById(R.id.next_song_art);
        text_song = findViewById(R.id.text_song);
        text_artist = findViewById(R.id.text_artist);
        btn_que = findViewById(R.id.imageView4);
        btn_fav = findViewById(R.id.fav);
        btn_menu = findViewById(R.id.imageView3);
        btn_shuffle = findViewById(R.id.shuffle);
        btn_equilizer = findViewById(R.id.equilizer);
        btn_repeat = findViewById(R.id.repeat);

        play_pause = findViewById(R.id.play_pause);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        totalDur = findViewById(R.id.total_dur);
        currentDur = findViewById(R.id.current_dur);

        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        rotate_again = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_again);

        onclick();

        songList = new ArrayList<>();

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);


        StartService();

        intentFilter = new IntentFilter("ServiceAction");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) {

            unbindService(serviceConnection);
            isBound = !isBound;
        }

        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onStart() {
        super.onStart();

        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        if (isBound){

            refreshUI();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private void onclick() {

        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!is_fav_waite){

                    if (is_fav){

                        removeFav();
                     //   Toast.makeText(musicService, "Removing favourite.", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    addToFav();
                  //  Toast.makeText(musicService, "Adding favourite.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        btn_equilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   showEqualizer();
               Intent intent = new Intent(act_Now_Playing.this, act_Equalizer1.class);
               intent.putExtra("sessionID", musicService.getSessionId());
               startActivity(intent);
               toggleMenu();
            }
        });

        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleShuffle();
            }
        });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleRepeat();
            }
        });

        btn_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleQue();

            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleMenu();
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBound) {

                    musicService.togglePlayer();

                    if (musicService.isPlaying()){

                        play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));

                    }else {

                        play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                    }


                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBound) {
                    musicService.prevSong();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBound) {
                    musicService.nextSong();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekBar.setProgress(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                handler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                handler.removeCallbacks(mUpdateTimeTask);

                long total = musicService.getTotlalDuration();

                int current = utilities.progressToTimer(seekBar.getProgress(), (int) total);


                musicService.seekTo(current);


                updateProgressBar();

            }
        });


    }

    private void isFav(){

        is_fav_waite = true;
        songViewModel.getSong((int) song.getSong_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Song>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Song song) {

                        is_fav = true;
                        is_fav_waite = false;
                        toggleFav();
                    }

                    @Override
                    public void onError(Throwable e) {

                        is_fav = false;
                        is_fav_waite = false;
                        toggleFav();
                        Log.e("IS_FAV ", "IS FAV ERROR "+e.getMessage());
                    }
                });

    }

    private void addToFav() {

        is_fav_waite = true;
        songViewModel.addSong(song)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                        is_fav = true;
                        is_fav_waite = false;
                        toggleFav();
                        Toast.makeText(musicService, "Added to favourite.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                        is_fav_waite = false;
                      //  Toast.makeText(musicService, "Added to favourite E."+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void removeFav() {

        is_fav_waite = true;
        songViewModel.deleteSong(song.getSong_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                        is_fav = false;
                        is_fav_waite = false;
                        toggleFav();
                        Toast.makeText(musicService, "Removed from favourite.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                        is_fav_waite = false;
                        Log.e("IS_FAV ", "IS FAV REMOVE ERROR "+e.getMessage());

                    }
                });
    }

    private void toggleFav() {

        int id = is_fav ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_white_24dp;

        btn_fav.setImageDrawable(getResources().getDrawable(id));
    }

    private void showEqualizer() {

        DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                .setAudioSessionId(musicService.getSessionId())
                .themeColor(ContextCompat.getColor(this, R.color.white))
                .textColor(ContextCompat.getColor(this, R.color.black))
                .accentAlpha(ContextCompat.getColor(this, R.color.red1))
                .darkColor(ContextCompat.getColor(this, R.color.black))
                .setAccentColor(ContextCompat.getColor(this, R.color.red1))
                .build();
        fragment.show(getSupportFragmentManager(), "eq");

    }

    private void toggleQue() {

//        if (que_hidden){
//
//            recyclerView.setVisibility(View.VISIBLE);
//        }else {
//
//            recyclerView.setVisibility(View.INVISIBLE);
//        }
//
//        que_hidden = !que_hidden;

        Intent intent = new Intent(this, act_Playlist.class);
        startActivity(intent);
    }

    private void refreshUI() {

        int pos = musicService.getCurrentSongPos();
        int next = pos == songList.size() - 1 ? 0 : pos + 1;
        song = songList.get(pos);



        text_song.setText(""+song.getTitle());
        text_artist.setText(""+song.getArtist());

        next_song.setText(""+songList.get(next).getTitle());

//        Picasso.get()
//                .load(song.getAlbum_art())
//                .placeholder(R.drawable.album_art1)
//                .into(artView);

        Picasso.get()
                .load(songList.get(next).getAlbum_art())
                .resize(800, 800)
                .onlyScaleDown()
                .centerCrop()
                .placeholder(R.drawable.album_art1)
                .into(next_song_img);

        Glide.with(getApplicationContext())
                .load(song.getAlbum_art())
                .apply(new RequestOptions().override(800,800))
                .placeholder(R.drawable.album_art)
                .error(R.drawable.album_art)
                .centerCrop()
                .into(new ViewTarget<ImageView, Drawable>(paletteShadowView) {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);

                        shadowImageView.setImageDrawable(placeholder, true);
                       // paletteShadowView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        super.onLoadCleared(placeholder);

                        shadowImageView.setImageDrawable(placeholder, true);

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);

                        shadowImageView.setImageDrawable(errorDrawable, true);
                      //  paletteShadowView.setImageDrawable(errorDrawable);

                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        shadowImageView.setImageDrawable(resource, true);
                      //  paletteShadowView.setImageDrawable(resource);

                    }
                });


        total = musicService.getTotlalDuration();
        totalDur.setText("" + utilities.milliSecondsToTimer(total));

        if (musicService.isPlaying()){

     //       currentDur.setText("" + utilities.milliSecondsToTimer(musicService.getCurrentDuration()));
            play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            updateProgressBar();
        }else {

            play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
        }

        isFav();

//        try
//        {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver() , Uri.parse(song.getAlbum_art()));
//
//            if (bitmap != null){
//
//                BlurImage.with(getApplicationContext()).load(bitmap).intensity(20).Async(true).into(blur_image);
//
//            }else {
//
//                BlurImage.with(getApplicationContext()).load(R.drawable.album_art1).intensity(20).Async(true).into(blur_image);
//
//            }
//        }
//        catch (Exception e)
//        {
//            //handle exception
//            BlurImage.with(getApplicationContext()).load(R.drawable.album_art1).intensity(20).Async(true).into(blur_image);
//
//        }
    }

    private void onSongComplete() {

        handler.removeCallbacks(mUpdateTimeTask);

    }

    private void onSongPrepared() {

        refreshUI();
    }

    private void onSongPause() {

        handler.removeCallbacks(mUpdateTimeTask);
    }

    private void onSongResume() {

        updateProgressBar();
    }

    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {

         //   long total = musicService.getTotlalDuration();
            long current = musicService.getCurrentDuration();

         //   totalDur.setText("" + utilities.milliSecondsToTimer(total));
            currentDur.setText("" + utilities.milliSecondsToTimer(current));


            int progress = (int) (utilities.getProgressPercentage(current, total));

            seekBar.setProgress(progress);

            handler.postDelayed(this, 100);
        }
    };

    private void toggleMenu() {

        if (btn_hidden) {

            btn_repeat.setVisibility(View.VISIBLE);
            btn_equilizer.setVisibility(View.VISIBLE);
            btn_shuffle.setVisibility(View.VISIBLE);
            btn_fav.setVisibility(View.VISIBLE);

            btn_menu.startAnimation(rotate);
            btn_shuffle.startAnimation(fade_in);
            btn_repeat.startAnimation(fade_in);
            btn_equilizer.startAnimation(fade_in);
            btn_fav.startAnimation(fade_in);

        } else {

            btn_repeat.setVisibility(View.INVISIBLE);
            btn_equilizer.setVisibility(View.INVISIBLE);
            btn_shuffle.setVisibility(View.INVISIBLE);
            btn_fav.setVisibility(View.INVISIBLE);

            btn_menu.startAnimation(rotate_again);
            btn_shuffle.startAnimation(fade_out);
            btn_repeat.startAnimation(fade_out);
            btn_equilizer.startAnimation(fade_out);
            btn_fav.startAnimation(fade_out);
        }

        btn_hidden = !btn_hidden;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void StartService() {

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

        BindService();
    }

    private void BindService() {

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getStringExtra("action");


            if (action != null) {
                switch (action) {

                    case "complete":
                        onSongComplete();
                        break;
                    case "pause":
                        onSongPause();
                        break;
                    case "resume":
                        onSongResume();
                        break;
                    case "prepared":
                        onSongPrepared();
                        break;

                }
            }

        }
    };



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.ServieBinder binder = (MusicService.ServieBinder) service;

            musicService = binder.getMusicService();
            isBound = true;


            current_var = musicService.resumePos;
            currentDur.setText("" + utilities.milliSecondsToTimer(current_var));
            //  seekBar.setMax((int) total);
        //    totalDur.setText("" + utilities.milliSecondsToTimer(total));

            songList = musicService.getSongList();
            int pos = musicService.getCurrentSongPos();
            total = songList.get(pos).getDuration();
            totalDur.setText(""+utilities.milliSecondsToTimer(total));
            int progress = (int) (utilities.getProgressPercentage(current_var, total));
          //  int max = (int) (utilities.getProgressPercentage(songList.get(pos).getDuration(), songList.get(pos).getDuration()));


          //  seekBar.setMax((int) total);
            seekBar.setProgress(progress);

           setShuffle();
           setRepeat();
           refreshUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void setShuffle(){
        int id = musicService.isShuffle ? R.drawable.ic_shuffle_on : R.drawable.ic_shuffle;

        btn_shuffle.setImageDrawable(getResources().getDrawable(id));
    }

    private void setRepeat(){

        int id = musicService.isReapeat ? R.drawable.ic_repeat_on : R.drawable.ic_repeat;

        btn_repeat.setImageDrawable(getResources().getDrawable(id));
    }


    private void toggleShuffle() {



        Log.e("isShuffle", "First "+musicService.isShuffle);

       musicService.isShuffle = !musicService.isShuffle;
       setShuffle();

        Log.e("isShuffle", ""+musicService.isShuffle);
    }

    private void toggleRepeat() {



        Log.e("isRepeat", "First "+musicService.isReapeat);

        musicService.isReapeat = !musicService.isReapeat;
        setRepeat();

        Log.e("isRepeat", ""+musicService.isReapeat);
    }


}