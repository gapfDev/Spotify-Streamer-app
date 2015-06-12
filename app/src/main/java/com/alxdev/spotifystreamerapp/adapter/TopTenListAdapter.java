package com.alxdev.spotifystreamerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.model.Constants;
import com.alxdev.spotifystreamerapp.model.TopTenItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopTenListAdapter extends RecyclerView.Adapter<TopTenListAdapter.MyViewHolder> {
    private List<TopTenItem> mTopTenItems;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TopTenListAdapter(List<TopTenItem> topTenItems, Activity activity) {

        try {
            mTopTenItems = topTenItems;
            mContext = activity.getApplicationContext();
            mLayoutInflater = LayoutInflater.from(mContext);
        }catch (Exception e){
            Log.d("----------", e.getMessage());
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item_top_ten, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TopTenItem topTenItem = mTopTenItems.get(position);
        holder.mTextView_album.setText(topTenItem.getmAlbumName());
        holder.mTextView_track.setText(topTenItem.getmTrackName());

        if (topTenItem.getmAlbumImage() != Constants.IMAGE_DEFAULT) {

            Picasso.with(mContext).load(topTenItem.getmAlbumImage()).into(holder.mImageViewAlbum);
        }

    }

    @Override
    public int getItemCount() {
        return mTopTenItems.size();
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
