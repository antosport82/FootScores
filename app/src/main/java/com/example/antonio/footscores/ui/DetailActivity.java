package com.example.antonio.footscores.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.utilities.Constants;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        int gameId = intent.getIntExtra(Constants.GAME_ID, 0);
        if (savedInstanceState == null) {
            DetailFragment scoresDetailsFragment = new DetailFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            scoresDetailsFragment.setGame(gameId);
            fragmentManager.beginTransaction()
                    .add(R.id.details_container_fragment, scoresDetailsFragment)
                    .commit();
        }
    }
}