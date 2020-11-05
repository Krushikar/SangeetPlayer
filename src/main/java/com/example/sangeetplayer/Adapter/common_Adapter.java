package com.example.sangeetplayer.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.R;
import com.example.sangeetplayer.entities.Song;

import java.util.List;

public class common_Adapter extends RecyclerView.Adapter<common_Adapter.ViewHolder> {

    private List<Song> songList;
    private Context context;
    private onClickAdapter onClick;

    public common_Adapter(List<Song> songList, Context context, onClickAdapter onClick) {
        this.songList = songList;
        this.context = context;
        this.onClick  = onClick;
    }

    @NonNull
    @Override
    public common_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tracks1, parent, false);
        return new common_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Song song = songList.get(position);


        holder.album_track.setText(song.getTitle());
     //   holder.album_artist.setText(song.getArtist());
        holder.duration.setText(milliSecondsToTimer(song.getDuration()));
        holder.song_no.setText(""+(position+1)+".");



//        if ((position+1)%2 != 0){
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                holder.itemView.setBackgroundColor(context.getColor(R.color.blackfaded));
//            }
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClick != null){

                    onClick.setSong(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView album_track, album_artist, duration, song_no;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            album_track = itemView.findViewById(R.id.view_track_song);
            album_artist = itemView.findViewById(R.id.view_track_artist);
            duration = itemView.findViewById(R.id.view_track_diration);
            song_no = itemView.findViewById(R.id.view_track_no);

        }
    }

    public void addSongs(List<Song> songs){

        this.songList.addAll(songs);
        notifyDataSetChanged();
    }

    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


}
