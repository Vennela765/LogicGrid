package com.example.logicgrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.logicgrid.data.Player;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder> {
    private final List<Player> players;

    public PlayersAdapter(List<Player> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.rankText.setText(String.valueOf(position + 1));
        holder.nameText.setText(player.getName());
        holder.levelText.setText(String.format("Level %d", player.getCurrentLevel()));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView rankText;
        final TextView nameText;
        final TextView levelText;

        ViewHolder(View view) {
            super(view);
            rankText = view.findViewById(R.id.rankText);
            nameText = view.findViewById(R.id.nameText);
            levelText = view.findViewById(R.id.levelText);
        }
    }
} 