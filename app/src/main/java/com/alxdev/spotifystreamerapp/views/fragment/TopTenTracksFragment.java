package com.alxdev.spotifystreamerapp.views.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.adapter.TopTenListAdapter;
import com.alxdev.spotifystreamerapp.views.DividerItemDecoration;
import com.alxdev.spotifystreamerapp.views.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
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

    private static final String COUNTRY = "country";
    private RecyclerView mRecyclerViewTopTen;
    private Subscription mSubscription;
    private String mCountry;

    public static TopTenTracksFragment getInstance(String id) {
        TopTenTracksFragment fragment = new TopTenTracksFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.ARTIST_ID, id);
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
        return inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCountry = this.getString(R.string.country);
        setUpRecyclerView();

        Bundle bundle = getArguments();
        String artistId = bundle.getString(MainActivity.ARTIST_ID);

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

    public void setUpList(Tracks tracks) {
        TopTenListAdapter topTenAdapter = new TopTenListAdapter(tracks, getActivity().getApplicationContext());
        mRecyclerViewTopTen.setAdapter(topTenAdapter);
    }

    public Observable<Tracks> getObservableRequest(final String country, final String code) {

        return Observable
                .create(new Observable.OnSubscribe<Tracks>() {
                    @Override
                    public void call(final Subscriber<? super Tracks> subscriber) {

                        SpotifyApi spotifyApi = new SpotifyApi();
                        SpotifyService spotifyService = spotifyApi.getService();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(COUNTRY, mCountry);

                        spotifyService.getArtistTopTrack(code, map, new Callback<Tracks>() {
                            @Override
                            public void success(Tracks tracks, Response response) {
                                subscriber.onNext(tracks);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                subscriber.onNext(null);
                            }
                        });

                    }
                });
    }

    public Observer<? super Tracks> getObserverResult() {
        return new Observer<Tracks>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Tracks tracks) {
                if (tracks != null) {
                    if (tracks.tracks.size() > 0) {
                        setUpList(tracks);
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

//        outState.putParcelable();
    }
}
