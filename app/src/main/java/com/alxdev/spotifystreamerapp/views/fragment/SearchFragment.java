package com.alxdev.spotifystreamerapp.views.fragment;


import android.content.Context;
import android.os.Bundle;
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
import com.alxdev.spotifystreamerapp.views.DividerItemDecoration;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
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

    private static final String ARTIST_NAME = "artistName";
    private RecyclerView mRecyclerViewSearch;
    private SearchListAdapter mSearchListAdapter;
    private ProgressBar mProgressBar;
    private EditText mEditTextSearch;

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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar_load_search);
        setUpRecycleView();

        if (savedInstanceState != null) {
            String artistName = savedInstanceState.getString(ARTIST_NAME);
            if (artistName != null && artistName.length() > 0) {
                searchArtists(artistName);
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

    public Observable<ArtistsPager> getObservableApi(final String name) {

        return Observable.create(new Observable.OnSubscribe<ArtistsPager>() {
            @Override
            public void call(final Subscriber<? super ArtistsPager> subscriber) {

                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotifyService = spotifyApi.getService();

                spotifyService.searchArtists(name, new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        subscriber.onNext(artistsPager);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        subscriber.onNext(null);
                    }
                });


            }
        });

    }

    public Observer<? super ArtistsPager> getObserverApiResult() {
        return new Observer<ArtistsPager>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showMessage();
            }

            @Override
            public void onNext(ArtistsPager artistsPager) {
                if (artistsPager != null) {

                    if (artistsPager.artists.items.size() > 0) {
                        setUpList(artistsPager);
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

    public void setUpList(ArtistsPager artistsPager) {
        mSearchListAdapter = new SearchListAdapter(artistsPager, getActivity());
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

        if (mEditTextSearch.getText().length() > 0) {
            outState.putString(ARTIST_NAME, mEditTextSearch.getText().toString());
        }

    }
}
