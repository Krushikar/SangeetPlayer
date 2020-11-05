package com.example.sangeetplayer.Adapter;


import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.R;

import com.example.sangeetplayer.entities.Song;

import java.util.List;

public class album_details_Adapter extends RecyclerView.Adapter<album_details_Adapter.ViewHolder> {

    private List<Song> songList;
    private onClickAdapter onClick;

    public album_details_Adapter(List<Song> songList, onClickAdapter onClick) {
        this.songList = songList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public album_details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tracks1, parent, false);
        return new album_details_Adapter.ViewHolder(view);
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

                    onClick.onClick(songList, position);
                }
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popupMenu = new PopupMenu(holder.menu.getContext(), holder.menu);

                popupMenu.inflate(R.menu.album_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.play:

                                if (onClick != null){

                                    onClick.onClick(songList, position);
                                }

                                popupMenu.dismiss();
                                break;
                            case R.id.play_next:

                                if (onClick != null){

                                    onClick.playNext(song);
                                }

                                popupMenu.dismiss();
                                break;
                            case R.id.menu_add_fav:

                                if (onClick != null){

                                    onClick.addToFav(song);
                                }

                                popupMenu.dismiss();
                                break;
                            case R.id.menu_add_playlist:

                                if (onClick != null){

                                    onClick.addToPlay(song, false);
                                }

                                popupMenu.dismiss();
                                break;

                        }
                        return false;
                    }
                });


                popupMenu.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView album_track, album_artist, duration, song_no;
        ImageView menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            album_track = itemView.findViewById(R.id.view_track_song);
            album_artist = itemView.findViewById(R.id.view_track_artist);
            duration = itemView.findViewById(R.id.view_track_diration);
            song_no = itemView.findViewById(R.id.view_track_no);
            menu = itemView.findViewById(R.id.menu_album);

        }
    }

    public void addAlbumSongs(List<Song> songs){

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
