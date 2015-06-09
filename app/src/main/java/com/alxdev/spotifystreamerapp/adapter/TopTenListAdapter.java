package com.alxdev.spotifystreamerapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxdev.spotifystreamerapp.R;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTenListAdapter extends RecyclerView.Adapter<TopTenListAdapter.MyViewHolder> {
    private Tracks mTracks;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TopTenListAdapter(Tracks tracks, Context context) {
        mTracks = tracks;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item_top_ten, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Track track = mTracks.tracks.get(position);
        holder.mTextView_album.setText(track.album.name);
        holder.mTextView_track.setText(track.name);

        if (track.album.images.size() >= 2) {
            Picasso.with(mContext).load(track.album.images.get(1).url).into(holder.mImageViewAlbum);
        }

    }

    @Override
    public int getItemCount() {
        return mTracks.tracks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageViewAlbum;
        TextView mTextView_track;
        TextView mTextView_album;

        public MyViewHolder(View itemView) {
            super(itemView);

            mImageViewAlbum = (ImageView) itemView.findViewById(R.id.imageView_album_itemTopTen);
            mTextView_album = (TextView) itemView.findViewById(R.id.textView_album_itemTopTen);
            mTextView_track = (TextView) itemView.findViewById(R.id.textView_track_itemTopTen);

            mTextView_track.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
