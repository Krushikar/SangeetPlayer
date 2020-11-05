package com.example.sangeetplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Act.act_Playlist;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.entities.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class playlistAdapter extends RecyclerView.Adapter<playlistAdapter.ViewHolder> {

    private List<Song> songs;
    private OnClick onClick;
    private Context context;

    public playlistAdapter(List<Song> songs, OnClick onClick, Context context){
        this.songs = songs;
        this.onClick = onClick;
        this.context = context;
    }



    @NonNull
    @Override
    public playlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tracks, parent, false);
        return new playlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull playlistAdapter.ViewHolder holder, int position) {

        Song song = songs.get(position);

        holder.track.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.duration.setText(milliSecondsToTimer(song.getDuration()));

        Picasso.get().load(song.getAlbum_art())
                .resize(800, 800)
                .centerCrop()
                .onlyScaleDown()
                .placeholder(R.drawable.album_art1)
                .into(holder.image);

        if (act_Playlist.CURRENT_SONG_ID == position){

            holder.view.setVisibility(View.VISIBLE);
         //   holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.max_yellow));
        }else {

            holder.view.setVisibility(View.GONE);
         //   holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.recycle_ripple));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClick != null){

                    act_Playlist.CURRENT_SONG_ID = position;
                    onClick.onClick(position);
                    notifyDataSetChanged();
                }
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(holder.menu.getContext(), holder.menu);

                popupMenu.inflate(R.menu.playlist_menu);

                if (act_Playlist.CURRENT_SONG_ID == position) {

                    popupMenu.getMenu().getItem(0).setVisible(false);
                    popupMenu.getMenu().getItem(1).setVisible(false);

                }


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.play:

                                if (onClick != null){

                                    onClick.onClick(position);
                                }

                                popupMenu.dismiss();
                                break;
                            case R.id.play_next:

                                if (onClick != null){

                                    onClick.playNext(song, position);
                                }

                                popupMenu.dismiss();
                                break;
                            case R.id.remove:

                                if (onClick != null){

                                    onClick.removeFromPlay(position);
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
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView track, duration, artist;
        ImageView image, menu;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            track = itemView.findViewById(R.id.view_track_song);
            duration = itemView.findViewById(R.id.view_track_diration);
            artist = itemView.findViewById(R.id.view_track_artist);
            image = itemView.findViewById(R.id.view_trackt_song_art);
            menu = itemView.findViewById(R.id.fav_ctx_menu);
            view = itemView.findViewById(R.id.view1);

        }
    }

    public void addSongs(List<Song> songs){

        this.songs.clear();
        this.songs.addAll(songs);
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


    public interface OnClick{
         public void onClick(int pos);
       //  void onLongClick(Song song);
        void removeFromPlay(int pos);
        void playNext(Song song, int pos);
    }
}
