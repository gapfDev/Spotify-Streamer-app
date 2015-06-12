package com.alxdev.spotifystreamerapp.views.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.adapter.TopTenListAdapter;
import com.alxdev.spotifystreamerapp.model.Constants;
import com.alxdev.spotifystreamerapp.model.TopTenItem;
import com.alxdev.spotifystreamerapp.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopTenTracksFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerViewTopTen;
    private Subscription mSubscription;
    private String mCountry;
    private ArrayList<TopTenItem> mTopTenItems;

    public static TopTenTracksFragment getInstance(String id) {
        TopTenTracksFragment fragment = new TopTenTracksFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARTIST_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        if (savedInstanceState != null){
            mTopTenItems = savedInstanceState.getParcelableArrayList(Constants.TOP_TEN_OBJECT);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCountry = this.getString(R.string.country);
        setUpRecyclerView();

        Bundle bundle = getArguments();
        String artistId = bundle.getString(Constants.ARTIST_ID);

        searchTopTen(mCountry, artistId);
    }

    private void searchTopTen(final String country, final String code) {

        mSubscription = AndroidObservable.bindFragment(this, getObservableRequest(country, code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverResult());
    }

    private void setUpRecyclerView() {
        mRecyclerViewTopTen = (RecyclerView) getView().findViewById(R.id.recyclerView_top_ten_fragmentTopTen);
        mRecyclerViewTopTen.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerViewTopTen.setHasFixedSize(true);
        mRecyclerViewTopTen.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerViewTopTen.setItemAnimator(new DefaultItemAnimator());

    }

    public void setUpList(List<TopTenItem> topTenItems) {

            TopTenListAdapter topTenAdapter = new TopTenListAdapter(topTenItems, getActivity());
            mRecyclerViewTopTen.setAdapter(topTenAdapter);


    }

    public Observable<List<TopTenItem>> getObservableRequest(final String country, final String code) {

        return Observable
                .create(new Observable.OnSubscribe<List<TopTenItem>>() {
                    @Override
                    public void call(final Subscriber<? super List<TopTenItem>> subscriber) {

                        SpotifyApi spotifyApi = new SpotifyApi();
                        SpotifyService spotifyService = spotifyApi.getService();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(Constants.COUNTRY, mCountry);

                        spotifyService.getArtistTopTrack(code, map, new Callback<Tracks>() {
                            @Override
                            public void success(Tracks tracks, Response response) {

                                List<Track> trackList = tracks.tracks;
                                mTopTenItems = new ArrayList<TopTenItem>();
                                for (Track item : trackList) {
                                    TopTenItem topTenItem = new TopTenItem(item);
                                    mTopTenItems.add(topTenItem);
                                }
                                subscriber.onNext(mTopTenItems);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                subscriber.onNext(null);
                            }
                        });

                    }
                });
    }

    public Observer<? super List<TopTenItem>> getObserverResult() {
        return new Observer<List<TopTenItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<TopTenItem> topTenItems) {
                if (topTenItems != null) {
                    if (topTenItems.size() > 0) {
                        setUpList(topTenItems);
                    } else {
                        showMessage();
                    }
                } else {
                    showMessage();
                }
                onCompleted();
                (getView().findViewById(R.id.progressBar_load_topTen)).setVisibility(View.GONE);
            }
        };
    }

    private void showMessage() {
        Snackbar.make(getView(), this.getString(R.string.artist_not_found), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.TOP_TEN_OBJECT, mTopTenItems);
    }
}
