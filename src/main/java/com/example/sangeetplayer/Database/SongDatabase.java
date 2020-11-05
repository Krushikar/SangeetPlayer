package com.example.sangeetplayer.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sangeetplayer.Dao.songDao;
import com.example.sangeetplayer.entities.Albums;
import com.example.sangeetplayer.entities.Artist;
import com.example.sangeetplayer.entities.Song;

@Database(entities = {Song.class, Albums.class, Artist.class}, version = 2)
public abstract class SongDatabase extends RoomDatabase {

    public abstract songDao songDao();

    private static  SongDatabase INSTANCE;

    public static SongDatabase getDatabase(Context context){

        if (INSTANCE == null){

            synchronized (SongDatabase.class){

                if (INSTANCE == null){

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SongDatabase.class, "sangeet").build();
                }
            }
        }

        return INSTANCE;
    }
}
