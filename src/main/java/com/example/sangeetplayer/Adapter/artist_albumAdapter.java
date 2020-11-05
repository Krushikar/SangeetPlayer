package com.example.sangeetplayer.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sangeetplayer.Act.act_AlbumDetail;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.entities.Albums;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class artist_albumAdapter extends RecyclerView.Adapter<artist_albumAdapter.ViewHolder> {

    private List<Albums> albumsList;
    private Context context;

    public artist_albumAdapter(List<Albums> albumsList, Context context) {
        this.albumsList = albumsList;
        this.context = context;
    }

    @NonNull
    @Override
    public artist_albumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_album_art, parent, false);
        return new artist_albumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull artist_albumAdapter.ViewHolder holder, int position) {

        Albums albums = albumsList.get(position);

        holder.albumName.setText(albums.getAlbum());

        Picasso.get().load(albums.getAlbum_art())
                .resize(800, 800)
                .onlyScaleDown()
                .centerCrop()
                .placeholder(R.drawable.album_art1)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        albums.setAlbum_art("null");
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, act_AlbumDetail.class);
                intent.putExtra("album", albums);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView albumName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.view_album_image);
            albumName = itemView.findViewById(R.id.view_album_name);

        }
    }

    public void addAlbums(List<Albums> albums){

        this.albumsList.addAll(albums);
        notifyDataSetChanged();
    }
}
