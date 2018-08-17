package com.example.antonio.footscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.data.GameContract;
import com.example.antonio.footscores.data.GameDbHelper;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {
        private final Context mContext;
        private Cursor mCursor;

        public ListRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();
            String[] selectionArgs = new String[]{String.valueOf(1)};
            GameDbHelper mOpenHelper = new GameDbHelper(getApplicationContext());
            // The widget will display the 5 most recent played games which were added to the favorites from the user
            String favQuery = "SELECT * FROM favorites WHERE played=? LIMIT 5";
            mCursor = mOpenHelper.getReadableDatabase().rawQuery(favQuery, selectionArgs);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor == null || mCursor.getCount() == 0) return null;
            mCursor.moveToPosition(position);
            String teamHome = mCursor.getString(mCursor.getColumnIndex(GameContract.GameEntry.COLUMN_TEAM_HOME));
            String teamAway = mCursor.getString(mCursor.getColumnIndex(GameContract.GameEntry.COLUMN_TEAM_AWAY));
            int scoreHome = mCursor.getInt(mCursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORE_HOME));
            int scoreAway = mCursor.getInt(mCursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORE_AWAY));
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.foot_scores_widget);
            String teams = teamHome + " v " + teamAway;
            String score = scoreHome + " - " + scoreAway;
            views.setTextViewText(R.id.widget_teams, teams);
            views.setTextViewText(R.id.widget_score, score);
            Intent fillInIntent = new Intent();
            views.setOnClickFillInIntent(R.id.widget_list_view, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}