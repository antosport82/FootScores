package com.example.antonio.footscores.ui;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
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
import com.example.antonio.footscores.adapters.FavListAdapter;
import com.example.antonio.footscores.data.GameContract;
import com.example.antonio.footscores.models.Game;
import com.example.antonio.footscores.utilities.Constants;
import com.example.antonio.footscores.utilities.JsonUtils;
import com.example.antonio.footscores.widget.FootScoresWidgetProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavFragment extends Fragment {

    private Context mContext;
    @BindView(R.id.rv_games)
    RecyclerView mRvFavorites;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    private FavListAdapter mFavListAdapter;
    private Unbinder unbinder;

    private final LoaderManager.LoaderCallbacks<Cursor> favoritesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            mProgressBar.setVisibility(View.VISIBLE);
            return new CursorLoader(mContext, GameContract.GameEntry.CONTENT_URI_FAVORITES, null, null, null, GameContract.GameEntry.COLUMN_GAME_ID);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            ArrayList<Game> mFavorites;
            mProgressBar.setVisibility(View.INVISIBLE);
            if (data.getCount() != 0) {
                showGamesDataView();
                mFavorites = JsonUtils.cursorToGames(data);
                mFavListAdapter.setFavorites(mFavorites);
                mFavListAdapter.notifyDataSetChanged();
            } else {
                showErrorMessage(getString(R.string.error_message_no_favorites));
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mFavListAdapter.setFavorites(null);
        }
    };

    private final FavListAdapter.GameAdapterOnClickHandler mClickHandler = new FavListAdapter.GameAdapterOnClickHandler() {
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

    private final FavListAdapter.GameAdapterOnClickHandlerFav mClickHandlerFav = new FavListAdapter.GameAdapterOnClickHandlerFav() {
        @Override
        public void onClick(int gameId) {
            removeGameFromFavorites(gameId);
        }
    };

    private void removeGameFromFavorites(int gameId) {
        mProgressBar.setVisibility(View.VISIBLE);
        ContentResolver scoresContentResolver = mContext.getContentResolver();
        scoresContentResolver.delete(GameContract.GameEntry.buildGameFavUriWithId(gameId), null, null);
        mFavListAdapter.notifyDataSetChanged();
        updateWidget();
        if (getActivity() != null) {
            FavFragment favFragment = new FavFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.list_games, favFragment)
                    .commit();
        }
    }

    private final FavListAdapter.GameAdapterOnClickHandler mClickHandlerTwoPane = new FavListAdapter.GameAdapterOnClickHandler() {
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

    public FavFragment() {
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
        mRvFavorites.setLayoutManager(gamesLayoutManager);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        if (this.getActivity() != null) {
            // check if it is a two-pane layout
            if (getActivity().findViewById(R.id.detail_game) != null) {
                mFavListAdapter = new FavListAdapter(mContext, mClickHandlerTwoPane, mClickHandlerFav);
            } else {
                mFavListAdapter = new FavListAdapter(mContext, mClickHandler, mClickHandlerFav);
            }
        }
        mRvFavorites.setAdapter(mFavListAdapter);
        getLoaderManager().initLoader(Constants.FAVORITES_LOADER, null, favoritesLoader);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showGamesDataView() {
        // games are visible, error message is hidden
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRvFavorites.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String error) {
        // error message is visible, games are hidden
        mRvFavorites.setVisibility(View.INVISIBLE);
        mErrorMessage.setText(error);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), FootScoresWidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(Constants.FAVORITES_LOADER, null, favoritesLoader);
    }
}