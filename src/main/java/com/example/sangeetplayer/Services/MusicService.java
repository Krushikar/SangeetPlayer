package com.example.sangeetplayer.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Virtualizer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.sangeetplayer.R;
import com.example.sangeetplayer.SharedPref.SharedPref;
import com.example.sangeetplayer.entities.Song;
import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayer;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;


public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_PAUSE_PLAY = PACKAGE_NAME + ".playpause";
    public static final String ACTION_NEXT = PACKAGE_NAME + ".next";
    public static final String ACTION_PREVIOUS = PACKAGE_NAME + ".prev";
    private static final String MAIN_ACTION = PACKAGE_NAME + ".main";

    public static IMediaPlayerFactory mediaPlayerFactory;
    public  Equalizer equalizer;
    public  BassBoost bassBoost;
    public  LoudnessEnhancer enhancer;
    public  Virtualizer virtualizer;

    // private  IBasicMediaPlayer player;
    private MediaPlayer player;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;

    private List<Song> songList;
    private Song song;
    //   private MediaPlayer player;
    public int resumePos = 0, songDur = 0;
    private int currentSongPos;
    public boolean firstLoad = true, isShuffle = false, isReapeat = false, isPrepaired = false, shouldPlay = false;
    private final String EQ = "eq", BASS = "bass", VR = "vr", LOUD = "loud";

    private SharedPref sharedPref;

    private final String CHANNEL_ID = "MusicService";
    private final int NOTIFICATION_ID = 101;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private final IBinder iBinder = new ServieBinder();
    private RemoteViews notificationLayout, bignotificationLayout;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }



    public void setBandLevel(short band, int i) {

        equalizer.setBandLevel(band, (short) i);
        sharedPref.setBandLevel(band, i);
    }

    public LoudnessEnhancer getEnhancer() {

        return this.enhancer;
    }

    public BassBoost getBass() {

        return this.bassBoost;
    }


    public class ServieBinder extends Binder {

        public MusicService getMusicService() {

            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPref = new SharedPref(getApplicationContext());

        songList = new ArrayList<>();

        currentSongPos = sharedPref.getSongPosPref();
     //   resumePos = sharedPref.getSongSeekToPref();
     //   songDur = sharedPref.getSongDurPref();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this::onAudioFocusChange, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        initPlayer();

        initEq();

        buildNotification();
    }

    private void initEq() {

        equalizer = new Equalizer(10,player.getAudioSessionId());
        bassBoost = new BassBoost(10, player.getAudioSessionId());
        virtualizer = new Virtualizer(10, player.getAudioSessionId());
        enhancer = new LoudnessEnhancer(player.getAudioSessionId());

    }


    private void buildNotification() {


        notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        bignotificationLayout = new RemoteViews(getPackageName(), R.layout.notification_big);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent buttonPlayIntent, buttonNext, buttonPrev;
        PendingIntent pendingPlay, pendingNext, pendingPrev, pendingIntent;
        final ComponentName serviceName = new ComponentName(this, MusicService.class);

        Intent notificationIntent = new Intent(this, MusicService.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        buttonPlayIntent = new Intent(this, MusicService.class);
        buttonPlayIntent.setAction(ACTION_PAUSE_PLAY);
        pendingPlay = PendingIntent.getService(this, 0, buttonPlayIntent, 0);

        buttonNext = new Intent(this, MusicService.class);
        buttonNext.setAction(ACTION_NEXT);
        pendingNext = PendingIntent.getService(this, 0, buttonNext, 0);

        buttonPrev = new Intent(this, MusicService.class);
        buttonPrev.setAction(ACTION_PREVIOUS);
        pendingPrev = PendingIntent.getService(this, 0, buttonPrev, 0);


        bignotificationLayout.setOnClickPendingIntent(R.id.notify_play, pendingPlay);

        bignotificationLayout.setOnClickPendingIntent(R.id.notify_next, pendingNext);

        bignotificationLayout.setOnClickPendingIntent(R.id.notify_prev, pendingPrev);

        notificationLayout.setOnClickPendingIntent(R.id.notify_play, pendingPlay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "music_channel",
                    NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);

        }


        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.interface5)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(bignotificationLayout)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        bignotificationLayout.setTextViewText(R.id.notify_song, ""+song.getTitle());
//        bignotificationLayout.setTextViewText(R.id.notify_artist, ""+song.getArtist());


        startForeground(NOTIFICATION_ID, builder.build());

    }

    public void removeNotificatio() {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        if (intent != null && intent.getAction() != null) {

            switch (intent.getAction()) {

                case ACTION_PAUSE_PLAY:

                    togglePlayer();
                    break;
                case ACTION_NEXT:

                    nextSong();
                    break;
                case ACTION_PREVIOUS:

                    prevSong();
                    break;
            }
        }

        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (player != null) {

         //   sharedPref.addSongPref(currentSongPos, player.getCurrentPosition(), player.getDuration());
            stopPlayer();


        }

        equalizer.release();
        bassBoost.release();
        virtualizer.release();
        enhancer.release();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            audioManager.abandonAudioFocusRequest(audioFocusRequest);
//        }

        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange){

            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (player != null && player.isPlaying()){
                    player.pause();
                    shouldPlay = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN :
                if (player != null){

                    if (shouldPlay){
                        player.start();
                        shouldPlay = false;
                    }

                }
                break;
//            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
//                if (player != null){
//                    player.setVolume(0.3f,0.3f);
//                }
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                if(player != null){
//                 //   player.setVolume(0.2f,0.2f);
//                    player.pause();
//                    shouldPlay = true;
//                }
//                break;


        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {


        if (!isReapeat) {

            setSongPos(true);
        }

        setCurrentSong();

        setPlayer();

        setPrepaire();

        sendBroadcast("complete");

        updateNotify();
    }


    private void updateNotify() {

        bignotificationLayout.setTextViewText(R.id.notify_song, "" + song.getTitle());
        bignotificationLayout.setTextViewText(R.id.notify_artist, "" + song.getArtist());

        notificationLayout.setTextViewText(R.id.notify_song, "" + song.getTitle());
        notificationLayout.setTextViewText(R.id.notify_artist, "" + song.getArtist());


        int id = isPlaying() ? R.drawable.pause : R.drawable.play;

        bignotificationLayout.setImageViewResource(R.id.notify_play, id);
        notificationLayout.setImageViewResource(R.id.notify_play, id);

        Picasso.get()
                .load(song.getAlbum_art())
                .into(bignotificationLayout, R.id.notify_img, NOTIFICATION_ID, builder.build());

        Picasso.get()
                .load(song.getAlbum_art())
                .into(notificationLayout, R.id.notify_img, NOTIFICATION_ID, builder.build());

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        Log.e("aaaaaaaaaaa", ""+what);

        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {


        sendBroadcast("prepared");
        isPrepaired = true;



        player.start();
        updateNotify();

    }

    private void initPlayer() {

        if (player == null) {

         //   mediaPlayerFactory = new OpenSLMediaPlayerFactory(getApplicationContext());
         //   player = mediaPlayerFactory.createMediaPlayer();
            player = new MediaPlayer();
            player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            player.setOnPreparedListener(this::onPrepared);
            player.setOnCompletionListener(this::onCompletion);
            player.setOnErrorListener(this::onError);
            isPrepaired = false;
//            player.prepareAsync();
            player.reset();


        }
    }

    public void stopPlayer() {

        player.stop();
        player.release();
     //   mediaPlayerFactory.release();
        player = null;

    }

    public void startAPlayer() {

        player.start();
    }


    public void setSongs(List<Song> songs) {

        songList.clear();
        songList.addAll(songs);
        setCurrentSong();
    }

    public void playNext(Song song){

//        if (songList.contains(song)){
//
//            songList.remove(song);
//        }

        songList.add(currentSongPos+1, song);

    }

    public void addToPlay(Song song, boolean shouldPlay){

        if (!songList.contains(song)){

            songList.add(songList.size(), song);

            if (shouldPlay){

                setCurrentSongPos(songList.size());

                setCurrentSong();

                setPlayer();

                setPrepaire();
            }

        }

        for (Song song1:songList){

            if (song1.getSong_id() == song.getSong_id()){

            }

        }

    }

    public void removeSongPlay(int pos){

        if (songList.size() <= 1 ){

            return;
        }

        songList.remove(pos);

        if (pos < currentSongPos){

            currentSongPos = currentSongPos - 1;
            return;
        }

        if (currentSongPos == pos){


           setCurrentSongPos(pos);

            setCurrentSong();

            setPlayer();

            setPrepaire();
        }


    }

    public int getTotalSong() {

        return songList.size();
    }

    public int getSessionId() {

        return player.getAudioSessionId();
    }

    public long getSongDuration() {

        return song.getDuration();
    }

    public List<Song> getSongList() {

        return this.songList;
    }

    public int nextSongPos() {

        return currentSongPos++;
    }

    public int getCurrentSongPos() {

        return currentSongPos;
    }

    public void setCurrentSongPos(int pos) {

        currentSongPos = pos > songList.size() - 1 ? 0 : pos;
    }

    public void setCurrentSong() {

        song = songList.get(currentSongPos);

    }

    public long getPlayerSeek() {

        return resumePos;
    }

    public void setSong() {

        song = songList.get(currentSongPos);
    }

    private void setSongPos(boolean isNext) {

        if (isShuffle) {

            int min = 0;
            int max = songList.size();
            currentSongPos = new Random().nextInt((max - min) + 1) + min;

            return;
        }


        if (isNext) {

            currentSongPos = currentSongPos >= songList.size() - 1 ? 0 : currentSongPos + 1;
            return;
        }

        currentSongPos = currentSongPos == 0 ? songList.size() : currentSongPos - 1;

    }

    public Song getCurrentSong() {

        return songList.get(currentSongPos);
    }

    public boolean isPlaying() {
        boolean returnValue = false;

        try {
            returnValue = player.isPlaying();
        } catch (IllegalStateException e) {
//            player.reset();
//            player.prepareAsync();
        }

        return returnValue;
    }


    public void togglePlayer() {

        if (!isPrepaired){

            setPrepaire();
        }

        if (player.isPlaying()) {

            pausePlayer();

        } else {

//            if (!isPrepaired) {
//
//                setPrepaire();
//                return;
//            }

            resumePlayer();

        }

    }

    public void setPlayer() {

        player.reset();

        try {

            player.setDataSource(song.getFilePath());
         //   player.prepareAsync();
            isPrepaired = false;

        } catch (IOException e) {

            e.printStackTrace();
        }



    }

    public void setPrepaire() {

        try {
            player.prepareAsync();
        }catch (IllegalStateException i){
            
        }
        isPrepaired  = false;
    }

    public void startResumePlayer() {


        try {

            player.setDataSource(song.getFilePath());
            player.seekTo(resumePos);


        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void nextSong() {


        setSongPos(true);

        setCurrentSong();

        setPlayer();

        setPrepaire();

    }

    private int getSongPos(boolean isNext) {


        return currentSongPos;

    }

    public void prevSong() {

        setSongPos(false);

        setCurrentSong();

        setPlayer();

        setPrepaire();
    }

    public void pausePlayer() {

        player.pause();
        resumePos = player.getCurrentPosition();
        sendBroadcast("pause");

        updateNotify();
    }

    public void seekTo(int seek) {

        resumePos = seek;



        player.seekTo(seek);
    }

    public long getCurrentDuration() {

        return isPrepaired ? player.getCurrentPosition() : 0;
    }

    public long getTotlalDuration() {

        return song.getDuration();
    }

    public void resumePlayer() {


        player.seekTo(resumePos);
        player.start();
        sendBroadcast("resume");

        updateNotify();
    }

    public int getDuration() {

        return player.getDuration();
    }

    private void sendBroadcast(String action) {

        Intent intent = new Intent("ServiceAction");
        intent.putExtra("action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}
