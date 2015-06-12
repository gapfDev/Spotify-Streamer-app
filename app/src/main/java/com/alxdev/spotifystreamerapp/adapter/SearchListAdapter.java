package com.alxdev.spotifystreamerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.model.ArtistItem;
import com.alxdev.spotifystreamerapp.model.Constants;
import com.alxdev.spotifystreamerapp.rxbus.RxBus;
import com.alxdev.spotifystreamerapp.views.activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {

    private List<ArtistItem> mArtistsPagers;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RxBus mRxBus;

    public SearchListAdapter(List<ArtistItem> artistsPager, Activity activity) {
        mArtistsPagers = artistsPager;
        mContext = activity.getApplicationContext();
        mLayoutInflater = LayoutInflater.from(mContext);

        mRxBus = ((MainActivity) activity).getRxBusSingleton();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.list_item_search, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        ArtistItem artist = mArtistsPagers.get(i);
        viewHolder.mTextViewArtistListItem.setText(artist.getName());

            if (artist.getThumbnailURL()!= Constants.IMAGE_DEFAULT){
                Picasso.with(mContext).load(artist.getThumbnailURL()).into(viewHolder.mImageViewArtistListItem);
            }
    }

    @Override
    public int getItemCount() {
        return mArtistsPagers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mImageViewArtistListItem;
        private final TextView mTextViewArtistListItem;

        public MyViewHolder(View itemView) {
            super(itemView);

            mImageViewArtistListItem = (ImageView) itemView.findViewById(R.id.imageView_artist_listItem);
            mTextViewArtistListItem = (TextView) itemView.findViewById(R.id.textView_artist_listItem);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mRxBus.hasObservers()) {
                String id = mArtistsPagers.get(getLayoutPosition()).getSpotifyId();
                String name = mArtistsPagers.get(getLayoutPosition()).getName();
                mRxBus.send(new MainActivity.clickOnSearchItem(id, name));
            }
        }
    }
}
