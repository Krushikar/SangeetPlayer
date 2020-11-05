package com.example.sangeetplayer.Act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sangeetplayer.R;
import com.example.sangeetplayer.Services.MusicService;
import com.example.sangeetplayer.SharedPref.SharedPref;
import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.audiofx.IBassBoost;
import com.h6ah4i.android.media.audiofx.IEqualizer;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;
import com.sdsmdg.harjot.crollerTest.Croller;

import java.util.ArrayList;
import java.util.List;

public class act_Equalizer1 extends AppCompatActivity {

    private LinearLayout linearLayout, mlinearLayout;
    private Spinner spinner;
    private ToggleButton toggleEq, toggleBass, toggleLoud, toggleVr;
    private SeekBar seekBar;

    private IMediaPlayerFactory mediaPlayerFactory;
    private IBasicMediaPlayer iBasicMediaPlayer;
    //   private IEqualizer iEqualizer;
    private Equalizer iEqualizer;

    private BassBoost bassBoost;
    private Virtualizer virtualizer;
    private LoudnessEnhancer enhancer;
    private PresetReverb reverb;
    private short noPreset, curresntPreset;
    private String presetName;
    private List<String> presetList;
    private short minEQLevel, maxEQLevel;
    private Croller bass, loud, virtual;
    private int sessionID;
    private boolean isEqEnabled = false, isBassEnabled = false, isLoudEnabled = false,
            isVrEnabled = false, isFirst = true, isBound = false, isChangeOne = false;
    private SharedPref pref;
    private final String EQ = "eq", BASS = "bass", VR = "vr", LOUD = "loud";
    private int PRBASS, PRVR, PRLOUD, CHANGE_BAND;
    private MusicService musicService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_equalizer1);

        spinner = findViewById(R.id.spinner_preset);
        presetList = new ArrayList<>();

        toggleEq = findViewById(R.id.switch_equalizer);
        toggleBass = findViewById(R.id.toggle_bass);
        toggleLoud = findViewById(R.id.toggle_loud);
        toggleVr = findViewById(R.id.toggle_vr);

        bass = findViewById(R.id.croller_bass);
        loud = findViewById(R.id.croller_loud);
        virtual = findViewById(R.id.croller_vr);

        linearLayout = findViewById(R.id.linear_eq);

        pref = new SharedPref(this);
        PRBASS = pref.getExtra(BASS);
        PRVR = pref.getExtra(VR);
        PRLOUD = pref.getExtra(LOUD);

        BindService();



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // linearLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , width));

        mlinearLayout = new LinearLayout(this);
        mlinearLayout.setOrientation(LinearLayout.VERTICAL);

        //      mediaPlayerFactory = new OpenSLMediaPlayerFactory(getApplicationContext());
