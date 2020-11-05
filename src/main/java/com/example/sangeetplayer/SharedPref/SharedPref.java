package com.example.sangeetplayer.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private final String WHERE = "where", POS = "pos", SEEK_TO = "seek_to", IF_ID = "if_id",
            DUR = "song_dur", SEEKBAR = "seekbar", PRESET = "preset", EXTRA = "extra_";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        preferences = context.getSharedPreferences("songInfo", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void addSongPref( int pos, int seek_to, int sonDur){


        editor.putInt(POS, pos);
        editor.putInt(SEEK_TO, seek_to);
        editor.putInt(DUR, sonDur);

        editor.commit();
    }

    public String getSongPref(){ return preferences.getString(WHERE, "songs");}

    public int getSongPosPref(){ return preferences.getInt(POS, 0);}

    public int getSongSeekToPref(){ return preferences.getInt(SEEK_TO, 0);}

    public long getIfId(){ return preferences.getLong(IF_ID, 0);}

    public void setWHERE(String where){

        editor.putString(WHERE, where);
        editor.commit();
    }

    public void setSongPos(int pos){

        editor.putInt(POS, pos);
        editor.commit();
    }

    public void setIF_ID(long id){

        editor.putLong(IF_ID, id);
        editor.commit();
    }

    public int getSongDurPref() {

        return preferences.getInt(DUR, 0);
    }


    public void setBandLevel(int id, int seek){

        editor.putInt(SEEKBAR+id, seek);
        editor.commit();
    }

    public int getBandLevel(int id){

        return preferences.getInt(SEEKBAR+id,0);
    }

    public void setPreset(int preset){

        editor.putInt(PRESET, preset);
        editor.commit();
    }

    public int getPreset(){

        return preferences.getInt(PRESET, 0);
    }

    public void setExtra(int extra, String key){

        editor.putInt(EXTRA+key, extra);
        editor.commit();
    }

    public int getExtra(String key){

        return preferences.getInt(EXTRA+key, 1);
    }

    public boolean isExtra(String key){

        return preferences.getBoolean(EXTRA+key+"_enable", false);
    }

    public void setISExtra(boolean isExtra, String key){

        editor.putBoolean(EXTRA+key+"_enable", isExtra);
        editor.commit();
    }

}
