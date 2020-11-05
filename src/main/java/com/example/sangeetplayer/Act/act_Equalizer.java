package com.example.sangeetplayer.Act;

import android.content.Context;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.sangeetplayer.R;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class act_Equalizer extends AppCompatActivity {

    private LinearLayout linearLayout, mlinearLayout;
    private Spinner spinner;
    private SeekBar seekBar;

    private Equalizer equalizer;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_equalizer1);

        spinner = findViewById(R.id.spinner_preset);
        presetList  = new ArrayList<>();

        bass = findViewById(R.id.croller_bass);
        loud = findViewById(R.id.croller_loud);
        virtual = findViewById(R.id.croller_vr);

        linearLayout = findViewById(R.id.linear_eq);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

       // linearLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , width));

        mlinearLayout = new LinearLayout(this);
        mlinearLayout.setOrientation(LinearLayout.VERTICAL);

        sessionID = getIntent().getIntExtra("sessionID",0);

        equalizer = new Equalizer(10,sessionID);
        equalizer.setEnabled(true);
        minEQLevel = equalizer.getBandLevelRange()[0];
        maxEQLevel = equalizer.getBandLevelRange()[1];

        setupUI();
        getPreset();

        bassBoost = new BassBoost(0,sessionID);
        bassBoost.setEnabled(true);

        bass.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {

                bassBoost.setStrength((short) (progress * 100));
                Log.e("BASS", "Bass "+(progress * 25 ));
            }
        });

        virtual.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {

            }
        });

        loud.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {

            }
        });
    }

    private void getPreset() {

        noPreset = equalizer.getNumberOfPresets();

        for (short i = 0; i < noPreset; i++){

            presetList.add(equalizer.getPresetName(i));
        }

     //   ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, presetList);

     //   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        presetAdapter adapter = new presetAdapter(this, presetList);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                equalizer.usePreset((short) position);

                seekBarPro();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void seekBarPro() {

        short bands = equalizer.getNumberOfBands();
        final short lowerEqualizerBandLevel = equalizer.getBandLevelRange()[0];

        for (short i = 0; i < bands; i++){
            final short band = i;

          SeekBar  seekBar = findViewById(band);
         //   seekBar.setProgress((maxEQLevel - minEQLevel) / 2);
           seekBar.setProgress(equalizer.getBandLevel(i)-minEQLevel);

        }
    }

    private void setupUI(){

        short bands = equalizer.getNumberOfBands();
        final short minEQLevel = equalizer.getBandLevelRange()[0];
        final short maxEQLevel = equalizer.getBandLevelRange()[1];
        linearLayout.setWeightSum(bands);

        for (short i = 0; i < bands; i++){

            final short band = i;

//            TextView freqTextView = new TextView(this);
//
//            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//            freqTextView.setText((equalizer.getCenterFreq(band) / 1000) + " Hz");
//            freqTextView.setTextSize(10);
//            freqTextView.setPadding(0,30,0,0);
//
//
//            linearLayout.addView(freqTextView);
//
//            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutParams1.weight = 1;
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams2.weight = 1;


            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(layoutParams2);
         //   row.setWeightSum(4);



            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(120,120);
          //  layoutParams1.weight = 1;

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
         //   minDbTextView.setRotation(90);
            minDbTextView.setTextSize(10);
         //   minDbTextView.setPadding(-45,0,0,0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              //  minDbTextView.setBackgroundColor(getColor(R.color.red1));
            }
            minDbTextView.setGravity(Gravity.CENTER);
         //   minDbTextView.setText((minEQLevel / 100) + " dB");
            int fr = equalizer.getCenterFreq(band) / 1000;

            if (fr >= 1000){
              //  DecimalFormat decimalFormat = new DecimalFormat("#.##");
              //  float f = fr/1000;
                minDbTextView.setText( ( fr / 1000) + " KHz");
            }else {

                minDbTextView.setText( fr + " Hz");
            }

         //   minDbTextView.setText((equalizer.getCenterFreq(band) / 1000) + " Hz");
         //   minDbTextView.setLayoutParams(layoutParams1);


            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
         //   maxDbTextView.setRotation(90);
            maxDbTextView.setTextSize(10);
         //   maxDbTextView.setPadding(25,45,0,0);
         //   maxDbTextView.setGravity(Gravity.BOTTOM|Gravity.CENTER);
               maxDbTextView.setGravity(Gravity.LEFT|Gravity.CENTER);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //    maxDbTextView.setBackgroundColor(getColor(R.color.red1));
            }
          //  maxDbTextView.setText((maxEQLevel / 100) + " dB");
            maxDbTextView.setText(""+ (equalizer.getBandLevel(band) / 100) + " dB");
          //  maxDbTextView.setLayoutParams(layoutParams1);


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 25);
            layoutParams.weight = 1;
            layoutParams.setMargins(5,0,5,0);

          //  SeekBar bar = new SeekBar(this);
            SeekBar   bar = (SeekBar) LayoutInflater.from(this).inflate(R.layout.custom_seek, null);
            bar.setPadding(35, 0, 35, 0);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(equalizer.getBandLevel(band));
            bar.setId(i);
         //   bar.setBackground(getResources().getDrawable(R.drawable.seekbar_custom1));
         //   bar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_style));



            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    equalizer.setBandLevel(band, (short) (progress + minEQLevel));
                    int fr = equalizer.getBandLevel(band) / 100;
                    maxDbTextView.setText(""+fr+" dB");
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);

            linearLayout.addView(row);

        }

     //   linearLayout.setRotation(270);
    }

    public boolean toggleBassBoost() {
        boolean newState = !bassBoost.getEnabled();
        bassBoost.setEnabled(newState);
        return newState;
    }

    public class presetAdapter extends ArrayAdapter<String> {


        public presetAdapter(@NonNull Context context, List<String> preset) {
            super(context,0, preset);
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