//        iBasicMediaPlayer = mediaPlayerFactory.createMediaPlayer();
//        iEqualizer = mediaPlayerFactory.createHQEqualizer();


        sessionID = getIntent().getIntExtra("sessionID", 0);
        //    iEqualizer = MusicService.mediaPlayerFactory.createHQEqualizer();


        //    equalizer = new Equalizer(10,sessionID);
        //    equalizer.setEnabled(true);





        //    bassBoost =  MusicService.mediaPlayerFactory.createBassBoost(sessionID);




    }

    private void crollerProgress() {
        bass.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {

              //  Log.e("str ", " " + bassBoost.getRoundedStrength() + " " + bassBoost.getStrengthSupported());

                bassBoost.setStrength((short) (progress * 25));
                pref.setExtra(progress, BASS);
                Log.e("BASS", "Bass " + (progress * 25));
            }
        });

        virtual.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {

                virtualizer.setStrength((short) (progress * 25));
                pref.setExtra(progress, VR);
            }
        });

        loud.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {

                enhancer.setTargetGain(progress * 25);

                pref.setExtra(progress, LOUD);

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

//        PRBASS = pref.getExtra(BASS);
//        PRVR = pref.getExtra(VR);
//        PRLOUD = pref.getExtra(LOUD);
    }

    private void init() {

        iEqualizer = musicService.equalizer;
//        iEqualizer.usePreset((short)pref.getPreset());
        isEqEnabled = pref.isExtra(EQ);
        toggleEq.setChecked(isEqEnabled);

        bassBoost =musicService.getBass();
        isBassEnabled = pref.isExtra(BASS);
        bass.setProgress(PRBASS);
      //  bassBoost.setStrength((short) (PRBASS * 25));
        bassBoost.setEnabled(isBassEnabled);
        toggleBass.setChecked(isBassEnabled);

        virtualizer = musicService.virtualizer;
        isVrEnabled = pref.isExtra(VR);
        virtual.setProgress(PRVR);
     //   virtualizer.setStrength((short) (PRVR * 25));
        virtualizer.setEnabled(isVrEnabled);
        toggleVr.setChecked(isVrEnabled);

        enhancer = musicService.getEnhancer();
        isLoudEnabled = pref.isExtra(LOUD);
        loud.setProgress(PRLOUD);
    //    enhancer.setTargetGain(PRLOUD * 25);
        enhancer.setEnabled(isLoudEnabled);
        toggleLoud.setChecked(isLoudEnabled);

        minEQLevel = iEqualizer.getBandLevelRange()[0];
        maxEQLevel = iEqualizer.getBandLevelRange()[1];

        crollerProgress();
    }

    private void BindService() {

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void onClick() {

        toggleEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleEQ();
            }
        });

        toggleBass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleBassBoost();
            }
        });

        toggleLoud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleLoudEn();
            }
        });

        toggleVr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleVR();
            }
        });

    }

    private void toggleLoudEn() {

        isLoudEnabled = !enhancer.getEnabled();
        enhancer.setEnabled(isLoudEnabled);
        toggleLoud.setChecked(isLoudEnabled);

    }

    private void toggleVR() {

        isVrEnabled = !virtualizer.getEnabled();
        virtualizer.setEnabled(isEqEnabled);
        toggleVr.setChecked(isVrEnabled);

    }

    private void toggleEQ() {

        isEqEnabled = !iEqualizer.getEnabled();
        iEqualizer.setEnabled(isEqEnabled);
        toggleEq.setChecked(isEqEnabled);

    }

    private void getPreset() {

        noPreset = iEqualizer.getNumberOfPresets();

        for (short i = 0; i < noPreset; i++) {

            presetList.add(iEqualizer.getPresetName(i));
        }

        presetList.add(noPreset, "Custom");

        //   ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, presetList);

        //   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        presetAdapter adapter = new presetAdapter(this, presetList);

        spinner.setAdapter(adapter);
        spinner.setSelection(pref.getPreset());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                boolean custom = (position == noPreset);

                if (!custom) {
                    iEqualizer.usePreset((short) position);
                }

                seekBarPro(custom);

                pref.setPreset(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void seekBarPro(boolean custom) {

        short bands = iEqualizer.getNumberOfBands();
        final short lowerEqualizerBandLevel = iEqualizer.getBandLevelRange()[0];


        if (isChangeOne){

            SeekBar seekBar = findViewById(CHANGE_BAND);
            int pr = pref.getBandLevel(CHANGE_BAND) - minEQLevel;
            seekBar.setProgress(pr);

            saveBandLevel();
            isChangeOne = false;
            return;
        }


        for (short i = 0; i < bands; i++) {
            final short band = i;

            SeekBar seekBar = findViewById(band);
            //   seekBar.setProgress((maxEQLevel - minEQLevel) / 2);
            int pr = custom ? pref.getBandLevel(i) - minEQLevel : iEqualizer.getBandLevel(i) - minEQLevel;
            seekBar.setProgress(pr);

        }
    }

    private void setupUI() {

        short bands = iEqualizer.getNumberOfBands();
        final short minEQLevel = iEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = iEqualizer.getBandLevelRange()[1];
        linearLayout.setWeightSum(bands);

        for (short i = 0; i < bands; i++) {

            final short band = i;

            TextView freqTextView = new TextView(this);

            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.RIGHT);
            freqTextView.setTextSize(10);
            freqTextView.setPadding(0, 0, 0, 0);
            freqTextView.setText("" + (iEqualizer.getBandLevel(band) / 100) + " dB");


            linearLayout.addView(freqTextView);
//
//            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutParams1.weight = 1;
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams2.weight = 1;


            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(layoutParams2);
            //   row.setWeightSum(4);


            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(120, 120);
            //  layoutParams1.weight = 1;

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
            //   minDbTextView.setRotation(90);
            minDbTextView.setTextSize(10);
            //   minDbTextView.setPadding(-45,0,0,0);
            minDbTextView.setGravity(Gravity.LEFT);
            int fr = iEqualizer.getCenterFreq(band) / 1000;

            if (fr >= 1000) {
                //  DecimalFormat decimalFormat = new DecimalFormat("#.##");
                //  float f = fr/1000;
                minDbTextView.setText((fr / 1000) + " KHz");
            } else {

                minDbTextView.setText(fr + " Hz");
            }
            //   minDbTextView.setText((minEQLevel / 100) + " dB");


            //   minDbTextView.setText((equalizer.getCenterFreq(band) / 1000) + " Hz");
            //   minDbTextView.setLayoutParams(layoutParams1);


            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new LinearLayout.LayoutParams(45, ViewGroup.LayoutParams.WRAP_CONTENT));
            //   maxDbTextView.setRotation(90);
            maxDbTextView.setTextSize(10);
            //   maxDbTextView.setPadding(25,45,0,0);
            //   maxDbTextView.setGravity(Gravity.BOTTOM|Gravity.CENTER);
            maxDbTextView.setGravity(Gravity.LEFT | Gravity.CENTER);


            //  maxDbTextView.setText((maxEQLevel / 100) + " dB");
            maxDbTextView.setText("" + (iEqualizer.getBandLevel(band) / 100) + " dB");
            //  maxDbTextView.setLayoutParams(layoutParams1);


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70);
            layoutParams.weight = 1;
            layoutParams.setMargins(5, 0, 5, 0);

            //  SeekBar bar = new SeekBar(this);
            SeekBar bar = (SeekBar) LayoutInflater.from(this).inflate(R.layout.custom_seek, null);
            bar.setPadding(45, 20, 20, 20);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(iEqualizer.getBandLevel(band));
            bar.setId(i);
            //   bar.setBackground(getResources().getDrawable(R.drawable.seekbar_custom1));
            //   bar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_style));


            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    iEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                    int fr = iEqualizer.getBandLevel(band) / 100;
                    freqTextView.setText("" + fr + " dB");



                 //   musicService.setBandLevel(band, (progress + minEQLevel));

                    if (fromUser){

                        if (spinner.getSelectedItemPosition() != noPreset){
                            isChangeOne = true;
                            CHANGE_BAND = band;
                            spinner.setSelection(noPreset);
                        }
                        pref.setBandLevel(band, fr * 100);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


            row.addView(minDbTextView);
            row.addView(bar);
            //   row.addView(maxDbTextView);
          //  linearLayout.addView(minDbTextView);
            linearLayout.addView(row);


        }

        //   linearLayout.setRotation(270);
    }

    private void saveBandLevel() {

        short bands = iEqualizer.getNumberOfBands();

        for (short i = 0; i < bands; i++) {
            final short band = i;

            int pr = iEqualizer.getBandLevel(i);

            pref.setBandLevel(band, pr);
        }
    }

    public void toggleBassBoost() {

        isBassEnabled = !bassBoost.getEnabled();
        bassBoost.setEnabled(isBassEnabled);
        toggleBass.setChecked(isBassEnabled);

        Log.e("Bass en", " " + bassBoost.getEnabled());
    }

    @Override
    protected void onStop() {
        super.onStop();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound){
            unbindService(serviceConnection);
        }



        pref.setISExtra(isEqEnabled, EQ);
        pref.setISExtra(isBassEnabled, BASS);
        pref.setISExtra(isLoudEnabled, LOUD);
        pref.setISExtra(isVrEnabled, VR);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.ServieBinder binder = (MusicService.ServieBinder) service;

            musicService = binder.getMusicService();
            isBound = true;

            init();
            onClick();

            setupUI();
            getPreset();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public class presetAdapter extends ArrayAdapter<String> {


        public presetAdapter(@NonNull Context context, List<String> preset) {
            super(context, 0, preset);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        private View initView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.view_preset, parent, false
                );
            }

            TextView textViewName = convertView.findViewById(R.id.presetName);

            textViewName.setText(presetList.get(position));


            return convertView;
        }
    }
}
