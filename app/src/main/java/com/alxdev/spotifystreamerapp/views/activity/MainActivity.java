package com.alxdev.spotifystreamerapp.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.model.Constants;
import com.alxdev.spotifystreamerapp.rxbus.RxBus;
import com.alxdev.spotifystreamerapp.views.fragment.SearchFragment;

import rx.Subscription;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity {


    private RxBus mRxBus;
    private Toolbar mToolBar;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolBar();

        if (savedInstanceState == null) {
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            fragmentManager.add(Constants.FRAGMENT_CONTAINER, SearchFragment.getInstance(), Constants.TAG_SEARCH_FRAGMENT);
            fragmentManager.commit();
        }

        mRxBus = getRxBusSingleton();
    }

    private void setUpToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    public RxBus getRxBusSingleton() {
        if (mRxBus == null) {
            mRxBus = new RxBus();
        }

        return mRxBus;
    }

    public static class clickOnSearchItem {
        private String mIdArtist;
        private String mName;

        public clickOnSearchItem(String id, String name) {
            this.mIdArtist = id;
            mName = name;
        }

        public String getmIdArtist() {
            return mIdArtist;
        }

        public String getmName() {
            return mName;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mSubscription = mRxBus.toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                        if (o.getClass().getName() == clickOnSearchItem.class.getName()) {
                            clickOnSearchItem clickOnSearchItem = (MainActivity.clickOnSearchItem) o;
                            Intent intent = new Intent(getApplicationContext(), TopTenTracksActivity.class);
                            intent.putExtra(Constants.ARTIST_ID, clickOnSearchItem.getmIdArtist());
                            intent.putExtra(Constants.ARTIST_NAME, clickOnSearchItem.getmName());
                            startActivity(intent);

                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSubscription.unsubscribe();
    }
}
