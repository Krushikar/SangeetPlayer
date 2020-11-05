package com.example.sangeetplayer.Preference;

import android.content.Context;
import android.content.SharedPreferences;

public class dataPref {

    private final String SHOULD_SCAN = "should_scan", IS_SCANNED = "is_scanned", FIRST_LOAD = "first_load";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public dataPref(Context context){
        preferences = context.getSharedPreferences("dataPref", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public boolean shouldScan(){ return preferences.getBoolean(SHOULD_SCAN, true);}
    public boolean isScanned(){ return preferences.getBoolean(IS_SCANNED, false);}
    public boolean firstLoad(){ return preferences.getBoolean(FIRST_LOAD, true);}

    public void setFisrt_load(boolean b){
        editor.putBoolean(FIRST_LOAD, b);
        editor.commit();
    }

    public void shouldScan(boolean b){
        editor.putBoolean(SHOULD_SCAN, b);
        editor.commit();
    }

    public void setScanned(boolean b){
        editor.putBoolean(IS_SCANNED, b);
        editor.commit();
    }
}
