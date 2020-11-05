package com.example.sangeetplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Act.act_artistDetail;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.entities.Artist;

import java.util.List;

public class artistAdapter extends RecyclerView.Adapter<artistAdapter.ViewHolder> {

    private List<Artist> artistList;
    private Context context;
    private OnClick onClick;

    public artistAdapter(List<Artist> artists, Context context, OnClick onClick) {
        this.artistList = artists;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public artistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_artist, parent,false);
        return new artistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull artistAdapter.ViewHolder holder, int position) {

        Artist artist = artistList.get(position);

        holder.artist.setText(artist.getArtist());
        holder.songs.setText("SONGS : "+artist.getSongs());
        holder.albums.setText("ALBUMS : "+artist.getAlbums());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(context, act_artistDetail.class);
//                intent.putExtra("artist", artist);
//                context.startActivity(intent);

                if (onClick != null){

                    onClick.onClick(artist);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView artist, songs, albums;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            artist = itemView.findViewById(R.id.view_artist_name);
            songs = itemView.findViewById(R.id.view_artist_song);
            albums = itemView.findViewById(R.id.view_artist_album);

        }

    }

    public void addArtist(List<Artist> artists){

        this.artistList.addAll(artists);
        notifyDataSetChanged();
    }

    public interface OnClick{

        public void onClick(Artist artist);
    }
}
