package com.example.sangeetplayer.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sangeetplayer.Act.act_AlbumDetail;
import com.example.sangeetplayer.R;
import com.example.sangeetplayer.entities.Albums;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.armcha.coloredshadow.ShadowImageView;

public class albumAdapter extends RecyclerView.Adapter<albumAdapter.ViewHolder> {

    private List<Albums> albumsList;
    private Context context;
    private OnClick onClick;

    public albumAdapter(List<Albums> albumsList, Context context, OnClick onClick) {
        this.albumsList = albumsList;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public albumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_album1, parent, false);
        return new albumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull albumAdapter.ViewHolder holder, int position) {

        Albums albums = albumsList.get(position);

        holder.artist.setText(albums.getArtist());
        holder.album.setText(albums.getAlbum());

        Picasso.get().load(albums.getAlbum_art())
                .resize(800, 800)
                .onlyScaleDown()
                .centerCrop()
                .placeholder(R.drawable.album_art1)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {


                      //  holder.shadowImageView.setImageDrawable(albums.getAlbum_art(), true);
                    }

                    @Override
                    public void onError(Exception e) {

                        albums.setAlbum_art("null");

                    }
                });



//        Glide.with(context)
//                .load(albums.getAlbum_art())
//                .apply(new RequestOptions().override(800,800))
//                .placeholder(R.drawable.album_art)
//                .error(R.drawable.album_art)
//                .centerCrop()
//                .into(new ViewTarget<ImageView, Drawable>(holder.shadowImageView) {
//
//                    @Override
//                    public void onLoadStarted(@Nullable Drawable placeholder) {
//                        super.onLoadStarted(placeholder);
//
//                        holder.shadowImageView.setImageDrawable(placeholder, true);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        super.onLoadCleared(placeholder);
//
//                        holder.shadowImageView.setImageDrawable(placeholder, true);
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        super.onLoadFailed(errorDrawable);
//
//                        holder.shadowImageView.setImageDrawable(errorDrawable, true);
//                    }
//
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//
//                        holder.shadowImageView.setImageDrawable(resource, true);
//                    }
//                });


        ViewCompat.setTransitionName(holder.image, "ID"+albums.getId());
        ViewCompat.setTransitionName(holder.album, "IDA"+albums.getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, act_AlbumDetail.class);
////                intent.putExtra("album_id", albums.getAlbum_id());
////                intent.putExtra("album_name", albums.getAlbum());
////                intent.putExtra("album_art", albums.getAlbum_art());
//                intent.putExtra("album", albums);
//                context.startActivity(intent);

                if (onClick != null){

                    onClick.onClick(albums, holder.image, holder.album);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView album, artist;
        ImageView image;
        ShadowImageView shadowImageView;
      //  CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            album = itemView.findViewById(R.id.view_album_name);
            artist = itemView.findViewById(R.id.view_album_artist);
            image = itemView.findViewById(R.id.view_album_image);
            shadowImageView = itemView.findViewById(R.id.sh_image);
          //  cardView = itemView.findViewById(R.id.album_art_cardview);

        }
    }

    public void addAlbums(List<Albums> albums){

        this.albumsList.addAll(albums);
        notifyDataSetChanged();
    }

    public interface OnClick{

        public void onClick(Albums albums, ImageView imageView, TextView albumsName);
    }
}
