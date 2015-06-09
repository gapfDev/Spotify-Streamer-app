package com.alxdev.spotifystreamerapp.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alxdev.spotifystreamerapp.R;
import com.alxdev.spotifystreamerapp.views.fragment.TopTenTracksFragment;

public class TopTenTracksActivity extends AppCompatActivity {

    private static final int FRAGMENT_CONTAINER = R.id.frameLayout_container_activityTopTen;
    private static final String TAG_TOP_TEN = "topTen";
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_tracks);

        Intent intent = getIntent();
        String artistId = intent.getStringExtra(MainActivity.ARTIST_ID);
        String artistName = intent.getStringExtra(MainActivity.ARTIST_NAME);

        setUpToolBar(artistName);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            fragmentManager.add(FRAGMENT_CONTAINER, TopTenTracksFragment.getInstance(artistId), TAG_TOP_TEN);
            fragmentManager.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    public void setUpToolBar(String subTitle) {
        mToolBar = (Toolbar) findViewById(R.id.toolBar_TopTen);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setSubtitle(subTitle);
    }
}
