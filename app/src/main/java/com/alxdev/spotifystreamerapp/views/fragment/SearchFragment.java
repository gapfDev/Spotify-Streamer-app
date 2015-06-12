package com.alxdev.spotifystreamerapp.views.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.adapter.SearchListAdapter;
import com.alxdev.spotifystreamerapp.model.ArtistItem;
import com.alxdev.spotifystreamerapp.model.Constants;
import com.alxdev.spotifystreamerapp.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    private RecyclerView mRecyclerViewSearch;
    private SearchListAdapter mSearchListAdapter;
    private ProgressBar mProgressBar;
    private EditText mEditTextSearch;
    private ArrayList<ArtistItem> mArtistList;

    public static SearchFragment getInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (savedInstanceState != null){
            mArtistList = savedInstanceState.getParcelableArrayList(Constants.ARTIST_OBJECT);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar_load_search);

        setUpRecycleView();

        if (savedInstanceState != null) {

            if (mArtistList != null && mArtistList.size() > 0) {
                setUpList(mArtistList);
            }

        }

        mEditTextSearch = (EditText) getView().findViewById(R.id.editText_search_fragmentSearch);
        mEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    hideSoftKeyboard(mEditTextSearch);
                    mProgressBar.setVisibility(View.VISIBLE);
                    searchArtists(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    public void searchArtists(String name) {

        AndroidObservable.bindFragment(this, getObservableApi(name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverApiResult());


    }

    public Observable<List<ArtistItem>> getObservableApi(final String name) {

        return Observable.create(new Observable.OnSubscribe<List<ArtistItem>>() {
            @Override
            public void call(final Subscriber<? super List<ArtistItem>> subscriber) {

                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotifyService = spotifyApi.getService();

                spotifyService.searchArtists(name, new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {

                        List<Artist> artists = artistsPager.artists.items;
                        mArtistList = new ArrayList<ArtistItem>();

                        for (Artist artistData : artists) {
                            ArtistItem artistItem = new ArtistItem(artistData);
                            mArtistList.add(artistItem);
                        }
                        subscriber.onNext(mArtistList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        subscriber.onNext(null);
                    }
                });


            }
        });

    }

    public Observer<? super List<ArtistItem>> getObserverApiResult() {
        return new Observer<List<ArtistItem>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showMessage();
            }

            @Override
            public void onNext(List<ArtistItem> artistItem) {
                if (artistItem != null) {

                    if (mArtistList.size() > 0) {
                        setUpList(artistItem);
                    } else {
                        showMessage();
                    }

                } else {
                    showMessage();
                }
                onCompleted();
                mProgressBar.setVisibility(View.GONE);

            }
        };
    }

    public void setUpList(List<ArtistItem> artistItems) {
        mSearchListAdapter = new SearchListAdapter(artistItems, getActivity());
        mRecyclerViewSearch.setAdapter(mSearchListAdapter);
    }

    private void showMessage() {
        Snackbar.make(getView(), this.getString(R.string.artist_not_found), Snackbar.LENGTH_SHORT).show();

    }


    private void setUpRecycleView() {
        mRecyclerViewSearch = (RecyclerView) getView().findViewById(R.id.recyclerView_search_fragmentSearch);
        mRecyclerViewSearch.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerViewSearch.setHasFixedSize(true);
        mRecyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerViewSearch.setItemAnimator(new DefaultItemAnimator());
    }

    private void hideSoftKeyboard(EditText editText) {

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState != null){
            outState.putParcelableArrayList(Constants.ARTIST_OBJECT, mArtistList);
        }

    }
}
