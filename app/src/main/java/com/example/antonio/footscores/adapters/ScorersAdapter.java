package com.example.antonio.footscores.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.antonio.footscores.R;

import java.util.ArrayList;

public class ScorersAdapter extends RecyclerView.Adapter<ScorersAdapter.ScorersViewHolder> {

    private Context mContext;
    private ArrayList<String> mScorers;

    public ScorersAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ScorersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutForScorer = R.layout.scorers_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(layoutForScorer, parent, false);
        return new ScorersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScorersAdapter.ScorersViewHolder holder, int position) {
        if (position % 2 != 0) {
            holder.mHomeScorer.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundScorer1));
            holder.mAwayScorer.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundScorer1));
        } else {
            holder.mHomeScorer.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundScorer2));
            holder.mAwayScorer.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundScorer2));
        }
        if (mScorers.get(position).contains("(H)")) {
            String homeScorer = mScorers.get(position).replace("(H)", "");
            holder.mHomeScorer.setText(homeScorer);
        } else {
            String awayScorer = mScorers.get(position).replace("(A)", "");
            holder.mAwayScorer.setText(awayScorer);
        }
        if (mScorers.get(position).length() == 0) {
            holder.mHomeScorer.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.mAwayScorer.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public int getItemCount() {
        if (mScorers == null) {
            return 0;
        } else {
            return mScorers.size();
        }
    }

    public class ScorersViewHolder extends RecyclerView.ViewHolder {

        final TextView mHomeScorer;
        final TextView mAwayScorer;

        public ScorersViewHolder(View itemView) {
            super(itemView);
            mHomeScorer = itemView.findViewById(R.id.home_scorer);
            mAwayScorer = itemView.findViewById(R.id.away_scorer);
        }
    }

    public void setGameScorers(ArrayList<String> scorers) {
        mScorers = scorers;
        notifyDataSetChanged();
    }
}