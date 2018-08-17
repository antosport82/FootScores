package com.example.antonio.footscores.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.utilities.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private int mGameType;
    @BindView(R.id.tv_games)
    TextView games;
    @BindView(R.id.tv_favorites)
    TextView favorites;
    @BindView(R.id.adView)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ButterKnife.bind(this);
        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mGameType = sharedPref.getInt(getString(R.string.saved_game_type), Constants.ALL_GAMES_TYPE);
        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment mainFragment = new MainFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.list_games, mainFragment)
                        .commit();
                mGameType = Constants.ALL_GAMES_TYPE;
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_game_type), mGameType);
                editor.apply();
                games.setTextColor(getResources().getColor(R.color.textSelected));
                games.setBackgroundColor(getResources().getColor(R.color.backgroundSelected));
                favorites.setTextColor(getResources().getColor(R.color.textDefault));
                favorites.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavFragment favFragment = new FavFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.list_games, favFragment)
                        .commit();
                mGameType = Constants.FAVORITE_TYPE;
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_game_type), mGameType);
                editor.apply();
                favorites.setTextColor(getResources().getColor(R.color.textSelected));
                favorites.setBackgroundColor(getResources().getColor(R.color.backgroundSelected));
                games.setTextColor(getResources().getColor(R.color.textDefault));
                games.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        if (savedInstanceState == null && mGameType == Constants.ALL_GAMES_TYPE) {
            MainFragment mainFragment = new MainFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.list_games, mainFragment)
                    .commit();
            mGameType = Constants.ALL_GAMES_TYPE;
            games.setTextColor(getResources().getColor(R.color.textSelected));
            games.setBackgroundColor(getResources().getColor(R.color.backgroundSelected));
            favorites.setTextColor(getResources().getColor(R.color.textDefault));
            favorites.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (mGameType == Constants.FAVORITE_TYPE) {
            FavFragment favFragment = new FavFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.list_games, favFragment)
                    .commit();
            favorites.setTextColor(getResources().getColor(R.color.textSelected));
            favorites.setBackgroundColor(getResources().getColor(R.color.backgroundSelected));
            games.setTextColor(getResources().getColor(R.color.textDefault));
            games.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            MainFragment mainFragment = new MainFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.list_games, mainFragment)
                    .commit();
            games.setTextColor(getResources().getColor(R.color.textSelected));
            games.setBackgroundColor(getResources().getColor(R.color.backgroundSelected));
            favorites.setTextColor(getResources().getColor(R.color.textDefault));
            favorites.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        // two-pane layout case
        if (findViewById(R.id.detail_game) != null) {
            if (savedInstanceState == null) {
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setGame(-1);
                fragmentManager.beginTransaction()
                        .add(R.id.detail_game, detailFragment)
                        .commit();
            } else {
                sharedPref = getPreferences(Context.MODE_PRIVATE);
                int game = sharedPref.getInt(getString(R.string.saved_game_id), -1);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setGame(game);
                fragmentManager.beginTransaction()
                        .replace(R.id.detail_game, detailFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(Constants.GAME_TYPE, mGameType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGameType = savedInstanceState.getInt(Constants.GAME_TYPE);
    }
}