package com.example.antonio.footscores.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.models.Game;
import com.example.antonio.footscores.utilities.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.GamesViewHolder> {

    private Context mContext;
    private ArrayList<Game> mGames;
    private ArrayList<Integer> mFavorites;

    //on-click handler defined to make it easy for an Activity to interface with
    private final GameAdapterOnClickHandler mClickHandler;
    private final GameAdapterOnClickHandlerFav mClickHandlerFav;

    // The interface that receives onClick messages.
    public interface GameAdapterOnClickHandler {
        void onClick(int id);
    }

    // The interface that receives onClick messages.
    public interface GameAdapterOnClickHandlerFav {
        void onClick(Game game, boolean fav);
    }

    public GamesListAdapter(Context mContext, GameAdapterOnClickHandler clickHandler, GameAdapterOnClickHandlerFav clickHandlerFav) {
        this.mContext = mContext;
        this.mClickHandler = clickHandler;
        this.mClickHandlerFav = clickHandlerFav;
    }

    @NonNull
    @Override
    public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutForGame = R.layout.games_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(layoutForGame, parent, false);
        return new GamesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesViewHolder holder, int position) {
        int gameId = mGames.get(position).getGameId();
        String league = mGames.get(position).getLeague();
        String homeTeam = mGames.get(position).getHomeTeam();
        String awayTeam = mGames.get(position).getAwayTeam();
        int played = mGames.get(position).getPlayed();
        String teams = homeTeam + " v " + awayTeam;
        holder.mTeams.setText(teams);
        if (played == 1) {
            int homeScore = mGames.get(position).getHomeScore();
            int awayScore = mGames.get(position).getAwayScore();
            String score = homeScore + " - " + awayScore;
            holder.mScore.setText(score);
        } else {
            long time = mGames.get(position).getKickoff();
            String formattedDate = JsonUtils.getGameDate(time);
            holder.mScore.setText(formattedDate);
        }
        int drawableFlag = JsonUtils.getDrawableFlag(league, mContext);
        Picasso.with(holder.mFlag.getContext())
                .load(drawableFlag)
                .placeholder(R.drawable.loading)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(holder.mFlag);
        boolean favorite = mFavorites != null && JsonUtils.isFavorite(mFavorites, gameId);
        if (favorite) {
            holder.mStar.setImageDrawable(holder.mStar.getContext().getResources().getDrawable(R.drawable.ic_star_full));
        } else {
            holder.mStar.setImageDrawable(holder.mStar.getContext().getResources().getDrawable(R.drawable.ic_star_empty));
        }
    }

    @Override
    public int getItemCount() {
        if (mGames == null) {
            return 0;
        } else {
            return mGames.size();
        }
    }

    public class GamesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final LinearLayout mGameNotes;
        final TextView mTeams;
        final TextView mScore;
        final ImageView mFlag;
        final ImageView mStar;

        GamesViewHolder(View itemView) {
            super(itemView);
            mGameNotes = itemView.findViewById(R.id.ll_game_notes);
            mTeams = itemView.findViewById(R.id.tv_teams);
            mScore = itemView.findViewById(R.id.tv_score);
            mFlag = itemView.findViewById(R.id.iv_flag_league);
            mStar = itemView.findViewById(R.id.iv_star);
            mGameNotes.setOnClickListener(this);
            mStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    int gameId = mGames.get(adapterPosition).getGameId();
                    boolean fav = JsonUtils.isFavorite(mFavorites, gameId);
                    if (fav) {
                        mStar.setImageDrawable(mStar.getContext().getResources().getDrawable(R.drawable.ic_star_empty));
                        mClickHandlerFav.onClick(mGames.get(adapterPosition), false);
                    } else {
                        mStar.setImageDrawable(mStar.getContext().getResources().getDrawable(R.drawable.ic_star_full));
                        mClickHandlerFav.onClick(mGames.get(adapterPosition), true);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int gameId = mGames.get(adapterPosition).getGameId();
            mClickHandler.onClick(gameId);
        }
    }

    public void setGameData(ArrayList<Game> games) {
        mGames = games;
        notifyDataSetChanged();
    }

    public void setFavorites(ArrayList<Integer> favorites) {
        mFavorites = favorites;
        notifyDataSetChanged();
    }
}