package com.example.antonio.footscores.ui;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.adapters.ScorersAdapter;
import com.example.antonio.footscores.data.GameContract;
import com.example.antonio.footscores.models.Game;
import com.example.antonio.footscores.utilities.Constants;
import com.example.antonio.footscores.utilities.JsonUtils;
import com.example.antonio.footscores.widget.FootScoresWidgetProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailFragment extends Fragment {

    private Context mContext;
    private Unbinder unbinder;
    private Game mGame;
    int mGameId;
    @BindView(R.id.tv_detail_teams)
    TextView tvTeams;
    @BindView(R.id.tv_detail_score)
    TextView tvScore;
    @BindView(R.id.iv_detail_flag_league)
    ImageView ivFlag;
    @BindView(R.id.iv_detail_star)
    ImageView ivStar;
    @BindView(R.id.rv_scorers)
    RecyclerView mRvScorers;
    private ScorersAdapter mScorersAdapter;
    private boolean mFavorite;

    private final LoaderManager.LoaderCallbacks<Cursor> cursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            String selection = GameContract.GameEntry.COLUMN_GAME_ID + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(mGameId)};
            return new CursorLoader(mContext, GameContract.GameEntry.CONTENT_URI_GAMES, null, selection, selectionArgs, null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                mGame = JsonUtils.cursorToGame(data);
                String allScorers = mGame.getScorers();
                ArrayList<String> scorers = JsonUtils.extractScorers(allScorers);

                mScorersAdapter.setGameScorers(scorers);

                String league = mGame.getLeague();
                String homeTeam = mGame.getHomeTeam();
                String awayTeam = mGame.getAwayTeam();
                int played = mGame.getPlayed();
                String teams = homeTeam + " v " + awayTeam;
                int drawableFlag = JsonUtils.getDrawableFlag(league, mContext);
                Picasso.with(mContext)
                        .load(drawableFlag)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error_outline_black_24dp)
                        .into(ivFlag);
                tvTeams.setText(teams);
                if (played == 1) {
                    int homeScore = mGame.getHomeScore();
                    int awayScore = mGame.getAwayScore();
                    String score = homeScore + " - " + awayScore;
                    tvScore.setText(score);
                } else {
                    long time = mGame.getKickoff();
                    String formattedDate = JsonUtils.getGameDate(time);
                    tvScore.setText(formattedDate);
                }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mScorersAdapter.setGameScorers(null);
        }
    };

    private final LoaderManager.LoaderCallbacks<Cursor> favLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            String selection = GameContract.GameEntry.COLUMN_GAME_ID + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(mGameId)};
            return new CursorLoader(mContext, GameContract.GameEntry.CONTENT_URI_FAVORITES, null, selection, selectionArgs, null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if ((!(data.moveToFirst()) || data.getCount() == 0)) {
                ivStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_empty));
                mFavorite = false;
            } else {
                ivStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_full));
                mFavorite = true;
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        LinearLayoutManager scorersLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRvScorers.setLayoutManager(scorersLayoutManager);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        if (this.getActivity() != null) {
            // check if it is a two-pane layout
            if (getActivity().findViewById(R.id.detail_game) != null) {
                ivStar.setVisibility(View.INVISIBLE);
                mScorersAdapter = new ScorersAdapter(mContext);
                mRvScorers.setAdapter(mScorersAdapter);
                if (mGameId != -1) {
                    getActivity().findViewById(R.id.linear_no_game).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.linear_game).setVisibility(View.VISIBLE);
                    getLoaderManager().initLoader(Constants.SINGLE_GAME_LOADER, null, cursorLoader);

                }
            } else {
                mScorersAdapter = new ScorersAdapter(mContext);
                mRvScorers.setAdapter(mScorersAdapter);
                getLoaderManager().initLoader(Constants.SINGLE_GAME_LOADER, null, cursorLoader);
                getLoaderManager().initLoader(Constants.SINGLE_FAV_LOADER, null, favLoader);
                ivStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mFavorite) {
                            ContentResolver scoresContentResolver = mContext.getContentResolver();
                            scoresContentResolver.delete(GameContract.GameEntry.buildGameFavUriWithId(mGameId), null, null);
                            updateWidget();
                            ivStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_empty));
                            mFavorite = false;
                        } else {
                            ContentResolver scoresContentResolver = mContext.getContentResolver();
                            ContentValues cv = JsonUtils.getGameContentValues(mGame);
                            scoresContentResolver.insert(GameContract.GameEntry.CONTENT_URI_FAVORITES, cv);
                            updateWidget();
                            ivStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_full));
                            mFavorite = true;
                        }
                    }
                });

            }

        }
    }

    public void setGame(int gameId) {
        mGameId = gameId;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), FootScoresWidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }
}