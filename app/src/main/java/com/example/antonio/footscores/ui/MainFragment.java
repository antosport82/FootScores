package com.example.antonio.footscores.ui;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.adapters.GamesListAdapter;
import com.example.antonio.footscores.data.GameContract;
import com.example.antonio.footscores.models.Game;
import com.example.antonio.footscores.sync.ScoresSyncUtils;
import com.example.antonio.footscores.utilities.Constants;
import com.example.antonio.footscores.utilities.JsonUtils;
import com.example.antonio.footscores.widget.FootScoresWidgetProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends Fragment {

    private Context mContext;
    @BindView(R.id.rv_games)
    RecyclerView mRvGames;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    private GamesListAdapter mGameListAdapter;
    private Unbinder unbinder;

    private final LoaderManager.LoaderCallbacks<Cursor> favoritesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            mProgressBar.setVisibility(View.VISIBLE);
            return new CursorLoader(mContext, GameContract.GameEntry.CONTENT_URI_FAVORITES, null, null, null, null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            ArrayList<Integer> mFavorites = new ArrayList<>();
            if (data.getCount() != 0) {
                mFavorites = JsonUtils.cursorToFavorites(data);
            }
            mGameListAdapter.setFavorites(mFavorites);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };
    private final LoaderManager.LoaderCallbacks<Cursor> cursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            mProgressBar.setVisibility(View.VISIBLE);
            return new CursorLoader(mContext, GameContract.GameEntry.CONTENT_URI_GAMES, null, null, null, GameContract.GameEntry.COLUMN_GAME_ID);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if (data.getCount() != 0) {
                mProgressBar.setVisibility(View.GONE);
                showGamesDataView();
                ArrayList<Game> mGames = JsonUtils.cursorToGames(data);
                mGameListAdapter.setGameData(mGames);
            } else {
                showErrorMessage(getString(R.string.error_loading_data));
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mGameListAdapter.setGameData(null);
        }
    };

    private final GamesListAdapter.GameAdapterOnClickHandler mClickHandler = new GamesListAdapter.GameAdapterOnClickHandler() {
        @Override
        public void onClick(int gameId) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Constants.GAME_ID, gameId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            } else {
                startActivity(intent);
            }
        }
    };

    private final GamesListAdapter.GameAdapterOnClickHandlerFav mClickHandlerFav = new GamesListAdapter.GameAdapterOnClickHandlerFav() {
        @Override
        public void onClick(Game game, boolean fav) {
            if (fav) {
                insertGameIntoFavorites(game);
            } else {
                int gameId = game.getGameId();
                removeGameFromFavorites(gameId);
            }
        }
    };

    private void removeGameFromFavorites(int gameId) {
        ContentResolver scoresContentResolver = mContext.getContentResolver();
        scoresContentResolver.delete(GameContract.GameEntry.buildGameFavUriWithId(gameId), null, null);
        scoresContentResolver.notifyChange(GameContract.GameEntry.CONTENT_URI_FAVORITES, null);
        updateWidget();
    }

    private void insertGameIntoFavorites(Game game) {
        ContentValues cv = new ContentValues();
        cv.put(GameContract.GameEntry.COLUMN_GAME_ID, game.getGameId());
        cv.put(GameContract.GameEntry.COLUMN_LEAGUE, game.getLeague());
        cv.put(GameContract.GameEntry.COLUMN_TEAM_HOME, game.getHomeTeam());
        cv.put(GameContract.GameEntry.COLUMN_TEAM_AWAY, game.getAwayTeam());
        cv.put(GameContract.GameEntry.COLUMN_KICKOFF, game.getKickoff());
        cv.put(GameContract.GameEntry.COLUMN_PLAYED, game.getPlayed());
        cv.put(GameContract.GameEntry.COLUMN_SCORE_HOME, game.getHomeScore());
        cv.put(GameContract.GameEntry.COLUMN_SCORE_AWAY, game.getAwayScore());
        cv.put(GameContract.GameEntry.COLUMN_SCORERS, game.getScorers());
        ContentResolver scoresContentResolver = mContext.getContentResolver();
        scoresContentResolver.insert(GameContract.GameEntry.CONTENT_URI_FAVORITES, cv);
        updateWidget();
    }

    private final GamesListAdapter.GameAdapterOnClickHandler mClickHandlerTwoPane = new GamesListAdapter.GameAdapterOnClickHandler() {
        @Override
        public void onClick(int gameId) {
            DetailFragment detailFragment = new DetailFragment();
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                detailFragment.setGame(gameId);
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_game_id), gameId);
                editor.apply();
                fragmentManager.beginTransaction()
                        .replace(R.id.detail_game, detailFragment)
                        .commit();
            }
        }
    };

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        LinearLayoutManager gamesLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRvGames.setLayoutManager(gamesLayoutManager);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        if (this.getActivity() != null) {
            // check if it is a two-pane layout
            if (getActivity().findViewById(R.id.detail_game) != null) {
                mGameListAdapter = new GamesListAdapter(mContext, mClickHandlerTwoPane, mClickHandlerFav);
            } else {
                mGameListAdapter = new GamesListAdapter(mContext, mClickHandler, mClickHandlerFav);
            }
            mRvGames.setAdapter(mGameListAdapter);
        }
        getLoaderManager().initLoader(Constants.FAVORITES_LOADER, null, favoritesLoader);
        getLoaderManager().initLoader(Constants.ALL_GAMES_LOADER, null, cursorLoader);
        ScoresSyncUtils.initialize(mContext);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showGamesDataView() {
        // games are visible, error message is hidden
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRvGames.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String error) {
        // error message is visible, games are hidden
        mRvGames.setVisibility(View.INVISIBLE);
        mErrorMessage.setText(error);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), FootScoresWidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }
}